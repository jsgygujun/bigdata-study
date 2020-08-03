package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/3 10:14 上午
 */
object GlomExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GlomExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list = List(1,2,3,4,5,6,7,8,9)
    val rdd1 = sc.parallelize(list, 2)
    // 将每一个分区的元素合并成一个数组，一个分区一个数组
    val rdd2 = rdd1.glom()
    val rdd3 = rdd2.map(_.toList) // 数组转List
    val array = rdd3.collect()
    array.foreach(println)
    sc.stop()
  }
}
