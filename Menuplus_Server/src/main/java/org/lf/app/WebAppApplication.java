package org.lf.app;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * App 시작
 * 
 * @author LF
 * 
 */
@SpringBootApplication
@EnableCaching
@EnableAsync
public class WebAppApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(WebAppApplication.class);
	}

	public static void main(String[] args) {
		//TODO# Live Reload↓ (로컬 개발 핫스왑 기능)
		System.setProperty("spring.devtools.restart.enabled", "true");
		System.setProperty("spring.devtools.livereload.enabled", "true");
		SpringApplication.run(WebAppApplication.class, args);
	}

	@Value("${spring.mail.username}")
    private String username;

    @Value("${server.port}")
    private String port;
    
    @Autowired
    private JavaMailSender sender;
    
    
	/**
	 * 종료 시 실행 함수
	 */
	@PreDestroy
	public void closeApp() {
		SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(username);
        message.setSubject("WebApp server port: " + port + " closed!");
        message.setText("WebApp server port: " + port + " closed!");
//        sender.send(message);
	}
	
}
