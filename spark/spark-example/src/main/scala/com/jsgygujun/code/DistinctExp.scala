package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object DistinctExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("FilterExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(1, 2, 3, 4, 2, 3, 5, 9, 3, 4)
    val rdd1 = sc.parallelize(list1, 2)
    val rdd2 = rdd1.distinct()
    rdd2.collect().foreach(println)
    sc.stop()
  }
}
