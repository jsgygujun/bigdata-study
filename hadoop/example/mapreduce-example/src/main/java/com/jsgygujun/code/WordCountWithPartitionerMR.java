package com.jsgygujun.code;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class WordCountWithPartitionerMR {

    public static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        Text text = new Text();
        IntWritable one = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 1. 获取一行
            String line = value.toString();
            // 2. 切割
            String[] words = line.split(" ");
            // 3. 输出
            for (String word : words) {
                text.set(word);
                context.write(text, one);
            }
        }
    }

    public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            // 1. 累加求和
            int sum = 0;
            for (IntWritable count : values) {
                sum += count.get();
            }
            // 2. 输出
            context.write(key, new IntWritable(sum));
        }
    }

    public static class WordCountPartitioner extends Partitioner<Text, IntWritable> {
        @Override
        public int getPartition(Text key, IntWritable value, int numPartitions) {
            return key.toString().charAt(0) % numPartitions; // 根据单词首字母ASCII奇偶来分区
        }
    }

    /**
     * hadoop jar mapreduce-example-0.9-SNAPSHOT.jar com.jsgygujun.code.WordCountMR /data/mapreduce/word-count/input /data/mapreduce/word-count/output
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // 1. 获取配置信息
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        // 2. 设置jar加载路径
        job.setJarByClass(WordCountWithPartitionerMR.class);

        // 3. 设置Map和Reduce类
        job.setMapperClass(WordCountMapper.class);
        job.setReducerClass(WordCountReducer.class);

        // 4. 设置Map输出
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 5. 设置Partitioner
        job.setPartitionerClass(WordCountPartitioner.class);

        // 6. 设置Reduce输出
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setNumReduceTasks(2);

        // 7. 设置输入和输出路径
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // 8. 提交
        boolean result = job.waitForCompletion(true);
        System.exit(result ? 0 : 1);
    }

}
