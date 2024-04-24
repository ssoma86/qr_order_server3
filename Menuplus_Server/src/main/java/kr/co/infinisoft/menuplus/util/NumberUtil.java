package kr.co.infinisoft.menuplus.util;

public class NumberUtil {

	//문자열 콤마추가
	public static String setComma(String value) {
		if(value.equals("")) {
			return "0";
		}
		
		String tmp1 = "";
		String tmp2 = "";
		tmp1 = value;
		
		while(tmp1.length() > 3) {
			tmp2 = "," + tmp1.substring(tmp1.length() - 3, tmp1.length()) + tmp2;
			tmp1 = tmp1.substring(0, tmp1.length() - 3);
		}
		return tmp1 + tmp2;
	}
}
