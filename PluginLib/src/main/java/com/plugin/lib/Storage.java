package com.plugin.lib;

import com.plugin.lib.entity.PluginEntity;

import java.util.HashMap;
import java.util.Map;

public class Storage {
    public int id;
    public final Map<Integer, PluginEntity> entities = new HashMap<>();

    public PluginEntity getPluginEntity(int id) {
        return entities.get(id);
    }

    public Map<Integer, PluginEntity> getEntities() {
        return entities;
    }

    public void add(int id, PluginEntity entity) {
        entities.put(id, entity);
    }

    public void remove(int id) {
        entities.remove(id);
    }

    public int getEntitiesSize() {
        return entities.size();
    }

    public static Storage getInstance() {
        return SmartyLoader.instance;
    }

    private static class SmartyLoader {
        private static final Storage instance = new Storage();
    }
}
