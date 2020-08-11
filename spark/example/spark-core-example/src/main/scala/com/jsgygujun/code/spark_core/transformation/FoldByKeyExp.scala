package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/11 11:36 上午
 */
object FoldByKeyExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("FoldByKeyExp")
    val sc = new SparkContext(conf)
    foldByKeyDemo(sc)
    sc.stop()
  }

  /**
   * 求和
   * @param sc
   */
  private def foldByKeyDemo(sc: SparkContext): Unit = {
    val list = List(("a",3), ("a",1), ("c",4), ("b",3), ("c",6), ("c",8))
    val rdd1 = sc.parallelize(list, 2)
    val rdd2 = rdd1.foldByKey(0)(_+_)
    rdd2.collect().foreach(println)
  }
}
