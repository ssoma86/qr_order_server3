package kr.co.infinisoft.menuplus.util;

import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class InnopayPasswordEncoder implements PasswordEncoder {
	private static final Logger logger = LoggerFactory.getLogger(InnopayPasswordEncoder.class);
	
	@Override
	public String encode(CharSequence rawPassword) {
		return CommonUtil.getHexCodeMD5(rawPassword.toString());
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		
		if (encodedPassword == null || encodedPassword.length() == 0) {
			logger.error("Empty encoded password");
			return false;
		}
		
		String encPwd = CommonUtil.getHexCodeMD5(rawPassword.toString());
		if(StringUtils.equals(encPwd, encodedPassword)) {
			return true;
		}else {
			return false;
		}
		
	}

} // end class
