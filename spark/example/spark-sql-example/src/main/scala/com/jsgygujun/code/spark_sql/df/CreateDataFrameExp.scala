package com.jsgygujun.code.spark_sql.df

import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 *
 * @author gujun@qiyi.com
 * @since 2020/8/6 4:14 下午
 */
object CreateDataFrameExp {

  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder()
      .master("local[*]")
      .appName("CreateDataFrameExp")
      .getOrCreate()
    val df = createDataFrameFromJson(spark)
    df.show()
//    +------+-------+
//    |Salary|   name|
//    +------+-------+
//    | 30000|Michael|
//    | 19000|   Andy|
//    | 40000| Justin|
//    | 24000|  Berta|
//    +------+-------+
    val df2 = createDataFrameFromText(spark)
    df2.show()
    spark.stop()
  }

  def createDataFrameFromJson(spark: SparkSession): DataFrame = {
    spark.read.json("data/employees.json")
  }

  def createDataFrameFromText(spark: SparkSession): DataFrame = {
    spark.read.text("data/employees.txt")
  }

  def createDataframeFromCsv(spark: SparkSession): DataFrame = {
    spark.read.csv("data/employees.txt")
  }
}
