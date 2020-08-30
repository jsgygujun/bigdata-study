package com.jsgygujun.code.uba.hotimtes

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.java.tuple.{Tuple, Tuple1}
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.WindowFunction
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

/**
 * 每5分钟统计一个小时内热门商品 TopN
 */
object HotItemsApp {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setMaxParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    // 输入流
    val inputStream = env.readTextFile("data/flink/uba/UserBehavior.csv")

    // 转成用户行为样例类
    val userActionStream = inputStream
      .map(data => {
        val fields = data.split(",")
        UserAction(fields(0).toLong, fields(1).toLong, fields(2).toInt, fields(3), fields(4).toLong)
      })
      .assignAscendingTimestamps(_.timestamp*1000L)

    // 过滤pv，开窗聚合统计个数
    val aggStream = userActionStream
      .filter(_.action == "pv")
      .keyBy("ItemId")
      .timeWindow(Time.hours(1), Time.minutes(5))
      // 直接做sum拿不到Window信息
      // CountAgg: 定义窗口聚合规则
      // ItemCountWindowResult: 定义输出数据结构
      // 结合增量聚合+全窗口函数
      .aggregate(new CountAgg, new ItemCountWindowResult)

  }

  /**
   * 定义「预聚合」函数，来一条数据就加1
   */
  class CountAgg() extends AggregateFunction[UserAction, Long, Long] {
    // 窗口内碰到一条数据就加1
    override def add(in: UserAction, acc: Long): Long = acc + 1
    // 状态累加器
    override def createAccumulator(): Long = 0L

    override def getResult(acc: Long): Long = acc

    override def merge(acc: Long, acc1: Long): Long = acc + acc1

  }

  /**
   * 定义窗口聚合函数，结合Window信息包装成样例类
   * IN: 输入累加器的类型
   * OUT: 窗口累加以后的输出类型
   * KEY: Tuple 范型，这里是itemId，窗口根据itemId聚合
   * W: 聚合的窗口,w.getEnd获取窗口的结束时间
   */
  class ItemCountWindowResult() extends WindowFunction[Long, ItemViewCount, Tuple, TimeWindow] {
    override def apply(key: Tuple, window: TimeWindow, input: Iterable[Long], out: Collector[ItemViewCount]): Unit = {
      val itemId = key.asInstanceOf[Tuple1[Long]].f0
      val windowEnd = window.getEnd
      val count = input.iterator.next()
      out.collect(ItemViewCount(itemId, windowEnd, count))
    }
  }
}

/**
 * 定义输入用户行为样例类
 */
case class UserAction(userId: Long, itemId: Long, categoryId: Int, action: String, timestamp: Long)

/**
 * 定义窗口聚合结果的样例类
 */
case class ItemViewCount(itemId: Long, windowEnd: Long, count: Long)


