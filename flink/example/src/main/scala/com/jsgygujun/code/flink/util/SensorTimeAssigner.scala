package com.jsgygujun.code.flink.util

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.windowing.time.Time

/**
 *
 * @author jsgygujun@gmail.com
 * @since 2020/8/20 11:35 上午
 */
class SensorTimeAssigner extends BoundedOutOfOrdernessTimestampExtractor[SensorData](Time.seconds(5)) {

  override def extractTimestamp(element: SensorData): Long = element.timestamp

}
