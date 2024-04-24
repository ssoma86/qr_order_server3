package org.lf.app.utils.system;

import java.util.Random;

public class RandomUtil {
	
	/**
	  * 生成指定范围内的随机数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandNum(int min, int max) {
		int randNum = min + (int) (Math.random() * ((max - min) + 1));
		return randNum;
	}

	/**
	  * 生成指定长度的随机数
	 * 
	 * @param min
	 * @param max
	 * @return
	 */
	public static String getRandNum(int len) {
		String code = "";
		Random rand = new Random();// 生成随机数
		for (int a = 0; a < len; a++) {
			code += rand.nextInt(10);// 生成6位验证码
		}
		return code;
	}

	/**
	  * 生成随机数字和字母
	 * 
	 * @param length
	 * @return
	 */
	public static String getStringRandom(int length) {

		String val = "";
		Random random = new Random();
		
		for (int i = 0; i < length; i++) {
			String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
			
			if ("char".equalsIgnoreCase(charOrNum)) {
				int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;
				val += (char) (random.nextInt(26) + temp);
			} else if ("num".equalsIgnoreCase(charOrNum)) {
				val += String.valueOf(random.nextInt(10));
			}
		}
		return val;
	}

}
