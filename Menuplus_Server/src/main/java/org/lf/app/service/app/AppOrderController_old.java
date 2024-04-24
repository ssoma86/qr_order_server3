package org.lf.app.service.app;

import java.io.IOException;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.config.socket.WebSocketAsync;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.order.discount.OrderDiscount;
import org.lf.app.models.business.order.pay.PayService;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.order.smenuOpt.OrderSmenuOpt;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 앱 회원 주문 관련 API
 * 
 * @author lby
 *
 */
@RestController
@RequestMapping("/app/api")
public class AppOrderController_old {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
    private AppUserService appUserService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private WebSocketAsync webSocketAsync;
	
	@Autowired
	private PayService payService;
	
	
	// JsonView
	public interface AppControllerJsonView {}

	
	
	/**
	 * 주문
	 * 
	 * @throws IOException 
	 */
	@PostMapping("/order")
	@Transactional
	@JsonView(AppControllerJsonView.class)
//	public Order order(@Validated(OrderValid.class) @RequestBody Order order,
//			HttpServletResponse res, Principal principal) throws IOException {
//		Store store = storeService.findOneByStoreId(order.getStoreId());
//		
//		// 벨리데이션 오류 처리
//		boolean error = false;
//		Map<String, Object> errorsMessage = new HashMap<String, Object>();
//		
//		if (ObjectUtils.isEmpty(store)) {
//			error = true;
//			errorsMessage.put("code", "isNotExist");
//			errorsMessage.put("field", "storeCd");
//			errorsMessage.put("value", order.getStoreId());
//			errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
//		} else if ("N".equals(store.getUseYn())) {
//			error = true;
//			errorsMessage.put("code", "isNotUsed");
//			errorsMessage.put("field", "useYn");
//			errorsMessage.put("value", "N");
//			errorsMessage.put("errorMsg", lanDataService.getLanData("사용기간 만료된 매장 입니다.", LocaleContextHolder.getLocale()));
//		} else {
//			// 오픈 여부 설정
//			storeService.setSniffling(store);	// 휴무 설정
//			storeService.setOpen(store);
//			
//			if (!store.isOpen()) {
//				error = true;
//				errorsMessage.put("code", "isNotOpen");
//				errorsMessage.put("field", "open");
//				errorsMessage.put("value", false);
//				errorsMessage.put("errorMsg", lanDataService.getLanData("영업시간이 종료 되었습니다.", LocaleContextHolder.getLocale()));
//			} else {
//				// 주문 정보 설정
//				order.setStore(store);
//				
//				if (principal != null) {
//					// 회원 설정
//					AppUser appUser = appUserService.findOne(principal.getName());
//					order.setAppUser(appUser);
//				}
//				
//				if (order.getSalesTpCd() < 1) {
//					error = true;
//					errorsMessage.put("code", "isNotSalesTpCd");
//					errorsMessage.put("field", "salesTpCd");
//					errorsMessage.put("value", order.getSalesTpCd());
//					errorsMessage.put("errorMsg", lanDataService.getLanData("판매 방식 코드를 전달하지 않았습니다.", LocaleContextHolder.getLocale()));
//				} else {
//					orderService.setOrderData(order);
//					order.setUseYn("N");
//					
//					orderService.setOrderId(order);
//					
//					orderService.save(order);
//					
//					// 결제 처리후 통지 보냄
//					// 주문 통지 보내기
////					webSocketAsync.sendMsg(store.getStoreId());
//					
//					return order;
//				}
//			}
//		}
//		
//		if (error) {
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
//			errorsMessages.add(errorsMessage);
//			
//			errorAttribute.put("errorsMessage", errorsMessages);
//			errorAttribute.put("message", "Validation failed for object='order'. Error count: 1");
//			errorAttribute.put("path", "/api/app/order");
//			
//			ObjectMapper om = new ObjectMapper();
//			
//			String json = om.writeValueAsString(errorAttribute);
//			
//			res.getWriter().print(json);
//		}
//		
//		return null;
//	}
	
	
	
