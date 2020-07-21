package com.jsgygujun.code.zkclient;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author jsgygujun@gmail.com
 * @since 2020/7/21 5:15 下午
 */
public class ZKClientUtil {
    private static final String CONNECT_STRING = "hadoop-11:2181,hadoop-12:2181,hadoop-13:2181";
    private static final int SESSION_TIMEOUT = 2000;
    private ZooKeeper zkClient;

    /**
     * 初始化 ZK 客户端
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        zkClient = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                // 收到事件通知后的回调函数（用户的业务逻辑）
                System.out.println(watchedEvent.getType() + "--" + watchedEvent.getPath());
                // 再次启动监听
                try {
                    zkClient.getChildren("/", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 创建子节点
     * @throws Exception
     */
    @Test
    public void create() throws Exception {
        String znode = zkClient.create("/root", "rootValue".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(znode);
    }

    /**
     * 获取子节点并监听节点变化
     * @throws Exception
     */
    @Test
    public void getChildrenAndWatch() throws Exception {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        // 延迟阻塞
        Thread.sleep(1000*15);
    }

    /**
     * 判断 Znode 是否存在
     * @throws Exception
     */
    @Test
    public void exist() throws Exception {
        Stat stat = zkClient.exists("/root", false);
        System.out.println(stat == null ? "not exist" : "exist");
    }
}
