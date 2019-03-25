package com.wj.zk;

import com.wj.utils.NetUtil;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperClient extends AbstractZookeeper implements Watcher {
    private static Logger log = LoggerFactory.getLogger(ZookeeperClient.class);

    private static final String ROOT_PATH = "/idCenter";
    private static final String EPHEMERAL_PATH = "/idCenter";

    private String currentNode;

    public ZookeeperClient(String host) {
        connect(host, this);
    }

    public void init() {
        if (!exists(ROOT_PATH, null)) {
            create(ROOT_PATH, "".getBytes(), CreateMode.PERSISTENT);
        }
        String ip = NetUtil.getLocalHostLANAddress();
        if (!exists(ROOT_PATH + "/" + ip, null)) {
            int value = Math.abs(ip.hashCode())%31;
            currentNode = create(ROOT_PATH + "/" + ip, String.valueOf(value).getBytes(), CreateMode.PERSISTENT);
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

    public int getdataCenterId() {
        String s = currentNode.substring(EPHEMERAL_PATH.length());
        return Integer.valueOf(s);
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
