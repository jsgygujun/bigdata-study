package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/4 3:38 下午
 */
object ReduceByKeyExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("ReduceByKeyExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(("female",1),("male",5),("female",5),("male",2))
    val rdd1 = sc.parallelize(list1)
    val rdd2 = rdd1.reduceByKey(_ + _)
    rdd2.collect.foreach(println)
    sc.stop()
  }
}
