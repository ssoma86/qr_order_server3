package org.lf.app.service.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.If.app.models.business.pushAlarm.ApnsSender;
import org.If.app.models.business.pushAlarm.FcmService;
import org.If.app.models.business.pushAlarm.PushLog;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.lf.app.ViewConstants;
import org.lf.app.config.socket.WebSocketAsync;
import org.lf.app.config.socket.WebSocketAsync4App;
import org.lf.app.models.business.delivery.Delivery;
import org.lf.app.models.business.member.MemberService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.OrderRepository;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.order.pay.PayService;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.order.smenuOpt.OrderSmenuOpt;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.SmenuService;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.CategoryService;
import org.lf.app.models.business.category.OrdSmenuInCategory;
import org.lf.app.models.business.cust.Cust;
import org.lf.app.models.business.cust.CustService;
import org.lf.app.models.business.cust.PayMethod;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreRoom;
import org.lf.app.models.business.store.StoreRoomService;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountRepository;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerTopJsonView;
import org.lf.app.models.system.history.HistoryRepository;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import kr.co.infinisoft.menuplus.util.AESCrypto;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

@RestController
@Transactional
@RequestMapping("/api")
public class WebAppController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(WebAppController.class);
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private StoreRoomService storeRoomService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private WebSocketAsync4App webSocketAsync;
	
	@Autowired
	private PayService payService;

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private SmenuService smenuService;
	
	@Autowired
	private CustService custService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private HistoryRepository historyRepository;
	
