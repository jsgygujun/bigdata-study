package com.jsgygujun.code.flowbean;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class FlowBeanMapper extends Mapper<LongWritable, Text, Text, FlowBean> {

    private final Text KEY = new Text();
    private final FlowBean VALUE = new FlowBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] fields = value.toString().split("\t", -1);
        // 手机号
        KEY.set(fields[1]);
        // 上行流量
        VALUE.setUpFlow(Long.parseLong(fields[fields.length-3]));
        // 下行流量
        VALUE.setDnFlow(Long.parseLong(fields[fields.length-2]));
        // 总流量
        VALUE.setSumFlow(Long.parseLong(fields[fields.length-1]));

        context.write(KEY, VALUE);
    }
}
