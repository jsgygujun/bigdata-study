package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object GroupByExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("GroupByExp")
    val sc = new SparkContext(conf)
    groupByDemo(sc)
    sc.stop()
  }

  private def groupByDemo(sc: SparkContext): Unit = {
    val list = List(1, 3, 5, 7, 9, 2, 4, 6, 8)
    val rdd1 = sc.parallelize(list, 4)
    val rdd2 = rdd1.groupBy(x => x % 2)
    rdd2.collect.foreach(println)
    val rdd3 = rdd2.map {
      case (k, it) => (k, it.sum)
    }
    rdd3.collect.foreach(println)
  }

}
