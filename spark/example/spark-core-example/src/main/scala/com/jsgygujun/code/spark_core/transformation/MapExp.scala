package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object MapExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("MapExp")
    val sc = new SparkContext(conf)
    mapDemo(sc)
    mapPartitionsDemo(sc)
    mapPartitionsWithIndexDemo(sc)
    sc.stop()
  }

  /**
   * map
   * 针对集合中的每个元素执行一次传递的函数
   * 一进一出
   * 将来用来做数据结构的调整
   *
   * @param sc
   */
  def mapDemo(sc: SparkContext): Unit = {
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list)
    val rdd2 = rdd1.map(_ * 2)
    rdd2.collect.foreach(println)
  }

  /**
   * mapPartitions
   * 传递的函数, 每个分区执行一次
   * 有可能会出现oom
   *
   * @param sc
   */
  def mapPartitionsDemo(sc: SparkContext): Unit = {
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list)
    val rdd2 = rdd1.mapPartitions(it => {
      // it 是一个Iterator类型
      it.map(_ * 3)
    })
    rdd2.collect.foreach(println)
  }

  /**
   * mapPartitionsWithIndex
   * 与mapPartitions一样，只是多个一个分区号
   *
   * @param sc
   */
  def mapPartitionsWithIndexDemo(sc: SparkContext): Unit = {
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.mapPartitionsWithIndex((index, it) => {
      // index, 分区号，it 是一个Iterator类型
      it.map(x => (index, x))
    })
    rdd2.collect.foreach(println)
  }
}
