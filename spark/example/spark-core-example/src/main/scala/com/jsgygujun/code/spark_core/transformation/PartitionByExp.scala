package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 10:25 上午
 */
object PartitionByExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("PartitionByExp")
    val sc = new SparkContext(conf)
    partitionByDemo(sc)
    sc.stop()
  }

  private def partitionByDemo(sc: SparkContext): Unit = {
    val list = List((1, "one"), (2, "two"), (3, "three"), (4, "four"), (5, "five"), (6, "six"), (7, "seven"), (8, "eight"))
    val rdd1 = sc.parallelize(list, 2)
    val rdd2 = rdd1.partitionBy(new HashPartitioner(4))
    rdd1.mapPartitionsWithIndex((index, it) => {
      it.map(x => (index, x))
    }).collect.foreach(println)
    rdd2.mapPartitionsWithIndex((index, it) => {
      it.map(x => (index, x))
    }).collect.foreach(println)
  }
}
