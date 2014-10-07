package com.mrzon.churchhub.model;

import java.io.Serializable;
import java.util.Date;

public class Attendance implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4391226524512440880L;
    private WorshipWeek worship;
    private Date mDate;
    private String mMessage;
    private User mUser;
    private Church mChurch;

    public WorshipWeek getWorship() {
        return worship;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date createdAt) {
        mDate = createdAt;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public Church getChurch() {
        return mChurch;
    }

    public void setChurch(Church church) {
        this.mChurch = church;
    }

    public void setWorshipWeek(WorshipWeek w) {
        worship = w;
    }

    public String getMessage() {
        // TODO Auto-generated method stub
        return mMessage;
    }

    public void setMessage(String m) {
        mMessage = m;
    }
}
