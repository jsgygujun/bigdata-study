package com.jsgygujun.code.join.mapjoin;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Map Join
 * 使用场景： 适用于一张表十分小、一张表很大的场景
 *
 * @author jsgygujun@gmail.com
 * @since 2020/7/29 2:06 下午
 */
public class MapJoinMR {

    private static class DistributedCacheMapper extends Mapper<LongWritable, Text, Text, NullWritable> {
        Map<String, String> pdMap = new HashMap<>();

        private Text KEY = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            // 获取缓存的文件
            try (BufferedReader br = Files.newBufferedReader(Paths.get("pd.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] fields = line.split("\t");
                    pdMap.put(fields[0], fields[1]);
                }
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] fields = line.split("\t", -1);
            String pid = fields[1];
            String pname = pdMap.get(pid);
            KEY.set(line + "\t" + pname);
            context.write(KEY, NullWritable.get());
        }
    }

    public static void main(String[] args) throws Exception {
        Path input = new Path("/opt/mapjoin/input");
        Path output = new Path("/opt/mapjoin/output");
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);
        job.setJarByClass(MapJoinMR.class);
        job.setMapperClass(DistributedCacheMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.setInputPaths(job, input);
        FileOutputFormat.setOutputPath(job, output);
        // 加载缓存数据
        job.addCacheFile(new URI("file:///opt/mapjoin/cache/pd.txt")); // 如果加载HDFS 协议为 hdfs://
        job.setNumReduceTasks(0);
        job.waitForCompletion(true);
    }
}
