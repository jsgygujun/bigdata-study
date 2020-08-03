package com.jsgygujun.code

import org.apache.spark.{SparkConf, SparkContext}

/**
 * groupBy(func)
 *     按照func的返回值进行分组，func返回值作为 key, 对应的值放入一个迭代器中. 返回的 RDD: RDD[(K, Iterable[T])
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/3 10:31 上午
 */
object GroupByExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("GlomExp").setMaster("local[2]")
    val sc = new SparkContext(conf)
    val list = List(1,2,3,4,5,6,7,8,9)
    val rdd1 = sc.parallelize(list, 2)
    // 按照奇偶来分组
    val rdd2 = rdd1.groupBy(_ % 2)
    val array = rdd2.collect()
    array.foreach(it => {
      print(s"p${it._1}: ")
      it._2.foreach(x => print(x + " "))
      println()
    })
  }
}
