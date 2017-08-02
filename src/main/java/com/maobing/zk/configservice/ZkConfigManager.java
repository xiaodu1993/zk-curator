package com.maobing.zk.configservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.alibaba.fastjson.JSON;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public class ZkConfigManager implements ConfigManager{

    private Map<String, String> cache;
    private String serviceConfig;
    private String root = "/root";
    private CuratorFramework client;
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public ZkConfigManager(String serviceConfig, String root) {
        this.serviceConfig = serviceConfig;
        this.root = root;
        init();
    }

    public void init() {
        client = CuratorFrameworkFactory.newClient(serviceConfig,
            new ExponentialBackoffRetry(10000, 3));
        client.start();
        initData();
        TreeCache treeCache = new TreeCache(client, root);
        treeCache.getListenable().addListener(new configListener(this));
        try {
            treeCache.start();
        } catch (Exception e) {
            System.out.println("start listener exception");
        }
    }

    private void initData() {
        Lock lk = lock.writeLock();
        waiting(lk);
        cache = new ConcurrentHashMap<String, String>();
        try {
            List<String> childs = client.getChildren().forPath(root);
            String key = null;
            String val = null;
            for (String str : childs) {
                key = str;
                val = new String(client.getData().forPath(concatKey(str)));
                cache.put(key, val);
            }
            System.out.println("init data success. data: " + JSON.toJSONString(cache));
        } catch (Exception e) {
            System.out.println("init data exception");
            return;
        } finally {
            lk.unlock();
        }
    }

    public void waiting(Lock lk) {
        boolean isFinish = false;
        try {
            while (!isFinish) {
                if (lk.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            System.out.println("get write lock exception");
            return;
        }
    }

    public void reload() {
        initData();
    }

    private String concatKey(String name) {
        return root.concat("/").concat(name);
    }

    public void add(String key, String val) {
        try {
            client.create().forPath(concatKey(key), val.getBytes());
        } catch (Exception e) {
            System.out.print("add key exception, key: " + key + " val: " + val);
        }
    }

    public void delete(String key) {
        try {
            client.delete().forPath(concatKey(key));
        } catch (Exception e) {
            System.out.print("add key exception, key: " + key);
        }
    }

    public void update(String key, String val) {
        try {
            client.setData().forPath(concatKey(key), val.getBytes());
        } catch (Exception e) {
            System.out.print("add key exception, key: " + key + " val: " + val);
        }
    }

    public String get(String key) {
        Lock lk = lock.readLock();
        waiting(lk);
        String val = cache.get(key);
        lk.unlock();
        return val;
    }

    public List<String> getVals(List<String> keys) {
        Lock lk = lock.readLock();
        waiting(lk);
        List<String> ret = new ArrayList<String>();
        for (String k : keys) {
            ret.add(cache.get(k));
        }
        lk.unlock();
        return ret;
    }

    public Map<String, String> getAll() {
        return cache;
    }

}
