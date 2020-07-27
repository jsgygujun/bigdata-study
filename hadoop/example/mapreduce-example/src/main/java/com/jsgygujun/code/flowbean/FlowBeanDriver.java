package com.jsgygujun.code.flowbean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * 统计手机号(String)的上行(long)，下行(long)，总流量(long)
 * 手机号为key,Bean{上行(long)，下行(long)，总流量(long)}为value
 */
public class FlowBeanDriver {

    public static void main(String[] args) throws Exception {
        // 1. 获取配置信息
        Configuration conf = new Configuration();

        // 2. 创建Job
        Job job = Job.getInstance(conf);
        job.setJobName("flow-bean");

        // 3. 设置jar加载路径
        job.setJarByClass(FlowBeanDriver.class);

        // 4. 设置Map和Reduce类
        job.setMapperClass(FlowBeanMapper.class);
        job.setReducerClass(FlowBeanReducer.class);

        // 5. 设置Map输出
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        // 6. 设置Reduce输出
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 7. 设置输入和输出路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 8. 提交
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
