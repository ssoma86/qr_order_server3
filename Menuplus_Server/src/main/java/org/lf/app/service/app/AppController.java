package org.lf.app.service.app;

import java.io.IOException;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.config.socket.WebSocketAsync;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUser.AppUserValid;
import org.lf.app.models.business.appUser.AppUserController.AppUserControllerJsonView;
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
import org.lf.app.models.business.user.User;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.Account.AccountPwValid;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.banner.Banner;
import org.lf.app.models.system.banner.BannerService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.terms.Terms;
import org.lf.app.models.system.terms.TermsAccept;
import org.lf.app.models.system.terms.TermsAcceptService;
import org.lf.app.models.system.terms.TermsService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.GpsUtil;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.RandomUtil;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/app")
public class AppController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());

	@Autowired
	private StoreService storeService;

	@Autowired
	private OrderService orderService;

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
	private BannerService bannerService;

	@Autowired
	private WebSocketAsync webSocketAsync;

	@Autowired
	private PayService payService;


	@Autowired
	private FileUtil fileUtil;

	@Value("${spring.application.name}")
    private String appNm;

	@Value("${spring.mail.username}")
    private String username;

	@Autowired
    private JavaMailSenderImpl sender;


	// JsonView
	public interface AppControllerJsonViewForStoreList {}
	public interface AppControllerJsonView {}
	public interface AppControllerJsonViewForOrderList {}
	public interface AppControllerJsonViewForOrder {}
	public interface AppControllerJsonViewForFavourite {}

	/**
	 * 상점 카테고리 리스트 조회
	 * @param lanId
	 * @return 상점 카테고리 리스트
	 */
//	@GetMapping("/getStoreTypeList/{lanId}")
//	@JsonView(AppControllerJsonView.class)
//	public List<Code> getStoreTypeList(@PathVariable String lanId) {
//		return null;
//	}


	/**
	 * 구분별 상점 리스트 조회
	 * @param lanId
	 * @param gpsLat
	 * @param gpsLng
	 * @return 상점 리스트 조회
	 */
	@GetMapping("/getStoreListByTp/{lanId}/{storeTp}/{gpsLat}/{gpsLng}")
	@JsonView(AppControllerJsonViewForStoreList.class)
	public Map<String, List<Store>> getStoreListByTp(@PathVariable String lanId, @PathVariable String storeTp,
			@PathVariable double gpsLat, @PathVariable double gpsLng, Principal principal) {
		appService.chkAndSettingLan(lanId);

		Set<Store> favourites = null;

		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());
			favourites = appUser.getFavourites();
		}

		List<Store> storeList = null;

		if ("00".equals(storeTp)) {
			storeList = storeService.findStoreList("Y");
		} else {
			storeList = storeService.findStoreList("Y", storeTp);
		}

		for (Store store : storeList) {
			storeService.settingStoreListDataForApp(store);

			// gps를 통해 거리 계산
			store.setDistance(GpsUtil.getDistance(store.getGpsLat(), store.getGpsLng(), gpsLat, gpsLng));

			for (Store favouriteStore : favourites) {
				if (favouriteStore.getStoreCd() == store.getStoreCd()) {
					store.setFavourite(true);
				}
			}
		}

		// 가까운 거리로 소트
		Collections.sort(storeList, new Comparator<Store>() {
			@Override
			public int compare(Store s1, Store s2) {
				double distance = s1.getDistance() - s2.getDistance();

				if (distance > 0) {
					return 1;
				} else if (distance < 0) {
					return -1;
				}

				return 0;
			}
		});

		Map<String, List<Store>> result = new HashMap<>();
		result.put("storeList", storeList);


		return result;
	}


	/**
	 * 상점 리스트 조회
	 * @param lanId
	 * @param gpsLat
	 * @param gpsLng
	 * @return 상점 리스트 조회
	 */
