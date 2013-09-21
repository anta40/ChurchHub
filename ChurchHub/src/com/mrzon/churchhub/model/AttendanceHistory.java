package com.mrzon.churchhub.model;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;

public class AttendanceHistory implements Serializable {
	public static final String filename = "ATTENDANCE_HISTORY";
	/**
	 * 
	 */
	private static final long serialVersionUID = 7754399732672114333L;
	private List<Attendance> attendances;
	
	public static void addAttendanceToHistory(Attendance att, Activity a) {
		List<Attendance> attendance = getAttendancesObject(a);
		attendance.add(att);
		try {
			Helper.writeSerializableObject(filename, attendance, a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Map<Church,List<Date>> getChurchesHistory(List<Attendance> a){
		Map<Church,List<Date>> churces = new HashMap<Church,List<Date>>();
		for(int i = 0; i < a.size(); i++) {
			addToMaps(churces, a.get(i).getChurch(), a.get(i).getDate());
		}
		return churces;
	}
	
	private static void addToMaps(Map<Church,List<Date>> maps, Church ch, Date date) {
		if(maps.containsKey(ch)) {
			List<Date> l = maps.get(ch);
			l.add(date);
		} else {
			List<Date> l = new ArrayList<Date>();
			l.add(date);
			maps.put(ch, l);
		}
	}
	
	public static List<Attendance> getAttendancesObject(Activity a) {
		List<Attendance> churches = null;
		try {
			churches = (List<Attendance>)Helper.readSerializableObject(filename, a);
		} catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			churches = new ArrayList<Attendance>();
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			churches = new ArrayList<Attendance>();
			e.printStackTrace();
		}
		return churches;
	}
}	
