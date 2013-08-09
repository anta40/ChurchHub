package com.mrzon.churchhub.model;

import java.io.Serializable;

import org.droidpersistence.annotation.Column;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;

public class Country implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8567934101631752185L;
	public static final String latestUpdate = "COUNTRY_LATEST_UPDATE";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIso() {
		return iso;
	}
	public void setIso(String iso) {
		this.iso = iso;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String name;
	private String iso;
	private String id;
	private String nickname;
	
	public void submit() throws ParseException{
		ParseObject po = new ParseObject("Country");
		po.put("name", name);
		po.put("iso", iso);
		if(nickname!=null) {
			po.put("nickname", nickname);
		}
		po.save();
		this.setId(po.getObjectId());
	}
	
	public ParseObject getPObject() {
		ParseObject po = ParseObject.createWithoutData("Country", this.getId());
		return po;
	}
	
	public void addProvince(Province p) {
		ParseRelation<ParseObject> rel = this.getPObject().getRelation("provinces");
		rel.add(p.getPObject());
	}
}