	/**
	 * 개별 회원 주문 리스트 조회
	 * 
	 * 0 전체  주문(최상위) -> 완료(최상위)
	 * 200(주문) 주문(<200) -> 완료(최상위) 
	 * 200(완료) 완료(<200)
	 * 
	 * 배송상태가 완료아닌거만 주문 시간 / 분
	 * 
	 * @param lanId
	 * @return 개별 회원 주문 리스트
	 */
//	@GetMapping("/getOrderList/{orderCd}/{lanId}")
//	public Map<String, Object> getOrderList(@PathVariable String lanId, @PathVariable Integer orderCd, Principal principal) {
//		appService.chkAndSettingLan(lanId);
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		Calendar calendar = Calendar.getInstance();
//		
//		Map<String, Object> result = new HashMap<>();
//		
//		// 총 주문 수량
//		// 사용자 전체 주문 조회 완료 상태로 정열
//		List<Order> orderList = orderService.findOrderListForAppUser(principal.getName(), "N");
//		result.put("total", orderList.size());
//		
//		List<Map<String, Object>> orderListTmp = new ArrayList<>();		// 미완료 주문
//		
//		int jumpCnt = 0;
//		boolean begin = 0 == orderCd ? true : false;
//		
//		for (Order orderTmp : orderList) {
//			// 시작점 찾기
//			if (!begin) {
//				jumpCnt++;
//				
//				if (orderTmp.getOrderCd().equals(orderCd)) {
//					begin = true;
//				}
//				continue;
//			}
//			
//			// 한번에 30개 씩 내려줌
//			if (orderListTmp.size() >= 30) {
//				break;
//			}
//			
//			jumpCnt++;
//			
//			Map<String, Object> tmp = new HashMap<>();
//			
//			tmp.put("orderCd", orderTmp.getOrderCd());
//			tmp.put("orderId", orderTmp.getOrderId());
//			
//			// 상점명
//			tmp.put("storeNmLan", orderTmp.getStore().getStoreNmLan());
//			// 주일 계산
//			calendar.setTime(orderTmp.getOrderDate());
//			int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
//			
//			String week = "";
//			switch (day_of_week) {
//			case 1: week = lanDataService.getLanData("일", LocaleContextHolder.getLocale()); break;
//			case 2: week = lanDataService.getLanData("월", LocaleContextHolder.getLocale()); break;
//			case 3: week = lanDataService.getLanData("화", LocaleContextHolder.getLocale()); break;
//			case 4: week = lanDataService.getLanData("수", LocaleContextHolder.getLocale()); break;
//			case 5: week = lanDataService.getLanData("목", LocaleContextHolder.getLocale()); break;
//			case 6: week = lanDataService.getLanData("금", LocaleContextHolder.getLocale()); break;
//			case 7: week = lanDataService.getLanData("토", LocaleContextHolder.getLocale()); break;
//			default: break;
//			}
//			
//			// 주문 일자
//			tmp.put("orderDate", sdf.format(orderTmp.getOrderDate()) + "(" + week + ")");
//			
//			long minute = (System.currentTimeMillis() - orderTmp.getOrderDate().getTime()) / 1000 / 60;
//			long hour = minute / 60;
//			
//	        // 하루전이면 시간 표시
//	        if (hour < 24) {
//	        	String orderDate = (hour > 0 ? hour + lanDataService.getLanData("시간 ", LocaleContextHolder.getLocale()) : "") +
//		        		(minute % 60 > 0 ? minute % 60 + lanDataService.getLanData("분", LocaleContextHolder.getLocale()) : "");
//		        tmp.put("orderDate", orderDate);
//	        }
//			
//	        
//			List<String> menuData = new ArrayList<>();
//			
//			for (OrderSmenu menu : orderTmp.getSmenus()) {
//				String menuDataTmp = menu.getSmenuNmLan();
//				
//				if (menu.getSmenuOpts().size() > 0) {
//					menuDataTmp += "(";
//				}
//				
//				for (int i = 0, len = menu.getSmenuOpts().size(); i < len; i++) {
//					if (i == len-1) {
//						menuDataTmp += menu.getSmenuOpts().get(i).getSmenuOptNmLan() + ")";
//					} else {
//						menuDataTmp += menu.getSmenuOpts().get(i).getSmenuOptNmLan() + " + ";
//					}
//				}
//				
//				menuDataTmp += " " + menu.getCnt() + lanDataService.getLanData("개", LocaleContextHolder.getLocale());
//				
//				menuData.add(menuDataTmp);
//			}
//			
//			tmp.put("menuData", menuData);
//			
//			tmp.put("orderAmt", DecimalFormat.getNumberInstance().format(orderTmp.getAmt()) + orderTmp.getStore().getUnit());
//			
//			tmp.put("orderStatus", lanDataService.getLanData(orderTmp.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
//			tmp.put("orderStatusVal", orderTmp.getOrderStatus().getVal());
//			tmp.put("cancelYn", orderTmp.getCancelYn());
//			
//			tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef1(), LocaleContextHolder.getLocale()));
//			
//			orderListTmp.add(tmp);
//		}
//		
//		result.put("list", orderListTmp);
//		result.put("remain", orderList.size() - jumpCnt);
//		
//		return result;
//	}
	
	
	/**
	 * 개별 회원 주문 리스트 조회
	 * 
	 * 0 전체  주문(최상위) -> 완료(최상위)
	 * 200(주문) 주문(<200) -> 완료(최상위) 
	 * 200(완료) 완료(<200)
	 * 
	 * @param lanId
	 * @return 개별 회원 주문 리스트
	 */
//	@GetMapping("/getOrderOverList/{orderCd}/{lanId}")
//	public Map<String, Object> getOrderOverList(@PathVariable String lanId, @PathVariable Integer orderCd, Principal principal) {
//		appService.chkAndSettingLan(lanId);
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		Calendar calendar = Calendar.getInstance();
//		
//		Map<String, Object> result = new HashMap<>();
//		
//		// 총 주문 수량
//		// 사용자 전체 주문 조회 완료 상태로 정열
//		List<Order> orderList = orderService.findOrderListForAppUser(principal.getName(), "Y");
//		result.put("total", orderList.size());
//		
//		List<Map<String, Object>> orderListTmp = new ArrayList<>();		// 미완료 주문
//		
//		int jumpCnt = 0;
//		boolean begin = 0 == orderCd ? true : false;
//		
//		for (Order orderTmp : orderList) {
//			// 시작점 찾기
//			if (!begin) {
//				jumpCnt++;
//				
//				if (orderTmp.getOrderCd().equals(orderCd)) {
//					begin = true;
//				}
//				continue;
//			}
//			
//			// 한번에 30개 씩 내려줌
//			if (orderListTmp.size() >= 30) {
//				break;
//			}
//			
//			jumpCnt++;
//			
//			Map<String, Object> tmp = new HashMap<>();
//			
//			tmp.put("orderCd", orderTmp.getOrderCd());
//			tmp.put("orderId", orderTmp.getOrderId());
//			
//			// 상점명
//			tmp.put("storeNmLan", orderTmp.getStore().getStoreNmLan());
//			// 주일 계산
//			calendar.setTime(orderTmp.getOrderDate());
//			int day_of_week = calendar.get(Calendar.DAY_OF_WEEK);
//			
//			String week = "";
//			switch (day_of_week) {
//			case 1: week = lanDataService.getLanData("일", LocaleContextHolder.getLocale()); break;
//			case 2: week = lanDataService.getLanData("월", LocaleContextHolder.getLocale()); break;
//			case 3: week = lanDataService.getLanData("화", LocaleContextHolder.getLocale()); break;
//			case 4: week = lanDataService.getLanData("수", LocaleContextHolder.getLocale()); break;
//			case 5: week = lanDataService.getLanData("목", LocaleContextHolder.getLocale()); break;
//			case 6: week = lanDataService.getLanData("금", LocaleContextHolder.getLocale()); break;
//			case 7: week = lanDataService.getLanData("토", LocaleContextHolder.getLocale()); break;
//			default: break;
//			}
//			
//			// 주문 일자
//			tmp.put("orderDate", sdf.format(orderTmp.getOrderDate()) + "(" + week + ")");
//			
//			List<String> menuData = new ArrayList<>();
//			
//			for (OrderSmenu menu : orderTmp.getSmenus()) {
//				String menuDataTmp = menu.getSmenuNmLan();
//				
//				if (menu.getSmenuOpts().size() > 0) {
//					menuDataTmp += "(";
//				} else {
//					menuDataTmp += " ";
//				}
//				
//				for (int i = 0, len = menu.getSmenuOpts().size(); i < len; i++) {
//					if (i == len-1) {
//						menuDataTmp += menu.getSmenuOpts().get(i).getSmenuOptNmLan() + ") ";
//					} else {
//						menuDataTmp += menu.getSmenuOpts().get(i).getSmenuOptNmLan() + " + ";
//					}
//				}
//				
//				menuDataTmp += menu.getCnt() + lanDataService.getLanData("개", LocaleContextHolder.getLocale());
//				
//				menuData.add(menuDataTmp);
//			}
//			
//			tmp.put("menuData", menuData);
//			
//			tmp.put("orderAmt", DecimalFormat.getNumberInstance().format(orderTmp.getAmt()) + orderTmp.getStore().getUnit());
//			
//			tmp.put("orderStatus", lanDataService.getLanData(orderTmp.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
//			tmp.put("orderStatusVal", orderTmp.getOrderStatus().getVal());
//			tmp.put("cancelYn", orderTmp.getCancelYn());
//			
//			// 배달 문구 설정
//			if ("Delivery".equals(orderTmp.getSalesTp().getVal())) {
//				tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef2(), LocaleContextHolder.getLocale()));
//			} else {
//				tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef1(), LocaleContextHolder.getLocale()));
//			}
//			
//			orderListTmp.add(tmp);
//		}
//		
//		result.put("list", orderListTmp);
//		result.put("remain", orderList.size() - jumpCnt);
//		
//		return result;
//	}
	
	
	/**
	 * 개별 회원 주문 상세 조회
	 * @param orderCd
	 * @param lanId
	 * @return 개별 회원 주문 상세
	 */
//	@GetMapping("/getOrder/{orderCd}/{lanId}")
//	public Map<String, Object> getOrder(@PathVariable Integer orderCd,
//			@PathVariable String lanId, Principal principal) {
//		appService.chkAndSettingLan(lanId);
//		
//		SimpleDateFormat sdf = new SimpleDateFormat(lanDataService.getLanData("yyyy년 MM월 dd일 HH:mm", LocaleContextHolder.getLocale()));
//		
//		Map<String, Object> tmp = new HashMap<>();
//		
//		AppUser appUser = appUserService.findOne(principal.getName());
//		
//		// 주문코드 없을 시
//		if (!ObjectUtils.isEmpty(orderCd)) {
//			Order orderTmp = orderService.findOne(orderCd);
//			
//			// 주문 정보 있을 시 , 내주문일 때만
//			if (!ObjectUtils.isEmpty(orderTmp) && orderTmp.getAppUser().getAccountId().equals(appUser.getAccountId())) {
//				tmp.put("orderCd", orderTmp.getOrderCd());
//				tmp.put("orderId", orderTmp.getOrderId());
//				
//				// 상점명
//				tmp.put("storeNmLan", orderTmp.getStore().getStoreNmLan());
//				// 주문 일자
//				tmp.put("orderDate", sdf.format(orderTmp.getOrderDate()));
//				
//				List<Map<String, Object>> menuData = new ArrayList<>();
//				
//				for (OrderSmenu menu : orderTmp.getSmenus()) {
//					Map<String, Object> menuDataTmp = new HashMap<>();
//					// 메뉴 정보
//					menuDataTmp.put("smenuNmLan", menu.getSmenuNmLan());
//					menuDataTmp.put("cnt", menu.getCnt());
//					
//					int menuAmt = menu.getPrice() * menu.getCnt();
//					
//					if (!ObjectUtils.isEmpty(menu.getDiscounts()) && menu.getDiscounts().size() > 0) {
//						// 메뉴 할인 계산
//						for (OrderDiscount discount : menu.getDiscounts()) {
//							if ("price".equals(discount.getDiscountTp().getVal())) {
//								menuAmt -= discount.getPrice() * menu.getCnt();
//							} else {
//								menuAmt -= (menu.getPrice() * discount.getPercente() * 0.01) * menu.getCnt();
//							}
//						}
//					}
//					
//					menuDataTmp.put("menuAmt", DecimalFormat.getNumberInstance().format(menuAmt) + orderTmp.getStore().getUnit());
//					
//					// 옵션 정보
//					List<Map<String, Object>> menuOptData = new ArrayList<>();
//					for (OrderSmenuOpt menuOpt : menu.getSmenuOpts()) {
//						Map<String, Object> menuOptDataTmp = new HashMap<>();
//						menuOptDataTmp.put("smenuOptNmLan", menuOpt.getSmenuOptNmLan());
//						menuOptDataTmp.put("cnt", menuOpt.getCnt());
//						menuOptDataTmp.put("menuOptAmt", DecimalFormat.getNumberInstance().format(menuOpt.getPrice() * menuOpt.getCnt()) + orderTmp.getStore().getUnit());
//						menuOptData.add(menuOptDataTmp);
//					}
//					menuDataTmp.put("menuOptData", menuOptData);
//					
//					menuData.add(menuDataTmp);
//				}
//				
//				tmp.put("menuData", menuData);
//				
//				tmp.put("orderAmt", DecimalFormat.getNumberInstance().format(orderTmp.getAmt()) + orderTmp.getStore().getUnit());
//				
//				tmp.put("orderStatus", lanDataService.getLanData(orderTmp.getOrderStatus().getNm(), LocaleContextHolder.getLocale()));
//				tmp.put("orderStatusVal", orderTmp.getOrderStatus().getVal());
//				tmp.put("cancelYn", orderTmp.getCancelYn());
//				
//				if ("OVER".equals(orderTmp.getOrderStatus().getVal()) && "Delivery".equals(orderTmp.getSalesTp().getVal())) {
//					tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef2(), LocaleContextHolder.getLocale()));
//				} else {
//					tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef1(), LocaleContextHolder.getLocale()));
//				}
//			}
//		}
//		
//		return tmp;
//	}
	
	
	
