package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object GlomExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("GlomExp")
    val sc = new SparkContext(conf)
    glomDemo(sc)
    sc.stop()
  }

  private def glomDemo(sc: SparkContext): Unit ={
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.glom().map(_.toList)
    rdd2.collect.foreach(println)
  }
}
