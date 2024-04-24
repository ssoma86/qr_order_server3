package org.lf.app.service.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.history.History;
import org.lf.app.models.system.history.HistoryService;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.DateUtil;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;

import kr.co.infinisoft.menuplus.util.AESCrypto;
import kr.co.infinisoft.menuplus.util.CommonUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 
 * 주문관리 앱관련 api 
 * 2023.11.10
 * @author ykpark
 *
 */
@RestController
@RequestMapping("/api/app")
public class AppOrderController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(AppOrderController.class);
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private HistoryService historyService;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CodeService codeService;
	
	@Value("${spring.application.name}")
	private String appNm;

	@Value("${spring.mail.username}")
	private String username;

	@Autowired
	private JavaMailSenderImpl sender;
	
	@Autowired(required=false)
	private IMessageService lanDataService;
	
	@Value("${crypto_key}") 
	private String crypto_key;
	
	// JsonView
	public interface AppOrderControllerJsonView extends LanControllerCommonJsonView, CodeControllerCommonJsonView {}

	/**
	 * 로그인처리
	 * @param accountId
	 * @param accountPw
	 * @return
	 */
	@PostMapping("/loginProc")
	@JsonView(AppOrderControllerJsonView.class)
	public Map<String, Object> loginProc(@RequestBody Account acct)  throws IOException {
		
		Account account = null;
		Map<String, Object> result = new HashMap<>();
		
		if(null!=acct.getCookieYn() && acct.getCookieYn().equals("Y")) { //cookie에 있는 accountId정보를 보낸경우
			
			String tempAccountId = null;
			try {
				AESCrypto aes = new AESCrypto(crypto_key);
				logger.info("===========acct.getAccountId():" + acct.getAccountId());
				tempAccountId = aes.AesECBDecode(acct.getAccountId());  //암호화된 accountId를 복호화 한다.
				logger.info("=======DECODE====tempAccountId:" + tempAccountId);
				
			}catch(Exception e) {
				logger.info(e.getMessage());
			}
			
			account = accountService.findOne((Serializable) tempAccountId); //복호화된 accountId를 검증한다.
			
			if(account == null) {
				result.put("resultCode", "0001");
				result.put("resultMsg","사용자 아이디가 없음. 확인 후 다시 입력하세요.");
				return result;
				
			}else {
				result.put("resultCode", "0000");
				result.put("resultMsg","로그인 성공");
				result.put("resultAccountId", acct.getAccountId());
			}
			
		}else{
			account = accountService.findOne((Serializable)acct.getAccountId());
			
			//ID 검증
			if(account == null) {
				logger.info("not match userId : " + acct.getAccountId());
				result.put("resultCode", "0001");
				result.put("resultMsg","사용자 아이디가 없음. 확인 후 다시 입력하세요.");
				return result;
				
			}			
			
			//PW 검증	
			if(!CommonUtil.getHexCodeMD5(acct.getAccountPw()).equals(account.getAccountPw())) {
				logger.info("not match PassWord");
				result.put("resultCode", "0002");
				result.put("resultMsg","패스워드 오류. 확인 후 다시 입력하세요.");
				return result;
			}	
			
			result.put("resultCode", "0000");
			result.put("resultMsg","로그인 성공");
			
			if(null!=acct.getAutoChkYn() && acct.getAutoChkYn().equals("Y")){
				String encAccountId = null;
				try {
					AESCrypto aes = new AESCrypto(crypto_key);
					encAccountId = aes.AesECBEncode(acct.getAccountId());    //accountId를 암호화 한다.					
					logger.info("=======encAccountId====encAccountId:" + encAccountId);					
					result.put("resultAccountId", encAccountId);			 //암호화된 accountId를 세팅하여 앱으로 보낸다.
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
				
			}
			
		}
		
		String sTempEncId = null;
		
		logger.info("===========acct.getAutoChkYn():" + acct.getAutoChkYn());	
		logger.info("===========acct.getAccountId():" + acct.getAccountId());
		if(null!=acct.getAutoChkYn() && acct.getAutoChkYn().equals("Y")){	 //자동로그인인 경우	
			if(null!=acct.getCookieYn() && acct.getCookieYn().equals("Y")) { //cookie에 있는 accountId정보를 보낸경우
				try {
					AESCrypto aes = new AESCrypto(crypto_key);
					sTempEncId = aes.AesECBDecode(acct.getAccountId());  //암호화된 accountId를 복호화 한다.				
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}else {
				sTempEncId = acct.getAccountId();
			}
			
		}else{
			sTempEncId = acct.getAccountId();
		}
		
		logger.info("===========sTempEncId:" + sTempEncId);	
		logger.info("===========acct.getOsType():" + acct.getOsType());	
		logger.info("===========acct.getToken():" + acct.getToken());	
		
		accountService.updatePushInfo4AccountId(acct.getToken(), acct.getOsType(), sTempEncId); //로그인 성공시 push 알림관련 정보 업데이트

		History history = new History();
		
		history.setInDtm(new Date());
		history.setAccount(account);
		history.setDeviceType("app"); //app에서 접속
    	
		historyService.save(history); //접속로그 저장
		
		return result;
	
	}	
	
	/**
	 * 로그아웃시 토큰 값 초기화
	 * @param accountId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/logOut/{accountId}")
	@JsonView(AppOrderControllerJsonView.class)
	public Map<String, Object> delToken4AccountId(@PathVariable String accountId){
		
		Map<String, Object> result = new HashMap<>();
		
		try {
			accountService.updatePushInfo4AccountId(null, null, accountId); //로그아웃시  token값  null로 변경
			
			result.put("resultCode", "0000");
			result.put("resultMsg","토큰 초기화 성공");
			
		}catch(Exception e) {
			logger.info(e.getMessage());
			
			result.put("resultCode", "0001");
			result.put("resultMsg","토큰 초기화 실패");
			
		}
		
		return result;
		
	}
	
	/**
	 * 주문리스트 조회
	 * 
	 * @throws IOException 
	 */
	@PostMapping("/orderlist/{accountId}/{startDate}/{endDate}/{orderStatusCds}/{cancelYn}")
	@JsonView(AppOrderControllerJsonView.class)
	public List<Map> getOrderList(@PathVariable String accountId
									, @PathVariable String startDate
									, @PathVariable String endDate
									, @PathVariable String orderStatusCds
									, @PathVariable String cancelYn) throws IOException {
		
		User user = userService.findOne(accountId);
		
		String[] orderStatusCd = orderStatusCds.split(",");		
		List<Integer> orderStatusCdList = new ArrayList<>();
		
		for (String cd : orderStatusCd) {
			orderStatusCdList.add(Integer.parseInt(cd));			
		}
		
		//cancelYn 값에  상관없이 리스트 가져오기 위한 조건 세팅
		if(null != cancelYn && cancelYn.equals("A")) {
			cancelYn = null;
		}
		
		List<Order> orderList = orderService.findOrderListForMngUser(user.getStore().getStoreCd(), startDate, endDate, cancelYn, "N", orderStatusCdList);
		
		Map<String, Object> result = null;
		List<Map> resultList = new ArrayList<>();
		JSONArray array = null;
		
		for(Order order : orderList) {			
			
			result = new HashMap<>();
			array = new JSONArray();

			for( OrderSmenu order5 : order.getSmenus()) {
				try {
					Gson gson = new Gson();
			        String reqStr = gson.toJson(order5);
			        JSONObject reqJson = (JSONObject) new JSONParser().parse(reqStr);    
			        
			        reqJson.put("smenuNmLan", order5.getSmenuNmLan());
			        
			        array.add(reqJson);
			        
				}catch(Exception e){
					e.getStackTrace();
				}
			}        
			
			result.put("orderCd", order.getOrderCd());
			result.put("orderId", order.getOrderId());
			result.put("orderStatusCd", order.getOrderStatus().getCd());      	//주문상태값
			result.put("cancelYn", order.getCancelYn());                      	//취소여부 
			result.put("orderDate", order.getOrderDate());                    	//주문일자 
			result.put("Amount", order.getAmt());                             	//주문금액 
			result.put("guestName", order.getGuestName());                    	//주문자이름
			result.put("smenus", array);										//메뉴정보
			result.put("storeRoomNm", order.getStoreRoom().getStoreRoomNm()); 	//객실명	
			result.put("waitTime", order.getWaitTime());						//대기시간 (24.02.14 추가)
			
			resultList.add(result);
			
		}
		
		return resultList;
		
	}	
	
	/**
	 * 주문상세정보 가져오기
	 * @param orderCd
	 * @return
	 * @throws IOException
	 */
	@GetMapping("/orderDetail/{orderCd}")
	@JsonView(AppOrderControllerJsonView.class)
	public Map getOrderDetail(@PathVariable Integer orderCd) throws IOException {
		
		Order order = orderService.findOne(orderCd);
		
		Map<String, Object> result = new HashMap<>();
		JSONArray array = new JSONArray();
		
		for( OrderSmenu orderSmenu : order.getSmenus()) {
			try {
				Gson gson = new Gson();
		        String reqStr = gson.toJson(orderSmenu);
		        JSONObject reqJson = (JSONObject) new JSONParser().parse(reqStr);   
		        
		        array.add(reqJson);
		        
			}catch(Exception e){
				e.getStackTrace();
			}
		}        
		
		result.put("orderCd", order.getOrderCd());
		result.put("orderId", order.getOrderId());
		result.put("orderStatusCd", order.getOrderStatus().getCd());		//주문상태값
		result.put("cancelYn", order.getCancelYn());						//취소여부
		result.put("orderDate", order.getOrderDate());						//주문일자
		result.put("Amount", order.getAmt());								//주문금액
		result.put("guestName", order.getGuestName());						//주문자이름
		result.put("smenus", array);										//메뉴정보
		result.put("storeRoomNm", order.getStoreRoom().getStoreRoomNm());	//객실명
		
		if(null!=order.getCancelDate()) {
			result.put("cancelDate", order.getCancelDate());					//취소일자
		}
		
		if(null!=order.getCancelReason()) {
			result.put("cancelReason", order.getCancelReason().getCd());		//취소사유 code값
		}
		
		String tempTel = "";
		
		if(null != order.getTel()) {			
			if(order.getTel().length() == 10) {
				tempTel = order.getTel().substring(0,3) + "-" + "***" + "-" + order.getTel().substring(6);
			}else {
				tempTel = order.getTel().substring(0,3) + "-" + "****" + "-" + order.getTel().substring(7);
			}
		}
		
		result.put("orderTel", tempTel);									//주문자전화번호
		result.put("waitTime", order.getWaitTime());						//대기시간 (24.02.14 추가)
		
		return result;
		
	}
	
	/**
	 * 매장정보 가져오기
	 * @param accountId
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/storeInfo/{accountId}")
	@JsonView(AppOrderControllerJsonView.class)
	public Map<String, Object> getStoreInfo(@PathVariable String accountId) throws IOException {
		
		User user = userService.findOne(accountId);
		Store store = storeService.findOne(user.getStore().getStoreCd());
		store.getBusiNum();
		store.getStoreNmLan();
		store.getTel();
		store.getCeoNm();
		store.getStoreAddrLan();
		
		Map<String, Object> result = new HashMap<>();
		result.put("busiNum", store.getBusiNum());
		result.put("storeNm", store.getStoreNmLan());
		result.put("tel", store.getTel());
		result.put("ceoNm", store.getCeoNm());
		result.put("addr", store.getStoreAddrLan());
		
		return result;
	}
	
	/**
	 * 주문 상태값 변경
	 * 대기 시간  waitTime 추가(24.02.15)
	 * @param orderCd
	 * @param orderStatusVal
	 * @param accountId
	 * @throws IOException
	 */
	@PostMapping("/orderStatus/{orderCd}/{orderStatusCd}/{waitTime}")
	@JsonView(AppOrderControllerJsonView.class)
	public Map<String, Object> updateOrderStatus(@PathVariable Integer orderCd, @PathVariable Integer orderStatusCd, @PathVariable String waitTime) throws IOException {
		
		Order order = orderService.findOne(orderCd);
		
		Map<String, Object> result = new HashMap<>();
		
		try {
			Code code = codeService.findOne(orderStatusCd);
			order.setOrderStatus(code);
			
			if(orderStatusCd == 30) { //준비중 상태일 때만  대기 시간을 세팅한다.
				order.setWaitTime(waitTime);
			}
			
			orderService.save(order);
			
			result.put("resultCode", "0000");
			result.put("resultMsg","주문 상태 변경 성공");
			
		}catch(Exception e) {
			logger.info(e.getMessage());
			
			result.put("resultCode", "0001");
			result.put("resultMsg","주문 상태 변경 실패");
			
		}
		
		return result;
		
	}
	
	/**
	 * 주문 취소
	 * 
	 * @param orderCds
	 * @throws Exception 
	 */
	@GetMapping("/cancelOrder/{orderCd}//{orderId}//{cancelPw}/{cancelReason}")
	public JSONObject cancelOrder(@PathVariable int orderCd, @PathVariable String orderId, @PathVariable String cancelPw, @PathVariable String cancelReason) throws Exception {
		
		Order order = orderService.findOne(orderCd);
		HttpURLConnection conn = null;
		JSONObject responseJson =  new JSONObject();
		
		if ("N".equals(order.getCancelYn())) {
			// 결제 되고 미 취소 인거만 결제 취소
			if ("Y".equals(order.getPayYn()) && "N".equals(order.getCancelPay())) {
				
				if(order.getAmt() != 0) { //거래금액이 0원 아닌경우
				
					Order od = orderService.findByOid(orderId);
					
					URL url = new URL("https://api.innopay.co.kr/api/cancelApi");
			        conn = (HttpURLConnection) url.openConnection();
			        
			        //Request 형식 설정
			        conn.setRequestMethod("POST");
			        conn.setRequestProperty("Content-Type", "application/json");
			     
			        //request에 JSON data 준비
			        conn.setDoOutput(true);
			        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			        
			        //commands라는 JSONArray를 담을 JSONObject 생성
			        JSONObject json = new JSONObject();
			        json.put("mid", od.getMid());
			        json.put("tid", od.getTid());
			        json.put("svcCd", od.getSvcCd());
			        json.put("partialCancelCode", "0");
			        json.put("cancelAmt", od.getAmt());
			        json.put("cancelMsg", "Menuplus Cancel Pay");	
			        json.put("cancelPwd", cancelPw);
			        
			        //request에 쓰기
			        bw.write(json.toString());
			        bw.flush();
			        bw.close();
			      
		            String responseMsg = conn.getResponseMessage();
			        int responseCode = conn.getResponseCode();
			        
			        //보내고 결과값 받기
			        logger.info("===================ResponseCode: "+responseCode);
			        logger.info("===================ResponseMsg: "+responseMsg);
			        
			        if (responseCode == 200) {
			            // 성공 후 응답 JSON 데이터받기
			            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
			            StringBuilder sb = new StringBuilder();
			            String line = "";
			            while ((line = br.readLine()) != null) {
			                sb.append(line);
			            }
			            
			            JSONParser parser = new JSONParser();
			            Object obj = parser.parse(sb.toString());
			            responseJson = (JSONObject) obj;
			            logger.info("===================Cancel Pay Response===================");
			            logger.info(responseJson.toString());
			            
			            if(responseJson.get("resultCode").equals("2001")) {
			            	order.setCancelYn("Y");
			            	
			            	Code code = codeService.findOne(Integer.parseInt(cancelReason)); 
			        		order.setCancelReason(code); //취소사유 세팅
			        		
			        		Code statusCode = codeService.findOne(32);  //주문상태값 32(완료)로 세팅
			        		order.setOrderStatus(statusCode); //주문상태 세팅
			        		
			        		Date now = new Date();		        		
			        		order.setCancelDate(now); //현재시간 세팅
			            	
			            	orderService.save(order);
			            }
			        
			        }else {
			        	logger.info("===================API 호출  에러!!===================");
			        	
			        }
				}else { //거래금액이 0원인 경우
					
					order.setCancelYn("Y");
	            	
	            	Code code = codeService.findOne(Integer.parseInt(cancelReason)); 
	        		order.setCancelReason(code); //취소사유 세팅
	        		
	        		Code statusCode = codeService.findOne(32);  //주문상태값 32(완료)로 세팅
	        		order.setOrderStatus(statusCode); //주문상태 세팅
	        		
	        		Date now = new Date();		        		
	        		order.setCancelDate(now); //현재시간 세팅
	            	
	            	orderService.save(order);
	            	
	            	responseJson.put("resultCode", "2001");
	            	responseJson.put("resultMsg", "주문이 취소되었습니다.");
					
				}
		   
			}
		}
		
		return responseJson; 
		
	}
	
	
	/**
	 * 이메일 통해서 비밀번호 찾기
	 * 
	 * @return 결과코드
	 */
	@PostMapping("/findPw")
	@Transactional
	public @ResponseBody Map<String, Object> forget(@RequestBody Account account, HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 계정 ID 입력 여부 체크
		if (StringUtils.isEmpty(account.getAccountId()) || "null".equals(account.getAccountId())) {
			result.put("resultCode", "0011");
			result.put("resultMsg", lanDataService.getLanData("아이디를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		// 이메일 입력여부 체크
//		} else if (StringUtils.isEmpty(account.getEmail())  || "null".equals(account.getEmail())) {
//			result.put("resultCode", "0012");
//			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
//		// 이메일 정확성 체크
//		} else if (!ValidUtil.isEmail(account.getEmail())) {
//			result.put("resultCode", "0013");
//			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 정확히 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		} else {
			
			logger.info("========================account.getAccountId():" + account.getAccountId());
			Account accountTmp = accountService.findOne(account.getAccountId());
			
			if (null == accountTmp) {
				result.put("resultCode", "0021");
				result.put("resultMsg", lanDataService.getLanData("등록된 계정정보가 없습니다.", LocaleContextHolder.getLocale()));			
			} else {
				result.put("resultCode", "0000");
				result.put("resultMsg", lanDataService.getLanData("비밀번호 변경 주소가 이메일로 발송 되었습니다.", LocaleContextHolder.getLocale()));
				
				SimpleMailMessage message = new SimpleMailMessage();
		        message.setFrom(username);
		        message.setTo(accountTmp.getEmail()); //입력받은 email정보가 아닌 DB에 저장된 email정보로 세팅한다.(2023.12.20)
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
	
}
