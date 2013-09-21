package com.mrzon.churchhub.model;

import java.io.Serializable;

import com.parse.ParseException;

abstract class CHObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract void getPObject();
	public abstract void create();
	public abstract void save() throws ParseException ;
	public abstract void saveIfNotExist();	
	public abstract void delete();
}
