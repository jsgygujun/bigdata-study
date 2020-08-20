package com.jsgygujun.code.flink.chapter05

import com.jsgygujun.code.flink.chapter05.util.SmokeLevel.SmokeLevel
import com.jsgygujun.code.flink.chapter05.util.{Alert, SmokeLevel, SmokeLevelSource}
import org.apache.flink.streaming.api.scala._
import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.co.CoFlatMapFunction
import org.apache.flink.util.Collector

/**
 *
 * 多流转换算子
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 2:20 下午
 */
object MultiStreamTransformations {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    // 温度输入
    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    // 烟雾输入
    val smokeData = env
      .addSource(new SmokeLevelSource)
      .setParallelism(1)

    // 温度按照传感器ID分组
    val keyed = sensorData
      .keyBy(_.id)

    // 触发报警
    val alerts = keyed
      .connect(smokeData.broadcast)
      .flatMap(new RaiseAlertFlatMap)

    alerts.print()

    env.execute("双流转换算子")
  }


  /**
   * 烟雾等级高的同时温度达到100度，则触发报警
   */
  class RaiseAlertFlatMap extends CoFlatMapFunction[SensorData, SmokeLevel, Alert] {

    var smokeLevel = SmokeLevel.Low

    override def flatMap1(value: SensorData, out: Collector[Alert]): Unit = {
      if (smokeLevel.equals(SmokeLevel.High) && value.temperature > 100) {
        out.collect(Alert("Risk of fire!", value.timestamp))
      }
    }

    override def flatMap2(value: SmokeLevel, out: Collector[Alert]): Unit = {
      smokeLevel = value
    }
  }
}
