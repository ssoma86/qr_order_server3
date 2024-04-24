package org.lf.app.models.system.error;

import javax.transaction.Transactional;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import org.lf.app.utils.system.LogUtil;

/**
 * 오류 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/error")
public class ErrorController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
}