//	@Autowired
//	private FcmService fcmService;
	
	@Value("${crypto_key}") 
	private String crypto_key;
	
	// JsonView
	public interface WebAppControllerJsonView extends LanControllerCommonJsonView, CodeControllerCommonJsonView {}
		
	
	/**
	 * 상점 데이타 조회
	 * @param clientTpVal
	 * @return 언어 데이타 리스트
	 */
	@GetMapping("/getStore/{storeId}")
	@JsonView(WebAppControllerJsonView.class)
	public Store getStoreInfo(@PathVariable String storeId) {
		
		Store store = storeService.findOneByStoreId(storeId);
	
		if (!ObjectUtils.isEmpty(store)) {
			storeService.settingStoreDataForWeb(store);	// 필요한 데이타 설정
		}
		
		return store;
	}
	
	/**
	 * 상점 객실정보 조회
	 * @param clientTpVal
	 * @return 언어 데이타 리스트
	 */
	@GetMapping("/getStoreRoom/{storeRoomCd}")
	@JsonView(WebAppControllerJsonView.class)
	public StoreRoom getStoreRoomInfo(@PathVariable int storeRoomCd) {
		
		logger.info("=======================start getStoreRoomInfo =========================");
		logger.info("===========getStoreRoomInfo storeRoomCd :" + storeRoomCd);
		StoreRoom storeRoom = storeRoomService.findOne(storeRoomCd);
	
		//qr접속로그저장(24.02.21)
		historyRepository.insertQrLog("Y", storeRoom.getStore().getStoreCd(), storeRoom.getStore().getStoreNm(), storeRoomCd, storeRoom.getStoreRoomNm());
		
		if (!ObjectUtils.isEmpty(storeRoom)) {
			storeService.settingStoreDataForWeb(storeRoom.getStore());	// 필요한 데이타 설정
			
			List<Category> tempCategorys = storeRoom.getStore().getCategorys();
			int categoryCd = 0;
			Smenu sTempMenu = null;
			List<Category> categorys = new ArrayList();
			List<String> saSmenuImgList = null;
			
			if(tempCategorys!=null && tempCategorys.size() > 0) {
				List<OrdSmenuInCategory> ordSmenuInCategorys  = null;
				for(Category cate : tempCategorys) {
					categoryCd = cate.getCategoryCd();
					ordSmenuInCategorys = categoryService.findSmenuOrdList4Category(categoryCd);
					
					if(ordSmenuInCategorys!=null && ordSmenuInCategorys.size() > 0) {
						
						for(OrdSmenuInCategory osc : ordSmenuInCategorys ) {							
							sTempMenu = smenuService.findOne(osc.getSmenuCd());
							if(null != sTempMenu && sTempMenu.getUseYn().contentEquals("Y")) { //23.10.24 useYn ='Y' 인 것만 담는다.
								
								saSmenuImgList = new ArrayList();
								
								if(null != sTempMenu.getSmenuImg() && !sTempMenu.getSmenuImg().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg());
								}
								
								if(null != sTempMenu.getSmenuImg1() && !sTempMenu.getSmenuImg1().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg1());
								}
								
								if(null != sTempMenu.getSmenuImg2() && !sTempMenu.getSmenuImg2().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg2());
								}
								
								sTempMenu.setSmenuImgList(saSmenuImgList.toArray(new String[saSmenuImgList.size()])); //(23.10.24) 메뉴이미지 리스트 세팅
								
								osc.setSmenu(sTempMenu);
							}
						}
						
					}
					
					cate.setOrdSmenuInCategory(ordSmenuInCategorys);
					categorys.add(cate);
					
				}
				
				storeRoom.getStore().setCategorys(categorys);
				
			}
			
		}
		
		return storeRoom;
	}
	
	/**
	 * yajo용 사용자화면 데이터 호출 api(최초 23.12.27)
	 * @param storeId
	 * @param storeRoomNm
	 * @return
	 */
	@GetMapping("/yajo/getStoreRoom/{storeId}/{storeRoomNm}")
	@JsonView(WebAppControllerJsonView.class)
	public StoreRoom getStoreRoomInfo4jajo(@PathVariable String storeId, @PathVariable String storeRoomNm) {
		
		logger.info("=======================start getStoreRoomInfo4jajo =========================");
		logger.info("===========getStoreRoomInfo4jajo storeRoomNm :" + storeRoomNm);
		
		Store store = storeService.findOneByStoreId(storeId);		
		int storeRoomCd = storeRoomService.getStoreRoomCd(store.getStoreCd(), storeRoomNm,"Y");
		
		StoreRoom storeRoom = storeRoomService.findOne(storeRoomCd);
		
		//yajo 접속로그저장 (24.04.05)
		historyRepository.insertQrLog("Y", storeRoom.getStore().getStoreCd(), storeRoom.getStore().getStoreNm(), storeRoomCd, storeRoom.getStoreRoomNm());
	
		if (!ObjectUtils.isEmpty(storeRoom)) {
			storeService.settingStoreDataForWeb(storeRoom.getStore());	// 필요한 데이타 설정
			
			List<Category> tempCategorys = storeRoom.getStore().getCategorys();
			int categoryCd = 0;
			Smenu sTempMenu = null;
			List<Category> categorys = new ArrayList();
			List<String> saSmenuImgList = null;
			
			if(tempCategorys!=null && tempCategorys.size() > 0) {
				List<OrdSmenuInCategory> ordSmenuInCategorys  = null;
				for(Category cate : tempCategorys) {
					categoryCd = cate.getCategoryCd();
					ordSmenuInCategorys = categoryService.findSmenuOrdList4Category(categoryCd);
					
					if(ordSmenuInCategorys!=null && ordSmenuInCategorys.size() > 0) {
						
						for(OrdSmenuInCategory osc : ordSmenuInCategorys ) {							
							sTempMenu = smenuService.findOne(osc.getSmenuCd());
							if(null != sTempMenu && sTempMenu.getUseYn().contentEquals("Y")) { //23.10.24 useYn ='Y' 인 것만 담는다.
								
								saSmenuImgList = new ArrayList();
								
								if(null != sTempMenu.getSmenuImg() && !sTempMenu.getSmenuImg().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg());
								}
								
								if(null != sTempMenu.getSmenuImg1() && !sTempMenu.getSmenuImg1().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg1());
								}
								
								if(null != sTempMenu.getSmenuImg2() && !sTempMenu.getSmenuImg2().isEmpty()) {
									saSmenuImgList.add(sTempMenu.getSmenuImg2());
								}
								
								sTempMenu.setSmenuImgList(saSmenuImgList.toArray(new String[saSmenuImgList.size()])); //(23.10.24) 메뉴이미지 리스트 세팅
								
								osc.setSmenu(sTempMenu);
							}
						}
						
					}
					
					cate.setOrdSmenuInCategory(ordSmenuInCategorys);
					categorys.add(cate);
					
				}
				
				storeRoom.getStore().setCategorys(categorys);
				
			}
			
		}
		
		return storeRoom;
	}
	
	@GetMapping("/getDelivery/{storeCd}")
	@JsonView(WebAppControllerJsonView.class)
	public List<Map<String,Object>> getDelivery(@PathVariable Integer storeCd) {
		List<Map<String,Object>> list = new ArrayList();
	
		list = memberService.selectDelivery(storeCd);
		
		return list;
	}
	
	/**
	 * 객실 주문 등록
	 * @throws IOException 
	 */
	@PostMapping("/order4Room")
	@JsonView(WebAppControllerJsonView.class)
	public Order order4Room(@Validated(OrderValid.class) @RequestBody Order order, HttpServletResponse res) throws Exception {
		Store store = storeService.findOneByStoreId(order.getStoreId());
		StoreRoom storeRoom = storeRoomService.findOne(order.getStoreRoomCd());
		
		// 벨리데이션 오류 처리
		boolean error = false;
		Map<String, Object> errorsMessage = new HashMap<String, Object>();
		
		int orderIdCnt = orderService.getCount4OrderId(order.getOrderId()); //주문번호 유무확인
		
		logger.info("=============orderIdCnt:" + orderIdCnt);
		
		//주문번호가 존재하지 않을때만 주문 등록을 한다.
		if(orderIdCnt==0) {
		
			if (ObjectUtils.isEmpty(store)) {
				error = true;
				errorsMessage.put("code", "isNotExist");
				errorsMessage.put("field", "storeCd");
				errorsMessage.put("value", order.getStoreId());
				errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
			} else if ("N".equals(store.getUseYn())) {
				error = true;
				errorsMessage.put("code", "isNotUsed");
				errorsMessage.put("field", "useYn");
				errorsMessage.put("value", "N");
				errorsMessage.put("errorMsg", lanDataService.getLanData("사용기간 만료된 매장 입니다.", LocaleContextHolder.getLocale()));
			} else {
				// 오픈 여부 설정
				storeService.setSniffling(store);	// 휴무 설정
				storeService.setOpen(store);
				
				if (!store.isOpen()) {
					error = true;
					errorsMessage.put("code", "isNotOpen");
					errorsMessage.put("field", "open");
					errorsMessage.put("value", false);
					errorsMessage.put("errorMsg", lanDataService.getLanData("영업시간이 종료 되었습니다.", LocaleContextHolder.getLocale()));
				} else {
					// 주문 정보 설정
					order.setStore(store);
					orderService.setOrderData4Room(order);
					order.setStoreRoom(storeRoom); //객실정보 order객체에 setting
					order.setUseYn("Y");
						
					orderService.save(order); //주문 등록
					
					return order;
				}
			}
		}else {
			error = true;
			errorsMessage.put("errorMsg", lanDataService.getLanData("이미 등록된 주문입니다.", LocaleContextHolder.getLocale()));
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
			errorAttribute.put("path", "/api/app/order");
			
			ObjectMapper om = new ObjectMapper();
			
			String json = om.writeValueAsString(errorAttribute);
			
			res.getWriter().print(json);
		}
		
		return null;
	}	
	
	/**
	 * 결제방법 조회
	 * @param storeRoomCd
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/payMethod/{storeRoomCd}")
	@JsonView(WebAppControllerJsonView.class)
	public PayMethod getPayMethod(@PathVariable int storeRoomCd) throws IOException {
		
		logger.info("=======================start getPayMethod =========================");
		logger.info("===========getPayMethod storeRoomCd :" + storeRoomCd);
		
		StoreRoom storeRoom = storeRoomService.findOne(storeRoomCd);
		Store store = storeRoom.getStore();
		
		Cust cust = store.getCust();
		PayMethod payMethod = custService.selectPayMethods4CustCd(cust.getCustCd());
		
		String [] saPaypayMethod = payMethod.getMethods().split(",");
		payMethod.setPayMethodList(Arrays.asList(saPaypayMethod));
		
		return payMethod;
	}	
	
		
	/**
	 * 주문 등록
	 * @throws IOException 
	 */
	@PostMapping("/order")
	@JsonView(WebAppControllerJsonView.class)
	public Order order(@Validated(OrderValid.class) @RequestBody Order order, HttpServletResponse res) throws IOException {
		Store store = storeService.findOneByStoreId(order.getStoreId());
		
		// 벨리데이션 오류 처리
		boolean error = false;
		Map<String, Object> errorsMessage = new HashMap<String, Object>();
		
		if (ObjectUtils.isEmpty(store)) {
			error = true;
			errorsMessage.put("code", "isNotExist");
			errorsMessage.put("field", "storeCd");
			errorsMessage.put("value", order.getStoreId());
			errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
		} else if ("N".equals(store.getUseYn())) {
			error = true;
			errorsMessage.put("code", "isNotUsed");
			errorsMessage.put("field", "useYn");
			errorsMessage.put("value", "N");
			errorsMessage.put("errorMsg", lanDataService.getLanData("사용기간 만료된 매장 입니다.", LocaleContextHolder.getLocale()));
		} else {
			// 오픈 여부 설정
			storeService.setSniffling(store);	// 휴무 설정
			storeService.setOpen(store);
			
			if (!store.isOpen()) {
				error = true;
				errorsMessage.put("code", "isNotOpen");
				errorsMessage.put("field", "open");
				errorsMessage.put("value", false);
				errorsMessage.put("errorMsg", lanDataService.getLanData("영업시간이 종료 되었습니다.", LocaleContextHolder.getLocale()));
			} else {
				// 주문 정보 설정
				
				List<Map<String, Object>> memberInfo = new ArrayList<>();
				
				memberInfo = memberService.selectMemberPost(order.getTel());
				
				boolean flag = false;
				
				//중복된 주소면 insert 안되도록 처리
				for(Map<String, Object> x : memberInfo) {
					String addr = x.get("addr").toString();
					String addrDtl = x.get("addr_dtl").toString();
					
					if(order.getAddr().contains(addr) && order.getAddrDtl().contains(addrDtl))					
						flag = true;
					
				}
				if(flag == false)
					memberService.insertMemberPost(order.getAddr(), order.getAddrDtl(), order.getTel());
				
					//order.setStore(store);
					orderService.setOrderData(order);
			 
				if ("PAY_FIRST".equals(order.getPayTp())) {
					order.setUseYn("Y");
				} else {
					// 후결제 시 주문 코드 발행 bsm
					//orderService.setOrderId(order);
					//order.setSalesTp(salesTp);
				}
					if(order.getAddr() != null && order.getAddrDtl() !=null) {
						
					String addr = order.getAddr() +" "+ order.getAddrDtl();
					order.setOrderAddr(addr);
					}
					orderService.save(order);
					
				// 선결제 시 주문 아직 사용 불가 상태임, 
				if ("PAY_FIRST".equals(order.getPayTp())) {
					// 주문 통지 보내기
					//webSocketAsync.sendMsg(store.getStoreId());
				}
				
				return order;
			}
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
			errorAttribute.put("path", "/api/app/order");
			
			ObjectMapper om = new ObjectMapper();
			
			String json = om.writeValueAsString(errorAttribute);
			
			res.getWriter().print(json);
		}
		
		return null;
	}	
	
	/**
	 * 시스템 장애로 인한 결제취소시 업데이트 
	 * @param tid
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/updateCancelYn4SysErr/{tid}")
	@JsonView(WebAppControllerJsonView.class)
	public String updateCancelYn4SysErr(@PathVariable String tid) throws IOException {
		
		logger.info("===========================================updateCancelYn4SysErr start===================================");
		
		try {
			logger.info("=========tid :" + tid);
			
			orderService.updateCancelYn4SysErr(tid);
			
			logger.info("===========================================updateCancelYn4SysErr end===================================");
			
			return "0000";
			
		}catch(Exception e) {
			e.toString();
			return "0001";
		}
		
		
		
	}
	
	/**
	 * 주소 조회 API
	 * @throws IOException 
	 */
	@PostMapping("/searchPost")
	public List<Map<String, Object>> member(@RequestBody HashMap<String,String> info,HttpServletRequest request) throws IOException {
		
		// 벨리데이션 오류 처리
		
		String tel = info.get("hp2");
		List<Map<String, Object>> memberInfo = new ArrayList<>();
		
		memberInfo = memberService.selectMemberPost(tel);	
								
				return memberInfo;
	}
		
	/**
	 * 주소 삭제 API
	 * @throws IOException 
	 */
	@PostMapping("/delAddr")
	public String deletePost(@RequestBody HashMap<String,Integer> cd) throws IOException {
		
		// 벨리데이션 오류 처리
		String resultCode="";
		int addrCd = cd.get("cd");
		List<Map<String, Object>> memberInfo = new ArrayList<>();
		try {
			memberService.delMemberPost(addrCd);
			resultCode="0000";
		}catch(Exception e) {
			 logger.error("delAddr: " + e.getMessage());
		}							
				return resultCode;
	}
		 
	 //주소 조회 API
	 @PostMapping({"/noti"})
	 public void noti(HttpServletRequest request) throws Exception {
	    request.setCharacterEncoding("utf-8");
	    logger.info("===========================================NotiResult===================================");
	    HashMap<String, String> resData = new HashMap<>();
	    Enumeration<?> em = request.getParameterNames();
	    try {
	    	
	      while (em.hasMoreElements()) {
	        String key = (String)em.nextElement();
	        resData.put(key, URLDecoder.decode(request.getParameter(key), "utf-8"));
	      } 
	      logger.info("NotiResult parameter [" + resData.toString() + "]");
	      String payMethod = resData.get("payMethod");
	      String mid = resData.get("shopCode");
	      String moid = resData.get("moid");
	      String tid = resData.get("transSeq");
	      String orderCode = resData.get("mallUserId");
	      String mallReserved = resData.get("mallReserved");
	      String goodsName = resData.get("goodsName");
	      String goodsAmt = resData.get("goodsAmt");
	      String status = resData.get("status");
	      String approvalNo = resData.get("approvalNo");
	      String pgAppDate = resData.get("pgAppDate");
	      String cancelAppDate = resData.get("cancelAppDate");
	      String cancelAmt = resData.get("cancelAmt");
	      try {		    	 
	    	  
	        if ("25".equals(status)) {
	        	
	          try {	        	
	            orderService.updateOrderList(moid);
	        	
	          } catch (Exception e) {
	            logger.error("NotiResult: " + e.getMessage());
	          } 
	        } else if ("85".equals(status)) {
	          logger.error("DB  status : " + status);
	        } 
	      } catch (Exception e) {
	        logger.error("NotiResult: " + e.getMessage());
	      } 
	    } catch (Exception e) {
	      e.printStackTrace();
	      logger.error("NotiResult: " + e.getMessage());
	    } 
	}
		 
	/**
	 * 주문 내역 조회
	 * @return 주문 상태
	 */
	@GetMapping("/getOrderStatus/{orderCd}")
	@JsonView({ CodeControllerTopJsonView.class })
	public Map<String, Object> getOrderStatus(@PathVariable Integer orderCd) {
		
		Map<String, Object> result = new HashMap<>();
	
				//orderService.getOrderCd(hp);
		//Store store = storeService.findOne(storeCd);
		
		// 상점 코드 맞는지 체크
		//if (!ObjectUtils.isEmpty(store)) {
			Order order = orderService.findOne(orderCd);
			
			// 주문 맞는지 체크 함
			if (!ObjectUtils.isEmpty(order)) {
		
				Code code = order.getOrderStatus();
				JSONArray array = new JSONArray();

				for( OrderSmenu order5 : order.getSmenus()) {
					try {
				  Gson gson = new Gson();
			        String reqStr = gson.toJson(order5);
			        JSONObject reqJson = (JSONObject) new JSONParser().parse(reqStr);    
			        reqJson.put("smenuNmLan", order5.getSmenuNmLan());
			        
			        array.add(reqJson);
					}
					catch(Exception e)
					{
						e.getStackTrace();
					}
				}        

				code.setNmLan(lanDataService.getLanData(code.getNm(), LocaleContextHolder.getLocale()));
				Code cancelCode = order.getCancelReason();				
				
				//결제성공한 건만 전달
				if(order!=null && order.getPayYn().equals("Y")) {
					result.put("orderStatus", code);
					result.put("cancelYn", order.getCancelYn());
					result.put("orderDate", order.getOrderDate());
					result.put("addr", order.getAddr());
					result.put("Amount", order.getAmt());
					result.put("addrDtl", order.getAddrDtl());
					result.put("smenus", array);
					result.put("deliveryCost", order.getDeliveryCost());
					result.put("storeRoomNm", order.getStoreRoom().getStoreRoomNm());
					result.put("waitTime", order.getWaitTime());						//대기시간 (24.02.14 추가)
					
					if(null != order.getCancelYn() && order.getCancelYn().equals("Y") && cancelCode!=null) {
						result.put("cancelReason", cancelCode.getNm());  //취소사유 (24.02.20추가)
					}else {
						result.put("cancelReason", "");
					}
					
					return result;
					
				}else {
					
					return null;
					
				}
					        
				
			//}
		}
		
		return null;
	}
	
	/**
	 * pg.tb_trans_inform 테이블에 등록된 inform_way 정보로 전달된 결제정보를 업데이트(24.01.15)
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/innopayNoti")
	public String innopayNoti(HttpServletRequest request, HttpServletResponse response) {
		
		logger.info("========================innopayNoti start===========================");
		
		//노티 수신 데이터
		Enumeration eNames = request.getAttributeNames();
		if(eNames.hasMoreElements()) {
			Map entries = new TreeMap();
			while(eNames.hasMoreElements()) {
				String name = (String) eNames.nextElement();
				List values = new ArrayList();
				values.add(request.getAttribute(name));
				if(values.size() > 0) {
					String value = values.get(0).toString();
					for(int i = 1; i < values.size(); i++) {
						value += "," + values.get(i);
					}
					logger.info(name + "[" + value + "]");
				}
			}
		}
		
		String orderId = request.getParameter("moid");				//주문번호
		String tid = request.getParameter("transSeq");				//거래번호
		String payMethod = request.getParameter("payMethod");		//결제수단
		String mid = request.getParameter("shopCode");				//mid
		String cancelYn = request.getParameter("status");			//25:승인 ,85:취소
		
		String svcCd = "";
		if(null != payMethod) {
			if(payMethod.contentEquals("0108") || payMethod.contentEquals("02") ) {
				svcCd = "01";
			}else {
				svcCd = "16";
			}
		}		
		
		int orderIdCnt = orderService.getCount4OrderId(orderId); //주문번호 유무확인
		List<String> accountIdList = null;
		Order order = orderService.findByOid(orderId);
		
		int orderIdPushCnt = orderService.getPushOrderCount4OrderId(orderId); //push알림이 발송안된 주문 건인지 확인
		//정상케이스
		//승인 - tid not null
		//취소 - cancelYN : Y
//		if(null != order) {
//			if("25".equals(cancelYn) && order.getTid() == null) {
//				//노티수신 : 승인, DB조회 : tid 없음(정상 업데이트 미처리)
//				//1. 승인 업데이트
//				//2. 알림 발송
//			} else if("85".equals(cancelYn) && !"Y".equals(order.getCancelYn())){
//				//노티수신 : 취소, DB조회 : cancelYn값  Y아님(정상 업데이트 미처리)
//				//1. 취소업데이트
//			}
//		}
		//return "0000";
		if(null!=order) {
		
			if("25".equals(cancelYn) && (order.getTid() == null || order.getTid().length() < 1 )) {
				try {		
					
					if(orderIdCnt > 0) {
						orderService.updateOrderData(orderId, tid, svcCd, mid, "noti");//OrderId 참조하여 PayYn 상태값, tid, svc_cd, mid, payResultDiv 값 변경.
						//webSocketAsync.sendMsg(storeId); // 주문 통지 보내기( 웹소켓 방식 철회함 23.10.6) 주문통지는 main.html에 1분마다 체크하여 메시지 보내는 것으로 방식 바꿈.
						
						String content = "";
						
						if(orderIdPushCnt > 0) {
							
							Store store = storeService.findOne(order.getStore().getStoreCd());	
						
							//app push 알림 start				
							Map<String, Object> pushMessage = null;
							
							
							if(order.getSmenus().size() > 1) {
								content = content + order.getSmenus().get(0).getSmenuNmLan() + " 외  " + (order.getSmenus().size() - 1) + "개";
								
							}else {
								content = content + order.getSmenus().get(0).getSmenuNmLan();
								
							}
							
							content = content + " " + order.getAmt() + "원";
							
							SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
							Date now = new Date();
							String current = sdf1.format(now);					
							content = content + " \n" + current;
												
							accountIdList = accountService.getAccountIdList(store.getStoreCd());
							
							Account account = null;
							FcmService fcmService = null;  
							ApnsSender apnsSender = null;					
							Map<String, Object> resultMap = null;
							
							for(String accountId : accountIdList) {
							
								webSocketAsync.sendMsg(accountId); //관리자 app 화면 토스 메세지 띄우기 위함(24.01.03)
								account = accountService.findOne(accountId);		
								
								if(null!=account.getToken() && account.getToken().length() > 0) {
									
									pushMessage = new HashMap<String, Object>();						
									pushMessage.put("title", "메뉴플러스 [신규주문]");	 	//제목 세팅	
									pushMessage.put("mesg", content);   		 	//내용 세팅
									pushMessage.put("token", account.getToken());   //token 세팅
									pushMessage.put("osType", account.getOsType()); //osType 세팅
									
									logger.info("=============innopayNoti=================== push send start ===================================");
									logger.info("================token:" + account.getToken());
									
									//if(account.getOsType().equals("android")) {
										fcmService = new FcmService();											
										resultMap = fcmService.SendPush(pushMessage);  	//push알림 보내기
										
//									}else{
//										apnsSender = new ApnsSender();
//										resultMap = apnsSender.SendPush(pushMessage);   //push알림 보내기(ios)
//										
//									}
									
									// push알림 로그 저장
									accountRepository.insertPushLog( order.getTid()
																		,"메뉴플러스 [신규주문]"
																		, content
																		, account.getToken()
																		, account.getOsType()
																		, resultMap.get("resultCode").toString()
																		, resultMap.get("resultMsg").toString());
									
									logger.info("===========innopayNoti===================== push send end ====================================");
									
								}
								
							}
							
							orderService.updatePushYn4Order(order.getOrderCd()); //push 발송 여부 Y로 업데이트
							
						}//app push 알림 end	
						
						return "0000";
					
					}else{				
						return "0001"; //존재하지 않는 주문번호  
					}
			
				}catch(Exception e){					
					logger.error(e.getMessage());
					return "0009";//업데이트 실패
				
				}
				
			}else if("85".equals(cancelYn) && !"Y".equals(order.getCancelYn())) {
				
				try {
					orderService.updateCancelYn4Order(tid); 
					return "0000";
				}catch(Exception e) {
					logger.error(e.getMessage());
					return "0009";  //업데이트 실패
				}
				
			}
			
			return "0002";//noti정보 업데이트 불필요
			
		}else{
			return "0002"; //noti정보 업데이트 불필요
		}
		
	}
	
	/**
	 * 결제후 결과 반영
	 * @return 주문 상태
	 */
	@GetMapping("/innopay/{orderId}/{storeId}/{tid}/{svcCd}/{mid}")
	@JsonView(WebAppControllerJsonView.class)
	public String Innopay(@PathVariable String orderId
							, @PathVariable String storeId
							, @PathVariable String tid
							, @PathVariable String svcCd
							, @PathVariable String mid
							, HttpServletResponse res) throws IOException {
		logger.info("===========================================Innopay start===================================");
		int orderIdCnt = orderService.getCount4OrderId(orderId); //주문번호 유무확인
		List<String> accountIdList = null;
		Order order = null;
		
		Store store = storeService.findOneByStoreId(storeId);	
		
		int orderIdPushCnt = 0;
		
		try {		
			
			if(orderIdCnt > 0) {
				orderService.updateOrderData(orderId, tid, svcCd, mid, "innopay");//OrderId 참조하여 PayYn 상태값, tid, svc_cd, mid, payResultDiv 값 변경.
				//orderService.updateOrderList(orderId); //OrderId 참조하여 PayYn 상태값 변경.
				//webSocketAsync.sendMsg(storeId); // 주문 통지 보내기( 웹소켓 방식 철회함 23.10.6) 주문통지는 main.html에 1분마다 체크하여 메시지 보내는 것으로 방식 바꿈.
				
				orderIdPushCnt = orderService.getPushOrderCount4OrderId(orderId); //push알림이 발송안된 주문 건인지 확인
				String content = "";
				
				if(orderIdPushCnt > 0) {
				
					//app push 알림 start				
					Map<String, Object> pushMessage = null;
					order = orderService.findByOid(orderId);
					
					if(order.getSmenus().size() > 1) {
						content = content + order.getSmenus().get(0).getSmenuNmLan() + " 외  " + (order.getSmenus().size() - 1) + "개";
						
					}else {
						content = content + order.getSmenus().get(0).getSmenuNmLan();
						
					}
					
					content = content + " " + order.getAmt() + "원";
					
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
					Date now = new Date();
					String current = sdf1.format(now);					
					content = content + " \n" + current;
										
					accountIdList = accountService.getAccountIdList(store.getStoreCd());
					
					Account account = null;
					FcmService fcmService = null;  
					ApnsSender apnsSender = null;					
					Map<String, Object> resultMap = null;
					
					for(String accountId : accountIdList) {
					
						webSocketAsync.sendMsg(accountId); //관리자 app 화면 토스 메세지 띄우기 위함(24.01.03)
						account = accountService.findOne(accountId);		
						
						if(null!=account.getToken() && account.getToken().length() > 0) {
							
							pushMessage = new HashMap<String, Object>();						
							pushMessage.put("title", "메뉴플러스 [신규주문]");	 	//제목 세팅	
							pushMessage.put("mesg", content);   		 	//내용 세팅
							pushMessage.put("token", account.getToken());   //token 세팅
							pushMessage.put("osType", account.getOsType()); //osType 세팅
							
							logger.info("===================Innopay============= push send start ===================================");
							logger.info("================token:" + account.getToken());
							
							fcmService = new FcmService();											
							resultMap = fcmService.SendPush(pushMessage);  	//push알림 보내기(android)
								
							// push알림 로그 저장
							accountRepository.insertPushLog( order.getTid()
																,"메뉴플러스 [신규주문]"
																, content
																, account.getToken()
																, account.getOsType()
																, resultMap.get("resultCode").toString()
																, resultMap.get("resultMsg").toString());
							
							logger.info("=================Innopay=============== push send end ====================================");
							
						}
						
					}
					
					orderService.updatePushYn4Order(order.getOrderCd()); //push 발송 여부 Y로 업데이트
					
				}//app push 알림 end	
				
				return "0000";
			
			}else {				
				return "0001"; //존재하지 않는 주문번호  
			}
	
		}catch(Exception e){					
			logger.error(e.getMessage());
			return "0009";//존재하지 않는 주문번호 
		
		}
		
	}
	
	/**
	 * 결제후 결과 반영
	 * @return 주문 상태
	 */
	@PostMapping("/innopay/payReturn")
	@JsonView(WebAppControllerJsonView.class)
	public String InnopayPost(@RequestBody Order order, HttpServletResponse res) throws IOException {
		
		String orderId = order.getOrderId();
		String mid =  order.getMid();
		String tid =  order.getTid();
		Order od = orderService.findByOid(orderId);
		od.setMid(mid);
		od.setTid(tid);
		od.setPayYn("Y");
		orderService.save(od);
		
		return "0000";
	}
	
	//주문내역 조회
	 @PostMapping("/getOrderCd/{tel}/{StoreCd}")
	  public List<String> getOrderCd(@PathVariable String tel, @PathVariable Integer StoreCd) throws Exception {
		 List<String> orderCdList = new ArrayList<>();
		 String cryptTelResult = "";
		 try {	 
			 
			 AESCrypto aes = new AESCrypto(crypto_key); 
			 cryptTelResult = aes.AesECBEncode(tel);//전화번호 암호화 한다.
			 
			 orderCdList = orderService.getOrderCd(cryptTelResult, StoreCd);
			
		 }catch(Exception e) {
			 e.toString();
		 }
		 return orderCdList;
	  }
		
	/**
	 * 주문 취소
	 * @param orderCd 주문 코드
	 * @param orderId 주문 아이디
	 * @param orderDate 주문 시간
	 * @param amt 주문 금액
	 * @return 주문 상태
	 * @throws Exception 
	 */
	@GetMapping("/cancelOrder/{storeCd}/{orderCd}/{orderId}/{orderDate}/{amt}")
	public String cancelOrder(@PathVariable Integer storeCd, @PathVariable Integer orderCd,
			@PathVariable String orderId, @PathVariable String orderDate, @PathVariable Integer amt,
			HttpServletRequest req, HttpServletResponse res) throws Exception {
		
		String result = "E";
		
		Store store = storeService.findOne(storeCd);
		
		// 상점 코드 맞는지 체크
		if (!ObjectUtils.isEmpty(store)) {
			Order order = orderService.findOne(orderCd);
			
			// 주문 맞는지 체크 함
			if (!ObjectUtils.isEmpty(store) && order.getOrderCd().equals(orderCd) && order.getAmt().equals(amt) &&
					DateUtil.dateTimeToStr(order.getOrderDate()).equals(orderDate)) {
				// 주문 상태만 취소 가능
				if ("ORDER".equals(order.getOrderStatus().getVal())) {
					if ("N".equals(order.getCancelYn())) {
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
								
								result = "S";
							} else {
								result = lanDataService.getLanData("카드 결제 취소 실패 하였습니다.", LocaleContextHolder.getLocale()) +
										" 결과메세지: " + resultMsg;
							}
						} else {
							order.setCancelYn("Y");
							orderService.save(order);
							
							result = "S";
						}
					} else {
						result = "S";
					}
				} else {
					result = lanDataService.getLanData("이미 준비중인 주문입니다. 취소 할 수 없습니다.", LocaleContextHolder.getLocale());
				}
			}
		}
		
		return result;
		
	}
	
	
