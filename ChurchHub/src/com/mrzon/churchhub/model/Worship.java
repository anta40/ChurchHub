package com.mrzon.churchhub.model;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;

public class Worship implements Serializable {
    public static final String latestUpdate = "WORSHIP_LATEST_UPDATE";
    /**
     *
     */
    private static final long serialVersionUID = 1193605312982554229L;
    private Church church;
    private String name;
    private double start;
    private double end;
    private String id;
    private int day;

    public ParseObject getPObject() {
        ParseObject pob = null;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Worship");
        try {
            pob = query.get(this.id);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return pob;
    }

    public Church getChurch() {
        if (church == null) {
            church = Helper.createChurch(getPObject().getParseObject("church"), null);
        }
        return church;
    }

    public void setChurch(Church church) {
        this.church = church;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void submit() throws ParseException {
        ParseObject po = new ParseObject("Worship");
        if (id != null) {
            po.setObjectId(id);
        }
        po.put("name", name);
        po.put("day", day);
        po.put("start", start);
        po.put("end", end);
        if (church != null) {
            po.put("church", church.getPObject());
        }
        po.save();
        this.setId(po.getObjectId());
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day2) {
        // TODO Auto-generated method stub
        this.day = day2;
    }

    public String getDayString() {
//		String [] days = Resources.getSystem().getStringArray(R.array.days);
        String[] days = {"Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday",
                "Sunday"};
        return days[day];
    }

    public String getStartString() {
        String hour = (int) getStart() + "";
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String minute = (int) (getStart() - (int) getStart()) * 60 + "";
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }

    public String getEndString() {
        String hour = (int) getEnd() + "";
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        String minute = (int) (getStart() - (int) getStart()) * 60 + "";
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        return hour + ":" + minute;
    }
}
