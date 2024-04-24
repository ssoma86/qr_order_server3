package org.lf.app.config.interceptors;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.history.History;
import org.lf.app.models.system.history.HistoryRepository;
import org.lf.app.models.system.resources.Resources;
import org.lf.app.models.system.resources.ResourcesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Web request log handler
 */
@Component
public class WebRequestLogHandler implements HandlerInterceptor {

	@Autowired
	private HistoryRepository historyRepository;
	
	@Autowired
	private ResourcesRepository resourcesRepository;
	
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
    	if (new AntPathRequestMatcher("/error").matches(req)) {
    		return true;
    	}
    	
    	HttpSession session = req.getSession();
    	Account account = null;
    	Resources resources = resourcesRepository.findOneByUrlAndMethod(req.getRequestURI(), req.getMethod());
    	
    	// 계정 정보 가져오기
    	if (null != session.getAttribute("sa_account")) {
    		account = (Account)session.getAttribute("sa_account");
    	}
    	
    	History history = new History();
    	history.setSessionId(req.getSession().getId());
    	history.setInDtm(new Date());
    	history.setInIp(req.getHeader("X-Real-IP"));
    	history.setAccount(account);
    	history.setResources(resources);
    	
    	try {
    		historyRepository.save(history);
    	} catch (Exception e) {
    		history.setAccount(null);
    		historyRepository.save(history);
    	}
    	
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView mav) throws Exception {
    	// 언어 코드 정보 화면에 전달
    	req.setAttribute("locale", LocaleContextHolder.getLocale());
    }
    
}
