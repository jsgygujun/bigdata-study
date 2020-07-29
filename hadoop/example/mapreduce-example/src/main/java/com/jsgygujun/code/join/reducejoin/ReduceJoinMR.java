package com.jsgygujun.code.join.reducejoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jsgygujun@gmail.com
 * @since 2020/7/29 10:57 上午
 */
public class ReduceJoinMR {

    private static class TableBean implements Writable {
        private String orderId; // 订单ID
        private String pid; // 产品ID
        private int amount; // 产品数量
        private String pname; // 产品数量
        private String source; // 标记表的来源

        public TableBean() {
        }

        public TableBean(String orderId, String pid, int amount, String pname, String source) {
            this.orderId = orderId;
            this.pid = pid;
            this.amount = amount;
            this.pname = pname;
            this.source = source;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(orderId);
            out.writeUTF(pid);
            out.writeInt(amount);
            out.writeUTF(pname);
            out.writeUTF(source);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            orderId = in.readUTF();
            pid = in.readUTF();
            amount = in.readInt();
            pname = in.readUTF();
            source = in.readUTF();
        }

        @Override
        public String toString() {
            return orderId + "\t" + pname + "\t" + amount + "\t" ;
        }
    }

    private static class TableMapper extends Mapper<LongWritable, Text, Text, TableBean> {
        private final TableBean TABLE_BEAN = new TableBean();
        private final Text KEY = new Text();
        private String source;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // 获取输入文件切片
            FileSplit split = (FileSplit) context.getInputSplit();
            // 获取输入文件名称
            source = split.getPath().getName();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 获取输入数据
            String line = value.toString();
            // 不同文件分别处理
            String[] fields = line.split("\t", -1);
            if (source.startsWith("order")) {
                // 订单表处理
                TABLE_BEAN.setOrderId(fields[0]);
                TABLE_BEAN.setPid(fields[1]);
                TABLE_BEAN.setAmount(Integer.parseInt(fields[2]));
                TABLE_BEAN.setPname("");
                TABLE_BEAN.setSource("order");
                KEY.set(fields[1]);
            } else {
                // 产品表
                TABLE_BEAN.setPid(fields[0]);
                TABLE_BEAN.setPname(fields[1]);
                TABLE_BEAN.setSource("pd");
                TABLE_BEAN.setAmount(0);
                TABLE_BEAN.setOrderId("");
                KEY.set(fields[0]);
            }
            context.write(KEY, TABLE_BEAN);
        }
    }

    private static class TableReducer extends Reducer<Text, TableBean, TableBean, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<TableBean> values, Context context) throws IOException, InterruptedException {
            // 存储订单的集合
            List<TableBean> orderBeans = new ArrayList<>();
            String pname = "";
            for (TableBean bean : values) {
                System.out.println("key: " + key + "\t" + bean + ", source: " + bean.getSource());
                if ("order".equals(bean.getSource())) {
                    // 订单表
                    // 拷贝传递过来的每条订单数据到集合中
                    TableBean orderBean = new TableBean(bean.getOrderId(), bean.getPid(), bean.getAmount(), bean.getPname(), bean.getSource());
                    orderBeans.add(orderBean);
                } else {
                    // 产品表
                    // 拷贝传递过来的产品表到内存中
                    if (pname.isEmpty()) {
                        pname = bean.getPname();
                    }
                }
            }
            // 表的拼接
            for (TableBean bean : orderBeans) {
                bean.setPname(pname);
                // 输出
                context.write(bean, NullWritable.get());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Path input = new Path("/opt/join/input");
        Path output = new Path("/opt/join/output");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(ReduceJoinMR.class);
        job.setMapperClass(TableMapper.class);
        job.setReducerClass(TableReducer.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TableBean.class);
        job.setOutputKeyClass(TableBean.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        job.waitForCompletion(true);
    }

}
