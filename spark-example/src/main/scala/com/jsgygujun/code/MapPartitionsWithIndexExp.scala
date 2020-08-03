package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object MapPartitionsWithIndexExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("MapPartitionsWithIndexExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(30, 50, 70, 60, 10, 20)
    val rdd1 = sc.parallelize(list1, 2)
    //mapPartitionsWithIndex: 带分区号
    val rdd2 = rdd1.mapPartitionsWithIndex((index, it) => {
      it.map((index, _))
    })
    rdd2.collect().foreach(println)
    sc.stop()
  }
}
