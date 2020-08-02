package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object FlatMap {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MapPartitions").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(30, 5, 70, 6, 1, 20)
    val rdd1 = sc.parallelize(list1, 2)
    val rdd2 = rdd1.flatMap(x => if (x % 2 == 0) List(x, x * x, x * x * x) else List[Int]())
    rdd2.collect().foreach(println)
  }
}
