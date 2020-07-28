package com.jsgygujun.code.flowbean;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class FlowBeanReducer extends Reducer<Text, FlowBean, Text, FlowBean> {

    private final FlowBean VALUE = new FlowBean();

    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        long sumUpFlow = 0, sumDnFlow = 0;
        for (FlowBean flowBean : values) {
            sumUpFlow += flowBean.getDnFlow();
            sumDnFlow += flowBean.getDnFlow();
        }
        VALUE.setUpFlow(sumUpFlow);
        VALUE.setDnFlow(sumDnFlow);
        VALUE.setSumFlow(sumUpFlow+sumDnFlow);

        context.write(key, VALUE);
    }
}
