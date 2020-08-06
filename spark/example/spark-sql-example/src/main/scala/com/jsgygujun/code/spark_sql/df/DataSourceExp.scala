package com.jsgygujun.code.spark_sql.df

import org.apache.spark.sql.SparkSession

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/6 5:20 下午
 */
object DataSourceExp {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("DataSourceExp")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    runParquetFileDataSourceExample(spark)
    //runJsonFileDataSourceExample(spark)
    //runCSVFileDataSourceExample(spark)
    spark.stop()
  }

  /**
   * Parquet 文件 读写
   * @param spark
   */
  private def runParquetFileDataSourceExample(spark: SparkSession): Unit = {
    val usersDF = spark.read.load("data/users.parquet")
    usersDF.show()
    usersDF.select("name", "favorite_color").write.save("data/namesAndFavColors.parquet")
  }

  /**
   * Json 文件 读写
   * @param spark
   */
  private def runJsonFileDataSourceExample(spark: SparkSession): Unit = {
    val peopleDF = spark.read.format("json").load("data/people.json")
    peopleDF.show()
    peopleDF.select("name", "age").write.format("json").save("data/output/nameAndAge.json")
  }

  /**
   * CSV 文件 读写
   */
  private def runCSVFileDataSourceExample(spark: SparkSession): Unit = {
    val peopleDF = spark.read.format("csv")
      .option("sep", ";")
      .option("inferSchema", "true")
      .option("header", "true")
      .load("data/people.csv")
    peopleDF.show()
    peopleDF.select("name", "job").write.format("csv")
      .option("header", "true")
      .save("data/output/nameAndJob.csv")
  }
}
