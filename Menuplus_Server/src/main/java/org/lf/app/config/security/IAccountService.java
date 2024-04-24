package org.lf.app.config.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring security 계정 정보 조회 Interface
 * @author lwj
 *
 */
public interface IAccountService {

	default UserDetails getUserDetails(String username) {
		return null;
	}
	
}