//	@GetMapping("/getStoreList/{lanId}/{gpsLat}/{gpsLng}")
//	@JsonView(AppControllerJsonViewForStoreList.class)
//	public List<Store> getStoreList(@PathVariable String lanId, @PathVariable double gpsLat, @PathVariable double gpsLng) {
//		appService.chkAndSettingLan(lanId);
//
//		List<Store> storeList = storeService.findStoreList("Y");
//
//		storeList.forEach(store -> {
//			storeService.settingStoreListDataForApp(store);
//
//			// gps를 통해 거리 계산
//			store.setDistance(GpsUtil.getDistance(store.getGpsLat(), store.getGpsLng(), gpsLat, gpsLng));
//		});
//
//		// 가까운 거리로 소트
//		Collections.sort(storeList, new Comparator<Store>() {
//			@Override
//			public int compare(Store s1, Store s2) {
//				double distance = s1.getDistance() - s2.getDistance();
//
//				if (distance > 0) {
//					return 1;
//				} else if (distance < 0) {
//					return -1;
//				}
//
//				return 0;
//			}
//		});
//
//		return storeList;
//	}


	/**
	 * 상점 데이타 조회
	 * @param clientTpVal
	 * @return 언어 데이타 리스트
	 */
	@GetMapping("/getStore/{storeId}/{lanId}")
	@Transactional
	@JsonView(AppControllerJsonView.class)
	public Store getStoreInfo(@PathVariable String storeId, @PathVariable String lanId, Principal principal) {
		appService.chkAndSettingLan(lanId);

		// 찜 조회
		Set<Store> favourites = null;

		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());
			favourites = appUser.getFavourites();
		}

		Store store = storeService.findOneByStoreId(storeId);

		if (!ObjectUtils.isEmpty(store)) {
			storeService.settingStoreDataForApp(store);	// 앱에서 필요한 데이타 설정

			// 찜 설정
			for (Store favouriteStore : favourites) {
				if (favouriteStore.getStoreCd() == store.getStoreCd()) {
					store.setFavourite(true);
				}
			}
		}

		return store;
	}



	/**
	 * 주문
	 * @param clientTpVal
	 * @throws IOException
	 */
