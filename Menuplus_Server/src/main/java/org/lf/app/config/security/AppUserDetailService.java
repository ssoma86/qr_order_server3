package org.lf.app.config.security;

import java.util.Date;

import javax.transaction.Transactional;

import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Spring Security - UserDetailsService 상속 받아서 로그인 인증 실행
 * 
 * @author LF
 *
 */
@Service
public class AppUserDetailService implements UserDetailsService {
	
	@Autowired(required=false)
	private AccountService accountService;
	
	@Autowired(required=false)
	private IMessageService service;
	
	/**
	 * 사용자 정보 호출 해서 Spring security에 전달
	 */
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDetails = null;
		boolean usernameNotFound = false;
		
		if (!ObjectUtils.isEmpty(accountService)) {
			userDetails = accountService.getUserDetails(username);
			
			if (ObjectUtils.isEmpty(userDetails)) {
				usernameNotFound = true;
			}
		} else {
			usernameNotFound = true;
		}
		
		if (usernameNotFound) {
			if (!ObjectUtils.isEmpty(service)) {
				throw new UsernameNotFoundException(service.getLanData("아이디, 비밀번호를 정확히 입력 하십시오.", LocaleContextHolder.getLocale()));
			} else {
				throw new UsernameNotFoundException("아이디, 비밀번호를 정확히 입력 하십시오.");
			}
		}
		
		userDetails.getAuthorities();
		
		// 로그인 시간 추가
		Account account = accountService.findOne(userDetails.getUsername());
		account.setLastLoginDtm(new Date());
		
		accountService.save(account);
		
		return userDetails;
	}

	
	
}
