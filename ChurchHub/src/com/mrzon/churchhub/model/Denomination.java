package com.mrzon.churchhub.model;

import com.parse.ParseException;
import com.parse.ParseObject;

import org.droidpersistence.annotation.*;

import java.io.Serializable;
import java.util.Date;

@Table(name = "DENOMINATION")
public class Denomination implements Serializable {
    public static final String latestUpdate = "DENOMINATION_LATEST_UPDATE";
    /**
     *
     */
    private static final long serialVersionUID = -2270340353866516345L;
    private Long code;

    private String id;
    private String name;
    private String abbreviation;
    private Date esDate;
    private String founder;
    private String wikilink;
    private String website;

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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Date getEsDate() {
        return esDate;
    }

    public void setEsDate(Date esDate) {
        this.esDate = esDate;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getWikilink() {
        return wikilink;
    }

    public void setWikilink(String wikilink) {
        this.wikilink = wikilink;
    }

    public void setWebsite(String we) {
        // TODO Auto-generated method stub
        website = we;
    }

    public void submit() throws ParseException {
        ParseObject po = new ParseObject("Denomination");
        po.put("name", name);
        po.put("abbreviation", abbreviation);
        po.put("wiki", wikilink);
        po.put("website", website);
        po.save();
        this.setId(po.getObjectId());
    }

    public ParseObject getPObject() {
        // TODO Auto-generated method stub
        ParseObject po = ParseObject.createWithoutData("Denomination", this.getId());
        return po;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

}
