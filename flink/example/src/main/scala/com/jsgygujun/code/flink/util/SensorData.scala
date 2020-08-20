package com.jsgygujun.code.flink.util

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 10:29 上午
 */
/**
 * 传感器数据
 * @param id 传感器ID
 * @param timestamp 时间戳
 * @param temperature 温度值
 */
case class SensorData(id: String, timestamp: Long, temperature: Double)

