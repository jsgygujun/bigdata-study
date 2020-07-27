package com.jsgygujun.code.wordcount;

import com.jsgygujun.code.WordCountMR;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 * 运行命令：
 * hadoop jar mapreduce-example-0.9-SNAPSHOT.jar com.jsgygujun.code.wordcount.WordCountDriver /data/wordcount/input /data/wordcount/output
 */
public class WordCountDriver {

    public static void main(String[] args) throws Exception {
        // 1. 获取配置信息
        Configuration conf = new Configuration();

        // 2. 创建Job
        Job job = Job.getInstance(conf);
        job.setJobName("word-count");

        // 3. 设置jar加载路径
        job.setJarByClass(WordCountMR.class);

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
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 8. 提交
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }

}
