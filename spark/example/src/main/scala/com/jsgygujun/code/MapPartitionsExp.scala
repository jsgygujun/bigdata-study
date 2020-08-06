package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object MapPartitionsExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MapPartitionsExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(30, 50, 70, 60, 10, 20)
    val rdd1 = sc.parallelize(list1, 2)
    //mapPartitions每个分区执行一次
    val rdd2 = rdd1.mapPartitions(it => it.map(_ * 2))
    rdd2.collect().foreach(println)
    sc.stop()
  }
}

/**
 * map： 传递的函数每个元素执行一次
 * mapPartitions： 传递的函数每个分区执行一次，有可能出现OOM
 */
