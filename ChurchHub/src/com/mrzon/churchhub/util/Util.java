package com.mrzon.churchhub.util;

public class Util {
	public static double distFrom(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;

		return dist * meterConversion;
	}
	
	public double getLatMin(double lat, int distance){
		return 0;
	}
	public double getLatMax(double lat, int distance){
		return 0;
	}
	public double getLonMin(double lon, int distance){
		return 0;
	}
	public double getLonMax(double lon, int distance){
		return 0;
	}
}
