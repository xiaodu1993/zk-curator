package com.maobing.zk.naming;

import java.util.UUID;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public class Main {

    public static void main(String[] args){
        NamingService namingService = new NamingService("localhost:2181");
        String name = null;
        for (int i = 0; i < 5; i++) {
            try {
                name = namingService.naming(UUID.randomUUID().toString());
                System.out.println(name);
            } catch (Exception e) {
                System.out.println(name + "is exists");
            }
        }
    }

}
