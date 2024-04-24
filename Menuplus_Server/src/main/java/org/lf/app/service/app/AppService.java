package org.lf.app.service.app;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.tools.seqBuilder.SeqBuilderService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.HttpUtil;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;


/**
 * 앱용 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class AppService {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());

	@Autowired
	private AuthService authService;
	
	@Autowired
    private AppUserService appUserService;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private SeqBuilderService seqBuilderService;
	
	@Autowired
	private FileUtil fileUtil;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	public static final String KAKAO_API_HOST = "https://kapi.kakao.com";
	public static final String KAKAO_GET_PROFILE = "/v2/user/me";
	
	public static final String NAVER_API_HOST = "https://openapi.naver.com";
	public static final String NAVER_GET_PROFILE = "/v1/nid/me";
	
	
	
	/**
	 * 회원 가입
	 * @throws Exception 
	 * 
	 */
	public AppUser registered(AppUser user) throws Exception {
		// 앱 사용자 ID설정
		user.setAccountId(seqBuilderService.getAppUserId());
		
		// 비밀번호 암호와
//		user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
		user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
		
		// 닉 네임 없을 시 이름으로 설정
		if (StringUtils.isEmpty(user.getAccountNm())) {
			user.setAccountNm("고객님");
		}
				
		// 닉 네임 없을 시 이름으로 설정
		if (StringUtils.isEmpty(user.getNickName())) {
			user.setNickName(user.getAccountNm());
		}
		
		// 이미지 저장
		if (!StringUtils.isEmpty(user.getProfileImg()) && user.getProfileImg().contains("base64")) {
			user.setProfileImg(fileUtil.saveFile("appUser", user.getAccountId(), user.getProfileImg()));
		}
		user.setStartDtm(new Date());
		
		// 왭 회원 권한 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("appUser");
		auths.add(auth);
		user.setAuths(auths);
		
		return appUserService.save(user);
	}
	
	
	/**
	 * 카카오 API 회원 정보 호출
	 * @throws Exception 
	 * 
	 */
	public AppUser kakaoLogin(String token) throws Exception {
		Map<String, Object> headers = new HashMap<>();
		headers.put("Authorization", "bearer " + token);
		
		// API 호출
		String result = HttpUtil.doGet(KAKAO_API_HOST + KAKAO_GET_PROFILE, headers);
		
		Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
		
		// properties 읽어 와서 설정
		String properties = MapUtils.getString(resultMap, "properties");
		
		Map<String, String> propertiesMap = new HashMap<>();
		
		String[] propertiesArray = properties.split(",");
		
		for (String propertie : propertiesArray) {
			
			String[] tmps = propertie.replace("{", "").replace("}", "").split("=");
			
			if (tmps.length > 1) {
				propertiesMap.put(tmps[0].trim(), tmps[1].trim());
			}
		}
		
		// kakao_account 읽어 와서 설정
		String kakaoAccount = MapUtils.getString(resultMap, "kakao_account");
		
		Map<String, String> kakaoAccountMap = new HashMap<>();
		
		String[] kakaoAccountArray = kakaoAccount.split(",");
		
		for (String kakaoAccountData : kakaoAccountArray) {
			String[] tmps = kakaoAccountData.replace("{", "").replace("}", "").split("=");
			
			if (tmps.length > 1) {
				kakaoAccountMap.put(tmps[0].trim(), tmps[1].trim());
			}
		}
		
		
		String socialId = MapUtils.getString(resultMap, "id");
		
		AppUser appUser = appUserService.findOneBySocialId(socialId);
		
		if (ObjectUtils.isEmpty(appUser)) {
			appUser = new AppUser();
			
			appUser.setSocialId(socialId);
			appUser.setSocialType("kakao");
			// 앱 사용자 ID설정
			appUser.setAccountId(seqBuilderService.getAppSocialUserId());
			
			String password = RandomUtil.getStringRandom(8);
			// 비밀번호 암호와
//			appUser.setAccountPw(new BCryptPasswordEncoder().encode(password));
			appUser.setAccountPw(new InnopayPasswordEncoder().encode(password));
			// 소셜 로그인 시 비번이 없어서 저장 해둥
			appUser.setSocialPassword(password);
			
			appUser.setAccountNm(MapUtils.getString(propertiesMap, "nickname"));
			appUser.setNickName(MapUtils.getString(propertiesMap, "nickname"));
			appUser.setProfileImg(MapUtils.getString(propertiesMap, "thumbnail_image"));
			appUser.setTel(MapUtils.getString(kakaoAccountMap, "phone_number"));
			appUser.setEmail(MapUtils.getString(kakaoAccountMap, "email"));
			
			// 왭 회원 권한 설정
			Set<Auth> auths = new HashSet<Auth>();
			Auth auth = authService.findOne("appUser");
			auths.add(auth);
			appUser.setAuths(auths);
		}
		
		// 로그인 시간 추가
		appUser.setStartDtm(new Date());
		appUser.setLastLoginDtm(new Date());
		
		return appUserService.save(appUser);
	}

	
	/**
	 * 네이버 API 회원 정보 호출
	 * @throws Exception 
	 * 
	 */
	public AppUser naverLogin(String token) throws Exception {
		Map<String, Object> headers = new HashMap<>();
		headers.put("Authorization", "bearer " + token);
		
		// API 호출
		String result = HttpUtil.doGet(NAVER_API_HOST + NAVER_GET_PROFILE, headers);
		
		Map<String, String> resultMap = objectMapper.readValue(result, Map.class);
		
		Map<String, String> responseMap = new HashMap<>();
		
		if ("00".equals(MapUtils.getString(resultMap, "resultcode"))) {
			// properties 읽어 와서 설정
			String response = MapUtils.getString(resultMap, "response");
			
			String[] responseArray = response.split(",");
			
			for (String responseData : responseArray) {
				
				String[] tmps = responseData.replace("{", "").replace("}", "").split("=");
				
				if (tmps.length > 1) {
					responseMap.put(tmps[0].trim(), tmps[1].trim());
				}
			}
		}
		
		
		String socialId = MapUtils.getString(responseMap, "id");
		
		AppUser appUser = appUserService.findOneBySocialId(socialId);
		
		if (ObjectUtils.isEmpty(appUser)) {
			appUser = new AppUser();
			
			appUser.setSocialId(socialId);
			appUser.setSocialType("naver");
			// 앱 사용자 ID설정
			appUser.setAccountId(seqBuilderService.getAppSocialUserId());
			
			String password = RandomUtil.getStringRandom(8);
			// 비밀번호 암호와
//			appUser.setAccountPw(new BCryptPasswordEncoder().encode(password));
			appUser.setAccountPw(new InnopayPasswordEncoder().encode(password));
			// 소셜 로그인 시 비번이 없어서 저장 해둥
			appUser.setSocialPassword(password);
			
			appUser.setAccountNm(MapUtils.getString(responseMap, "nickname"));
			appUser.setNickName(MapUtils.getString(responseMap, "nickname"));
			appUser.setProfileImg(MapUtils.getString(responseMap, "profile_image"));
			appUser.setEmail(MapUtils.getString(responseMap, "email"));
			
			// 왭 회원 권한 설정
			Set<Auth> auths = new HashSet<Auth>();
			Auth auth = authService.findOne("appUser");
			auths.add(auth);
			appUser.setAuths(auths);
		}
		
		// 로그인 시간 추가
		appUser.setStartDtm(new Date());
		appUser.setLastLoginDtm(new Date());
				
		return appUserService.save(appUser);
	}
	
	
	/**
	 * Token 가져오기
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws Exception 
	 */
	public Map<String, Object> getToken(String username, String password, String oauthUrl) throws Exception {
		Map<String, Object> headers = new HashMap<>();
		headers.put("Authorization", "Basic YXBwOjEyMzQ=");
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		String param = "grant_type=password&scope=app&username=" + username + "&password=" + password;
		
		// API 호출
		String result = HttpUtil.doPost(oauthUrl, param, headers);
		
		Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
		
		return resultMap;
	}
	
	
	/**
	 * 앱에서 언어 파라메타 받을 시 시스템 언어값 설정 해줌
	 */
	public void chkAndSettingLan(String lanId) {
		// 사용중인 언어 코드 인지 체크
		Lan lan = lanService.findOne(lanId);
		
		if (ObjectUtils.isEmpty(lan)) {
			lan = lanService.getDefaultLan();
		}
		// 시스템 언어 설정
		LocaleContextHolder.setLocale(new Locale(lan.getId()));
	}
	
	
	
}
