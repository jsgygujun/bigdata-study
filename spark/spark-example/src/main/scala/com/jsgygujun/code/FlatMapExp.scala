package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object FlatMapExp {
  def main(args: Array[String]): Unit = {
    flatMapDemo01()
    flatMapDemo02()
  }

  def flatMapDemo01(): Unit = {
    val conf = new SparkConf().setAppName("flatMapDemo01").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(1 to 3, 1 to 5, 10 to 20)
    val rdd1 = sc.parallelize(list1, 2)
    val rdd2 = rdd1.flatMap(x => x) // flatMap(func), 一定要保证func返回的是一个集合
    rdd2.collect.foreach(println)
    sc.stop()
  }

  def flatMapDemo02(): Unit = {
    val conf = new SparkConf().setAppName("flatMapDemo02").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(1,2,3,4,5,6,7,8,9)
    val rdd1 = sc.parallelize(list1, 2)
    val rdd2 = rdd1.flatMap(x => if (x % 2 == 0) List(x, x * x, x * x * x) else List[Int]())
    rdd2.collect.foreach(println)
    sc.stop()
  }
}
