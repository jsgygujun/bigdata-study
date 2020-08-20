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
 * 传感器数据源，用来模拟传感器数据的生产
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
    // 随机生成10个传感器温度值，并且不停在之前温度基础上更新（随机上下波动）
    var currFTemp = (0 to 9).map(i => ("sensor_" + i, 65 + (rand.nextGaussian() * 20)))
    // 无限循环，生成随机数据流
    while (running) {
      // 在当前温度基础上，随机生成微小波动
      currFTemp = currFTemp.map(data => (data._1, data._2 + (rand.nextGaussian() * 0.5)))
      // 获取当前时间
      val currTime = Calendar.getInstance.getTimeInMillis
      // 包装成样例类，用ctx发出数据
      currFTemp.foreach(data => ctx.collect(SensorData(data._1, currTime, data._2)))
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
