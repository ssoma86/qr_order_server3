package org.lf.app.config.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.Base;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Validation 다국어 변환, 디비에서 가져와서 처리
 * 
 * @author LF
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationErrorAttributes implements ErrorAttributes, HandlerExceptionResolver, Ordered {

	private static final String ERROR_ATTRIBUTE = ValidationErrorAttributes.class.getName() + ".ERROR";
	
	@Autowired(required=false)
	private IMessageService lanDataService;
	
	private final boolean includeException;

	/**
	 * Create a new {@link DefaultErrorAttributes} instance that does not include the
	 * "exception" attribute.
	 */
	public ValidationErrorAttributes() {
		this(false);
	}

	/**
	 * Create a new {@link DefaultErrorAttributes} instance.
	 * @param includeException whether to include the "exception" attribute
	 */
	public ValidationErrorAttributes(boolean includeException) {
		this.includeException = includeException;
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		storeErrorAttributes(request, ex);
		return null;
	}

	private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
		request.setAttribute(ERROR_ATTRIBUTE, ex);
	}

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest webRequest,
			boolean includeStackTrace) {
		Map<String, Object> errorAttributes = new LinkedHashMap<>();
		errorAttributes.put("timestamp", new Date());
		addStatus(errorAttributes, webRequest);
		addErrorDetails(errorAttributes, webRequest, includeStackTrace);
		addPath(errorAttributes, webRequest);
		return errorAttributes;
	}

	private void addStatus(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		Integer status = getAttribute(requestAttributes,
				"javax.servlet.error.status_code");
		if (status == null) {
			errorAttributes.put("status", 999);
			errorAttributes.put("error", "None");
			return;
		}
		errorAttributes.put("status", status);
		try {
			errorAttributes.put("error", HttpStatus.valueOf(status).getReasonPhrase());
		}
		catch (Exception ex) {
			// Unable to obtain a reason
			errorAttributes.put("error", "Http Status " + status);
		}
	}

	private void addErrorDetails(Map<String, Object> errorAttributes,
			WebRequest webRequest, boolean includeStackTrace) {
		Throwable error = getError(webRequest);
		if (error != null) {
			while (error instanceof ServletException && error.getCause() != null) {
				error = ((ServletException) error).getCause();
			}
			if (this.includeException) {
				errorAttributes.put("exception", error.getClass().getName());
			}
			addErrorMessage(errorAttributes, error);
			if (includeStackTrace) {
				addStackTrace(errorAttributes, error);
			}
		}
		Object message = getAttribute(webRequest, "javax.servlet.error.message");
		if ((!StringUtils.isEmpty(message) || errorAttributes.get("message") == null)
				&& !(error instanceof BindingResult)) {
			errorAttributes.put("message",
					StringUtils.isEmpty(message) ? "No message available" : message);
		}
	}

	private void addErrorMessage(Map<String, Object> errorAttributes, Throwable error) {
		BindingResult result = extractBindingResult(error);
		if (result == null) {
			errorAttributes.put("message", error.getMessage());
			return;
		}
		
		if (result.getErrorCount() > 0) {
			// JPA Validation 오류 있을 시 리턴 값에 다국어 추가
			if (result.getFieldErrorCount() > 0) {
				List<Map<String, Object>> errorsMessage = new ArrayList<>();
				
				result.getFieldErrors().forEach(fe -> {
					Map<String, Object> tmpError = new HashMap<>();
					tmpError.put("field", fe.getField());
					tmpError.put("value", fe.getRejectedValue());
					tmpError.put("code", fe.getCode());
					
					String defaultMsg = fe.getDefaultMessage();
					
					if (!StringUtils.isEmpty(defaultMsg)) {
						String[] msgs = defaultMsg.split(Base.SPLIT);
						
						// 다국어 코드 설정
						if (null != msgs && msgs.length > 1) {
							Object[] paramsTmp = msgs[1].split(",");
							Object[] params = new Object[paramsTmp.length];
							
							for (int i = 0, len = paramsTmp.length; i < len; i++) {
								params[i] = lanDataService.getLanData(paramsTmp[i].toString(), LocaleContextHolder.getLocale());
							}
							
							if (null != lanDataService) {
								tmpError.put("errorMsg", MessageFormat.format(
										lanDataService.getLanData(msgs[0], LocaleContextHolder.getLocale()), params));
							} else {
								tmpError.put("errorMsg", MessageFormat.format(msgs[0], params));
							}
						} else {
							if (null != lanDataService) {
								tmpError.put("errorMsg", lanDataService.getLanData(msgs[0], LocaleContextHolder.getLocale()));
							} else {
								tmpError.put("errorMsg", msgs[0]);
							}
						}
					} else {
						tmpError.put("errorMsg", defaultMsg);
					}
					
					errorsMessage.add(tmpError);
				});
				
				// 오류 메세지 정열, 기본은 랜덤으로 나옴 
				Collections.sort(errorsMessage, new ValidComparator());
				errorAttributes.put("errorsMessage", errorsMessage);
			}
			
			errorAttributes.put("errors", result.getAllErrors());
			errorAttributes.put("message",
					"Validation failed for object='" + result.getObjectName()
							+ "'. Error count: " + result.getErrorCount());
			
		} else {
			errorAttributes.put("message", "No errors");
		}
	}

	private BindingResult extractBindingResult(Throwable error) {
		if (error instanceof BindingResult) {
			return (BindingResult) error;
		}
		if (error instanceof MethodArgumentNotValidException) {
			return ((MethodArgumentNotValidException) error).getBindingResult();
		}
		return null;
	}

	private void addStackTrace(Map<String, Object> errorAttributes, Throwable error) {
		StringWriter stackTrace = new StringWriter();
		error.printStackTrace(new PrintWriter(stackTrace));
		stackTrace.flush();
		errorAttributes.put("trace", stackTrace.toString());
	}

	private void addPath(Map<String, Object> errorAttributes,
			RequestAttributes requestAttributes) {
		String path = getAttribute(requestAttributes, "javax.servlet.error.request_uri");
		if (path != null) {
			errorAttributes.put("path", path);
		}
	}

	@Override
	public Throwable getError(WebRequest webRequest) {
		Throwable exception = getAttribute(webRequest, ERROR_ATTRIBUTE);
		if (exception == null) {
			exception = getAttribute(webRequest, "javax.servlet.error.exception");
		}
		return exception;
	}

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(RequestAttributes requestAttributes, String name) {
		return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
	}
	
	/**
	 * Validation 정열 순서 정의
	 * @author lwj
	 *
	 */
	private class ValidComparator implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            String code1 = o1.get("code").toString(); 
            String code2 = o2.get("code").toString();
            return convertCode(code1).compareTo(convertCode(code2));
        }
		
		private String convertCode(String code) {
			int codeInt = 1;
			switch (code) {
			case "NotNull":
			case "NotBlank":
			case "NotEmpty":
			case "Null": codeInt = 9; break;
			
			case "Size":
			case "Length":
			case "Min":
			case "DecimalMin": codeInt = 8; break;
			
			case "Range":
			case "Digits": codeInt = 7; break;
			
			case "Max":
			case "DecimalMax": codeInt = 6; break;
			
			default: codeInt = 1; break;
			}
			
			return String.valueOf(codeInt);
		}
		
	}
}
