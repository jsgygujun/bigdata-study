package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 9:53 上午
 */
object CoalesceExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("CoalesceExp")
    val sc = new SparkContext(conf)
    coalesceDemo(sc)
    sc.stop()
  }

  private def coalesceDemo(sc: SparkContext): Unit = {
    val list = List(1,2,3,4,5,6,45,3,2,3,4,5,6,7,8,7,8,6)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.coalesce(2)
    val rdd3 = rdd2.mapPartitionsWithIndex((index, it) => {
      it.map(x => (index, x))
    })
    rdd2.collect.foreach(println)
    rdd3.collect.foreach(println)
  }
}
