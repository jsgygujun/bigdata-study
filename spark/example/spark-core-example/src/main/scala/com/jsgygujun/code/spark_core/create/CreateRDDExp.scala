package com.jsgygujun.code.spark_core.create

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmai.com
 * @since 2020/8/6 3:41 下午
 */
object CreateRDDExp {
  def main(args: Array[String]): Unit = {
    createRDDFromCollection()
  }

  def createRDDFromCollection(): Unit = {
    // 1. 创建 SparkContext
    val conf = new SparkConf().setMaster("local[*]").setAppName("CreateRDDExp")
    val sc = new SparkContext(conf)
    // 2. 创建RDD 从 Scala 集合创建
    val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val rdd = sc.parallelize(list)
    println(rdd) // ParallelCollectionRDD[0] at parallelize at CreateRDDExp.scala:21
    sc.stop()
  }
}
