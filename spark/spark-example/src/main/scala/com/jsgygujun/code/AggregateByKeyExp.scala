package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/4 3:47 下午
 */
object AggregateByKeyExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AggregateByKeyExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list1 = List(("a",3),("a",2),("c",4),("b",3),("c",6),("c",8))
    val rdd1 = sc.parallelize(list1)
    // 取出每个分区相同key对应值的最大值，然后相加
    val rdd2 = rdd1.aggregateByKey(Int.MinValue)(math.max, _+_)
    rdd2.collect.foreach(println)
    sc.stop()
  }
}
