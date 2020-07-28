package com.jsgygujun.code.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 本地运行直接在IDEA中启动
 * @author jsgygujun@gmail.com
 * @since 2020/7/28 4:16 下午
 */
public class WordCountLocalDriver {
    public static void main(String[] args) throws Exception {
        // 本地目录
        Path inputPath = new Path("/opt/wordcount/input");
        Path outputPath = new Path("/opt/wordcount/output");

        // 1. 获取配置信息
        Configuration conf = new Configuration();
        conf.set("", ""); // 可是修改配置

        // 确保输出目录不存在
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        // 2. 创建Job
        Job job = Job.getInstance(conf);
        job.setJobName("word-count");

        // 3. 设置jar加载路径
        job.setJarByClass(WordCountDriver.class);

        // 4. 设置Map和Reduce类
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        // 5. 设置Map输出
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 6. 设置Reduce输出
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 7. 设置输入和输出路径
        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        // 8. 提交
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }
}
