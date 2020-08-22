package com.jsgygujun.code.flink.chapter07

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.util.Collector

object StatefulProcessFunction {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.getCheckpointConfig.setCheckpointInterval(10*1000L) // 10秒生成一次checkpoint
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    val keyedSensorData = sensorData
      .keyBy(_.id)

    val alerts = keyedSensorData
      .process(new SelfCleaningTemperatureAlertFunction(1.5))

    alerts.print

    env.execute("产生温度报警")
  }

  /**
   * 可以清除状态的 KeyedProcessFunction
   * @param threshold
   */
  class SelfCleaningTemperatureAlertFunction(val threshold: Double) extends KeyedProcessFunction[String, SensorData, (String, Double, Double)] {

    // 用于存储最近一次温度的键控状态引用
    private var lastTempState: ValueState[Double] = _
    // 用于存储上一个注册的计时器的键控状态引用
    private var lastTimerState: ValueState[Long] = _

    override def open(parameters: Configuration): Unit = {
      // 注册用于最近一次温度的状态
      val lastTempDesc = new ValueStateDescriptor[Double]("lastTemp", classOf[Double])
      lastTempState = getRuntimeContext.getState[Double](lastTempDesc)
      // 注册用于上一个计时器的状态
      val lastTimerDesc = new ValueStateDescriptor[Long]("lastTimer", classOf[Long])
      lastTimerState = getRuntimeContext.getState[Long](lastTimerDesc)
    }

    override def processElement(
                                 value: SensorData,
                                 ctx: KeyedProcessFunction[String, SensorData, (String, Double, Double)]#Context,
                                 out: Collector[(String, Double, Double)]): Unit = {
      // 将清理状态的计时器设置为比上次记录时间戳晚一个小时
      val newTimer = ctx.timestamp() + (3600 * 1000)
      // 获取当前计时器的时间戳
      val curTimer = lastTimerState.value()
      // 删除前一个计时器并注册一个新的计时器
      ctx.timerService().deleteEventTimeTimer(curTimer)
      ctx.timerService().registerEventTimeTimer(newTimer)
      // 更新计时器时间戳
      lastTimerState.update(newTimer)

      // 从状态中获取上一次的温度
      val lastTemp = lastTempState.value()
      // 检查是否需要发出警报
      val tempDiff = (value.temperature - lastTemp).abs
      if (tempDiff > threshold) {
        // 温度增加超过阈值
        out.collect((value.id, value.temperature, tempDiff))
      }

      // 更新lastTemp状态
      this.lastTempState.update(value.temperature)
    }

    override def onTimer(
                          timestamp: Long,
                          ctx: KeyedProcessFunction[String, SensorData, (String, Double, Double)]#OnTimerContext,
                          out: Collector[(String, Double, Double)]): Unit = {
      // clear all state for the key
      lastTempState.clear()
      lastTimerState.clear()
    }
  }
}
