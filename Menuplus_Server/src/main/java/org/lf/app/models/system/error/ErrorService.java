package org.lf.app.models.system.error;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 오류
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class ErrorService extends BaseService<Error> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private ErrorRepository repository;
	
	
	/**
	 * 오류 저장
	 * 
	 * @param request
	 * @param response
	 * @param errorAttributes
	 */
	public void saveError(HttpServletRequest req, HttpServletResponse res, Map<String, Object> errorAttributes, HttpStatus status) {
		StringBuilder session = new StringBuilder();
		StringBuilder params = new StringBuilder();
		
		Enumeration<String> attributeNames = req.getSession().getAttributeNames();
		
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			session.append("attributeName = " + attributeName + "  |  " + req.getSession().getAttribute(attributeName));
		}
		
		Enumeration<String> parameterNames = req.getParameterNames();
		Enumeration<String> attributesNames = req.getAttributeNames();
		
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			params.append("parameterName = " + parameterName + "  |  " + req.getParameter(parameterName));
		}
		
		while (attributesNames.hasMoreElements()) {
			String attributeName = attributesNames.nextElement();
			params.append("attributeName = " + attributeName + "  |  " + req.getAttribute(attributeName));
		}
		
		saveError(req.getRequestURI(), session.toString(), params.toString(), params.toString(), errorAttributes.toString(), status.value(), "");
	}
	
	/**
	 * 오류 저장
	 * 
	 * @param url
	 * @param session
	 * @param param
	 * @param dparam
	 * @param errorMsg
	 * @param resultMsg
	 */
	public void saveError(String url, String session, String param, String dparam, String errorMsg, int status, String resultMsg) {
		Error error = new Error();
		
		error.setUrl(url);
		error.setSession(session);
		error.setParam(param);
		error.setDparam(dparam);
		error.setErrorMsg(errorMsg);
		error.setStatus(status);
		error.setResultMsg(resultMsg);
		
		repository.save(error);
	}
	
	
}
