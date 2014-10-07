package com.mrzon.churchhub.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.Serializable;

public class User implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2236515851942087185L;
    private double lon;
    private String username;
    private double lat;
    private Region region;
    private Church church;
    private String fName;
    private String lName;
    private String bod;
    private String id;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Church getChurch() {
        return church;
    }

    public void setChurch(Church church) {
        this.church = church;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getBod() {
        return bod;
    }

    public void setBod(String bod) {
        this.bod = bod;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        // TODO Auto-generated method stub
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public void addSavedChurch(Church ch) {
        ParseRelation<ParseObject> q = ParseUser.getCurrentUser().getRelation("savedChurch");
        q.add(ch.getPObject());
    }

    public void removeSavedChurch(Church ch) {
        ParseRelation<ParseObject> q = ParseUser.getCurrentUser().getRelation("savedChurch");
        q.remove(ch.getPObject());
    }

    public boolean savedChurchExist(Church ch) {
        ParseRelation<ParseObject> pr = ParseUser.getCurrentUser().getRelation("savedChurch");
        ParseQuery<ParseObject> pq = pr.getQuery();

        try {
            ParseObject po = pq.get(ch.getId());
            if (po == null) {
                return false;
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
