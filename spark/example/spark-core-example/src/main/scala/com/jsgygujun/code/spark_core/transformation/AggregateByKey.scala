package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 11:25 上午
 */
object AggregateByKey {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("AggregateByKey")
    val sc = new SparkContext(conf)
    aggregateByKeyDemo(sc)
    sc.stop()
  }

  /**
   * 计算每个分区最大值然后相加
   * @param sc
   */
  private def aggregateByKeyDemo(sc: SparkContext): Unit = {
    val list = List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8))
    val rdd1 = sc.parallelize(list, 2)
    val rdd2 = rdd1.aggregateByKey(Int.MinValue)(math.max, _+_)
    rdd2.collect.foreach(println)
  }
}
