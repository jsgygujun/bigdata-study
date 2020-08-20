package com.jsgygujun.code.flink.chapter05

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.api.common.functions.{FilterFunction, FlatMapFunction, MapFunction}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 *
 * 基本转换算子
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 11:41 上午
 */
object BasicTransformations {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(2)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    // 添加数据源、设置时间、水位线
    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    // filter
    val filteredSensorData = sensorData.filter(data => data.temperature >= 25)

    // map
    val sensorIds = filteredSensorData.map(data => data.id)

    // flatMap
    val splitIds = sensorIds.flatMap(id => id.split("_"))

    splitIds.print()

    env.execute("基本转换示例")
  }

  // UDF Filter 函数
  class TemperatureFilter(threshold: Long) extends FilterFunction[SensorData] {
    override def filter(t: SensorData): Boolean = t.temperature >= threshold
  }

  // UDF Map 函数
  class ProjectionMap extends MapFunction[SensorData, String] {
    override def map(t: SensorData): String = t.id
  }

  // UDF FlatMap 函数
  class SplitIdFlatMap extends FlatMapFunction[String, String] {
    override def flatMap(t: String, collector: Collector[String]): Unit = {
      t.split("_")
    }
  }
}
