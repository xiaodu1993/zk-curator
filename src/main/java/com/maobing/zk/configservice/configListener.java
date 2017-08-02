package com.maobing.zk.configservice;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public class configListener implements TreeCacheListener {

    private ZkConfigManager configManager;

    public configListener(ZkConfigManager configManager) {
        this.configManager = configManager;
    }

    public void childEvent(CuratorFramework curatorFramework, TreeCacheEvent event) throws Exception {
        switch (event.getType()) {
            case NODE_ADDED:
            case NODE_REMOVED:
            case NODE_UPDATED:
                configManager.reload();
        }
    }

}
