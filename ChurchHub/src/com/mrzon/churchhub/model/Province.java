package com.mrzon.churchhub.model;

import com.parse.ParseObject;

import org.droidpersistence.annotation.Column;

import java.io.Serializable;

public class Province implements Serializable {
    @Column(name = "latest")
    public static final String latestUpdate = "PROVINCE_LATEST_UPDATE";
    /**
     *
     */
    @Column(name = "uid")
    private static final long serialVersionUID = 7965725728692835240L;
    private Country country;
    private String name;
    private String id;
    private ParseObject po;

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParseObject getPObject() {
        // TODO Auto-generated method stub
        return po;
    }
}
