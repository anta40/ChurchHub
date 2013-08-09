package com.mrzon.churchhub.model;

import java.io.Serializable;
import java.util.Date;

public class WorshipWeek implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6971541929403658363L;
	public static final String latestUpdate = "WORSHIPWEEK_LATEST_UPDATE";
	public Worship getWorship() {
		return worship;
	}
	public void setWorship(Worship worship) {
		this.worship = worship;
	}
	public String getSpeaker() {
		return speaker;
	}
	public void setSpeaker(String speaker) {
		this.speaker = speaker;
	}
	public int getWorshipDate() {
		return worshipWeek;
	}
	public void setWorshipDate(int worshipWeek) {
		this.worshipWeek = worshipWeek;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private Worship worship;
	private String speaker;
	private int worshipWeek;
	private String id;
}
