package com.jsgygujun.code.spark_core.transformation

import org.apache.spark.{SparkConf, SparkContext}

object SampleExp {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("SampleExp")
    val sc = new SparkContext(conf)
    sampleDemo(sc)
    sc.stop()
  }

  private def sampleDemo(sc: SparkContext): Unit = {
    val list = List(1,2,3,4,5,6,7,8,9)
    val rdd1 = sc.makeRDD(list, 4)
    val rdd2 = rdd1.sample(withReplacement = true, 0.5)
    println("withReplacement:\n")
    rdd2.collect.foreach(println)
    val rdd3 = rdd1.sample(withReplacement = false, 0.5)
    print("withoutReplacement:\n")
    rdd3.collect.foreach(println)
  }
}
