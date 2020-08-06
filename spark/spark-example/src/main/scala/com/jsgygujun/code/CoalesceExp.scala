package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

/**
 * coalesce(numPartitions)
 *     缩减分区数到指定的数量，用于大数据集过滤后，提高小数据集的执行效率。
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/3 10:45 上午
 */
object CoalesceExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("CoalesceExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val rdd1 = sc.parallelize(1 to 100, 4)
    val array = rdd1
      .filter(_ % 2 == 1)
      .coalesce(2) // 减少分区到2
      .collect()
    array.foreach(x => print(x + ", "))
    sc.stop()
  }
}
