package com.mrzon.churchhub.model;

import com.parse.ParseException;
import com.parse.ParseObject;

import org.droidpersistence.annotation.Column;

import java.io.Serializable;

public class Region implements Serializable {

    @Column(name = "latest")
    public static final String latestUpdate = "REGION_LATEST_UPDATE";
    /**
     *
     */
    @Column(name = "uid")
    private static final long serialVersionUID = 2584799363827999595L;
    private Province province;
    private String id;
    private String name;
    private ParseObject po;

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

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

    public ParseObject getPObject() {
        ParseObject po = ParseObject.createWithoutData("Region", this.getId());
        return po;
    }

    public void setPObject(ParseObject po) {
        this.po = po;
    }

    public void submit() throws ParseException {
        po = new ParseObject("Region");
        po.put("name", name);
        po.put("province", province.getPObject());
    }
}
