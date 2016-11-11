package com.imaginetventures.masterkey.modelClasses;

import java.io.Serializable;

/**
 * Created by Sanjay on 3/29/16.
 */
public class EmployeeData implements Serializable {
    private String id;
    private String name;
    private String flag;

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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

}
