package org.lf.app.config.i18n;

import java.util.Locale;

import org.springframework.stereotype.Component;

/**
 * 다국어 조회
 * @author lwj
 *
 */
@Component
public interface IMessageService {

	default String getLanData(String code, Locale locale) {
		return code;
	};
	
}
