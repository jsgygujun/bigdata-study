package com.jsgygujun.code.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * 导包时，导入 org.apache.hadoop.mapreduce包下的类(2.0的新api)
 * 1. 自定义的类必须复合MR的Mapper的规范
 * 2. 在MR中，只能处理key-value格式的数据
 *    KEYIN, VALUEIN：mapper输入的k-v类型。 由当前Job的InputFormat的RecordReader决定，封装输入的key-value由RR自动进行。
 *    KEYOUT, VALUEOUT： mapper输出的k-v类型: 自定义。
 * 3. InputFormat的作用：
 *     a. 验证输入目录中文件格式，是否符合当前Job的要求
 *     b. 生成切片，每个切片都会交给一个MapTask处理
 *     c. 提供RecordReader，由RR从切片中读取记录，交给Mapper进行处理
 *    方法：
 *        List<InputSplit> getSplits： 切片
 *        RecordReader<K,V> createRecordReader： 创建RR
 *    默认hadoop使用的是TextInputFormat，TextInputFormat使用LineRecordReader.
 * 4. 在Hadoop中，如果有Reduce阶段。通常key-value都需要实现序列化协议。
 */
public class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final Text KEY = new Text();
    private final IntWritable ONE = new IntWritable(1);

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        System.out.println("keyIn: " + key + ", valueIn: " + value);
        String[] words = value.toString().split("\t", -1);
        for (String word : words) {
            KEY.set(word);
            context.write(KEY, ONE);
        }
    }

}
