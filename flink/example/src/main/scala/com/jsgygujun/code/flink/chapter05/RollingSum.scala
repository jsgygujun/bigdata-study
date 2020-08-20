package com.jsgygujun.code.flink.chapter05

import org.apache.flink.streaming.api.scala._

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 2:44 下午
 */
object RollingSum {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val inputStream: DataStream[(Int, Int, Int)] = env.fromElements(
      (1, 2, 2), (2, 3, 1), (2, 2, 4), (1, 5, 3))

    val resultStream: DataStream[(Int, Int, Int)] = inputStream
      .keyBy(0)
      .sum(1)

    resultStream.print()

    /** Rolling Sum
     * 3> (1,2,2)
     * 4> (2,3,1)
     * 4> (2,5,1)
     * 3> (1,7,2)
     */

    env.execute("Rolling Sum Example")
  }

}
