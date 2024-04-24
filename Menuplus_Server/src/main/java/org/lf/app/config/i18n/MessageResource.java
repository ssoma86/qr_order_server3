package org.lf.app.config.i18n;

import java.text.MessageFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.stereotype.Component;

/**
 * 다국어 별도 처리
 * 
 * @author LF
 *
 */
@Component
public class MessageResource extends AbstractResourceBasedMessageSource {
	
	@Autowired(required=false)
	private IMessageService service;
	
	@Override
	@Cacheable("lan")
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (null != service) {
			return new MessageFormat(service.getLanData(code, locale));
		} else {
			return new MessageFormat(code);
		}
	}

}
