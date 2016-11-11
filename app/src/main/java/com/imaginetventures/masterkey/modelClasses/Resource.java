package com.imaginetventures.masterkey.modelClasses;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by IM028 on 6/8/16.
 */
public class Resource {
    private String id;
    private String name;
    private Calendar lastService;
    private int periodicity;

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

    public Calendar getLastService() {
        return lastService;
    }

    public void setLastService(Calendar lastService) {
        this.lastService = lastService;
    }

    public int getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(int periodicity) {
        this.periodicity = periodicity;
    }
}
