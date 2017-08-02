package com.maobing.zk.configservice;

import java.util.List;
import java.util.Map;

/**
 * @author maobing.dmb
 * @date 2017/08/02
 */
public interface ConfigManager {

    void init();

    void reload();

    void add(String key, String val);

    void delete(String key);

    void update(String key, String val);

    String get(String key);

    List<String> getVals(List<String> keys);

    Map<String, String> getAll();

}
