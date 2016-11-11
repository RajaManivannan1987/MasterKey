package com.imaginetventures.masterkey.modelClasses;

import java.io.Serializable;

/**
 * Created by IM028 on 6/2/16.
 */
public class Account implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
