package com.jsgygujun.code.flink.chapter06

import com.jsgygujun.code.flink.util.{SensorData, SensorSource, SensorTimeAssigner}
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 4:26 下午
 */
object ProcessFunctionTimers {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)

    // 添加数据源
    val sensorData = env
      .addSource(new SensorSource)
      .assignTimestampsAndWatermarks(new SensorTimeAssigner)

    val warnings = sensorData
      .keyBy(_.id)
      .process(new TemperatureIncreaseAlertFunction)

    warnings.print()

    env.execute("监控传感器温度")
  }

  /**
   * 温度持续上升1秒产生报警信息
   */
  class TemperatureIncreaseAlertFunction extends KeyedProcessFunction[String, SensorData, String] {

    // 记录上一次温度值
    lazy val lastTemp: ValueState[Double] = getRuntimeContext.getState(
      new ValueStateDescriptor[Double]("lastTemp", Types.of[Double])
    )

    // 记录当前定时器时间戳
    lazy val currTimer: ValueState[Long] = getRuntimeContext.getState(
      new ValueStateDescriptor[Long]("timer", Types.of[Long])
    )

    /**
     * 每来一条数据都会调用
     * @param value 数据
     * @param ctx 上下文
     * @param out 输出
     */
    override def processElement(
                                 value: SensorData,
                                 ctx: KeyedProcessFunction[String, SensorData, String]#Context,
                                 out: Collector[String]): Unit = {
      // 获取上一次温度值
      val prevTemp = lastTemp.value()
      // 跟新当前温度
      lastTemp.update(value.temperature)

      val currTimerTimestamp = currTimer.value()
      if (prevTemp == 0.0) {
        // 第一次获取温度，无法比较和前一次的温度
      } else if (value.temperature < prevTemp) {
        // 温度下降，删除定时器
        ctx.timerService().deleteProcessingTimeTimer(currTimerTimestamp)
      } else if (value.temperature > prevTemp && currTimerTimestamp == 0) {
        // 温度上升，但是还未设置定时器
        val timerTs = ctx.timerService().currentProcessingTime() + 1000L
        ctx.timerService().registerEventTimeTimer(timerTs)
        // 记录定时器
        currTimer.update(timerTs)
      }
    }

    /**
     * 定时器回调函数
     * @param timestamp 时间戳
     * @param ctx 上下文
     * @param out 输出
     */
    override def onTimer(
                          timestamp: Long,
                          ctx: KeyedProcessFunction[String, SensorData, String]#OnTimerContext,
                          out: Collector[String]): Unit = {
      out.collect("传感器: " + ctx.getCurrentKey + ", 温度已经持续上升1秒")
      currTimer.clear()
    }
  }
}
