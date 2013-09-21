package com.mrzon.churchhub.model;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import junit.framework.Assert;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.webkit.GeolocationPermissions;
import android.widget.Toast;

import com.mrzon.churchhub.BrowseChurchActivity;
import com.mrzon.churchhub.BrowseCountryActivity;
import com.mrzon.churchhub.util.Util;
import com.parse.*;
public class Helper {
	public static void attend(WorshipWeek w,String message) {
		ParseObject attend = new ParseObject("Attendance");
		attend.put("message", message);
		ParseUser user = ParseUser.getCurrentUser();
		Assert.assertNotNull(user);
		attend.put("user", user);
		ParseObject wk = ParseObject.createWithoutData("WeeklyWorship", w.getId());
		attend.put("weeklyworship", wk);
		attend.put("church", w.getWorship().getChurch().getPObject());
		attend.saveInBackground();
	}

	public static Denomination createDenomination(ParseObject ob) {
		Denomination denomination = new Denomination();
		denomination.setAbbreviation(ob.getString("abbreviation"));
		denomination.setEsDate(ob.getDate("established"));
		denomination.setFounder(ob.getString("founder"));
		denomination.setId(ob.getObjectId());
		denomination.setName(ob.getString("name"));
		denomination.setWikilink(ob.getString("wiki"));
		return denomination;
	}

