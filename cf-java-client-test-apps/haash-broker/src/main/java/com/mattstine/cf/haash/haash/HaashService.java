package com.mattstine.cf.haash.haash;


import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HaashService {

    private Map<String, Map<Object, Object>> hashMaps = new HashMap<>();

    public void create(String id) {
        hashMaps.put(id, new HashMap<Object, Object>());
    }

    public void delete(String id) {
        hashMaps.remove(id);
    }

    public void put(String id, Object key, Object value) {
        Map<Object, Object> mapInstance = hashMaps.get(id);
        mapInstance.put(key, value);
    }

    public Object get(String id, Object key) {
        Map<Object, Object> mapInstance = hashMaps.get(id);
        return mapInstance.get(key);
    }

    public void delete(String id, Object key) {
        Map<Object, Object> mapInstance = hashMaps.get(id);
        mapInstance.remove(key);
    }

}
