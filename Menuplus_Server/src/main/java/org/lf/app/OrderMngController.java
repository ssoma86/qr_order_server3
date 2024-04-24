package org.lf.app;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class OrderMngController {

	private static final Logger logger = LoggerFactory.getLogger(OrderMngController.class);
	
	/**
	 * @Name : login
	 * @Description : 초기 진입화면
	 * @CreateDate : 2023. 11. 20.
	 * @Creator : swlee
	 * ------------------------------------------
	 *
	 * @return
	 * @throws Exception
	 */
	//초기 페이지(자동로그인 체크)
	@GetMapping("/app/login")
	public ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		
		try {
			logger.info("========== app/login start ==========");
			accessDevice(request);
			String cookieValue = getCookie(request, "loginCookie");
			String accountId = getCookie(request, "accountId");
			
			JSONObject loginData = new JSONObject();
			JSONObject loginCheck = new JSONObject();
			
			//token 쿠키 추가
			Cookie appToken = addCookie(request, response, "appToken", request.getHeader("appToken"));
			if(appToken.getValue() != null) {
				logger.info("login appToken : " + appToken.getValue());
			}
			
			loginData.put("token", appToken.getValue());				
			loginData.put("osType", getClientOS(request.getHeader("USER-AGENT")));
			
			if(cookieValue != null && accountId != null) { //자동 로그인
				response.sendRedirect("/app/loginProc");
				return null;
				//계정검증
//				loginData.put("accountId", cookieValue);
//				loginData.put("autoChkYn", "Y");
//				loginData.put("cookieYn", "Y");
//				logger.info("accountId : " + loginData.get("accountId"));
//				logger.info("autoChkYn : " + loginData.get("autoChkYn"));
//				logger.info("cookieYn : " + loginData.get("cookieYn"));
//				logger.info("token : " + loginData.get("token"));
//				logger.info("osType : " + loginData.get("osType"));
//				
//				CloseableHttpClient httpClient = HttpClients.createSystem();
//				String body = "";
//				int httpStatusCode = -1;
//				
//				StringEntity postData = new StringEntity(JSONObject.toJSONString(loginData), "utf-8");
//				HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/loginProc"); //운영
//				
//				httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
//				httpPost.setEntity(postData);
//				HttpResponse httpResponse = httpClient.execute(httpPost);
//				httpStatusCode = httpResponse.getStatusLine().getStatusCode();
//				
//				if(httpStatusCode != 200) {
//					throw new Exception("loginCheck Error httpStatus : " + httpStatusCode);
//				} else {
//					body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
//					logger.info("loginCheck :: {}", body.trim());
//					
//					loginCheck =  (JSONObject) new JSONParser().parse(body.trim());
//				}
//				
//				if(loginCheck.get("resultAccountId") != null && "0000".equals(loginCheck.get("resultCode"))) { //자동 로그인
//					String result = autoLoginCookie(request, response, cookieValue, accountId); //쿠키값 갱신
//					if(result.equals("0000")) {
//						response.sendRedirect("/app/index");
//						return null;
//					} else {
//						throw new Exception("system Error");
//					}
//				} else if("0000".equals(loginCheck.get("resultCode")) && loginCheck.get("resultAccountId") == null) { //일반 로그인
//					logger.info("userId : " + accountId);
//					//새션 셋팅(accountId)
//					session.setAttribute("accountId", accountId);
//					response.sendRedirect("/app/index");
//					return null;
//				} else { //계정검증 실패
//					logger.info("userId : " + accountId);
//					model.addAttribute("resultCode", loginCheck.get("resultCode"));
//					
//					return ViewConstants.LOGIN;
//				}
			} else { //자동로그인 X
				
				mav.setViewName(ViewConstants.LOGIN);
				
				return mav;
				//return ViewConstants.LOGIN;
			}
			
		} catch(Exception e) {
			logger.error("login Error", e);
			
			String accountId = getCookie(request, "accountId");
			
			if(accountId != null) {
				String tokenResult = initToken(accountId);
				logger.info("tokenResult : " + tokenResult);				
			}
			
			HttpSession session = request.getSession();
			session.invalidate(); //세션초기화
			deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
			deleteCookie(response, "accountId"); //계정쿠키 초기화
			
			model.addAttribute("resultCode", "9999");
			mav.setViewName(ViewConstants.LOGIN);
			
			return mav;
			//return ViewConstants.ERROR;
		}
	}
	
	//로그인과정
	@RequestMapping("/app/loginProc")
	public String loginProc(HttpServletRequest request, HttpServletResponse response, Model model) throws Exception{
		
		try {
			logger.info("========== app/loginProc start ==========");
			//CSRF
			String pageToken = UUID.randomUUID().toString();
			HttpSession session = request.getSession();
			
			session.setAttribute("pageToken", pageToken);
			logger.info("pageToken : " + pageToken);
			
			//appToken 검증
			String ip = String.valueOf(session.getAttribute("access IP"));
			if("null".equals(ip)) {
				accessDevice(request);
				ip = String.valueOf(session.getAttribute("access IP"));
			}
			logger.info("ip : " + ip);
			if(!"211.104.172.44".equals(ip) && !"10.10.10.14".equals(ip) && !"10.10.10.16".equals(ip) && !"0:0:0:0:0:0:0:1".equals(ip)) {
				if(getCookie(request, "appToken") == null || getCookie(request, "appToken").isEmpty()) {
					throw new Exception("appToken is null");
				}				
			}
			
			//계정검증
			JSONObject loginData = new JSONObject();
			JSONObject loginCheck = new JSONObject();
			
			loginData.put("token", getCookie(request, "appToken"));
			loginData.put("osType", getClientOS(request.getHeader("USER-AGENT")));
			
			String cookieValue = getCookie(request, "loginCookie");
			String accountId = getCookie(request, "accountId");
			
			if(cookieValue != null && accountId != null) { //자동 로그인
				loginData.put("accountId", cookieValue);
				loginData.put("autoChkYn", "Y");
				loginData.put("cookieYn", "Y");
				logger.info("accountId : " + loginData.get("accountId"));
				logger.info("autoChkYn : " + loginData.get("autoChkYn"));
				logger.info("cookieYn : " + loginData.get("cookieYn"));
				logger.info("token : " + loginData.get("token"));
				logger.info("osType : " + loginData.get("osType"));
				
				CloseableHttpClient httpClient = HttpClients.createSystem();
				String body = "";
				int httpStatusCode = -1;
				
				StringEntity postData = new StringEntity(JSONObject.toJSONString(loginData), "utf-8");
				HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/loginProc"); //운영
				
				httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
				httpPost.setEntity(postData);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				
				if(httpStatusCode != 200) {
					throw new Exception("loginCheck Error httpStatus : " + httpStatusCode);
				} else {
					body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
					logger.info("loginCheck :: {}", body.trim());
					
					loginCheck =  (JSONObject) new JSONParser().parse(body.trim());
				}
				
				if(loginCheck.get("resultAccountId") != null && "0000".equals(loginCheck.get("resultCode"))) { //자동 로그인
					String result = autoLoginCookie(request, response, cookieValue, accountId); //쿠키값 갱신
					if(result.equals("0000")) {
						response.sendRedirect("/app/index");
						return null;
					} else {
						throw new Exception("system Error");
					}
				} else if("0000".equals(loginCheck.get("resultCode")) && loginCheck.get("resultAccountId") == null) { //일반 로그인
					logger.info("userId : " + accountId);
					//새션 셋팅(accountId)
					session.setAttribute("accountId", accountId);
					response.sendRedirect("/app/index");
					return null;
				} else { //계정검증 실패
					logger.info("userId : " + accountId);
					model.addAttribute("resultCode", loginCheck.get("resultCode"));
					
					return ViewConstants.LOGIN;
				}
			}
			
			String isAutoLogin = request.getParameter("autoLogin");
			String userId = request.getParameter("userId");
			String userPw = request.getParameter("userPw");
			
			if(isAutoLogin != null) {
				loginData.put("autoChkYn", "Y");
			}
			loginData.put("accountId", userId);
			loginData.put("accountPw", userPw);
			
			logger.info("accountId : " + loginData.get("accountId"));
			logger.info("autoChkYn : " + loginData.get("autoChkYn"));
			logger.info("token : " + loginData.get("token"));
			logger.info("osType : " + loginData.get("osType"));
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			StringEntity postData = new StringEntity(JSONObject.toJSONString(loginData), "utf-8");
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/loginProc"); //운영
			
			httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setEntity(postData);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("loginCheck Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("loginCheck :: {}", body.trim());
				
				loginCheck =  (JSONObject) new JSONParser().parse(body.trim());
			}
			
			if(isAutoLogin != null && loginCheck.get("resultAccountId") != null && "0000".equals(loginCheck.get("resultCode"))) { //자동 로그인
				String result = autoLoginCookie(request, response, String.valueOf(loginCheck.get("resultAccountId")), userId); //쿠키값 셋팅
				if(result.equals("0000")) {
					response.sendRedirect("/app/index");
					return null;
				} else {
					throw new Exception("system Error");
				}
			} else if("0000".equals(loginCheck.get("resultCode")) && loginCheck.get("resultAccountId") == null) { //일반 로그인
				logger.info("userId : " + userId);
				logger.info("userPw : " + userPw);
				//새션 셋팅(accountId)
				session.setAttribute("accountId", userId);
				response.sendRedirect("/app/index");
				return null;
			} else { //계정검증 실패
				logger.info("userId : " + userId);
				logger.info("userPw : " + userPw);
				model.addAttribute("resultCode", loginCheck.get("resultCode"));
				
				return ViewConstants.LOGIN;
			}
			
		} catch(Exception e) {
			logger.error("loginProc Error", e);
			
			if(e.getMessage().contains("CSRF")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = request.getParameter("userId");
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				model.addAttribute("resultCode", "9999");
				return ViewConstants.ERROR;
			}
		}
	}
	
	//로그아웃
	@GetMapping("/app/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception{
		try {
			logger.info("========== app/logout start ==========");
			HttpSession session = request.getSession();
			String accountId = String.valueOf(session.getAttribute("accountId"));
			String tokenResult = initToken(accountId);
			
			logger.info("tokenResult : " + tokenResult);
			
			session.invalidate(); //세션초기화
			deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
			deleteCookie(response, "accountId"); //계정쿠키 초기화
			
			return ViewConstants.LOGIN;
		} catch(Exception e) {
			logger.error("logout Error", e);
			
			return ViewConstants.LOGIN;
		}
		
	}
	
	//비밀번호 찾기
	@ResponseBody
	@PostMapping("/app/findUser")
	public JSONObject findUser(@RequestBody Map<String, Object> findData, HttpServletRequest request, HttpServletResponse response, Model model) throws Exception {
		JSONObject resultObj = new JSONObject();
		
		try {
			logger.info("========== app/findUser start ==========");
			//CSRF
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			
			logger.info("========== find Id ==========");
			logger.info("accountId : " + findData.get("accountId"));
			logger.info("email : " + findData.get("email"));
			logger.info("========== find Id ==========");
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			StringEntity postData = new StringEntity(JSONObject.toJSONString(findData), "utf-8");
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/findPw"); //운영
			
			httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
			httpPost.setEntity(postData);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("findObj Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("findObj :: {}", body.trim());
				
				resultObj =  (JSONObject) new JSONParser().parse(body.trim());
			}
			return resultObj;
			
		} catch(Exception e) {
			logger.error("findUser Error", e);
			
			if(e.getMessage().contains("CSRF")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				resultObj.put("resultCode", "9999");
				resultObj.put("resultMsg", "시스템오류 재시도필요");
				
				return resultObj;
			}
		}
	}
	
	//고객센터
	@GetMapping("/app/customerCenter")
	public ModelAndView customerCenter(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		try {
			logger.info("========== app/customerCenter start ==========");
			logger.info("customerCenter start");
			
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			mav.setViewName(ViewConstants.CUSTOMERCENTER);
		} catch(Exception e) {
			logger.error("customerCenter Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		
		return mav;
	}
	
	//사업장정보
	@GetMapping("/app/businessInfo")
	public ModelAndView businessInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		try {
			logger.info("========== app/businessInfo start ==========");
			logger.info("businessInfo start");
			
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			JSONObject storeInfo = new JSONObject(); //매장정보
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/storeInfo/" + accountId); //운영
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("storeInfo Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("storeInfo :: {}", body.trim());
				
				storeInfo =  (JSONObject) new JSONParser().parse(body.trim());
			}
			
			mav.addObject("storeInfo", storeInfo);
			mav.setViewName(ViewConstants.BUSINESSINFO);
		} catch(Exception e) {
			logger.error("businessInfo Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		return mav;
	}
	
	//알림 셋팅
	@GetMapping("/app/setting")
	public ModelAndView setting(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		try {
			logger.info("========== setting start ==========");
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			String token = (String) session.getAttribute("pageToken");
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			logger.info("accountId : " + accountId);
			logger.info("token : " + token);
			mav.addObject("accountId", accountId);
			mav.addObject("token", session.getAttribute("token"));
			mav.setViewName(ViewConstants.SETTING);
		} catch(Exception e) {
			logger.error("setting Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		return mav;
	}
	
	//주문관리 화면
	@GetMapping("/app/index")
	public ModelAndView indexPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		try {
			logger.info("========== app/index start ==========");
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			//appToken 검증
			String ip = String.valueOf(session.getAttribute("access IP"));
			if(!"211.104.172.44".equals(ip) && !"10.10.10.14".equals(ip) && !"10.10.10.16".equals(ip) && !"0:0:0:0:0:0:0:1".equals(ip)) {
				if(getCookie(request, "appToken") == null || getCookie(request, "appToken").isEmpty()) {
					throw new Exception("appToken is null");
				}
			}
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String startDate = sdf.format(cal.getTime()); //주문 조회일자(시작)
			
			cal.add(Calendar.DATE, 1);
			String endDate = sdf.format(cal.getTime()); //주문 조회일자(끝)
			
			
			logger.info("주문리스트 호출(accountId) : " + accountId + "[" + startDate + " ~ " + endDate + "]");
			String orderStatusCds = "28,30,32"; //주문상태
			
			JSONObject storeInfo = new JSONObject(); //매장정보
			JSONArray orderList = new JSONArray();; //주문정보
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/storeInfo/" + accountId); //운영
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("storeInfo Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("storeInfo :: {}", body.trim());
				
				storeInfo =  (JSONObject) new JSONParser().parse(body.trim());
				
				httpPost = new HttpPost("https://admin.menuplus.kr/api/app/orderlist/" + accountId + "/" + startDate+ "/" + endDate +"/" + orderStatusCds + "/A"); //운영
				httpResponse = httpClient.execute(httpPost);
				httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				
				if(httpStatusCode != 200) {
					throw new Exception("orderList Error httpStatus : " + httpStatusCode);
				} else {
					body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
					logger.info("orderList :: {}", body.trim());
					
					orderList =  (JSONArray) new JSONParser().parse(body.trim());
				}
			}

			mav.addObject("loginCookie", getCookie(request, "loginCookie"));
			mav.addObject("storeInfo", storeInfo);
			mav.addObject("orderList", orderList);
			mav.setViewName(ViewConstants.INDEX);
		} catch(Exception e) {
			logger.error("index Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.ERROR);
			}
		}
		
		return mav;
	}
	
	//매출조회
	@GetMapping("/app/salesList")
	public ModelAndView salesList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		try {
			logger.info("========== app/salesList start ==========");
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			
			String calendarDate = request.getParameter("calendarDate");
			String approveCheck = request.getParameter("approveCheck"); //승인 체크유무
			String cancelCheck = request.getParameter("cancelCheck"); //취소 체크 유무 
			String range = request.getParameter("range"); //조회일자 클릭 유무
			String status = "";
			
			if((approveCheck == null && cancelCheck == null) || ("on".equals(approveCheck) && "on".equals(cancelCheck))) {
				status = "A";
			} else if("on".equals(approveCheck) && !"on".equals(cancelCheck)) {
				status = "N";
			} else if(!"on".equals(approveCheck) && "on".equals(cancelCheck)){
				status = "Y";
			}
			
			String startDate = "";
			String endDate = "";
			String calendarStart = ""; //캘린더 표시 날짜(시작)
			String calendarEnd = ""; //캘린더 표시 날짜(끝)
			
			Date now = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Calendar cal = Calendar.getInstance();
			
			if(calendarDate != null && calendarDate != "") {
				String[] dateArray = calendarDate.split("~");
				startDate = dateArray[0].replace(".", "").replace(" ", ""); //주문 조회일자(시작)
				endDate = dateArray[1].replace(".", "").replace(" ", ""); //주문 조회일자(끝)
				
				Date date = sdf.parse(endDate);
				cal.setTime(date);
			} else {
				cal.setTime(now);
				startDate = sdf.format(cal.getTime()); //주문 조회일자(시작)
			}
			
			cal.add(Calendar.DATE, 1); //주문 조회일자(끝 + 1일)
			endDate = sdf.format(cal.getTime()); //주문 조회일자(끝 + 1일)
			cal.add(Calendar.DATE, -1); //주문 조회일자(끝  - 1일) 캘린더 표시 날짜
			
			calendarStart= startDate.substring(0, 4) + ". " + startDate.substring(4, 6) + ". " + startDate.substring(6, 8);
			
			calendarEnd = sdf.format(cal.getTime()).substring(0, 4) + ". " + 
					  sdf.format(cal.getTime()).substring(4, 6) + ". " + 
					  sdf.format(cal.getTime()).substring(6, 8);
			
			logger.info(calendarStart + " / " + calendarEnd);
			
			logger.info("주문리스트 호출(accountId) : " + accountId + "[" + startDate + " ~ " + endDate + "]");
			String orderStatusCds = "32"; //주문상태
			
			JSONObject storeInfo = new JSONObject(); //매장정보
			JSONArray orderList = new JSONArray();; //주문정보
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/storeInfo/" + accountId); //운영
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("storeInfo Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("storeInfo :: {}", body.trim());
				
				storeInfo =  (JSONObject) new JSONParser().parse(body.trim());
				
				httpPost = new HttpPost("https://admin.menuplus.kr/api/app/orderlist/" + accountId + "/" + startDate+ "/" + endDate +"/" + orderStatusCds + "/" + status); //운영
				httpResponse = httpClient.execute(httpPost);
				httpStatusCode = httpResponse.getStatusLine().getStatusCode();
				
				if(httpStatusCode != 200) {
					throw new Exception("orderList Error httpStatus : " + httpStatusCode);
				} else {
					body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
					logger.info("orderList :: {}", body.trim());
					
					orderList =  (JSONArray) new JSONParser().parse(body.trim());
				}
			}

			mav.addObject("loginCookie", getCookie(request, "loginCookie"));
			mav.addObject("storeInfo", storeInfo);
			mav.addObject("orderList", orderList);
			mav.addObject("calendarStart", calendarStart);
			mav.addObject("calendarEnd", calendarEnd);
			mav.addObject("range", range);
			mav.addObject("payStatus", status);
			mav.setViewName(ViewConstants.SALES);
			
		} catch(Exception e) {
			logger.error("salesList Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		
		return mav;
	}
	
	//주문내역 상세보기
	@GetMapping("/app/orderDetail/{orderCd}")
	public ModelAndView orderDetail(@PathVariable("orderCd") String orderCd, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		try {
			logger.info("========== app/orderDetail start ==========");
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			logger.info("accountId : " + accountId);
			logger.info("orderCd : " + orderCd);
			
			JSONObject orderData = new JSONObject(); //주문상세정보
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpGet httpGet = new HttpGet("https://admin.menuplus.kr/api/app/orderDetail/" + orderCd); //운영
			HttpResponse httpResponse = httpClient.execute(httpGet);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("orderData Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("orderData :: {}", body.trim());
				
				orderData =  (JSONObject) new JSONParser().parse(body.trim());
			}
			
			String orderStatusCd = String.valueOf(orderData.get("orderStatusCd"));

			//상세화면 분기처리
			if("28".equals(orderStatusCd)) { //작업 대기
				mav.setViewName(ViewConstants.DETAILNEW);
			} else if("30".equals(orderStatusCd)) { //작업중
				mav.setViewName(ViewConstants.DETAILWORK);
			} else if("32".equals(orderStatusCd)) { //완료
				if("N".equals(orderData.get("cancelYn"))) { //결제
					mav.setViewName(ViewConstants.DETAILCOMPLETE);
				} else { //결제취소
					mav.setViewName(ViewConstants.DETAILCANCEL);
				}
			}
			
			mav.addObject("orderData", orderData);
		} catch(Exception e) {
			logger.error("orderDetail Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		return mav;
	}
	
	//매줄조회 상세보기
	@GetMapping("/app/salesDetail")
	public ModelAndView salesDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView();
		
		try {
			logger.info("========== app/salesDetail start ==========");
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			String orderCd = request.getParameter("orderCd");
			String calendarDate = request.getParameter("calendarDate");
			String approveCheck = request.getParameter("approveCheck"); //승인 체크유무
			String cancelCheck = request.getParameter("cancelCheck"); //취소 체크 유무 
			String range = request.getParameter("range"); //조회일자 클릭 유무
			
			logger.info("accountId : " + accountId);
			
			JSONObject orderData = new JSONObject(); //주문상세정보
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpGet httpGet = new HttpGet("https://admin.menuplus.kr/api/app/orderDetail/" + orderCd); //운영
			HttpResponse httpResponse = httpClient.execute(httpGet);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("orderData Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("orderData :: {}", body.trim());
				
				orderData =  (JSONObject) new JSONParser().parse(body.trim());
			}
			
			String orderStatusCd = String.valueOf(orderData.get("orderStatusCd"));

			//상세화면 분기처리
			if("N".equals(orderData.get("cancelYn"))) { //결제
				mav.setViewName(ViewConstants.SALESCOMPLETE);
			} else { //결제취소
				mav.setViewName(ViewConstants.SALESCANCEL);
			}
			
			mav.addObject("calendarDate", calendarDate);
			mav.addObject("approveCheck", approveCheck);
			mav.addObject("cancelCheck", cancelCheck);
			mav.addObject("range", range);
			mav.addObject("orderData", orderData);
		} catch(Exception e) {
			logger.error("salesDetail Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				HttpSession session = request.getSession();
				String accountId = String.valueOf(session.getAttribute("accountId"));
				String tokenResult = initToken(accountId);
				
				logger.info("tokenResult : " + tokenResult);
				
				session.invalidate(); //세션초기화
				deleteCookie(response, "loginCookie"); //로그인쿠키 초기화
				deleteCookie(response, "accountId"); //계정쿠키 초기화
				
				mav.addObject("resultCode", "9999");
				mav.setViewName(ViewConstants.LOGIN);
			}
		}
		return mav;
	}
	
	//주문상태 변경
	@ResponseBody
	@PostMapping("/app/changeStatus")
	public JSONObject changeStatus(@RequestBody Map<String, Object> changeData, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("========== app/changeStatus start ==========");
		logger.info(changeData.toString());
		
		JSONObject resultObj = new JSONObject();
		
		try {
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			String orderCd = String.valueOf(changeData.get("orderCd"));
			String orderStatusCd = String.valueOf(changeData.get("orderStatusCd"));
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/orderStatus/" + orderCd + "/" + orderStatusCd); //운영
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("loginCheck Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("orderChange :: {}", body.trim());
				
				resultObj =  (JSONObject) new JSONParser().parse(body.trim());
			}
		} catch (IOException e) {
			logger.error("changeStatus Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				resultObj.put("resultCode", "9999");
				resultObj.put("resultMsg", "시스템오류 재시도필요");
				
				return resultObj;
			}
		}
		return resultObj;
	}
	
	//주문취소
	@ResponseBody
	@PostMapping("/app/cancelOrder")
	public JSONObject cancelOrder(@RequestBody Map<String, Object> cancelData, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.info("========== app/cancelOrder start ==========");
		logger.info(cancelData.toString());
		JSONObject cancelResult = new JSONObject();
		
		try {
			HttpSession session = request.getSession();
			
			//CSRF검증
			String pageToken = (String) session.getAttribute("pageToken");
			
			if(pageToken == null) {
				logger.info("CSRF is null");
				throw new Exception("CSRF is null");
			}
			//세션 검증
			String accountId = (String) session.getAttribute("accountId"); //로그인ID
			
			if(accountId == null) {
				logger.info("accountId is null");
				throw new Exception("accountId is null");
			}
			
			String orderCd = String.valueOf(cancelData.get("orderCd"));
			String orderId = String.valueOf(cancelData.get("orderId"));
			String cancelPw = String.valueOf(cancelData.get("cancelPw"));
			String cancelReason = String.valueOf(cancelData.get("cancelReason"));
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpGet httpGet = new HttpGet("https://admin.menuplus.kr/api/app/cancelOrder/" + orderCd + "/" + orderId + "/" + cancelPw + "/" + cancelReason); //운영
			HttpResponse httpResponse = httpClient.execute(httpGet);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("orderData Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("cancelResult :: {}", body.trim());
				
				cancelResult = (JSONObject) new JSONParser().parse(body.trim());
			}
		} catch(Exception e) {
			logger.error("cancelOrder Error", e);
			
			if(e.getMessage().contains("CSRF") || e.getMessage().contains("accountId")) {
				response.sendRedirect("/app/loginProc");
				return null;
			} else {
				cancelResult.put("resultCode", "9999");
				cancelResult.put("resultMsg", "시스템오류 재시도필요");
				
				return cancelResult;
			}
		}
		
		return cancelResult;
	}
	
	//토큰 초기화
	public String initToken(String accountId) {
		try {
			
			logger.info("========== app/initToken start ==========");
			logger.info("accountId : " + accountId);
			
			JSONObject resultObj = new JSONObject(); //초기화 결과
			
			CloseableHttpClient httpClient = HttpClients.createSystem();
			String body = "";
			int httpStatusCode = -1;
			
			HttpPost httpPost = new HttpPost("https://admin.menuplus.kr/api/app/logOut/" + accountId); //운영
			HttpResponse httpResponse = httpClient.execute(httpPost);
			httpStatusCode = httpResponse.getStatusLine().getStatusCode();
			
			if(httpStatusCode != 200) {
				throw new Exception("initToken Error httpStatus : " + httpStatusCode);
			} else {
				body = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
				logger.info("resultObj :: {}", body.trim());
				
				resultObj =  (JSONObject) new JSONParser().parse(body.trim());
				return String.valueOf(resultObj.get("resultCode"));
			}
		} catch(Exception e) {
			logger.error("initToken Error", e);
			return "9999";
		}
	}
	
	//자동로그인 쿠키갱신
	public String autoLoginCookie(HttpServletRequest request, HttpServletResponse response, String cookieValue, String accountId) {
		try {
			//쿠키 업데이트(loginCookie)
			Cookie loginCookie = addCookie(request, response, "loginCookie", cookieValue);
			//쿠키 업데이트(accountId)
			Cookie accountIdCookie = addCookie(request, response, "accountId", accountId);
			//세션 셋팅(accountId)
			request.getSession().setAttribute("accountId", accountId);
			
			String loginExpireDate = "";
			String accountIdExpireDate = "";
			Date CookieDate = new Date();
			
			SimpleDateFormat cookieDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			
			cal.setTime(CookieDate);
			cal.add(Calendar.SECOND, loginCookie.getMaxAge());
			cal.add(Calendar.SECOND, accountIdCookie.getMaxAge());
			
			loginExpireDate = cookieDateFormat.format(cal.getTime());
			accountIdExpireDate = cookieDateFormat.format(cal.getTime());
			
			logger.info("==================== 쿠키정보(loginCookie) ====================");
			logger.info("쿠키셋팅 값 : " + loginCookie.getValue());
			logger.info("쿠키셋팅 유효기간 : " + loginExpireDate);
			logger.info("쿠키셋팅 경로 : " + loginCookie.getPath());
			logger.info("==================== 쿠키정보(loginCookie) ====================");
			
			logger.info("==================== 쿠키정보(accountId) ====================");
			logger.info("쿠키셋팅 값 : " + accountIdCookie.getValue());
			logger.info("쿠키셋팅 유효기간 : " + accountIdExpireDate);
			logger.info("쿠키셋팅 경로 : " + accountIdCookie.getPath());
			logger.info("==================== 쿠키정보(accountId) ====================");
			
			return "0000";
		} catch (Exception e) {
			logger.error("autoLoginCookie Error", e);
			return "9999";
		}
	}
	
	//쿠키셋팅
	@ResponseBody
	@PostMapping("/app/addCookie")
	public Cookie addCookie(HttpServletRequest request, HttpServletResponse response, String cookieKey, String cookieValue) throws Exception{
		logger.info("========== app/addCookie start ==========");
		logger.info("cookieKey : " + cookieKey);
		logger.info("cookieValue : " + cookieValue);
			
		try {
			//쿠키 생성
			Cookie cookieData = new Cookie(cookieKey, cookieValue);
			cookieData.setPath("/app");
			cookieData.setHttpOnly(true);
			//쿠키 제한시간 설정
			cookieData.setMaxAge(60*60*24*7); //7일
			
			//응답헤더에 쿠키 추가
			response.addCookie(cookieData);
			
			//응답헤더 인코딩 및 Content-Type 설정
			//response.setCharacterEncoding("utf-8");
			//response.setHeader("Set-Cookie", cookieData.toString());
				
			return cookieData;
		} catch (Exception e) {
			logger.error("addCookie Error : " + e.getMessage());
			return null;
		}
	}
	
	//쿠키조회
	public String getCookie(HttpServletRequest req, String cookieKey){
	    Cookie[] cookies=req.getCookies(); // 모든 쿠키 가져오기
	    if(cookies!=null){
	        for (Cookie c : cookies) {
	            String name = c.getName(); // 쿠키 이름 가져오기
	            String value = c.getValue(); // 쿠키 값 가져오기
	            if (name.equals(cookieKey)) {
	                return value;
	            }
	        }
	    }
	    return null;
	}

	public String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
	
	//쿠키 만료
	public void deleteCookie(HttpServletResponse res, String cookieKey){
	    Cookie cookie = new Cookie(cookieKey, null); // 삭제할 쿠키에 대한 값을 null로 지정
	    cookie.setMaxAge(0); // 유효시간을 0으로 설정해서 바로 만료시킨다.
	    res.addCookie(cookie); // 응답에 추가해서 없어지도록 함
	}
	
	//접근 디바이스 정보
	public static void accessDevice(HttpServletRequest request) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String accessDate = sdf.format(cal.getTime());
		
		String agent = request.getHeader("USER-AGENT");

		String os = getClientOS(agent);
		String browser = getClientBrowser(agent);
		String ip = (String) request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || ip.toLowerCase().equals("unknown")) ip = (String) request.getRemoteAddr();

		logger.info("========== access device ==========");
		logger.info("accessDate : " + accessDate);
		logger.info("IP : " + ip);
		logger.info("header : " + agent);
		logger.info("OS : " + os);
		logger.info("browser : " + browser);
		logger.info("========== access device ==========");
		
		HttpSession session = request.getSession();
		session.setAttribute("access IP", ip);
		session.setAttribute("access Header", agent);
		session.setAttribute("access OS", os);
		session.setAttribute("access browser", browser);
	}

	//OS정보
	public static String getClientOS(String userAgent) {
		String os = "";
		userAgent = userAgent.toLowerCase();
		
		if (userAgent.indexOf("windows nt 10.0") != -1) {
			os = "Windows10";
		} else if (userAgent.indexOf("windows nt 6.1") != -1) {
			os = "Windows7";                           
		} else if (userAgent.indexOf("windows nt 6.2") != -1 || userAgent.indexOf("windows nt 6.3") != -1) {
			os = "Windows8";                           
		} else if (userAgent.indexOf("windows nt 6.0") != -1) {
			os = "WindowsVista";                       
		} else if (userAgent.indexOf("windows nt 5.1") != -1) {
			os = "WindowsXP";                          
		} else if (userAgent.indexOf("windows nt 5.0") != -1) {
			os = "Windows2000";                        
		} else if (userAgent.indexOf("windows nt 4.0") != -1) {
			os = "WindowsNT";
		} else if (userAgent.indexOf("windows 98") != -1) {
			os = "Windows98";                      
		} else if (userAgent.indexOf("windows 95") != -1) {
			os = "Windows95";
		} else if (userAgent.indexOf("iphone") != -1) {
			os = "iPhone";
		} else if (userAgent.indexOf("ipad") != -1 || userAgent.indexOf("macintosh") != -1) {
			os = "iPad";
		} else if (userAgent.indexOf("android") != -1) {
			os = "android";
		} else if (userAgent.indexOf("linux") != -1) {
			os = "Linux";
		} else {
			os = "Other";
		}
		
		return os;
	}
	
	//브라우저 정보
	public static String getClientBrowser(String userAgent) {
		String browser = "";
		userAgent = userAgent.toLowerCase();

		if (userAgent.indexOf("trident/7.0") != -1) {
			browser = "ie11";
		} else if (userAgent.indexOf("msie 10") != -1) {
			browser = "ie10";
		} else if (userAgent.indexOf("msie 9") != -1) {
			browser = "ie9";
		} else if (userAgent.indexOf("msie 8") != -1) {
			browser = "ie8";
		} else if (userAgent.indexOf("chrome/") != -1 && userAgent.indexOf("edg/") == -1) {
			browser = "Chrome";
		} else if (userAgent.indexOf("safari/") != -1 && userAgent.indexOf("edg/") == -1) {
			browser = "Safari";
		} else if (userAgent.indexOf("firefox/") != -1 && userAgent.indexOf("edg/") == -1) {
			browser = "Firefox";
		} else if (userAgent.indexOf("edg/") != -1) {
			browser = "Edg";
		} else {
			browser = "Other";
		}
		
		return browser;
	}

}
