package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 10:24 上午
 */
object ReduceByKeyExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("ReduceByKeyExp")
    val sc = new SparkContext(conf)
    reduceByKey(sc)
    sc.stop()
  }

  private def reduceByKey(sc: SparkContext): Unit = {
    val list = List(("female",1),("male",5),("female",5),("male",2))
    val rdd1 = sc.parallelize(list)
    val rdd2 = rdd1.reduceByKey(_ + _)
    rdd2.collect.foreach(println)
  }
}
