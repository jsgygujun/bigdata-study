package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 10:07 上午
 */
object SortByExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("SortByExp")
    val sc = new SparkContext(conf)
    sortByDemo(sc)
    sc.stop()
  }

  private def sortByDemo(sc: SparkContext): Unit = {
    val list = List(-1,2,3,-4,3,2,-2,-3,8,9,-6,6,4,-1,1,2,-3,-9,6)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.sortBy(x => Math.abs(x))
    rdd2.collect.foreach(println)
  }
}
