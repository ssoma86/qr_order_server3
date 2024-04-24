package org.lf.app.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
  * SpringWebSecurityAdapter - Spring security
  * 
  * @author LF
  *
  */
@Configuration
@EnableWebSecurity
@EnableAuthorizationServer
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
    private AppAccessDecisionManager appAccessDecisionManager;
	
	@Autowired
    private AppUserDetailService appUserDetailService;
	
	
	/**
	 * refresh_token 사용 하기 위해서는 이걸 해야함
	 * 설정 파일에서 authorizationServer 설정 시 이놈을 추가 해줘야 함
	 * 아니면 재구현 할려면 자동 설정을 사용 할 수 없음
	 * @param auth
	 * @throws Exception
	 */
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
				// enable in memory based authentication with a user named "user" and "admin"
				.inMemoryAuthentication().withUser("user").password("password").roles("USER")
				.and().withUser("admin").password("password").roles("USER", "ADMIN");
//        auth.userDetailsService( appUserDetailService );
    }
	
	
	/**
	 * Spring Security에서 제공되는 비번 암호와 방법
	 * @return PasswordEncoder
	 */
    @Bean
	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
    	return new InnopayPasswordEncoder();
	}
    
	
	@Override
    public void init(WebSecurity web) throws Exception {
		super.init(web);
		// 권한 체크에서 제외  
        web.ignoring().antMatchers("/favicon.ico", "/error/**"
        		, "/css/**", "/img/**", "/js/**", "/libs/**", "/upload/**"
        		, "/api/getLanData/*", "/api/app/registered", "/api/app/socialLogin", "/api/app/forget/*", "/api/getStoreRoom/*", "/api/getStoreRoom/*/*", "/api/order4Room"
        		, "/api/getStore/*", "/api/getTerms/*/*", "/api/order","/api/noti","/api/innopay/*","/api/innopay/*/*","/api/innopay/*/*/*/*/*","/pay/receive"
        		, "/api/pay/approval", "/api/app/mpay", "/api/app/rpay", "/api/app/cpay","/api/searchPost","/api/delAddr","/api/getOrderCd/*","/api/getOrderCd/*/*"
        		,"/api/getDelivery/*","/api/payMethod/*", "/api/updateCancelYn4SysErr/*", "/api/getOrderStatus/*", "/api/cancelOrder/*/*/*/*/*"   
        		,"/websocket/**" , "/websocket/*/*", "/biz/send", "/api/app/chkEmail", "/store/**", "/store", "/api/app/loginProc"
        		, "/api/app/storeInfo/*", "/api/app/orderDetail/*", "/api/app/logOut/*", "/api/app/orderStatus/*/*", "/api/app/orderStatus/*/*/*", "/api/app/cancelOrder/*/*/*/*"
			    , "/api/app/orderlist/*/*/*/*/*", "/api/app/findPw", "/api/testReceiveAlarm", "/api/yajo/getStoreRoom/*/*", "/api/innopayNoti");
    }
    
	/**
	 * HttpSecurity 설정
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.httpBasic().disable();
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/").permitAll();
		// Spring Security X-Frame-Options'응답 헤더를 비활성화
		http.headers().frameOptions().disable();
		
//		http.httpBasic().disable();
//		http.csrf().disable();
//		http.authorizeRequests().antMatchers("/").permitAll()
//		 .antMatchers(HttpMethod.OPTIONS, "/**").permitAll();
//		// Spring Security X-Frame-Options'응답 헤더를 비활성화
//		http.headers().frameOptions().disable();
	}
	
	
	
	
	/**
	 * Security 리소스 서버 권한 설정  
	 * @author lwj
	 *
	 */
	@Configuration
	@EnableResourceServer
	public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// 접속 권한 설정
			http.antMatcher("/api/**")
				.authorizeRequests()
				.accessDecisionManager(appAccessDecisionManager)
				.anyRequest().authenticated();
		}
	}
}
