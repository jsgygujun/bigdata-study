package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object FilterExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("FilterExp")
    val sc = new SparkContext(conf)
    filterDemo(sc)
    sc.stop()
  }

  private def filterDemo(sc: SparkContext): Unit = {
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.filter(_ % 3 != 0)
    rdd2.collect.foreach(println)
  }
}
