package com.jsgygujun.code.flink.chapter05

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._

/**
 *
 * 分区流转换算子
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 2:07 下午
 */
object KeyedTransformations {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    // 根据传感器ID分组
    val keyedSensorData = sensorData
      .keyBy(_.id)

    // reduce
    val maxTempPerSensor = keyedSensorData
      .reduce((d1, d2) => {
        if (d1.temperature > d2.temperature) d1 else d2
      })

    maxTempPerSensor.print()

    env.execute("分区转换算子")
  }
}
