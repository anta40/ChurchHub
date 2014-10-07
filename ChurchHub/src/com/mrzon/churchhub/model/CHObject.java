package com.mrzon.churchhub.model;

import com.parse.ParseException;

import java.io.Serializable;

abstract class CHObject implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public abstract void getPObject();

    public abstract void create();

    public abstract void save() throws ParseException;

    public abstract void saveIfNotExist();

    public abstract void delete();
}
