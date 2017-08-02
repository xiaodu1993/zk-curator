package com.maobing.zk.clusterservice;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;

/**
 * @author maobing.dmb
 * @date 2017/08/03
 */
public class GroupListener implements PathChildrenCacheListener {

    private ClustorClient clustorClient;

    public GroupListener(ClustorClient clustorClient) {
        this.clustorClient = clustorClient;
    }

    public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
            case CHILD_ADDED:
            case CHILD_REMOVED:
                clustorClient.updateGroupMap();
        }
    }

}
