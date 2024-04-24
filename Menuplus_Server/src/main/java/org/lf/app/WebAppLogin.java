package org.lf.app;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.codec.binary.Base64;
import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.action.Action;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.history.History;
import org.lf.app.models.system.history.HistoryService;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.RandomUtil;
import org.lf.app.utils.validation.ValidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import kr.co.infinisoft.menuplus.util.CommonUtil;
import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 로그인 처리 컨트롤
 * 
 * @author LF
 *
 */
@Controller
@Transactional
@SessionAttributes({ "sa_account", "sa_show_nm", "sa_actions" })
public class WebAppLogin {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(WebAppLogin.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private HistoryService historyService;
	
	@Value("${spring.application.name}")
	private String appNm;

	@Value("${spring.mail.username}")
	private String username;

	@Autowired
	private JavaMailSenderImpl sender;

	@Autowired(required=false)
	private IMessageService lanDataService;
	
	
	
	/**
	 * 로그인 페이지
	 * 
	 * @return /?/login.html
	 * @throws IOException 
	 */
	@GetMapping("/")
	public void login(@RequestParam(required = false) Boolean failure, Model model, HttpSession session, HttpServletResponse response) throws IOException {
		//TODO#
		//개발
//		response.sendRedirect("http://172.16.10.6:8181/mer/Login.do");
		//운영
		response.sendRedirect("https://admin.innopay.co.kr/mer/Login.do");
	}
	
	//메뉴플러스 로그인페이지
	@GetMapping("/login")
	public String loginPage(@RequestParam(required = false) Boolean failure, Model model, HttpSession session, HttpServletResponse response) throws IOException {
		//TODO#
		String token = UUID.randomUUID().toString();
		session.setAttribute("token", token);
		model.addAttribute("token", token);
		
		return "main/login";
	}
	
	/**
	 * 로그인 처리(메뉴플러스 로그인페이지)
	 * @return /main/main.html
	 */
	@RequestMapping(value="/loginProc", method={RequestMethod.GET, RequestMethod.POST})
	public String loginProc(HttpSession session, Model model, HttpServletRequest request ) {
		
		//token 체크
//		if(request.getParameter("_csrf") == null ||
//		   request.getParameter("_csrf") == "" ||
//		  !request.getParameter("_csrf").equals(session.getAttribute("token"))) {
//			System.out.println("not match token");
//			model.addAttribute("resultMsg", "잘못된 접근 경로입니다.");
//			return "error/error";
//		}
		
		Account account1 = null;
		
		if(null != session.getAttribute("sa_account")) {
			account1 = (Account)session.getAttribute("sa_account");
		}
		
		Account account = null;		
		String id = request.getParameter("accountId");
		if(null != id) { 
			
			logger.info("=================================id:" + id);
			
			account = accountService.findAccountByAccountId(id);
			//account = accountService.findOne((Serializable) id);
			
			//ID 검증
			if(account == null) {
				logger.info("not match userId : " + request.getParameter("accountId"));
				model.addAttribute("resultMsg", "사용자 아이디가 없음. 확인 후 다시 입력하세요.");
				return "error/error";
			}
			
			logger.info("=================================account.getAccountId():" + account.getAccountId());
			
			logger.info("================request.getParameter(\"accountPw\")" + request.getParameter("accountPw"));
			logger.info("================account.getAccountPw()" + account.getAccountPw());
			logger.info("================CommonUtil.getHexCodeMD5(request.getParameter(\"accountPw\"))" + CommonUtil.getHexCodeMD5(request.getParameter("accountPw")));
			
			//PW 검증	
			if(!CommonUtil.getHexCodeMD5(request.getParameter("accountPw")).equals(account.getAccountPw())) {
				logger.info("not match PassWord");
				model.addAttribute("resultMsg", "패스워드 오류. 확인 후 다시 입력하세요.");
				return "error/error";
			}
			
		}else {
			account = account1;
		}

		History history = new History();
		history.setSessionId(request.getSession().getId());
		history.setInIp(request.getRemoteHost());
		
		logger.info(request.getRemoteHost());
		
		history.setInDtm(new Date());
		history.setAccount(account);
    	
		historyService.save(history);
		
		
		
		// Session에 로그인 정보 설정
		model.addAttribute("sa_account", account);
		model.addAttribute("sa_show_nm", " ");
		
		List<String> actions = new LinkedList<>();
		
		// 권한 설정
		account.getAuths().forEach(auth -> {
			auth.getResources().forEach(res -> {
				if (res instanceof Action) {
					Action action = (Action) res;
					
					if (!actions.contains(action.getActionId())) {
						actions.add(action.getActionId());
					}
				}
			});
		});
		
		model.addAttribute("sa_actions", actions);
		
		model.addAttribute("storeId", "NO_ID");
		model.addAttribute("isOrderNoti", false);
		
		Iterator<Auth> auths = account.getAuths().iterator();
		
		while (auths.hasNext()) {
			String authId = auths.next().getAuthId();
			
			logger.info("=================================authId:" + authId);
			
			if ("custMng".equals(authId)) {
				User user = userService.findOne(account.getAccountId());
				model.addAttribute("sa_show_nm", user.getCust().getCustNm());
				break;
			}
			
			if ("storeMng".equals(authId)) {
				User user = userService.findOne(account.getAccountId());
				model.addAttribute("storeId", user.getStore().getStoreId());
				model.addAttribute("isOrderNoti", user.getStore().isOrderNoti());
				model.addAttribute("sa_show_nm", user.getStore().getStoreNmLan());
				break;
			}
		}
		
		return "main/main";
	}
	
	/**
	 * 로그인 설공
	 * @return /main/main.html
	 */
	@PostMapping("/main")
	public String main(Model model, HttpServletRequest request) {

		String uid = request.getParameter("menuplus");
		byte[] decodedBytes = Base64.decodeBase64(uid);
		String id = new String(decodedBytes);
		Account account = accountService.findOne((Serializable) id);

		History history = new History();
		history.setSessionId(request.getSession().getId());
		history.setInIp(request.getRemoteHost());
		logger.info(request.getRemoteHost());
		history.setInDtm(new Date());
		history.setAccount(account);
    	
		historyService.save(history);
		
		// Session에 로그인 정보 설정
		model.addAttribute("sa_account", account);
		model.addAttribute("sa_show_nm", " ");
		
		List<String> actions = new LinkedList<>();
		
		// 권한 설정
		account.getAuths().forEach(auth -> {
			auth.getResources().forEach(res -> {
				if (res instanceof Action) {
					Action action = (Action) res;
					
					if (!actions.contains(action.getActionId())) {
						actions.add(action.getActionId());
					}
				}
			});
		});
		
		model.addAttribute("sa_actions", actions);
		
		model.addAttribute("storeId", "NO_ID");
		model.addAttribute("isOrderNoti", false);
		
		Iterator<Auth> auths = account.getAuths().iterator();
		
		while (auths.hasNext()) {
			String authId = auths.next().getAuthId();
			
			if ("custMng".equals(authId)) {
				User user = userService.findOne(account.getAccountId());
				model.addAttribute("sa_show_nm", user.getCust().getCustNm());
				break;
			}
			
			if ("storeMng".equals(authId)) {
				User user = userService.findOne(account.getAccountId());
				model.addAttribute("storeId", user.getStore().getStoreId());
				model.addAttribute("isOrderNoti", user.getStore().isOrderNoti());
				model.addAttribute("sa_show_nm", user.getStore().getStoreNmLan());
				break;
			}
		}
		
		return "main/main";
	}
	
	
	/**
	 * 로그아웃
	 * 
	 * @return index.html
	 */
	@GetMapping("/logout")
	public String logout(History history, @SessionAttribute(value = "sa_account", required = false) Account account) {
		if (null != account) {
			// 로그인 정보 가져오기
			WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails)SecurityContextHolder.getContext().getAuthentication().getDetails();
			
			history.setSessionId(webAuthenticationDetails.getSessionId());	// Spring security를 사용 시 생성한 SessionId와 HttpSession에 저장된건 같지 않다 Spring security 기준으로 함
			history.setInIp(webAuthenticationDetails.getRemoteAddress());
			history.setInDtm(new Date());
			history.setAccount(account);
	    	
			historyService.save(history);
		}
		
		return "main/login";
	}
	
	
	/**
	 * 이메일 통해서 비밀번호 찾기
	 * 
	 * @return 결과코드
	 */
	@PostMapping("/forget")
	@Transactional
	public @ResponseBody Map<String, Object> forget(@RequestBody Account account, HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 계정 ID 입력 여부 체크
		if (StringUtils.isEmpty(account.getAccountId())) {
			result.put("resultCode", 11);
			result.put("resultMsg", lanDataService.getLanData("아이디를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		// 이메일 입력여부 체크
		} else if (StringUtils.isEmpty(account.getEmail())) {
			result.put("resultCode", 12);
			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		// 이메일 정확성 체크
		} else if (!ValidUtil.isEmail(account.getEmail())) {
			result.put("resultCode", 13);
			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 정확히 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		} else {
			Account accountTmp = accountService.findOne(account.getAccountId());
			
			if (null == accountTmp) {
				result.put("resultCode", 21);
				result.put("resultMsg", lanDataService.getLanData("등록된 계정정보가 없습니다.", LocaleContextHolder.getLocale()));
			} else if (!account.getEmail().equals(account.getEmail())) {
				result.put("resultCode", 22);
				result.put("resultMsg", lanDataService.getLanData("등록된 계정정보가 없습니다.", LocaleContextHolder.getLocale()));
			} else {
				result.put("resultCode", 0);
				result.put("resultMsg", lanDataService.getLanData("비밀번호 변경 주소 이메일로 발송 되엇습니다.", LocaleContextHolder.getLocale()));
				
				SimpleMailMessage message = new SimpleMailMessage();
		        message.setFrom(username);
		        message.setTo(account.getEmail());
		        message.setSubject(appNm);
		        
		        String showMsg = lanDataService.getLanData("새로운 비밀번호 변경 주소: ", LocaleContextHolder.getLocale());
		        
		        String serverUrl = req.getRequestURL().toString().replace(req.getRequestURI(), "");
		        
		        accountTmp.setTmpPassword(RandomUtil.getStringRandom(50));
		        
		        message.setText(showMsg + serverUrl + "/pwChangeView/" + accountTmp.getTmpPassword());
		        
		        sender.send(message);
		        
		        accountService.save(accountTmp);
			}
		}
		
		return result;
	}

	
	/**
	 * 이메일 통해서 비밀번호 찾기
	 * 
	 */
	@GetMapping("/pwChangeView/{cd}")
	public ModelAndView pwChangeView(@PathVariable String cd) {
		ModelAndView mav = new ModelAndView("main/pw");
		mav.addObject("cd", cd);
		return mav;
	}
	
	
	/**
	 * 이메일 통해서 비밀번호 찾기
	 * 
	 * @return 결과코드
	 * @throws Exception 
	 */ 
	@PostMapping("/pwChange")
	@Transactional
	public @ResponseBody String pwChange(@RequestBody Account acct) throws Exception {
		Account account = accountService.findOneByTmpPassword(acct.getTmpPassword());
		
		logger.info("=================acct.getTmpPassword():" + acct.getTmpPassword());
		logger.info("=================acct.getAccountPw():" + acct.getAccountPw());
		
		if (!ObjectUtils.isEmpty(account)) {
//			account.setAccountPw(new BCryptPasswordEncoder().encode(pw));
			account.setAccountPw(new InnopayPasswordEncoder().encode(acct.getAccountPw()));
			account.setTmpPassword(null);
			
			accountService.save(account);
		} else {
			return lanDataService.getLanData("인증 코드 오류.", LocaleContextHolder.getLocale());
		}
		
		return lanDataService.getLanData("비밀번호 변경 되었습니다.", LocaleContextHolder.getLocale());
	}
	
	/**
	 * 메세지팝업처리(23.11.30)
	 * @param model
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="/message", method={RequestMethod.GET, RequestMethod.POST})
	public String messagePopup(Model model, HttpServletRequest request) throws Exception {
		
		String orderCnt = request.getParameter("orderCnt");
		String msg = "";
		
		msg = msg + orderCnt + "건의 주문이 대기중입니다.";		
		model.addAttribute("msg", msg);
		
		return "main/message";
	}
	
}
