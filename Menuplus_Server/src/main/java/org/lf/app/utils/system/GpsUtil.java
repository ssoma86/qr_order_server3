package org.lf.app.utils.system;

public class GpsUtil {

	// 지구 반경
	private static double EARTH_RADIUS = 6378.138;
	
	private static double rad(double d) {
	    return d * Math.PI / 180.0;
	}
	
	// 거리계산, M로 나옴
	public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
	    double radLat1 = rad(lat1);
	    double radLat2 = rad(lat2);
	    double a = radLat1 - radLat2;
	    double b = rad(lng1) - rad(lng2);
	    
	    double s = 2 * Math.atan(Math.sqrt(Math.pow(Math.sin(a/2),2) + Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
	    s = s * EARTH_RADIUS;
	    s = Math.round(s * 10000) / 10;
	    return s;
	}
	
	
//	public static void main(String[] args) {
//		
//		// 37.4859728,127.0295146 회사 422.0M
//		// 37.484462,127.033907 양재역
//		
//		
//		double lat1 = 37.497992;
//		double lng1 = 127.027544;
//		double lat2 = 37.500673;
//		double lng2 = 127.036503;
//		
//		double s = getDistance(lat1, lng1, lat2, lng2);
//		
//		System.out.println(s);
//	}
	
}
