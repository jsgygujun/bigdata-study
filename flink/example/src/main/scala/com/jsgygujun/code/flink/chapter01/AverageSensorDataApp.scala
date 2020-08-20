package com.jsgygujun.code.flink.chapter01

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 10:44 上午
 */
object AverageSensorDataApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 设置并行度
    env.setParallelism(2)
    // 设置应用的事件标准为事件时间
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 设置watermark产生时间间隔
    env.getConfig.setAutoWatermarkInterval(1000L)
    // 添加数据源，并设置水位线生产方式
    val sensorDataStream = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner())

    val avgTempStream = sensorDataStream
      // 华氏度 转 摄氏度
      .map(data => SensorData(data.id, data.timestamp, (data.temperature-32)*(5.0/9.0)))
      // 按照传感器id分组
      .keyBy(_.id)
      // 3秒长度的窗口
      .timeWindow(Time.seconds(3))
      // 计算
      .apply(new calcAgvTempFunction)

    // 打印结果
    avgTempStream.print

    // 执行
    env.execute("计算传感器平均温度")
  }

  class calcAgvTempFunction extends WindowFunction[SensorData, SensorData, String, TimeWindow] {
    /**
     * app 方法一个窗口调用一次
     * @param key
     * @param window
     * @param input
     * @param out
     */
    override def apply(
                        key: String,
                        window: TimeWindow,
                        input: Iterable[SensorData],
                        out: Collector[SensorData]): Unit = {
      val (cnt, sum) = input.foldLeft((0, 0.0))((c, r) => (c._1 + 1, c._2 + r.temperature))
      out.collect(SensorData(key, window.getEnd, sum/cnt))
    }
  }
}
