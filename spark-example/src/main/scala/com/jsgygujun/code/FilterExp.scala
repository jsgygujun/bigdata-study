package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object FilterExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("FilterExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(30, 50, 70, 60, 10, 20)
    val rdd1 = sc.parallelize(list1, 2)
    val rdd2 = rdd1.filter(_ > 20)
    rdd2.collect().foreach(println)
    sc.stop()
  }
}
