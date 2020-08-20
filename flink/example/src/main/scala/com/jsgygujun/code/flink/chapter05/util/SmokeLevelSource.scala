package com.jsgygujun.code.flink.chapter05.util

import com.jsgygujun.code.flink.chapter05.util.SmokeLevel.SmokeLevel
import org.apache.flink.streaming.api.functions.source.{RichParallelSourceFunction, SourceFunction}

import scala.util.Random

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 2:21 下午
 */
class SmokeLevelSource extends RichParallelSourceFunction[SmokeLevel]{

  var running = true

  override def cancel(): Unit = {
    running = false;
  }

  override def run(ctx: SourceFunction.SourceContext[SmokeLevel]): Unit = {
    val rand = new Random()
    while (running) {
      if (rand.nextGaussian() > 0.8) {
        ctx.collect(SmokeLevel.High)
      } else {
        ctx.collect(SmokeLevel.Low)
      }
      Thread.sleep(1000L)
    }
  }
}