//	@PostMapping("/order")
//	@Transactional
//	@JsonView(AppControllerJsonView.class)
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
//					orderService.save(order);
//
//					// 주문 통지 보내기
//					webSocketAsync.sendMsg(store.getStoreId(), order.getOrderDesc());
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
//		Order orderTmp = orderService.findOne(orderCd);
//
//		if (!ObjectUtils.isEmpty(orderTmp)) {
//			tmp.put("orderCd", orderTmp.getOrderCd());
//			tmp.put("orderId", orderTmp.getOrderId());
//
//			// 상점명
//			tmp.put("storeNmLan", orderTmp.getStore().getStoreNmLan());
//			// 주문 일자
//			tmp.put("orderDate", sdf.format(orderTmp.getOrderDate()));
//
//			List<Map<String, Object>> menuData = new ArrayList<>();
//
//			for (OrderSmenu menu : orderTmp.getSmenus()) {
//				Map<String, Object> menuDataTmp = new HashMap<>();
//				// 메뉴 정보
//				menuDataTmp.put("smenuNmLan", menu.getSmenuNmLan());
//				menuDataTmp.put("cnt", menu.getCnt());
//
//				int menuAmt = menu.getPrice() * menu.getCnt();
//
//				if (!ObjectUtils.isEmpty(menu.getDiscounts()) && menu.getDiscounts().size() > 0) {
//					// 메뉴 할인 계산
//					for (OrderDiscount discount : menu.getDiscounts()) {
//						if ("price".equals(discount.getDiscountTp().getVal())) {
//							menuAmt -= discount.getPrice() * menu.getCnt();
//						} else {
//							menuAmt -= (menu.getPrice() * discount.getPercente() * 0.01) * menu.getCnt();
//						}
//					}
//				}
//
//				menuDataTmp.put("menuAmt", DecimalFormat.getNumberInstance().format(menuAmt) + orderTmp.getStore().getUnit());
//
//				// 옵션 정보
//				List<Map<String, Object>> menuOptData = new ArrayList<>();
//				for (OrderSmenuOpt menuOpt : menu.getSmenuOpts()) {
//					Map<String, Object> menuOptDataTmp = new HashMap<>();
//					menuOptDataTmp.put("smenuOptNmLan", menuOpt.getSmenuOptNmLan());
//					menuOptDataTmp.put("cnt", menuOpt.getCnt());
//					menuOptDataTmp.put("menuOptAmt", DecimalFormat.getNumberInstance().format(menuOpt.getPrice() * menuOpt.getCnt()) + orderTmp.getStore().getUnit());
//					menuOptData.add(menuOptDataTmp);
//				}
//				menuDataTmp.put("menuOptData", menuOptData);
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
//
//			if ("OVER".equals(orderTmp.getOrderStatus().getVal()) && "Delivery".equals(orderTmp.getSalesTp().getVal())) {
//				tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef2(), LocaleContextHolder.getLocale()));
//			} else {
//				tmp.put("orderStatusDesc", lanDataService.getLanData(orderTmp.getOrderStatus().getRef1(), LocaleContextHolder.getLocale()));
//			}
//
//		}
//
//		return tmp;
//	}



	/**
	 * 주문 취소
	 * @param orderCd
	 * @param lanId
	 * @return 개별 회원 주문 상세
	 * @throws Exception
	 */
	@GetMapping("/cancelOrder/{orderCd}/{lanId}")
	public void cancelOrder(@PathVariable Integer orderCd,
			@PathVariable String lanId, Principal principal, HttpServletRequest req, HttpServletResponse res) throws Exception {
		appService.chkAndSettingLan(lanId);

		boolean error = false;
		List<Map<String, Object>> errorsMessages = new ArrayList<>();
		Map<String, Object> errorsMessage = new HashMap<String, Object>();

		AppUser appUser = appUserService.findOne(principal.getName());

		Order order = orderService.findOne(orderCd);

		// 값 체크해서 정확하지 안으면 정상 진입이 아님 처리안하고 끝내 버림
		if (!ObjectUtils.isEmpty(principal) && !ObjectUtils.isEmpty(appUser) && !ObjectUtils.isEmpty(orderCd) && !ObjectUtils.isEmpty(order)) {
			// 자기 주문만 처리
			if (order.getAppUser().getAccountId().equals(appUser.getAccountId())) {
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
									" 결과메세지: " + resultMsg);
							errorsMessages.add(errorsMessage);
						}
					} else {
						order.setCancelYn("Y");
						orderService.save(order);
					}
				}

				if (error) {
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					res.setCharacterEncoding("UTF-8");
					res.setContentType("application/json; charset=utf-8");

					Map<String, Object> errorAttribute = new HashMap<String, Object>();
					errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
					errorAttribute.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					errorAttribute.put("error", "Order cancel error");
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



	/**
	 * 약관동의 여부 조회
	 *
	 */
	@GetMapping("/getTermsAccept/{lanId}/{uuid}")
	public List<Terms> getTermsAccept(@PathVariable String lanId, @PathVariable String uuid) {
		appService.chkAndSettingLan(lanId);

		List<Terms> result = new ArrayList<>();

		// 해당 UUID 동의한 내용 조회
		List<TermsAccept> termsAcceptList = termsAcceptService.findByUuid(uuid);
		// 서비스에서 제공하는 약관 내용 조회
		List<Terms> termsList = termsService.findByLan(lanId);

		// 서비스에서 제공하는 약관과 해당 UUID 동의 한 약관정보 비교해서 미동의 약관내용 내려주기
		for (Terms terms : termsList) {
			boolean accept = false;

			for (TermsAccept termsAccept : termsAcceptList) {
				if (terms.getTermsTp().getCd().equals(termsAccept.getTermsTp().getCd())) {
					accept = true;
				}
			}

			if (!accept) {
				result.add(terms);
			}
		}

		return result;
	}



	/**
	 * 약관동의
	 *
	 */
	@PostMapping("/termsAccept/{uuid}/{termsTpVal}")
	public void termsAccept(@PathVariable String uuid, @PathVariable String termsTpVal) {
		Code termsTp = codeService.findOneByTopCodeValAndVal("TERMS_TP", termsTpVal);

		TermsAccept termsAccept = new TermsAccept();
		termsAccept.setUuid(uuid);
		termsAccept.setTermsTp(termsTp);
		termsAccept.setAcceptDtm(new Date());

		termsAcceptService.save(termsAccept);
	}


	/**
	 * 소셜 회원 가입
	 * @param lanId
	 * @throws Exception
	 */
	@PostMapping("/socialLogin")
	@JsonView(AppUserControllerJsonView.class)
	public Map<String, Object> socialLogin(String socialType, String token, HttpServletRequest req) throws Exception {
		AppUser user = null;

		// 소셜 회원 등록
		if ("kakao".equals(socialType)) {
			user = appService.kakaoLogin(token);
		} else if ("naver".equals(socialType)) {
			user = appService.naverLogin(token);
		}

		String serverUrl = req.getRequestURL().toString().replace(req.getRequestURI(), "");
		// 토큰 주소
		String oauthUrl = serverUrl + "/oauth/token";

		// 자체 서버에서 토큰 발행 하기
		Map<String, Object> result = appService.getToken(user.getAccountId(), user.getSocialPassword(), oauthUrl);
		result.put("appUser", user);

		return result;
	}


	/**
	 * 회원 가입
	 * @param lanId
	 * @throws Exception
	 */
	@PostMapping("/registered")
	@JsonView(AppUserControllerJsonView.class)
	public AppUser registered(@Validated({AppUserValid.class, AccountPwValid.class }) @RequestBody AppUser user, HttpServletResponse res) throws Exception {
		List<Account> accountList = accountService.findOneByEmail(user.getEmail());

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

		// 이메일 사용여부 체크
		if (isExist) {
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
			errorsMessage.put("value", user.getEmail());
			errorsMessage.put("errorMsg", lanDataService.getLanData("이미 회원 가입 되어 있는 이메일주소입니다.", LocaleContextHolder.getLocale()));

			errorsMessages.add(errorsMessage);

			errorAttribute.put("errorsMessage", errorsMessages);
			errorAttribute.put("message", "Validation failed for object='appUser'. Error count: 1");
			errorAttribute.put("path", "/api/app/registered");

			ObjectMapper om = new ObjectMapper();

			String json = om.writeValueAsString(errorAttribute);

			res.getWriter().print(json);

			return null;
		} else {
			return appService.registered(user);
		}
	}


	/**
	 * 회원 정보 조회
	 * @param accountId
	 * @throws Exception
	 */
	@GetMapping("/user")
	@JsonView(AppUserControllerJsonView.class)
	@Transactional
	public AppUser user(Principal principal) throws Exception {
		if (principal != null) {
			return appUserService.findOne(principal.getName());
		} else {
			return null;
		}
	}


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
//				user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
//              user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
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
	@PostMapping("/push/{token}")
	@Transactional
	public void pushToken(@PathVariable String token, Principal principal) throws Exception {
		if (principal != null) {
			AppUser user = appUserService.findOne(principal.getName());
			user.setPushToken(token);
			appUserService.save(user);
		}
	}


	/**
	 * 이메일 통해서 비밀번호 찾기
	 *
	 * @return 결과코드
	 */
	@GetMapping("/forget/{email}")
	public @ResponseBody Map<String, Object> forget(@PathVariable String email,
			HttpServletResponse res, HttpServletRequest req) {
		Map<String, Object> result = new HashMap<String, Object>();

		// 이메일 입력여부 체크
		if (StringUtils.isEmpty(email)) {
			result.put("resultCode", 12);
			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		// 이메일 정확성 체크
		} else if (!ValidUtil.isEmail(email)) {
			result.put("resultCode", 13);
			result.put("resultMsg", lanDataService.getLanData("이메일 주소를 정확히 입력 하여 주십시오.", LocaleContextHolder.getLocale()));
		} else {
			List<AppUser> accountTmp = appUserService.findOneByEmail(email);

			// 앱 회원만 가져와서 체크함
			AppUser account = null;

			for (AppUser tmp : accountTmp) {
				for (Auth auth : tmp.getAuths()) {
					if (auth.getAuthId().equals("appUser")) {
						account = tmp;
						break;
					}
				}
			}

			if (ObjectUtils.isEmpty(account)) {
				result.put("resultCode", 21);
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

		        account.setTmpPassword(RandomUtil.getStringRandom(50));

		        message.setText(showMsg + serverUrl + "/pwChangeView/" + account.getTmpPassword());

		        sender.send(message);

		        appUserService.save(account);
			}
		}

		return result;
	}


	/**
	 * 배너 조회
	 * @return 배너 이미지 주소
	 */
	@GetMapping("/getBanner")
	public List<String> getBanner() {
		List<String> result = new ArrayList<>();

		List<Banner> bannerList = bannerService.findBannerList("Y");

		for (Banner banner : bannerList) {
			result.add(banner.getBannerImg());
		}

		return result;
	}


	/**
	 * 찜 정보 추가
	 * @param storeId
	 * @throws Exception
	 */
	@PostMapping("/favourite/{storeId}")
	@Transactional
	public void favourite(@PathVariable String storeId, Principal principal, HttpServletResponse res) throws Exception {
		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());

			Set<Store> favourites = appUser.getFavourites();

			// 찜 정보 있는지 체크 없으면 새로 생성
			if (ObjectUtils.isEmpty(favourites)) {
				favourites = new HashSet<>();
			}

			Store store = storeService.findOneByStoreId(storeId);

			// 상점 있는지 체크
			if (ObjectUtils.isEmpty(store)) {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				res.setCharacterEncoding("UTF-8");
				res.setContentType("application/json; charset=utf-8");

				Map<String, Object> errorAttribute = new HashMap<String, Object>();
				errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
				errorAttribute.put("error", "Bad Request");

				List<Map<String, Object>> errorsMessages = new ArrayList<>();

				Map<String, Object> errorsMessage = new HashMap<String, Object>();
				errorsMessage.put("code", "storeIdIsNotExist");
				errorsMessage.put("field", "storeId");
				errorsMessage.put("value", storeId);
				errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));

				errorsMessages.add(errorsMessage);

				errorAttribute.put("errorsMessage", errorsMessages);
				errorAttribute.put("message", "Validation failed for storeId. Error count: 1");
				errorAttribute.put("path", "/api/app/favourite/" + storeId);

				ObjectMapper om = new ObjectMapper();

				String json = om.writeValueAsString(errorAttribute);

				res.getWriter().print(json);
			} else {
				favourites.add(store);
				appUser.setFavourites(favourites);

				appUserService.save(appUser);
			}
		}
	}


	/**
	 * 찜 리스트 가져오기
	 * @return 찜 리스트
	 */
	@GetMapping("/favourite")
	@JsonView(AppControllerJsonViewForFavourite.class)
	public Map<String, Set<Store>> getFavourite(Principal principal) {

		Map<String, Set<Store>> result = new HashMap<>();

		Set<Store> favourites = null;

		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());
			favourites = appUser.getFavourites();
		}

		favourites.forEach(store -> {
			storeService.settingStoreListDataForApp(store);
		});

		result.put("storeList", favourites);

		return result;
	}


	/**
	 * 찜 정보 추가
	 * @param storeId
	 * @throws Exception
	 */
	@PostMapping("/delFavourite/{storeId}")
	@Transactional
	public void delFavourite(@PathVariable String storeId, Principal principal, HttpServletResponse res) throws Exception {
		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());

			Store store = storeService.findOneByStoreId(storeId);

			// 상점 있는지 체크
			if (ObjectUtils.isEmpty(store)) {
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				res.setCharacterEncoding("UTF-8");
				res.setContentType("application/json; charset=utf-8");

				Map<String, Object> errorAttribute = new HashMap<String, Object>();
				errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
				errorAttribute.put("error", "Bad Request");

				List<Map<String, Object>> errorsMessages = new ArrayList<>();

				Map<String, Object> errorsMessage = new HashMap<String, Object>();
				errorsMessage.put("code", "storeIdIsNotExist");
				errorsMessage.put("field", "storeId");
				errorsMessage.put("value", storeId);
				errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));

				errorsMessages.add(errorsMessage);

				errorAttribute.put("errorsMessage", errorsMessages);
				errorAttribute.put("message", "Validation failed for storeId. Error count: 1");
				errorAttribute.put("path", "/api/app/favourite/" + storeId);

				ObjectMapper om = new ObjectMapper();

				String json = om.writeValueAsString(errorAttribute);

				res.getWriter().print(json);
			} else {
				Set<Store> favourites = appUser.getFavourites();

				// 찜 정보 있는지 체크 없으면 새로 생성
				if (!ObjectUtils.isEmpty(favourites)) {
					favourites.removeIf(storeTmp -> store.getStoreId().equals(storeTmp.getStoreId()));
				}

				appUser.setFavourites(favourites);

				appUserService.save(appUser);
			}
		}
	}


}
