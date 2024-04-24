package org.lf.app.models.business.order;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.If.app.models.business.pushAlarm.FcmService;
import org.lf.app.models.PageJsonView;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.bizclient.BizClientService;
import org.lf.app.models.business.order.pay.PayService;
import org.lf.app.models.business.push.PushService;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreRoom.StoreRoomValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 주문 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/order")
public class OrderController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService service;
	
	@Autowired
	private PayService payService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private BizClientService bizClientService;
	
	@Autowired
	private PushService pushService;
	
	
	
	public interface OrderControllerJsonView extends CodeControllerCommonJsonView,
		LanControllerCommonJsonView, BaseJsonView {};
	
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param store
	 * @param nationCd
	 * @return 매장정보 리스트
	 */
//	@GetMapping("/total")
//	@JsonView(OrderControllerJsonView.class)
//	public Integer searchTotal(String startDate, String endDate, String orderStatusCds, String cancelYn, String cancelPay,
//			@SessionAttribute(value = "sa_account") Account account) {
//		User user = userService.findOne(account.getAccountId());
//		
//		String[] orderStatusCd = orderStatusCds.split(",");
//		
//		List<Integer> orderStatusCdList = new ArrayList<>();
//		
//		for (String cd : orderStatusCd) {
//			orderStatusCdList.add(Integer.parseInt(cd));
//		}
//		
//		return service.findOrderListTotal(user.getStore().getStoreCd(), startDate, endDate, orderStatusCdList, cancelYn, cancelPay);
//	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param store
	 * @param nationCd
	 * @return 매장정보 리스트
	 * @throws ParseException 
	 */
	@GetMapping
	@JsonView(OrderControllerJsonView.class)
	public PageJsonView<Order> search(String startDate, String endDate, String orderStatusCds, String cancelYn, String cancelPay,
			Integer pageSize, Integer currPage, @SessionAttribute(value = "sa_account") Account account) throws ParseException {
		User user = userService.findOne(account.getAccountId());
		
		String[] orderStatusCd = orderStatusCds.split(",");
	
		List<Integer> orderStatusCdList = new ArrayList<>();
		
	
		
		for (String cd : orderStatusCd) {
			orderStatusCdList.add(Integer.parseInt(cd));
			
		}
		
		Page<Order> orderList = service.findOrderList(user.getStore().getStoreCd(),
				startDate, endDate, orderStatusCdList, cancelYn, cancelPay, PageRequest.of(currPage, pageSize));
		
		orderList.forEach(orderTmp -> {
			if (!ObjectUtils.isEmpty(orderTmp.getSalesTp())) {
				orderTmp.getSalesTp().setNmLan(
						lanDataService.getLanData(orderTmp.getSalesTp().getNm(), LocaleContextHolder.getLocale()));
			}
			
			if (!ObjectUtils.isEmpty(orderTmp.getOrderStatus())) {
				orderTmp.getOrderStatus().setNmLan(
						lanDataService.getLanData(orderTmp.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
			}
			
			// 이미 조회 함의 표시, 이미 조회 된거면 통지 안함
			orderTmp.setSearchYn(true);
		});
		
		return new PageJsonView<Order>(orderList);
	}
	
	
	/**
	 * 주문상태가 '주문접수'인 건수 가져오기(23.10.06)
	 * @param storeId
	 * @return
	 */
	@GetMapping("/orderSearchCnt")
	public @ResponseBody Integer getOrderSearchCnt(@RequestParam(required = false) String storeId) throws IOException {
		
		Integer cnt = service.findSearchCnt(storeId);
		
		return cnt;
	}
	
	/**
	 * 주문 상태 변경
	 * 
	 * @param orderCd
	 * @param orderStatusVal
	 */
	@PostMapping("/status/result/{orderCd:\\d+}/{waitTime}/{orderStatusVal}")
	public void orderStatus(@PathVariable Integer orderCd
								, @PathVariable String waitTime
								, @PathVariable String orderStatusVal
								, @SessionAttribute(value = "sa_account") Account account) throws Exception {
		
		User user = userService.findOne(account.getAccountId());
		Order order = service.findOne(orderCd);
		String storeHp =null;
		// 자기 매장 주문만 처리
		if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
			Code code = codeService.findOneByTopCodeValAndVal("ORDER_STATUS", orderStatusVal);
			order.setOrderStatus(code);
			
			//주문 상태가 ready(준비중) 일 때만 대기시간 세팅한다. (24.02.14)
			if(orderStatusVal.equals("READY")) {
				order.setWaitTime(waitTime);
			}
			
			service.save(order);
			
			// 전화 번호  등록 있는 것 만 알림 정송
			if (!StringUtils.isEmpty(order.getTel())) {
				String[] ts = !StringUtils.isEmpty(code.getRef3()) ? code.getRef3().split(",") : null;
				
				// 준비 완료 || 배달중 앱 회원 전용
				if ("READY".equals(code.getVal()) || "DELIVERY".equals(code.getVal())) {
					storeHp = order.getStore().getTel().replaceAll("-","");
					//sendSmsApi(order.getMid(), order.getTid(), storeHp, order.getTel(), "메뉴플러스 안내문자", code.getRef1()); //sms문자보내기
					//bizClientService.sendBiz(storeHp, code.getRef1(), order.getTel(), "메뉴플러스 안내문자");
				}
			}
			
			// 푸시 키 있는 앱 사용자 푸시 전송
			AppUser appUser = order.getAppUser();
			
			// 앱회원 체크
			if (!ObjectUtils.isEmpty(appUser)) {
				String pushToken = appUser.getPushToken();
				
				if (!StringUtils.isEmpty(pushToken)) {
					
					JSONObject param = new JSONObject();
					param.appendField("to", pushToken);
					
					JSONObject data = new JSONObject();
					data.appendField("val", code.getVal());
					data.appendField("nm", code.getNm());
					data.appendField("msg", code.getRef1());
					
					param.appendField("data", data);
					
					try {
						pushService.sendPush(param.toJSONString());
					} catch (IOException e) {
						logger.info("======e.getMessage() : " + e.getMessage());						
					}
				}
			}
		}
	}
	
	
	/**
	 * 주문 취소
	 * amt가 0원인 경우 cancel api 로직을 타지 않게 수정(24.02.15)
	 * @param orderCds
	 * @throws Exception 
	 */
	@GetMapping("/delOrder/{orderCd}/{orderId}/{cancelPw}/{cancelReason}")
	public JSONObject delOrder(@PathVariable int orderCd
								, @PathVariable String orderId
								, @PathVariable String cancelPw
								, @PathVariable String cancelReason
								, @SessionAttribute(value = "sa_account") Account account
								, HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		logger.info("================================= Start delOrder ==============================================");
		
		User user = userService.findOne(account.getAccountId());
		Order order = service.findOne(orderCd);
		String MID =user.getStore().getStoreId();
		HttpURLConnection conn = null;
		JSONObject responseJson =  new JSONObject();
		String message = "";
		String resultMsg = "";
		Map<String,String> transInfo = new HashMap();
		
		// 자기 매장 주문만 처리
		if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
			if ("N".equals(order.getCancelYn())) {
				// 결제 되고 미 취소 인거만 결제 취소
				if ("Y".equals(order.getPayYn()) && "N".equals(order.getCancelPay())) {
					
					if(order.getAmt() != 0) { // 결제금액이 0원보다 큰 경우
					
						Order od = service.findByOid(orderId);
						
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
				            	
				            	service.save(order);
				            }
				        
				        }else {
				        	logger.info("===================API 호출  에러!!===================");
				        }
			        
					 }else { //0원 거래인 경우
						 logger.info("=================== 0원 거래인경우 ===================");		
						 
						 responseJson.put("resultMsg", "주문이 취소되었습니다.");				        
						 order.setCancelYn("Y");
			            	
						 Code code = codeService.findOne(Integer.parseInt(cancelReason)); 
						 order.setCancelReason(code); //취소사유 세팅
		        		
						 Code statusCode = codeService.findOne(32);  //주문상태값 32(완료)로 세팅
						 order.setOrderStatus(statusCode); //주문상태 세팅
		        		
						 Date now = new Date();		        		
						 order.setCancelDate(now); //현재시간 세팅
		            	
						 service.save(order);
				     }
			    } 
			   
			}
		}
		
		return responseJson; 
		
	}

			
	/**
	 * sms문자발송(주문상태관련)2023.08.07
	 * @param mid
	 * @param tid
	 * @param storeTel
	 * @param guestTel
	 * @param title
	 * @param content
	 * @throws Exception
	 */
	private void sendSmsApi(String mid, String tid, String storeTel, String guestTel, String title, String content) throws Exception {
		
		logger.info("===================================== sendSmsApi start ===========================================");
		
		HttpURLConnection conn = null;
		
		URL url = new URL("https://api.innopay.co.kr/api/sendSMS"); //운영
		//URL url = new URL("http://180.67.115.172:8182/api/sendSMS"); //개발
        conn = (HttpURLConnection) url.openConnection();
        //Request 형식 설정
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        
        logger.info("===================================== sendSmsApi https://api.innopay.co.kr/api/sendSMS===========================================");
        
        //request에 JSON data 준비
        conn.setDoOutput(true);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
        //commands라는 JSONArray를 담을 JSONObject 생성
        
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("mtype", "1");
        dataMap.put("mid", mid);
        dataMap.put("fromNo", storeTel);
        dataMap.put("toNo", "01023384352");
        dataMap.put("msg", content);
        dataMap.put("title", title);
        dataMap.put("seq", tid);
        dataMap.put("merchantKey", "Ma29gyAFhvv/+e4/AHpV6pISQIvSKziLIbrNoXPbRS5nfTx2DOs8OJve+NzwyoaQ8p9Uy1AN4S1I0Um5v7oNUg==");
        
        JSONObject json = new JSONObject(dataMap);
//        json.put("mtype", "1");
//        json.put("mid", mid);
//        json.put("fromNo", storeTel);
//        json.put("toNo", "01023384352");
//        json.put("msg", content);
//        json.put("title", title);
//        json.put("seq", tid);
//        json.put("merchantKey", "Ma29gyAFhvv/+e4/AHpV6pISQIvSKziLIbrNoXPbRS5nfTx2DOs8OJve+NzwyoaQ8p9Uy1AN4S1I0Um5v7oNUg==");
        
        //request에 쓰기
        bw.write(json.toString());
        bw.flush();
        bw.close();
        
        String responseMsg = conn.getResponseMessage();
        int responseCode = conn.getResponseCode();
        //보내고 결과값 받기
        logger.info("===================ResponseCode: "+responseCode);
        logger.info("===================ResponseMsg: "+responseMsg);
	}
	
	
	
	 /**
	  * 대기시간 저장(24.02.14 최초 개발)
	  * @param store
	  */
	 @PostMapping("/waitTime")
	 public void upateWaitTime4Order(@Validated({ StoreRoomValid.class }) @RequestBody Order order) {
		 //service.updateDesign(store.getStoreCd(), store.getDesign());
	 }
	
	
	
	/**
	 * 결제 취소
	 * 
	 * @param orderCd
	 * @throws Exception 
	 */
	@DeleteMapping("/cancelPay/{orderCd}")
	public void cancelPay(@PathVariable Integer orderCd, @SessionAttribute(value = "sa_account") Account account) throws Exception {
//		User user = userService.findOne(account.getAccountId());
//		Order order = service.findOne(orderCd);
//		
//		// 자기 매장 주문만 처리
//		if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
//			// 결제 되고 미 취소 인거만 결제 취소
//			if ("Y".equals(order.getPayYn()) && "N".equals(order.getCancelPay())) {
//				if (payService.cancelPay(order.getPay())) {
//					order.setPayYn("N");
//					order.setCancelPay("Y");
//					service.save(order);
//				} else {
//					throw new Exception();
//				}
//			}
//		}
	}
	
	
//	@Autowired
//	private OrderRepository rep;
//	
//	/**
//	 * 삭제
//	 * 
//	 * @param smenuCd
//	 */
//	@DeleteMapping("/del/{orderCds}")
//	public void del(@PathVariable Integer[] orderCds) {
//		for (Integer orderCd : orderCds) {
//			rep.deleteById(orderCd);
//		}
//	}
	
}
