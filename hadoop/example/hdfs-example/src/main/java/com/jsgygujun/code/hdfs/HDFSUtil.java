package com.jsgygujun.code.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;

import java.net.URI;

public class HDFSUtil {

    @Test
    public void init_HDFS() throws Exception {
        // 创建配置信息对象
        Configuration conf = new Configuration();
        // 获取文件系统
        FileSystem fs = FileSystem.get(conf);
        // 打印文件系统
        System.out.println(fs);
    }

    @Test
    public void copy_from_local() throws Exception {
        // 获取文件系统
        Configuration conf = new Configuration();
        conf.set("dfs.replication", "2");
        FileSystem fs = FileSystem.get(new URI("hdfs://hadoop-11:9000"), conf, "bigdata");
        // 上传文件
        fs.copyFromLocalFile(new Path("/Users/jun/GitLab/bigdata-study/data/hello.txt"), new Path("/data/hello.txt"));
        // 关闭资源
        fs.close();
        System.out.println("done~");
    }

}
