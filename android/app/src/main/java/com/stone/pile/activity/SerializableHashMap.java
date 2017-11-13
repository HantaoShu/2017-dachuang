package com.stone.pile.activity;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by admin on 2017/10/28.
 */
class SerializableHashMap implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<String,jsonbean> map;

    public HashMap<String, jsonbean> getMap() {
        return map;
    }

    public void setMap(HashMap<String, jsonbean> map) {
        this.map = map;
    }
}
