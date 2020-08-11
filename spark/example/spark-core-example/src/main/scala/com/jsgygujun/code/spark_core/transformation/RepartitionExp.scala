package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 10:01 上午
 */
object RepartitionExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("RepartitionExp")
    val sc = new SparkContext(conf)
    repartitionDemo(sc)
    sc.stop()
  }

  private def repartitionDemo(sc: SparkContext): Unit = {
    val list = List(1,2,3,4,5,6,7,8,6,4,3,3,3,4,3,3,3,4,5)
    val rdd1 = sc.parallelize(list, 2)
    val rdd2 = rdd1.repartition(4)
    rdd2.mapPartitionsWithIndex((index, it) => {
      it.map(x => (index, x))
    }).collect.foreach(println)
  }
}
