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
    // 将每一个分区的元素合并成一个数组
    val rdd2 = rdd1.glom()
    val array = rdd2.collect()
    array.foreach(it => {
      print("p: ")
      it.foreach(x => print(x + " "))
      println()
    })
  }
}
