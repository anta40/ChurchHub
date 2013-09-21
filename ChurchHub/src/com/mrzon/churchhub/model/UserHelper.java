package com.mrzon.churchhub.model;

import com.parse.ParseUser;

public class UserHelper {
	public static User currentUser = getCurrentUser();
	public static void setAvatar() {
	}
	
	private static User getCurrentUser() {
		User u = new User();
		ParseUser pu = ParseUser.getCurrentUser();
		if(pu == null) {
			return null;
		}
		u.setUserName(pu.getUsername());
		u.setId(pu.getObjectId());
		return u;
	}

	public static void setBirthdate() {
		
	}
	
	public static void setChurch() {
		
	}
	
	public static void setToken() {
		
	}
	
	public static void logout() {
		
	}
	
	public static void login() {
		
	}
	
	
}
