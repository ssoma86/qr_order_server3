package org.lf.app.service.mng;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.bizclient.BizClientService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.order.pay.PayService;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.push.PushService;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.service.app.AppService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.minidev.json.JSONObject;


/**
 * 
 * 매장 관리자 API
 * 
 * @author lby
 *
 */
@RestController
@RequestMapping("/api/app/mng")
public class MngController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private PayService payService;
	
	@Autowired
	private BizClientService bizClientService;
	
	@Autowired
	private PushService pushService;
	
	// JsonView
	public interface MngControllerJsonView extends OrderControllerJsonView {}
		
	
	
	/**
	 * 상점 주문 리스트 조회
	 */
	@GetMapping("/order/{lanId}")
	@JsonView(MngControllerJsonView.class)
	public List<Order> order(Principal principal, String startDate, String endDate, String cancelYn,
			String orderStatusVal, @PathVariable String lanId, HttpServletResponse res) throws IOException, ParseException {
		appService.chkAndSettingLan(lanId);
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		
		// 주문 상태 코드 설정
		List<Integer> orderStatusCd = null;
		Code orderStatusCode = null;
		
		// 주문 상태 파라메타가 없으면 설정 안함
		if (!StringUtils.isEmpty(orderStatusVal)) {
			
			orderStatusCode = codeService.findOneByTopCodeValAndVal("ORDER_STATUS", orderStatusVal);
			
			if (!ObjectUtils.isEmpty(orderStatusCode)) {
				orderStatusCd = new ArrayList<>();
				orderStatusCd.add(orderStatusCode.getCd());
			}
		}
		
		if (ObjectUtils.isEmpty(orderStatusCd)) {
			Code orderStatus = codeService.findOneByTopCodeValAndVal(null, "ORDER_STATUS");
			
			orderStatusCd = new ArrayList<>();
			
			for (Code tmp : orderStatus.getSubCode()) {
				orderStatusCd.add(tmp.getCd());
			}
		}
		
		return service.findOrderListForMngUser(user.getStore().getStoreCd(),
				startDate, endDate, cancelYn, cancelYn, orderStatusCd);
	}
	
	
	
	/**
	 * 상점 주문 상세 조회
	 */
	@GetMapping("/order/{orderCd}/{lanId}")
	@JsonView(MngControllerJsonView.class)
	public Order orderInfo(Principal principal, @PathVariable Integer orderCd, @PathVariable String lanId) {
		appService.chkAndSettingLan(lanId);
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		
		Order order = service.findOne(orderCd);
		
		// 주문 있는지 체크
		if (!ObjectUtils.isEmpty(order)) {
			// 내 매장 주문인지 체크
			if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
				
				order.getSalesTp().setNmLan(lanDataService.getLanData(order.getSalesTp().getNm(), LocaleContextHolder.getLocale()));
				order.getOrderStatus().setNmLan(lanDataService.getLanData(order.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
				
				return order;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * 상점 주문 리스트 조회
	 * @param principal
	 * @return 상점 주문 리스트
	 */
	@GetMapping("/orderSync/{lanId}")
	@Transactional
	public List<Map<String, Object>> orderSync(Principal principal, String orderStatusVal, @PathVariable String lanId) {
		appService.chkAndSettingLan(lanId);
		
		List<Map<String, Object>> result = new ArrayList<>();
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		
		// 주문 상태 코드 설정
		Integer orderStatusCd = null;
		// 주문 상태 파라메타가 없으면 설정 안함
		if (!StringUtils.isEmpty(orderStatusVal)) {
			Code orderStatusCode = codeService.findOneByTopCodeValAndVal("ORDER_STATUS", orderStatusVal);
			
			if (!ObjectUtils.isEmpty(orderStatusCode)) {
				orderStatusCd = orderStatusCode.getCd();
			}
		}
		
		// 조회
		List<Order> orderList = service.findOrderListForMngUser(user.getStore().getStoreCd(), orderStatusCd, false);
		
		for (Order order : orderList) {
			
			order.setSearchYn(true);
			
			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("orderCd", order.getOrderCd());
			orderMap.put("orderId", order.getOrderId());
			orderMap.put("tableId", order.getTableId());
			
			List<OrderSmenu> orderSmenuList = order.getSmenus();
			
			Map<String, Integer> smenus = new HashMap<>();
			
			for (OrderSmenu orderSmenu : orderSmenuList) {
				String orderSmenuNmLan = orderSmenu.getSmenuNmLan();
				
				if (smenus.containsKey(orderSmenuNmLan)) {
					smenus.put(orderSmenuNmLan, smenus.get(orderSmenuNmLan) + orderSmenu.getCnt());
				} else {
					smenus.put(orderSmenuNmLan, orderSmenu.getCnt());
				}
			}
			
			String menus = "";
			int smenuCnt = 0;
			
			for (String key : smenus.keySet()) {
				smenuCnt += smenus.get(key);
				menus += key + "(" + smenus.get(key) + lanDataService.getLanData("개", LocaleContextHolder.getLocale()) + ") / ";
			}
			
			orderMap.put("smenuCnt", smenuCnt);
			orderMap.put("smenus", menus.substring(0, menus.length() - 3));
			
			orderMap.put("orderDt", order.getOrderDt());
			orderMap.put("orderTm", order.getOrderTm());
			orderMap.put("appUser.accountNm", !ObjectUtils.isEmpty(order.getAppUser()) ? order.getAppUser().getAccountNm() : "");
			orderMap.put("salesTpNm", order.getSalesTp().getNm());
			orderMap.put("orderStatusNm", order.getOrderStatus().getNm());
			orderMap.put("orderStatusVal", order.getOrderStatus().getVal());
			orderMap.put("payYn", order.getPayYn());
			orderMap.put("cancelYn", order.getCancelYn());
			orderMap.put("cancelPay", order.getCancelPay());
			orderMap.put("amt", order.getAmt());
			orderMap.put("unit", order.getUnit());
			orderMap.put("orderAddr", StringUtils.isEmpty(order.getOrderAddr()) ? "" : order.getOrderAddr());
			orderMap.put("orderDesc", order.getOrderDesc());
			
			result.add(orderMap);
		}
		
		return result;
	}
	
	
	
	/**
	 * 상점 주문 리스트 조회
	 * @param principal
	 * @return 상점 주문 리스트
	 */
	@PostMapping("/orderUpSync")
	@Transactional
	public void orderUpSync(Principal principal, String orderCds) {
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		Store store = user.getStore();
		
		String[] orderCdList = orderCds.split(",");
		
		for (String orderCd : orderCdList) {
			
			try {
				Integer ocd = Integer.parseInt(orderCd);
				
				Order order = service.findOne(ocd);
				
				// 로그인 사용자와 같은 상점일 때만 업데이트
				if (store.getStoreCd() == order.getStore().getStoreCd()) {
					order.setAppSyncYn(true);
					
					service.save(order);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 상점 주문 리스트 조회
	 */
	@GetMapping("/getOrders/{lanId}")
	@JsonView(MngControllerJsonView.class)
	public List<Map<String, Object>> getOrders(Principal principal, String startDate, String endDate, String cancelYn,
			String orderStatusVal, @PathVariable String lanId, HttpServletResponse res) throws IOException, ParseException {
		appService.chkAndSettingLan(lanId);
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		
		// 주문 상태 코드 설정
		List<Integer> orderStatusCd = null;
		Code orderStatusCode = null;
		
		// 주문 상태 파라메타가 없으면 설정 안함
		if (!StringUtils.isEmpty(orderStatusVal)) {
			
			orderStatusCode = codeService.findOneByTopCodeValAndVal("ORDER_STATUS", orderStatusVal);
			
			if (!ObjectUtils.isEmpty(orderStatusCode)) {
				orderStatusCd = new ArrayList<>();
				orderStatusCd.add(orderStatusCode.getCd());
			}
		}
		
		if (ObjectUtils.isEmpty(orderStatusCd)) {
			Code orderStatus = codeService.findOneByTopCodeValAndVal(null, "ORDER_STATUS");
			
			orderStatusCd = new ArrayList<>();
			
			for (Code tmp : orderStatus.getSubCode()) {
				orderStatusCd.add(tmp.getCd());
			}
		}
		
		List<Order> orderList = service.findOrderListForMngUser(user.getStore().getStoreCd(),
				startDate, endDate, cancelYn, cancelYn, orderStatusCd);
		
		
		List<Map<String, Object>> result = new ArrayList<>();
		
		for (Order order : orderList) {
			
			order.setSearchYn(true);
			
			Map<String, Object> orderMap = new HashMap<>();
			orderMap.put("orderCd", order.getOrderCd());
			orderMap.put("orderId", order.getOrderId());
			orderMap.put("tableId", order.getTableId());
			
			List<OrderSmenu> orderSmenuList = order.getSmenus();
			
			Map<String, Integer> smenus = new HashMap<>();
			
			for (OrderSmenu orderSmenu : orderSmenuList) {
				String orderSmenuNmLan = orderSmenu.getSmenuNmLan();
				
				if (smenus.containsKey(orderSmenuNmLan)) {
					smenus.put(orderSmenuNmLan, smenus.get(orderSmenuNmLan) + orderSmenu.getCnt());
				} else {
					smenus.put(orderSmenuNmLan, orderSmenu.getCnt());
				}
			}
			
			String menus = "";
			int smenuCnt = 0;
			
			for (String key : smenus.keySet()) {
				smenuCnt += smenus.get(key);
				menus += key + "(" + smenus.get(key) + lanDataService.getLanData("개", LocaleContextHolder.getLocale()) + ") / ";
			}
			
			orderMap.put("smenuCnt", smenuCnt);
			orderMap.put("smenus", menus.substring(0, menus.length() - 3));
			
			orderMap.put("orderDt", order.getOrderDt());
			orderMap.put("orderTm", order.getOrderTm());
			orderMap.put("accountNm", !ObjectUtils.isEmpty(order.getAppUser()) ? order.getAppUser().getAccountNm() : "");
			orderMap.put("salesTpNm", order.getSalesTp().getNm());
			orderMap.put("orderStatusNm", order.getOrderStatus().getNm());
			orderMap.put("orderStatusVal", order.getOrderStatus().getVal());
			orderMap.put("payYn", order.getPayYn());
			orderMap.put("cancelYn", order.getCancelYn());
//			orderMap.put("cancelPay", order.getCancelPay());
			orderMap.put("amt", order.getAmt());
			orderMap.put("unit", lanDataService.getLanData("원", LocaleContextHolder.getLocale()));
			orderMap.put("orderAddr", StringUtils.isEmpty(order.getOrderAddr()) ? "" : order.getOrderAddr());
			orderMap.put("orderDesc", order.getOrderDesc());
			
			result.add(orderMap);
		}


		return result;
	}
	
	
	
	/**
	 * 상점 주문 상세 조회
	 */
	@GetMapping("/orderDetail/{orderCd}/{lanId}")
	@JsonView(MngControllerJsonView.class)
	public Order orderDetail(Principal principal, @PathVariable Integer orderCd, @PathVariable String lanId) {
		appService.chkAndSettingLan(lanId);
		
		// 로그인 사용자 설정
		User user = userService.findOne(principal.getName());
		
		Order order = service.findOne(orderCd);
		
		// 주문 있는지 체크
		if (!ObjectUtils.isEmpty(order)) {
			// 내 매장 주문인지 체크
			if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
				
				order.getSalesTp().setNmLan(lanDataService.getLanData(order.getSalesTp().getNm(), LocaleContextHolder.getLocale()));
				order.getOrderStatus().setNmLan(lanDataService.getLanData(order.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
				
				return order;
			}
		}
		
		return null;
	}
	
	
	
	/**
	 * 주문 상태 변경
	 * 
	 * @param orderCd
	 * @param orderStatusVal
	 * @throws IOException 
	 */
	@PostMapping("/order/status/{orderCd:\\d+}/{orderStatusVal}/{deliveryTime:\\d+}")
	public void orderStatus(Principal principal, @PathVariable Integer orderCd, @PathVariable Integer deliveryTime, 
			@PathVariable String orderStatusVal, HttpServletResponse res) throws IOException {
		User user = userService.findOne(principal.getName());
		
		boolean error = false;
		Map<String, Object> errorsMessage = new HashMap<String, Object>();
		
		// 주문 코드 체크
		if (!ObjectUtils.isEmpty(orderCd)) {
			Order order = service.findOne(orderCd);
			
			// 주문 정보 체크
			if (!ObjectUtils.isEmpty(order)) {
				
				// 상점 체크
				if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
					// 주문 코드 있는지 체크
					if (!ObjectUtils.isEmpty(order)) {
						
						if (!StringUtils.isEmpty(orderStatusVal)) {
							Code orderStatus = codeService.findOneByTopCodeValAndVal("ORDER_STATUS", orderStatusVal);
							
							if (!ObjectUtils.isEmpty(orderStatus)) {
								order.setOrderStatus(orderStatus);
								order.setDeliveryTime(deliveryTime);
								service.save(order);
								
								// 전화 번호  등록 있는 것 만 알림 정송
								if (!StringUtils.isEmpty(order.getTel())) {
									String[] ts = !StringUtils.isEmpty(orderStatus.getRef3()) ? orderStatus.getRef3().split(",") : null;
									
									// 준비 완료 || 배달중 앱 회원 전용
									if ("READY".equals(orderStatus.getVal()) || "DELIVERY".equals(orderStatus.getVal())) {
										bizClientService.sendBiz(order.getTel(), orderStatus.getRef1(),
												(!ObjectUtils.isEmpty(ts) && ts.length > 1 ? ts[0] : ""),
												(!ObjectUtils.isEmpty(ts) && ts.length > 1 ? ts[1] : ""));
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
										data.appendField("val", orderStatus.getVal());
										data.appendField("nm", orderStatus.getNm());
										data.appendField("msg", orderStatus.getRef1());
										
										param.appendField("data", data);
										
										try {
											pushService.sendPush(param.toJSONString());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							} else {
								error = true;
								errorsMessage.put("code", "isNotExist");
								errorsMessage.put("field", "orderStatusVal");
								errorsMessage.put("value", orderStatusVal);
								errorsMessage.put("errorMsg", lanDataService.getLanData("주문 상태 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
							}
						} else {
							error = true;
							errorsMessage.put("code", "isNotExist");
							errorsMessage.put("field", "orderStatusVal");
							errorsMessage.put("value", orderStatusVal);
							errorsMessage.put("errorMsg", lanDataService.getLanData("주문 상태 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
						}
					}
				}
			} else {
				error = true;
				errorsMessage.put("code", "isNotExist");
				errorsMessage.put("field", "order");
				errorsMessage.put("value", orderCd);
				errorsMessage.put("errorMsg", lanDataService.getLanData("주문 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
			}
		} else {
			error = true;
			errorsMessage.put("code", "isNotExist");
			errorsMessage.put("field", "orderCd");
			errorsMessage.put("value", orderCd);
			errorsMessage.put("errorMsg", lanDataService.getLanData("주문 코드 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
		}
		
		if (error) {
			res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json; charset=utf-8");
			
			Map<String, Object> errorAttribute = new HashMap<String, Object>();
			errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
			errorAttribute.put("error", "Bad Request");
			
			List<Map<String, Object>> errorsMessages = new ArrayList<>();
			errorsMessages.add(errorsMessage);
			errorAttribute.put("errorsMessage", errorsMessages);
			errorAttribute.put("message", "Validation failed for object='order'. Error count: 1");
			errorAttribute.put("path", "/api/app/mng/order/status");
			
			ObjectMapper om = new ObjectMapper();
			
			String json = om.writeValueAsString(errorAttribute);
			
			res.getWriter().print(json);
		}
	}
	
	
	
	/**
	 * 주문 취소
	 * 
	 * @param orderCds
	 * @throws Exception 
	 */
	@GetMapping("/orderDel/{orderCd}")
	public void del(@PathVariable Integer orderCd, Principal principal,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		User user = userService.findOne(principal.getName());
		
		boolean error = false;
		Map<String, Object> errorsMessage = new HashMap<String, Object>();
		
		// 주문 코드 체크
		if (!ObjectUtils.isEmpty(orderCd)) {
			Order order = service.findOne(orderCd);
			
			// 주문 정보 체크
			if (!ObjectUtils.isEmpty(order)) {
		
				// 자기 매장 주문만 처리, 한심한놈이 이상한 짓을 하는걸 방지 하기 위함
				if (user.getStore().getStoreCd() == order.getStore().getStoreCd()) {
					if ("N".equals(order.getCancelYn())) {
						order.setCancelYn("Y");
						
						// 결제 되고 미 취소 인거만 결제 취소
						if ("Y".equals(order.getPayYn()) && "N".equals(order.getCancelPay())) {
							Map<String, String> resHm = payService.cancelPay(order.getPay(), req);
							
							String resultCd   = (String)resHm.get("resultCd");
							String resultMsg  = (String)resHm.get("resultMsg");
							
							if(resultCd.equals("S")) {
								order.setPayYn("N");
								order.setCancelPay("Y");
								order.setCancelYn("Y");
								service.save(order);
							} else {
								error = true;
								errorsMessage.put("code", "cancelPayError");
								errorsMessage.put("field", "cancelPay");
								errorsMessage.put("errorMsg", lanDataService.getLanData("카드 결제 취소 실패 하였습니다.", LocaleContextHolder.getLocale()) +
										" " + resultMsg);
							}
						} else {
							order.setCancelYn("Y");
							service.save(order);
						}
					}
				}
			} else {
				error = true;
				errorsMessage.put("code", "isNotExist");
				errorsMessage.put("field", "order");
				errorsMessage.put("value", orderCd);
				errorsMessage.put("errorMsg", lanDataService.getLanData("주문 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
			}
		} else {
			error = true;
			errorsMessage.put("code", "isNotExist");
			errorsMessage.put("field", "orderCd");
			errorsMessage.put("value", orderCd);
			errorsMessage.put("errorMsg", lanDataService.getLanData("주문 코드 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
		}
		
		if (error) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setCharacterEncoding("UTF-8");
			res.setContentType("application/json; charset=utf-8");
			
			Map<String, Object> errorAttribute = new HashMap<String, Object>();
			errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			errorAttribute.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			errorAttribute.put("error", "Order cancel error");
			
			List<Map<String, Object>> errorsMessages = new ArrayList<>();
			errorsMessages.add(errorsMessage);
			errorAttribute.put("errorsMessage", errorsMessages);
			errorAttribute.put("message", "Validation failed for object='order'. Error count: 1");
			errorAttribute.put("path", "/api/app/mng/orderDel/" + orderCd);
			
			ObjectMapper om = new ObjectMapper();
			
			String json = om.writeValueAsString(errorAttribute);
			
			res.getWriter().print(json);
		}
	}
	
	
}
