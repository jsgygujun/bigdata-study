package com.jsgygujun.code.flink.util

import java.util.Calendar

import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.util.Random

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 10:30 上午
 */
/**
 * 传感器源，用来模拟传感器数据的生产
 */
class SensorSource extends RichParallelSourceFunction[SensorData] {
  // 数据源是否还在运行的标志
  var running: Boolean = true

  /**
   * 通过SourceContext 持续产生 SensorData 数据
   * @param ctx
   */
  override def run(ctx: SourceFunction.SourceContext[SensorData]): Unit = {
    // 初始化随机数产生器
    val rand = new Random()
    // 根据并行任务数产生传感器ID
    val taskIdx = this.getRuntimeContext.getIndexOfThisSubtask
    // 初始化传感器Id和温度
    var currFTemp = (1 to 10).map(i => ("sensor_" + (taskIdx * 10 + i), 65 + (rand.nextGaussian() * 20)))
    while (running) {
      // 更新温度
      currFTemp = currFTemp.map(t => (t._1, t._2 + (rand.nextGaussian() * 0.5)))
      // 获取当前时间
      val currTime = Calendar.getInstance.getTimeInMillis
      // 发送新的传感器数据
      currFTemp.foreach(t => ctx.collect(SensorData(t._1, currTime, t._2)))
      // 控制传感器数据生产速度
      Thread.sleep(100)
    }
  }

  /**
   * 停止数据源生产数据
   */
  override def cancel(): Unit = {
    running = false
  }
}
