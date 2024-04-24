package org.lf.app;

import java.util.Locale;
import java.util.Set;

import org.lf.app.config.exception.ValidationErrorAttributes;
import org.lf.app.config.i18n.MessageResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * WebApp 별도 설정
 * 
 * @author LF
 *
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
	
	@Autowired(required=false)
	private Set<HandlerInterceptor> handlers;
	
	
	/**
	 * 다국어 설정 - Cookie사용
	 * 
	 * @return LocaleResolver
	 */
	@Bean
    public LocaleResolver localeResolver() {
		CookieLocaleResolver slr = new CookieLocaleResolver();
        slr.setDefaultLocale(Locale.KOREAN);
        return slr;
    }
	
	
	
	/**
	 * 다국어 가져오는 MessageSource 재설정(디비에서 데이타 가져옴)
	 * 
	 * @return MessageSource
	 */
	@Bean
	public MessageSource messageSource() {
		return new MessageResource();
	}
	
	
	
	/**
	 * Validation 다국어 가져오는 방식 재설정(디비에서 데이타 가져옴)
	 * * 편법임
	 * 
	 * @return ErrorAttributes
	 */
	@Bean
	public ErrorAttributes errorAttributes() {
		return new ValidationErrorAttributes();
	}
	
	
	
	/**
	 * CORS 크로스 도메인 설정
	 * @return
	 */
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**");
    }
	
	
	
	/**
	 * Interceptors 설정
	 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	// 다국어 Interceptor 설정
        registry.addInterceptor(new LocaleChangeInterceptor());

        // 사용자 추가한 Interceptor 설정
		handlers.forEach(handler -> registry.addInterceptor(handler));
    }
    
    
    
    /**
     * Async 풀 설정
     */
    @Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    	configurer.setDefaultTimeout(60 * 1000L);
        configurer.registerCallableInterceptors(timeoutInterceptor());
        configurer.setTaskExecutor(threadPoolTaskExecutor());
	}
    
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }
    
	@Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor t = new ThreadPoolTaskExecutor();
        t.setCorePoolSize(10);
        t.setMaxPoolSize(1000);
        t.setThreadNamePrefix("app");
        return t;
    }
	
    
    
}
