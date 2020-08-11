package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 9:49 上午
 */
object DistinctExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("DistinctExp")
    val sc = new SparkContext(conf)
    distinctDemo(sc)
    sc.stop()
  }

  /**
   * distinct
   * 去重，可同时减少分区
   * @param sc
   */
  private def distinctDemo(sc: SparkContext): Unit = {
    val list = List(1,2,3,4,2,3,4,2,3,4,5,6,3,4,5,3)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.distinct()
    rdd2.collect.foreach(println)
  }
}
