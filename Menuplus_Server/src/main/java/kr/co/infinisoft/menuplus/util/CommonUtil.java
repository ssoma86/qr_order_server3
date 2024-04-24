package kr.co.infinisoft.menuplus.util;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtil {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
	public static String getHexCodeMD5(String inputValue) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}

		String eip;
		byte[] bip;
		String temp = "";
		String tst = inputValue;

		bip = md5.digest(tst.getBytes());
		for (int i = 0; i < bip.length; i++) {
			eip = "" + Integer.toHexString((int) bip[i] & 0x000000ff);
			if (eip.length() < 2)
				eip = "0" + eip;
			temp = temp + eip;
		}
		return temp;
	}
	/*
	 * public static void main(String[] args) throws Exception{ String pw =
	 * "12345678"; System.out.println(CommonUtil.getHexCodeMD5(pw)); }
	 */ 
} // end class
