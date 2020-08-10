package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object FlatMapExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("FlatMapExp")
    val sc = new SparkContext(conf)
    flatMapDemo(sc)
    sc.stop()
  }

  /**
   * flatMap
   * @param sc
   */
  private def flatMapDemo(sc: SparkContext): Unit = {
    val list = List(1,3,5,7,9,2,4,6,8)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.flatMap(x => if (x % 2 == 0) List(x, x * x, x * x * x) else List[Int]())
    rdd2.collect.foreach(println)
  }
}
