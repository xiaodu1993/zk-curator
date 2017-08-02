package com.maobing.zk.namingservice;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public class NamingService {

    private String namingRoot = "/namingservice";
    private CuratorFramework curatorFramework;

    public NamingService(String serviceConfig) {
        curatorFramework = CuratorFrameworkFactory.newClient(serviceConfig,
            new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        try {
            curatorFramework.create().forPath(namingRoot, namingRoot.getBytes());
        } catch (Exception e) {}
    }

    public String naming(String name) throws Exception {
        String concatKey = namingRoot.concat("/").concat(name);
        try {
            curatorFramework.create().forPath(concatKey);
            return name;
        }catch (Exception e){
            throw new Exception("name already is exists");
        }
    }

}
