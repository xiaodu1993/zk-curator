package com.maobing.zk.clusterservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode;
import org.apache.curator.framework.recipes.nodes.PersistentEphemeralNode.Mode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

/**
 * @author maobing.dmb
 * @date 2017/08/03
 */
public class ClustorClient {

    private String serviceConfig;
    private String clustorPath = "/clustor";
    private CuratorFramework client;
    private String hostAddr;
    private List<String> machineList = new ArrayList<String>();

    public ClustorClient(String serviceConfig) {
        this.serviceConfig = serviceConfig;
        init();
    }

    public ClustorClient(String serviceConfig, String clustorPath) {
        this.serviceConfig = serviceConfig;
        this.clustorPath = clustorPath;
        init();
    }

    private void init() {
        client = CuratorFrameworkFactory.newClient(serviceConfig, new ExponentialBackoffRetry(1000, 3));
        client.start();
        if (!nodeExists(clustorPath)) {
            createNode(clustorPath);
        }
        this.hostAddr = UUID.randomUUID().toString();
        String hostPath = concatPath(hostAddr);
        PathChildrenCache cache = new PathChildrenCache(client, hostPath, true);
        cache.getListenable().addListener(new GroupListener(this));
        try {
            cache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String concatPath(String pathName) {
        return clustorPath.concat("/").concat(pathName);
    }

    private void createNode(String path) {
        try {
            new PersistentEphemeralNode(client, Mode.EPHEMERAL_SEQUENTIAL, path, path.getBytes());
        } catch (Exception e) {
            System.out.println("create path exception");
        }
    }

    private boolean nodeExists(String key) {
        try {
            Stat stat = client.checkExists().forPath(concatPath(key));
            return stat != null;
        } catch (Exception e) {
            System.out.println("key already do not exists");
            return false;
        }
    }

    public void updateGroupMap() throws Exception {
        machineList = client.getChildren().forPath(clustorPath);
    }

    private String masterSelectFirst() {
        return machineList.get(0);
    }

    private String masterSelectRandom() {
        return machineList.get(new Random(machineList.size()).nextInt());
    }

    private List<String> activeMachine() {
        return machineList;
    }

}