//	@GetMapping("/testReceiveAlarm")
//	public void testReceiveAlarm() {
//		
//		logger.info("=========================================== testReceiveAlarm start ====================================");
//		
//		CloseableHttpClient httpClient = HttpClients.createSystem();
//		//int httpStatusCode = -1;
//		
//		try {
//		
//			HttpGet httpGet = new HttpGet("http://10.10.10.16:8187/app/receiveAlarm/S200000002"); 
//			HttpResponse httpResponse = httpClient.execute(httpGet);
//			
//		}catch(Exception e) {
//			e.toString();
//		}
//		
//		logger.info("=========================================== testReceiveAlarm end ====================================");
//	}
	
	
	@GetMapping("/testReceiveAlarm")
	public void testReceiveAlarm() {
		
		logger.info("=========================================== testReceiveAlarm start ====================================");
		
		String myResult = "";

        try {
            //   URL 설정하고 접속하기 
            URL url = new URL("http://10.10.10.16:8187/app/receiveAlarm/S200000002"); // URL 설정 

            HttpURLConnection http = (HttpURLConnection) url.openConnection(); // 접속 
            //-------------------------- 
            //   전송 모드 설정 - 기본적인 설정 
            //-------------------------- 
//            http.setDefaultUseCaches(false);
//            http.setDoInput(true); // 서버에서 읽기 모드 지정 
//            http.setDoOutput(true); // 서버로 쓰기 모드 지정  
            http.setRequestMethod("GET"); // 전송 방식은 POST

            //--------------------------
            // 헤더 세팅
            //--------------------------
            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다 
            http.setRequestProperty("content-type", "text/plain; charset=utf-8");
            //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //http.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            //-------------------------- 
            //   서버로 값 전송 
            //-------------------------- 
            StringBuffer buffer = new StringBuffer();
            
            HashMap<String, String> pList = new HashMap<String, String>();
            pList.put("accountId", "S200000002");	//PARAM

            //HashMap으로 전달받은 파라미터가 null이 아닌경우 버퍼에 넣어준다
            if (pList != null) {

                Set key = pList.keySet();

                for (Iterator iterator = key.iterator(); iterator.hasNext();) {
                    String keyName = (String) iterator.next();
                    String valueName = pList.get(keyName);
                    buffer.append(keyName).append("=").append(valueName);
                }
            }
            
            OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outStream);
            writer.write(buffer.toString());
            writer.flush();
            
            //-------------------------- 
            //   서버에서 전송받기 
            //-------------------------- 
            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();
           
            logger.info("===============myResult:" + myResult);
			
		}catch(Exception e) {
			e.toString();
		}
		
		logger.info("=========================================== testReceiveAlarm end ====================================");
	}
	
}
