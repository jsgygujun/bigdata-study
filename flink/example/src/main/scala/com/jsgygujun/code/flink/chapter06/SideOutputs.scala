package com.jsgygujun.code.flink.chapter06

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector

/**
 *
 * 侧输出流
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 5:01 下午
 */
object SideOutputs {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // 设置 checkpoint 每隔10秒
    env.getCheckpointConfig.setCheckpointInterval(10 * 1000)

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    // 添加数据源
    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    val monitorData = sensorData
      .process(new FreezingMonitor)

    monitorData
      .getSideOutput(new OutputTag[String]("freezing-alarms"))
      .print()

    env.execute("侧输出流")
  }

  /**
   * 自定义 ProcessFunction
   * 温度低于32度，加入到侧输出流
   */
  class FreezingMonitor extends ProcessFunction[SensorData, SensorData] {
    // 定义侧输出流标签
    lazy val freezingAlarmOutput = new OutputTag[String]("freezing-alarms")

    override def processElement(
                                 value: SensorData,
                                 ctx: ProcessFunction[SensorData, SensorData]#Context,
                                 out: Collector[SensorData]): Unit = {
      if (value.temperature < 32.0) {
        // 侧输出流
        ctx.output(freezingAlarmOutput, s"Freezing Alarm for ${value.id}")
      }
      // 常规输出
      out.collect(value)
    }
  }
}
