package org.lf.app.config.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * 권한 인증
 * 
 * @author LF
 *
 */
@Service
public class AppAccessDecisionManager implements AccessDecisionManager {
	private static final Logger logger = LoggerFactory.getLogger(AppAccessDecisionManager.class);
	
	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private IMessageService service;
	
	@Autowired
	private AccountService accountService;
	
	
	@Override
	@Transactional
	public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
		
		// 로그인 페이지 진입 과 로그아웃은 권한 체크 안함, ignoring에서 설정 해놓으면 CSRF값이 적용 안됨
		if (new AntPathRequestMatcher("/").matches(request) && request.getMethod().equals("GET") ||
			new AntPathRequestMatcher("/logout").matches(request) && request.getMethod().equals("GET") ||
			new AntPathRequestMatcher("/language").matches(request) && request.getMethod().equals("GET") ||
			new AntPathRequestMatcher("/forget").matches(request) && request.getMethod().equals("POST") ||
			new AntPathRequestMatcher("/pwChangeView/*").matches(request) && request.getMethod().equals("GET") ||
			new AntPathRequestMatcher("/pwChange/*/*").matches(request) && request.getMethod().equals("PATCH")
			) {
			return;
        }
		
		// 여기서 디비 정보를 가져와서 자원 방문 권한이 있는지 체크한다
		// 로그인 사용자 Role 정보 가져올수 있음
		try {
			Iterator<? extends GrantedAuthority> authIterator = null;
			
			// JWT 토큰으로 접속시 권한 정보가 없음 다시 조회 함
			if (authentication.getName().startsWith("AU20")) {
				Account account = accountService.findOne(authentication.getName());
				
				Set<Auth> auths = account.getAuths();
				
				boolean isAppUser = false;
				
				for (Auth auth : auths) {
					if ("appUser".equals(auth.getAuthId())) {
						isAppUser = true;
						break;
					}
				}
				
				if (isAppUser) {
					authIterator = account.getAuthorities().iterator();
				} else {
					authIterator = authentication.getAuthorities().iterator();
				}
				
			} else {
				authIterator = authentication.getAuthorities().iterator();
			}
			
			while(authIterator.hasNext()) {
				GrantedAuthority auth = authIterator.next();
				
				String authority = auth.getAuthority();
//				logger.info("**** authority ["+authority+"]");
				
				if (!"ROLE_ANONYMOUS".equals(authority)) {
					String[] urlMethod = authority.split(";");
					
					if (new AntPathRequestMatcher(urlMethod[0]).matches(request) && request.getMethod().equals(urlMethod[1])) {
						return;
	                }
				}
			}
		} catch (Exception e) {
			logger.error("****decide Exception");
			e.printStackTrace();
		}
//		logger.info("**** service "+service.toString());
		// 권한 없을 시 Exception 리턴
		if (!ObjectUtils.isEmpty(service)) {
			throw new InsufficientAuthenticationException(service.getLanData("접속 권한이 없습니다.", LocaleContextHolder.getLocale()));
		} else {
			throw new InsufficientAuthenticationException("접속 권한이 없습니다.");
		}

	}

	@Override
	public boolean supports(ConfigAttribute arg0) {
		return true;
	}

	@Override
	public boolean supports(Class<?> arg0) {
		return true;
	}

	
}
