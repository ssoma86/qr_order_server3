package org.lf.app.config.exception;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lf.app.models.system.error.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 오류 일괄 설정
 * 
 * @author LF
 *
 */
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class WebAppErrorController extends AbstractErrorController {
	@Autowired
	private ErrorService errorService;
	
	public WebAppErrorController(ErrorAttributes errorAttributes) {
		 super(errorAttributes);
	}

	@Override
	public String getErrorPath() {
		return "error";
	}
	
	
	
	/**
	 * Html 오류처리
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView(error/error)
	 */
	@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
	public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = getStatus(request);
		Map<String, Object> errorAttributes = getErrorAttributes(request, false);
		
		ModelAndView mav = new ModelAndView();
		mav.addAllObjects(errorAttributes);
		
		switch (status.value()) {
		case 403:
			mav.setViewName("error/notPermissions");
			break;
		case 500:
			mav.setViewName("error/error");
			errorService.saveError(request, response, errorAttributes, status);
			break;
		default:
			mav.setViewName("error/error");
			break;
		}
		
		return mav;
	}

	/**
	 * Json 오류처리
	 * 
	 * @param request
	 * @return Json형식의 오류 내용
	 */
	@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		Map<String, Object> errorAttributes = getErrorAttributes(request, false);
		
		if (status.is5xxServerError()) {
			errorService.saveError(request, null, errorAttributes, status);
		}
		
		return new ResponseEntity<>(errorAttributes, status);
	}
	
	/**
	 * Session timeout 처리
	 * 
	 * @throws IOException 
	 */
	@RequestMapping("/timeout")
	public void timeout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getHeader("Accept") != null
				&& request.getHeader("Accept").contains("application/json")) { // ajax 호출 시 텍스트로 값 전달

			response.sendError(HttpServletResponse.SC_REQUEST_TIMEOUT, "Timeout");
		} else {
			response.sendRedirect("/error/timeoutPage");
		}
	}
	
	/**
	 * Session timeout page
	 * 
	 * @return ModelAndView(error/timeout)
	 */
	@RequestMapping("/timeoutPage")
	public ModelAndView timeoutPage() {
		return new ModelAndView("error/timeout");
	}
	
}
