package com.jsgygujun.code.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class HDFSUtil {

    private static final String HDFS_URI = "hdfs://hadoop-11:9000";
    private static final String HDFS_USR = "hadoop";

    // HDFS 文件系统
    private FileSystem fs;

    @Before
    public void setUp() throws Exception {
        // 创建配置信息对象
        Configuration conf = new Configuration();
        // 测试可设置副本数为1
        conf.set("dfs.replication", "1");
        // 获取文件系统对象
        fs = FileSystem.get(new URI(HDFS_URI), conf, HDFS_USR);
    }

    @After
    public void tearDown() throws Exception {
        if (fs != null) fs.close();
    }

    /**
     * 创建目录，支持递归创建目录
     * @throws Exception
     */
    @Test
    public void mkdir() throws Exception {
        fs.mkdirs(new Path("/data/dir1"));
    }

    /**
     * 创建目录并指定目录权限
     * @throws Exception
     */
    @Test
    public void mkdir_with_permission() throws Exception {
        fs.mkdirs(new Path("/data/dir2"), new FsPermission(FsAction.READ_WRITE, FsAction.READ, FsAction.READ));
    }

    /**
     * 创建文件并写入内容, 如果文件存在则覆盖
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        try (FSDataOutputStream fos = fs.create(new Path("/data/dir1/hello.txt"), true, 4096)) {
            fos.write("HELLO HADOOP".getBytes());
        }
    }

    /**
     * 判断文件是否存在
     * @throws Exception
     */
    @Test
    public void exists() throws Exception {
        boolean exists = fs.exists(new Path("/data/dir1/hello.txt"));
        System.out.println(exists);
    }

    /**
     * 查看文件内容，注意多个资源相互嵌套的try-with-resources用法
     * @throws Exception
     */
    @Test
    public void read() throws Exception {
        try (FSDataInputStream fis = fs.open(new Path("/data/dir1/hello.txt"));
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {
            System.out.println(br.readLine());
        }
    }

    /**
     * 文件重命名
     * @throws Exception
     */
    @Test
    public void rename() throws Exception {
        Path oldPath = new Path("/data/dir1/hello.txt");
        Path newPath = new Path("/data/dir1/hello2.txt");
        boolean result = fs.rename(oldPath, newPath);
        System.out.println(result);
    }

    /**
     * 删除文件或者目录，当删除目录时，可指定是否递归删除
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        boolean result = fs.delete(new Path("/data/dir1/hello2.txt"), true);
        System.out.println(result);
    }

    /**
     * 查看指定目录下所有文件的信息
     * 中包含了文件的基本信息，比如文件路径，是否是文件夹，修改时间，访问时间，所有者，所属组，文件权限，是否是符号链接等
     * @throws Exception
     */
    @Test
    public void list_files() throws Exception {
        FileStatus[] fileStatuses = fs.listStatus(new Path("/data"));
        for (FileStatus fileStatus : fileStatuses) {
            System.out.println(fileStatus);
        }
    }

    @Test
    public void init_HDFS() throws Exception {
        // 创建配置信息对象
        Configuration conf = new Configuration();
        // 获取文件系统
        FileSystem fs = FileSystem.get(conf);
        // 打印文件系统
        System.out.println(fs);
    }

    /**
     * 上传文件
     * @throws Exception
     */
    @Test
    public void copy_from_local() throws Exception {
        Path localPath = new Path("/tmp/hello.txt");
        Path hadoopPath = new Path("/data/dir2/hello.txt");
        fs.copyFromLocalFile(localPath, hadoopPath);
    }

    /**
     * 下载文件
     * @throws Exception
     */
    @Test
    public void copy_to_local() throws Exception {
        Path hadoopPath = new Path("/data/hello.txt");
        Path localPath = new Path("/tmp/hello.txt");
        fs.copyToLocalFile(hadoopPath, localPath);
    }
}
