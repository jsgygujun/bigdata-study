package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

object SortByExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SortByExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(5,4,2,8,3,6,9)
    val rdd1 = sc.parallelize(list1)
    val rdd2 = rdd1.sortBy(x => x, ascending = false) // sortBy(func), 按照func返回值进行排序
    rdd2.collect.foreach(println)
    sc.stop()
  }
}
