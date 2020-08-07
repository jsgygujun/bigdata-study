package com.jsgygujun.code.flink

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.api.scala._

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/7 11:26 上午
 */
object SourceExp {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //runCollectionSourceDemo(env)
    runTextFileSourceDemo(env)
    env.execute()
  }

  /**
   * 从集合中读取
   * @param env
   */
  private def runCollectionSourceDemo(env: StreamExecutionEnvironment): Unit = {
    val stream = env.fromCollection(List(
      SensorReading("sensor_1", 1547718199, 35.8),
      SensorReading("sensor_6", 1547718201, 15.4),
      SensorReading("sensor_7", 1547718202, 6.7),
      SensorReading("sensor_10", 1547718205, 38.1)
    ))
    stream.print("stream").setParallelism(1)
  }

  case class SensorReading(id: String, timestamp: Long, temperature: Double )
  case class People(id: String, age: Int)

  /**
   * 从文件读取
   * @param env
   */
  private def runTextFileSourceDemo(env: StreamExecutionEnvironment): Unit = {
    val stream1 = env.readTextFile("data/people.txt")
    val stream2 = stream1.map(line => {
      val fields = line.split(",")
      People(fields(0), fields(1).trim.toInt)
    })
    stream2.print("stream").setParallelism(2)
  }
}
