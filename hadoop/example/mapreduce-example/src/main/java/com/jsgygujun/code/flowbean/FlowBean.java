package com.jsgygujun.code.flowbean;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class FlowBean implements Writable {

    private long upFlow; // 上行流量
    private long dnFlow; // 下行流量
    private long sumFlow; // 总流量

    public long getUpFlow() {
        return upFlow;
    }

    public long getDnFlow() {
        return dnFlow;
    }

    public long getSumFlow() {
        return sumFlow;
    }

    public void setUpFlow(long upFlow) {
        this.upFlow = upFlow;
    }

    public void setDnFlow(long dnFlow) {
        this.dnFlow = dnFlow;
    }

    public void setSumFlow(long sumFlow) {
        this.sumFlow = sumFlow;
    }

    /**
     * MR 专用序列化
     * 在写出属性时，如果为引用数据类型，属性不能为null
     * @param dataOutput
     * @throws IOException
     */
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeLong(upFlow);
        dataOutput.writeLong(dnFlow);
        dataOutput.writeLong(sumFlow);
    }

    /**
     * MR 专用反序列化
     * 序列化和反序列化的顺序要一致
     * @param dataInput
     * @throws IOException
     */
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        upFlow = dataInput.readLong();
        dnFlow = dataInput.readLong();
        sumFlow = dataInput.readLong();
    }

    @Override
    public String toString() {
        return "FlowBean{" +
                "upFlow=" + upFlow +
                ", dnFlow=" + dnFlow +
                ", sumFlow=" + sumFlow +
                '}';
    }

}
