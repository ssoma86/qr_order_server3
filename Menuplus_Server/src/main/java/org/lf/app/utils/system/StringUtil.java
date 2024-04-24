package org.lf.app.utils.system;

/**
 * String util
 * @author LF
 * 
 */
public class StringUtil {

	/**
	 * 왼쪽 문자 붙이기
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 */
	public static String lpad(String str, int len, String pad) {
		while (str.length() < len) {
			str = pad + str;
		}
		return str;
	}
	
	/**
	 * 오른쪽 문자 붙이기
	 * @param str
	 * @param len
	 * @param pad
	 * @return
	 */
	public static String rpad(String str, int len, String pad) {
		while (str.length() < len) {
			str = str + pad;
		}
		return str;
	}
	
}