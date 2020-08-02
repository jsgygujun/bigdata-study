package com.jsgygujun.code

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object CreateRDD {

  def main(args: Array[String]): Unit = {
    // 1. 得到SparkContext
    val conf: SparkConf = new SparkConf().setMaster("local[2]").setAppName("CreateRDD")
    val sc: SparkContext = new SparkContext(conf)
    // 2. 创建RDD 从Scala集合得到RDD
    val arr1: Array[Int] = Array(30,50,70,60,10,20)
    val rdd: RDD[Int] = sc.parallelize(arr1)
    // 3. 转换
    val rdd1 = rdd.map(x => x * 2)
    // 4. 行动算子
    val array = rdd1.collect()
    array.foreach(println)
    // 5. 关闭SparkContext
    sc.stop()
  }

}

/**
 * 得到RDD：
 * 1. 从数据源
 * 2. 从其他RDD转换得到
 */