	/**
	 * 주문 취소
	 * 
	 * @param orderCd
	 * @param lanId
	 * @return 개별 회원 주문 상세
	 * @throws Exception 
	 */
	@GetMapping("/cancelOrder/{orderCd}/{lanId}")
	public void cancelOrder(Principal principal, HttpServletRequest req, HttpServletResponse res,
			@PathVariable Integer orderCd, @PathVariable String lanId) throws Exception {
		appService.chkAndSettingLan(lanId);
		
		boolean error = false;
		Map<String, Object> errorsMessage = new HashMap<String, Object>();
		
		AppUser appUser = appUserService.findOne(principal.getName());
		
		// 주문코드 있을 시
		if (!ObjectUtils.isEmpty(orderCd)) {
			Order order = orderService.findOne(orderCd);
			
			// 값 체크해서 정확하지 안으면 정상 진입이 아님 처리안하고 끝내 버림
			if (!ObjectUtils.isEmpty(principal) && !ObjectUtils.isEmpty(appUser) && !ObjectUtils.isEmpty(order)) {
				// 자기 주문만 처리
				if (order.getAppUser().getAccountId().equals(appUser.getAccountId())) {
					// 접수전 주문만 취소 가능
					if ("ORDER".equals(order.getOrderStatus().getVal())) {
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
									orderService.save(order);
								} else {
									error = true;
									errorsMessage.put("code", "cancelPayError");
									errorsMessage.put("field", "cancelPay");
									errorsMessage.put("errorMsg", lanDataService.getLanData("카드 결제 취소 실패 하였습니다.", LocaleContextHolder.getLocale()) +
											" " + resultMsg);
								}
							} else {
								order.setCancelYn("Y");
								orderService.save(order);
							}
						}
					} else {
						error = true;
						errorsMessage.put("code", "cancelOrderStatus");
						errorsMessage.put("field", "cancelOrderStatus");
						errorsMessage.put("errorMsg", lanDataService.getLanData("이미 준비중입니다. 취소 할 수 없습니다.", LocaleContextHolder.getLocale()));
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
						errorAttribute.put("path", "/api/app/cancelOrder/" + orderCd);
						
						ObjectMapper om = new ObjectMapper();
						
						String json = om.writeValueAsString(errorAttribute);
						
						res.getWriter().print(json);
					}
				}
			}
		}
	}
	
	
	
}