	public static User createUser(ParseUser pu) {
		User u = new User();
		try {
			pu.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		u.setUserName(pu.getUsername());
		return u;
	}

	public static Attendance createAttendance(ParseObject ob, WorshipWeek w) {
		Attendance attendance = new Attendance();
		try {
			ob.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		attendance.setWorshipWeek(w);
		attendance.setDate(ob.getCreatedAt());
		attendance.setMessage(ob.getString("message"));
		attendance.setUser(createUser(ob.getParseUser("user")));
		attendance.setChurch(createChurch(ob.getParseObject("church"), null));
		return attendance;
	}
	public static List<Attendance> getAttendances(long l, boolean cache,
			Activity activity) {
		List<Attendance> attendances = new ArrayList<Attendance>();
		if (ParseUser.getCurrentUser() == null) {
			return attendances;
		}
		String FILENAME = "ATTENDANCE_HISTORY_"+UserHelper.currentUser.getId();
		boolean rewrite = false;

		try {
			attendances = (List<Attendance>)readSerializableObject(FILENAME, activity);
		} catch (FileNotFoundException e) {
			cache = false;
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			cache = false;
			e.printStackTrace();
		} catch (IOException e) {
			cache = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Attendance");
			List<ParseObject> objects;
			List<Attendance> temp = new ArrayList<Attendance>();
			if(l!=-1l) {
				query.whereGreaterThanOrEqualTo("updatedAt", new Date(l));
			} else {
				rewrite = true;
			}
			query.whereEqualTo("user", ParseUser.getCurrentUser());
			try {
				objects = query.find();
				for(int i=0; i < objects.size(); i++) {
					temp.add(createAttendance(objects.get(i),null));
				}
				if(!rewrite) {
					attendances.addAll(temp);
				} else {
					attendances = temp;
				}
				writeSerializableObject(FILENAME, attendances, activity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return attendances;
	}

	public static ArrayList<Attendance> getAttendance(Church church, boolean cache, int limit, Activity a) {
		ArrayList<Attendance> attends = new ArrayList<Attendance>();
		String filename = "ATTENDANCE_"+church.getId();
		boolean rewrite = false;
		if(cache) {
			try {
				attends = (ArrayList<Attendance>) readSerializableObject(filename, a);
			} catch (OptionalDataException e) {
				cache = false;
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				cache = true;
				e.printStackTrace();
			} catch (IOException e) {
				cache = true;
				e.printStackTrace();
			}
		}
		
		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Attendance");
			query.whereEqualTo("church", church.getPObject());
			query.setLimit(limit);
			List<ParseObject> list = null;
			try {
				try {
					list = query.find();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				ArrayList<Attendance> atts = new ArrayList<Attendance>();
				for(int i = 0; i < list.size(); i++) {
					try {
						list.get(i).fetchIfNeeded();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ParseObject pow = list.get(i).getParseObject("worshipweek");
					atts.add(createAttendance(list.get(i), null));
				}
				writeSerializableObject(filename, atts, a);
				return atts;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return attends;
	}

	/**
	 * To get the list of denomination.
	 * @param l latest update 
	 * @param cache if true will try to get the data from cache first
	 * @param activity the activity which called the method
	 * @return list of denomination
	 */
	@SuppressWarnings("unchecked")
	public static List<Denomination> getDenominations(long l, boolean cache, Activity activity) {
		List<Denomination> d =  new ArrayList<Denomination>();

		String FILENAME = "denomination_list";
		boolean rewrite = false;

		try {
			d = (List<Denomination>)readSerializableObject(FILENAME, activity);
		} catch (FileNotFoundException e) {
			cache = false;
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			cache = false;
			e.printStackTrace();
		} catch (IOException e) {
			cache = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Denomination");
			List<ParseObject> objects;
			List<Denomination> temp = new ArrayList<Denomination>();
			if(l!=-1l&&d.size()>0) {
				query.whereGreaterThanOrEqualTo("updatedAt", new Date(l));
			} else {
				rewrite = true;
			}
			try {
				objects = query.find();
				for(int i=0; i < objects.size(); i++) {
					temp.add(createDenomination(objects.get(i)));
				}
				if(!rewrite) {
					d.addAll(temp);
				} else {
					d = temp;
				}
				writeSerializableObject(FILENAME, d, activity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return d;
	}

	public static Church createChurch(ParseObject ob,Denomination dn) {
		Church church = new Church();
		try {
			ob.fetchIfNeeded();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		church.setName(ob.getString("name"));
		church.setAddress(ob.getString("address"));
		church.setPhoneNumber(ob.getString("phoneNumber"));
		church.setAddress(ob.getString("address"));
		church.setDenomination(dn);
		ParseGeoPoint geo = ob.getParseGeoPoint("location");
		church.setLat(geo.getLatitude());
		church.setId(ob.getObjectId());
		church.setLon(geo.getLongitude());
		return church;
	}

	public static Country createCountry(ParseObject ob) {
		Country country = new Country();
		country.setId(ob.getObjectId());
		country.setName(ob.getString("name"));
		country.setIso(ob.getString("iso"));
		return country;
	}

	public static Worship createWorship(ParseObject po, Church ch) {
		// TODO Auto-generated method stub
		Worship worship = new Worship();
		try {
			po.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(ch!=null)
			worship.setChurch(ch);
		worship.setStart(po.getDouble("start"));
		worship.setEnd(po.getDouble("end"));
		worship.setId(po.getObjectId());
		worship.setName(po.getString("name"));
		worship.setDay(po.getInt("day"));
		return worship;
	}

	public static WorshipWeek createWorshipWeek(ParseObject po) {
		// TODO Auto-generated method stub
		WorshipWeek worshipWeek = new WorshipWeek();

		try {
			po.fetchIfNeeded();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		worshipWeek.setWorship(createWorship(po.getParseObject("worship"), null));
		worshipWeek.setId(po.getObjectId());
		worshipWeek.setSpeaker(po.getString("speaker"));
		worshipWeek.setWorshipWeek(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
		return worshipWeek;
	}

	public static List<Church> getChurches(Region region,
			Denomination denomination, long l, boolean cache, Activity activity) {
		List<Church> d = new ArrayList<Church>();
		String d_id=denomination==null?"":denomination.getId();
		String r_id=region==null?"":region.getId();

		String FILENAME = "church_list_of_"+d_id+r_id;
		boolean rewrite = false;

		try {
			d = (List<Church>)readSerializableObject(FILENAME, activity);
		} catch (FileNotFoundException e) {
			cache = false;
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			cache = false;
			e.printStackTrace();
		} catch (IOException e) {
			cache = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Church");
			if(region!=null) {
				query.whereEqualTo("region", region.getPObject());
			}
			if(denomination!=null) {
				query.whereEqualTo("denomination", denomination.getPObject());
			} else {
				query.whereEqualTo("denomination", JSONObject.NULL);
			}
			if(l!=-1l) {
				query.whereGreaterThan("updatedAt", l);
			} else {
				rewrite = true;
			}
			List<ParseObject> objects;
			try {
				objects = query.find();
				List<Church> temp = new ArrayList<Church>();
				for(int i=0; i < objects.size(); i++) {
					temp.add(createChurch(objects.get(i), denomination));
				}
				if(!rewrite) {
					d.addAll(temp);
				} else {
					d = temp;
				}
				writeSerializableObject(FILENAME, d, activity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return d;
	}

	public static List<Country> getCountries(long l, boolean cache, Activity activity) {
		List<Country> c = new ArrayList<Country>();
		String FILENAME = "country_list";
		boolean rewrite = false;

		try {
			c = (List<Country>)readSerializableObject(FILENAME, activity);
		} catch (FileNotFoundException e) {
			cache = false;
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			cache = false;
			e.printStackTrace();
		} catch (IOException e) {
			cache = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Country");

			List<ParseObject> objects;
			List<Country> temp = new ArrayList<Country>();
			if(l!=-1l) {
				query.whereGreaterThanOrEqualTo("updatedAt", new Date(l));
			} else {
				rewrite = true;
			}
			try {
				objects = query.find();
				for(int i=0; i < objects.size(); i++) {
					temp.add(createCountry(objects.get(i)));
				}
				if(!rewrite) {
					c.addAll(temp);
				} else {
					c = temp;
				}
				writeSerializableObject(FILENAME, c, activity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return c;
	}

	public static Object readSerializableObject(String filename, Activity activity) throws OptionalDataException, ClassNotFoundException, IOException{
		FileInputStream in = activity.openFileInput(filename);
		ObjectInputStream ob = new ObjectInputStream(in);
		Object o = ob.readObject();
		in.close();
		return o;
	}

	public static void writeSerializableObject(String filename, Object o, Activity activity) throws IOException {
		FileOutputStream fos = null;
		fos = activity.openFileOutput(filename, Context.MODE_PRIVATE);

		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(o);
		out.close();
	}

	public static List<Worship> getWorships(Church church, long l, boolean cache,
			Activity activity) {
		List<Worship> worships = new ArrayList<Worship>();

		String FILENAME = "worship_list_of_"+church.getId();
		boolean rewrite = false;

		try {
			worships = (List<Worship>)readSerializableObject(FILENAME, activity);
		} catch (FileNotFoundException e) {
			cache = false;
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			cache = false;
			e.printStackTrace();
		} catch (IOException e) {
			cache = false;
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(!cache) {
			ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Worship");
			List<ParseObject> objects;
			List<Worship> temp = new ArrayList<Worship>();
			if(l!=-1l) {
				query.whereGreaterThanOrEqualTo("updatedAt", new Date(l));
			} else {
				rewrite = true;
			}
			query.whereEqualTo("church", church.getPObject());
			try {
				objects = query.find();
				for(int i=0; i < objects.size(); i++) {
					temp.add(createWorship(objects.get(i), church));
				}
				if(!rewrite) {
					worships.addAll(temp);
				} else {
					worships = temp;
				}
				writeSerializableObject(FILENAME, worships, activity);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return worships;
	}
	

	public static void getTodaysActiveWorship(Church ch, List<Worship> pa,List<WorshipWeek> ww) {
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("WeeklyWorship");
		ParseQuery<ParseObject> worshipQuery = new ParseQuery<ParseObject>("Worship");
		worshipQuery.whereEqualTo("church", ch.getPObject());
		int today = Util.getCurrentDayOfTheWeek();
		worshipQuery.whereEqualTo("day", today);
		query.whereMatchesQuery("worship", worshipQuery);
		query.whereEqualTo("weekOfYear", Util.getCurrentWeekOfTheYear());
		try {
			List<ParseObject>objects = query.find();
			ww.clear();
			for(int i=0; i < objects.size(); i++) {
				ww.add(createWorshipWeek(objects.get(i)));
			}
			objects = worshipQuery.find();
			pa.clear();
			for(int i=0; i < objects.size(); i++) {
				pa.add(createWorship(objects.get(i),ch));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
