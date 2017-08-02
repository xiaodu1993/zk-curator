package com.maobing.zk.configservice;

import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public class Main {

    public static void main(String[] args) {
        ZkConfigManager manager = new ZkConfigManager("localhost:2181", "/config");
        manager.add("test10", "6");
        int i = 1;
        while (i < 300) {
            Map map = manager.getAll();
            System.out.println("map data: " + JSON.toJSONString(map));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

}
