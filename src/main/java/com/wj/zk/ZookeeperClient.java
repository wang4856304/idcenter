package com.wj.zk;

import com.wj.service.RedisService;
import com.wj.utils.NetUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class ZookeeperClient extends AbstractZookeeper implements Watcher {
    private static Logger log = LoggerFactory.getLogger(ZookeeperClient.class);

    private static final String ROOT_PATH = "/idCenter";
    private static final String WORK_ID_PATH = "/workId";
    private static final String EPHEMERAL_PATH = "/idCenter";

    private static final String WORK_ID_KEY = "workId";

    @Value("${zookeeper.host}")
    private String host;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        connect(host, this);
        if (!exists(ROOT_PATH, null)) {
            create(ROOT_PATH, "".getBytes(), CreateMode.PERSISTENT);
        }
    }

    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            countDownLatch.countDown();
        }
        if (watchedEvent.getState() == Event.KeeperState.Disconnected) {
            log.error("session is disconnected, reconnect now");
            init();
        }
    }

    public int getWorkerId() {
        String ip = NetUtil.getLocalHostLANAddress();
        String s = getData(ROOT_PATH + "/" + ip);
        return Integer.valueOf(s);
    }

    public int getWorkerId(String ipAddr) {
        String key = WORK_ID_KEY + ":" + ipAddr;
        String workIdPath = ROOT_PATH + "/" + "workId/" + ipAddr;
        try {
            if (!exists(ROOT_PATH  + "/workId", null)) {
                create(ROOT_PATH  + "/workId", "".getBytes(), CreateMode.PERSISTENT);
            }
            if (!exists(workIdPath, null)) {
                long workId = redisService.incr(WORK_ID_KEY, 1);
                if (workId > 31) {
                    throw new RuntimeException("work id greater than 31");
                }
                create(workIdPath, String.valueOf(workId).getBytes(), CreateMode.PERSISTENT);
                redisService.set(key, workId);
            }
        }
        catch (Exception e) {
            redisService.decr(WORK_ID_KEY, 1);
            throw new RuntimeException(e);
        }

        try {
            return redisService.get(key);
        }
        catch (Exception e) {
            String s = getData(workIdPath);
            return Integer.valueOf(s);
        }
    }

    public int getdataCenterId() {

        return 0;
    }

    /*public void taskExecute(Task... tasks) {
        List<String> nodes = getChild(ROOT_PATH);
        nodes.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                o1 = o1.substring(EPHEMERAL_PATH.length());
                o2 = o2.substring(EPHEMERAL_PATH.length());
                int a = Integer.valueOf(o1);
                int b = Integer.valueOf(o2);
                return a < b ? -1:1;
            }
        });
        String curNode = currentNode.substring(ROOT_PATH.length() + 1);
        if (curNode.equals(nodes.get(0))) {
            log.info(NetUtil.getLocalHostLANAddress() + ":" + currentNode + " is master");
            for (Task task: tasks) {
                task.execute();
            }
        }

        for (int k = 0; k < nodes.size(); k++) {
            String node = nodes.get(k);
            if (!node.equals(nodes.get(0))) {
                exists(ROOT_PATH + "/" + nodes.get(k-1), this);
            }
        }
    }*/

   /* public static void main(String args[]) {




        ZookeeperClient zookeeperClient = new ZookeeperClient("192.168.88.128", 2181);
        //ZookeeperClient zookeeperClient1 = new ZookeeperClient("192.168.88.128", 2181);
        zookeeperClient.init();
        //zookeeperClient1.init();
        //List<String> childList = zookeeperClient.getChild(ROOT_PATH );

        Task task = new ScheduledTasks();

        zookeeperClient.taskExecute(task);
        //zookeeperClient1.getMasterNode(zookeeperClient.getChild(ROOT_PATH ));

        while (true) {

        }
    }*/
}
