package org.lf.app.utils.validation;

import java.util.regex.Pattern;

public class ValidUtil {

	public static final String REGEXP_TEL = "\\+?[\\d]*\\s*[\\d-]+|";
	
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	
	public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
	
	public static final String REGEX_IP_ADDR = "(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";

	public static final String REGEX_NUMBER = "^[0-9]+$";

	
	public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }
	
	public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }
	
	public static boolean isIPAddress(String ipAddress) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddress);
    }
}
