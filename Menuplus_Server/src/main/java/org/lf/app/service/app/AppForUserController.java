package org.lf.app.service.app;

import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUser.AppUserValid;
import org.lf.app.models.business.appUser.AppUserController.AppUserControllerJsonView;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.business.inquiry.Inquiry;
import org.lf.app.models.business.inquiry.Inquiry.InquiryValid;
import org.lf.app.models.business.inquiry.InquiryService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.Account.AccountPwValid;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.terms.Terms;
import org.lf.app.models.system.terms.TermsAccept;
import org.lf.app.models.system.terms.TermsAcceptService;
import org.lf.app.models.system.terms.TermsService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.RandomUtil;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 앱 회원 사용자 관련 API
 * 
 * @author lby
 *
 */
@RestController
@RequestMapping("/api/app")
public class AppForUserController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
    private TermsService termsService;
	
	@Autowired
    private TermsAcceptService termsAcceptService;
	
	@Autowired
    private CodeService codeService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
    private AppUserService appUserService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private InquiryService inquiryService;
	
	
	
	@Autowired
	private FileUtil fileUtil;
	
	@Value("${spring.application.name}")
    private String appNm;
	
	@Value("${spring.mail.username}")
    private String username;
	
	@Autowired
    private JavaMailSenderImpl sender;
	
	
	
	/**
	 * 약관동의 여부 조회
	 * 
	 */
//	@GetMapping("/getTermsAccept/{lanId}/{uuid}")
//	public List<Terms> getTermsAccept(@PathVariable String lanId, @PathVariable String uuid) {
//		appService.chkAndSettingLan(lanId);
//		
//		List<Terms> result = new ArrayList<>();
//		
//		// 해당 UUID 동의한 내용 조회
//		List<TermsAccept> termsAcceptList = termsAcceptService.findByUuid(uuid);
//		// 서비스에서 제공하는 약관 내용 조회
//		List<Terms> termsList = termsService.findByLan(lanId);
//		
//		// 서비스에서 제공하는 약관과 해당 UUID 동의 한 약관정보 비교해서 미동의 약관내용 내려주기
//		for (Terms terms : termsList) {
//			boolean accept = false;
//			
//			for (TermsAccept termsAccept : termsAcceptList) {
//				if (terms.getTermsTp().getCd().equals(termsAccept.getTermsTp().getCd())) {
//					accept = true;
//				}
//			}
//			
//			if (!accept) {
//				result.add(terms);
//			}
//		}
//		
//		return result;
//	}
	
	
	
	/**
	 * 약관동의
	 * 
	 */
//	@PostMapping("/termsAccept/{uuid}/{termsTpVal}")
//	public void termsAccept(@PathVariable String uuid, @PathVariable String termsTpVal) {
//		Code termsTp = codeService.findOneByTopCodeValAndVal("TERMS_TP", termsTpVal);
//		
//		TermsAccept termsAccept = new TermsAccept();
//		termsAccept.setUuid(uuid);
//		termsAccept.setTermsTp(termsTp);
//		termsAccept.setAcceptDtm(new Date());
//		
//		termsAcceptService.save(termsAccept);
//	}
	
	
	/**
	 * 소셜 회원 가입
	 * @param lanId
	 * @throws Exception 
	 */
