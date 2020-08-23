package com.jsgygujun.code.flink.chapter06

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.api.common.functions.{AggregateFunction, ReduceFunction}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

object WindowFunctions {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getCheckpointConfig.setCheckpointInterval(10 * 1000L)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    val minTempPerWindow = sensorData
      .map(data => (data.id, data.temperature))
      .keyBy(_._1)
      .timeWindow(Time.seconds(15))
      .reduce((d1, d2) => (d1._1, d1._2.min(d2._2)))

    val minTempPerWindow2: DataStream[(String, Double)] = sensorData
      .map(r => (r.id, r.temperature))
      .keyBy(_._1)
      .timeWindow(Time.seconds(15))
      .reduce(new MinTempFunction)

    val avgTempPerWindow: DataStream[(String, Double)] = sensorData
      .map(r => (r.id, r.temperature))
      .keyBy(_._1)
      .timeWindow(Time.seconds(15))
      .aggregate(new AvgTempFunction)

    // 每5秒钟输出最低和最高温度
    val minMaxTempPerWindow: DataStream[MinMaxTemp] = sensorData
      .keyBy(_.id)
      .timeWindow(Time.seconds(5))
      .process(new HighAndLowTempProcessFunction)

    val minMaxTempPerWindow2: DataStream[MinMaxTemp] = sensorData
      .map(r => (r.id, r.temperature, r.temperature))
      .keyBy(_._1)
      .timeWindow(Time.seconds(5))
      .reduce(
        // incrementally compute min and max temperature
        (r1: (String, Double, Double), r2: (String, Double, Double)) => {
          (r1._1, r1._2.min(r2._2), r1._3.max(r2._3))
        },
        // finalize result in ProcessWindowFunction
        new AssignWindowEndProcessFunction()
      )

    minTempPerWindow.print()

    env.execute("窗口处理函数")
  }

  class MinTempFunction extends ReduceFunction[(String, Double)] {
    override def reduce(r1: (String, Double), r2: (String, Double)) = {
      (r1._1, r1._2.min(r2._2))
    }
  }

  class AvgTempFunction
    extends AggregateFunction[(String, Double), (String, Double, Int), (String, Double)] {

    override def createAccumulator() = {
      ("", 0.0, 0)
    }

    override def add(in: (String, Double), acc: (String, Double, Int)) = {
      (in._1, in._2 + acc._2, 1 + acc._3)
    }

    override def getResult(acc: (String, Double, Int)) = {
      (acc._1, acc._2 / acc._3)
    }

    override def merge(acc1: (String, Double, Int), acc2: (String, Double, Int)) = {
      (acc1._1, acc1._2 + acc2._2, acc1._3 + acc2._3)
    }
  }

  case class MinMaxTemp(id: String, min: Double, max:Double, endTs: Long)

  /**
   * A ProcessWindowFunction that computes the lowest and highest temperature
   * reading per window and emits a them together with the
   * end timestamp of the window.
   */
  class HighAndLowTempProcessFunction
    extends ProcessWindowFunction[SensorData, MinMaxTemp, String, TimeWindow] {

    override def process(
                          key: String,
                          ctx: Context,
                          vals: Iterable[SensorData],
                          out: Collector[MinMaxTemp]): Unit = {

      val temps = vals.map(_.temperature)
      val windowEnd = ctx.window.getEnd

      out.collect(MinMaxTemp(key, temps.min, temps.max, windowEnd))
    }
  }

  class AssignWindowEndProcessFunction
    extends ProcessWindowFunction[(String, Double, Double), MinMaxTemp, String, TimeWindow] {

    override def process(
                          key: String,
                          ctx: Context,
                          minMaxIt: Iterable[(String, Double, Double)],
                          out: Collector[MinMaxTemp]): Unit = {

      val minMax = minMaxIt.head
      val windowEnd = ctx.window.getEnd
      out.collect(MinMaxTemp(key, minMax._2, minMax._3, windowEnd))
    }
  }
}