//	@PostMapping("/socialLogin")
//	@JsonView(AppUserControllerJsonView.class)
//	public Map<String, Object> socialLogin(String socialType, String token, HttpServletRequest req) throws Exception {
//		AppUser user = null;
//		
//		// 소셜 회원 등록
//		if ("kakao".equals(socialType)) {
//			user = appService.kakaoLogin(token);
//		} else if ("naver".equals(socialType)) {
//			user = appService.naverLogin(token);
//		}
//		
//		String serverUrl = req.getRequestURL().toString().replace(req.getRequestURI(), "");
//		// 토큰 주소
//		String oauthUrl = serverUrl + "/oauth/token";
//		
//		// 자체 서버에서 토큰 발행 하기
//		Map<String, Object> result = appService.getToken(user.getAccountId(), user.getSocialPassword(), oauthUrl);
//		result.put("appUser", user);
//		
//		return result;
//	}
	
	
	/**
	 * 이메일 중복 체크
	 * @throws IOException 
	 */
	@GetMapping("/chkEmail")
	public Map<String, Object> chkEmail(@RequestParam Map<String, Object> params, HttpServletResponse res) throws IOException {
		String email = MapUtils.getString(params, "email", null);
		
		if (StringUtils.isEmpty(email)) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json; charset=utf-8");
			
			Map<String, Object> errorAttribute = new HashMap<String, Object>();
			errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
			errorAttribute.put("error", "Bad Request");
			
			List<Map<String, Object>> errorsMessages = new ArrayList<>();
			
			Map<String, Object> errorsMessage = new HashMap<String, Object>();
			errorsMessage.put("code", "isExist");
			errorsMessage.put("field", "email");
			errorsMessage.put("value", email);
			errorsMessage.put("errorMsg", lanDataService.getLanData("이메일주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
			
			errorsMessages.add(errorsMessage);
			
			errorAttribute.put("errorsMessage", errorsMessages);
			errorAttribute.put("message", "Validation failed for object='appUser'. Error count: 1");
			errorAttribute.put("path", "/api/app/chkEmail");
			
			ObjectMapper om = new ObjectMapper();
			
			String json = om.writeValueAsString(errorAttribute);
			
			res.getWriter().print(json);
			
			return null;
		} else {
			List<Account> accountList = accountService.findOneByEmail(email);
			
			// 디비상 이메일 중복은 허용 되지만 앱회원은 이메일 중복 허용 안함, 여기서 앱 회원 이메일 계정 등록 된게 있는지 체크 함
			boolean isExist = false;
			
			for (Account tmp : accountList) {
				for (Auth auth : tmp.getAuths()) {
					if (auth.getAuthId().equals("appUser")) {
						isExist = true;
						break;
					}
				}
			}
			
			Map<String, Object> result = new HashMap<>();
			result.put("isExist", isExist);
			return result;
		}
	}
	
	
	/**
	 * 회원 가입
	 * @param lanId
	 * @throws Exception 
	 */
//	@PostMapping("/registered")
//	@JsonView(AppUserControllerJsonView.class)
//	public AppUser registered(@Validated({AppUserValid.class, AccountPwValid.class }) @RequestBody AppUser user, HttpServletResponse res) throws Exception {
//		if (StringUtils.isEmpty(user.getEmail())) {
//			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//			res.setCharacterEncoding("UTF-8");
//			res.setContentType("application/json; charset=utf-8");
//			
//			Map<String, Object> errorAttribute = new HashMap<String, Object>();
//			errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//			errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
//			errorAttribute.put("error", "Bad Request");
//			
//			List<Map<String, Object>> errorsMessages = new ArrayList<>();
//			
//			Map<String, Object> errorsMessage = new HashMap<String, Object>();
//			errorsMessage.put("code", "isExist");
//			errorsMessage.put("field", "email");
//			errorsMessage.put("value", user.getEmail());
//			errorsMessage.put("errorMsg", lanDataService.getLanData("이메일주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
//			
//			errorsMessages.add(errorsMessage);
//			
//			errorAttribute.put("errorsMessage", errorsMessages);
//			errorAttribute.put("message", "Validation failed for object='appUser'. Error count: 1");
//			errorAttribute.put("path", "/api/app/chkEmail");
//			
//			ObjectMapper om = new ObjectMapper();
//			
//			String json = om.writeValueAsString(errorAttribute);
//			
//			res.getWriter().print(json);
//			
//			return null;
//		} else {
//			List<Account> accountList = accountService.findOneByEmail(user.getEmail());
//			
//			// 디비상 이메일 중복은 허용 되지만 앱회원은 이메일 중복 허용 안함, 여기서 앱 회원 이메일 계정 등록 된게 있는지 체크 함
//			boolean isExist = false;
//			
//			for (Account tmp : accountList) {
//				for (Auth auth : tmp.getAuths()) {
//					if (auth.getAuthId().equals("appUser")) {
//						isExist = true;
//						break;
//					}
//				}
//			}
//			
//			// 이메일 사용여부 체크
//			if (isExist) {
//				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//				res.setCharacterEncoding("UTF-8");
//				res.setContentType("application/json; charset=utf-8");
//				
//				Map<String, Object> errorAttribute = new HashMap<String, Object>();
//				errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
//				errorAttribute.put("error", "Bad Request");
//				
//				List<Map<String, Object>> errorsMessages = new ArrayList<>();
//				
//				Map<String, Object> errorsMessage = new HashMap<String, Object>();
//				errorsMessage.put("code", "isExist");
//				errorsMessage.put("field", "email");
//				errorsMessage.put("value", user.getEmail());
//				errorsMessage.put("errorMsg", lanDataService.getLanData("이미 회원 가입 되어 있는 이메일주소입니다.", LocaleContextHolder.getLocale()));
//				
//				errorsMessages.add(errorsMessage);
//				
//				errorAttribute.put("errorsMessage", errorsMessages);
//				errorAttribute.put("message", "Validation failed for object='appUser'. Error count: 1");
//				errorAttribute.put("path", "/api/app/registered");
//				
//				ObjectMapper om = new ObjectMapper();
//				
//				String json = om.writeValueAsString(errorAttribute);
//				
//				res.getWriter().print(json);
//				
//				return null;
//			} else {
//				return appService.registered(user);
//			}
//		}
//	}
	
	
	/**
	 * 회원 정보 조회
	 * @param accountId
	 * @throws Exception 
	 */
//	@GetMapping("/user")
//	@JsonView(AppUserControllerJsonView.class)
//	@Transactional
//	public AppUser user(Principal principal) throws Exception {
//		if (principal != null) {
//			return appUserService.findOne(principal.getName());
//		} else {
//			return null;
//		}
//	}
	
	
	/**
	 * 회원 정보 수정
	 * @param accountId
	 * @throws Exception 
	 */
//	@PostMapping("/user")
//	@Transactional
//	public void user(@Validated({AppUserValid.class, AccountPwValid.class }) @RequestBody AppUser user, Principal principal) throws Exception {
//		if (principal != null) {
//			user.setAccountId(principal.getName());
//			
//			AppUser tmp = appUserService.findOne(principal.getName());
//			
//			user.setStartDtm(tmp.getStartDtm());
//			user.setEmail(tmp.getEmail());
//			
//			// 비번 수정 되었으면 암호화 해줌
//			if (!tmp.getAccountPw().equals(user.getAccountPw())) {
////				user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
//				user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
//			}
//					
//			// 닉 네임 없을 시 이름으로 설정
//			if (StringUtils.isEmpty(user.getNickName())) {
//				user.setNickName(user.getAccountNm());
//			}
//			
//			// 이미지 저장
//			if (!StringUtils.isEmpty(user.getProfileImg()) && user.getProfileImg().contains("base64")) {
//				user.setProfileImg(fileUtil.saveFile("appUser", user.getAccountId(), user.getProfileImg()));
//			}
//			
//			// 왭 회원 권한 설정
//			Set<Auth> auths = new HashSet<Auth>();
//			Auth auth = authService.findOne("appUser");
//			auths.add(auth);
//			user.setAuths(auths);
//					
//			appUserService.save(user);
//		}
//	}
	
	
	/**
	 * 회원 정보 수정
	 * @param accountId
	 * @throws Exception 
	 */
//	@PostMapping("/push/{token}")
//	@Transactional
//	public void pushToken(@PathVariable String token, Principal principal) throws Exception {
//		if (principal != null) {
//			AppUser user = appUserService.findOne(principal.getName());
//			user.setPushToken(token);
//			appUserService.save(user);
//		}
//	}
	
	
	/**
	 * 이메일 통해서 비밀번호 찾기
	 * 
	 * @return 결과코드
	 */
//	@GetMapping("/forget/{email}")
//	public @ResponseBody Map<String, Object> forget(@PathVariable String email,
//			HttpServletResponse res, HttpServletRequest req) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		
//		// 이메일 입력여부 체크
//		if (StringUtils.isEmpty(email)) {
//			result.put("resultCode", 12);
//			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
//		// 이메일 정확성 체크
//		} else if (!ValidUtil.isEmail(email)) {
//			result.put("resultCode", 13);
//			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 정확히 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
//		} else {
//			List<AppUser> accountTmp = appUserService.findOneByEmail(email);
//			
//			// 앱 회원만 가져와서 체크함
//			AppUser account = null;
//			
//			for (AppUser tmp : accountTmp) {
//				for (Auth auth : tmp.getAuths()) {
//					if (auth.getAuthId().equals("appUser")) {
//						account = tmp;
//						break;
//					}
//				}
//			}
//			
//			if (ObjectUtils.isEmpty(account)) {
//				result.put("resultCode", 21);
//				result.put("resultMsg", lanDataService.getLanData("등록된 계정정보가 없습니다.", LocaleContextHolder.getLocale()));
//			} else {
//				result.put("resultCode", 0);
//				result.put("resultMsg", lanDataService.getLanData("비밀번호 변경 주소 이메일로 발송 되엇습니다.", LocaleContextHolder.getLocale()));
//				
//				SimpleMailMessage message = new SimpleMailMessage();
//		        message.setFrom(username);
//		        message.setTo(account.getEmail());
//		        message.setSubject(appNm);
//		        
//		        String showMsg = lanDataService.getLanData("새로운 비밀번호 변경 주소: ", LocaleContextHolder.getLocale());
//		        
//		        String serverUrl = req.getRequestURL().toString().replace(req.getRequestURI(), "");
//		        
//		        account.setTmpPassword(RandomUtil.getStringRandom(50));
//		        
//		        message.setText(showMsg + serverUrl + "/pwChangeView/" + account.getTmpPassword());
//		        
//		        sender.send(message);
//		        
//		        appUserService.save(account);
//			}
//		}
//		
//		return result;
//	}
	
	
	/**
	 * 1:1 문의 등록
	 * @throws Exception 
	 */
	@PostMapping("/inquiry")
	@Transactional
	public void inquiry(@Validated(InquiryValid.class) @RequestBody Inquiry inquiry, Principal principal) throws Exception {
		if (principal != null) {
			AppUser user = appUserService.findOne(principal.getName());
			
			inquiry.setSendDtm(new Date());
			inquiry.setAnswered(false);
			// 문의 저장
			inquiry = inquiryService.save(inquiry);
			
			// 사용자 문의 정보 조회
			List<Inquiry> inquirys = user.getInquirys();
			
			// 문의 내용 없으면 초기화
			if (ObjectUtils.isEmpty(inquirys)) {
				inquirys = new ArrayList<>();
			}
			
			inquirys.add(inquiry);
			
			// 문의 추가
			user.setInquirys(inquirys);
			
			appUserService.save(user);
		}
	}
	
	
	/**
	 * 1:1 문의 조회
	 * @throws Exception 
	 */
	@GetMapping("/inquiry/{inquiryCd}/{lanId}")
	@Transactional
	public Map<String, Object> inquirySearch(Principal principal, @PathVariable String lanId,
			@PathVariable Integer inquiryCd) throws Exception {
		appService.chkAndSettingLan(lanId);
		
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> inquirys = new ArrayList<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat(lanDataService.getLanData("yyyy년 MM월 dd일 HH:mm", LocaleContextHolder.getLocale()));
		
		if (principal != null) {
			AppUser user = appUserService.findOne(principal.getName());
			
			// 사용자 문의 정보 조회
			List<Inquiry> inquiryTmps = user.getInquirys();
			
			result.put("total", inquiryTmps.size());
			
			int jumpCnt = 0;
			boolean begin = 0 == inquiryCd ? true : false;
			
			for (Inquiry inquiryTmp : inquiryTmps) {
				// 시작점 찾기
				if (!begin) {
					jumpCnt++;
					
					if (inquiryTmp.getInquiryCd().equals(inquiryCd)) {
						begin = true;
					}
					continue;
				}
				
				// 한번에 30개 씩 내려줌
				if (inquirys.size() >= 30) {
					break;
				}
				
				jumpCnt++;
				
				Map<String, Object> tmp = new HashMap<>();
				
				tmp.put("inquiryCd", inquiryTmp.getInquiryCd());
				tmp.put("title", inquiryTmp.getTitle());
				tmp.put("content", inquiryTmp.getContent());
				tmp.put("sendDtm", sdf.format(inquiryTmp.getSendDtm()));
				tmp.put("answer", inquiryTmp.getAnswer());
				tmp.put("answered", inquiryTmp.getAnswered());
				
				inquirys.add(tmp);
			}
			
			result.put("list", inquirys);
			result.put("remain", inquiryTmps.size() - jumpCnt);
		}
		
		return result;
	}
	
	
}
