package org.lf.app.service.app;

import java.net.InetAddress;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.lf.app.config.socket.WebSocketAsync;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.order.pay.Pay;
import org.lf.app.models.business.order.pay.PayService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kr.co.tpay.webtx.Encryptor;

@RestController
@RequestMapping("/api/app")
public class AppPay {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
    private AppUserService appUserService;
	
	@Autowired
	private PayService service;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private WebSocketAsync webSocketAsync;
	
	// 가맹점 ID (mid, 꼭 해당 mid 로 바꿔주세요)
	@Value("${mid}")
    private String mid;
	
	// 가맹점 서명키 (꼭 해당 가맹점 서명키로 바꿔주세요)
	@Value("${merchantKey}")
    private String merchantKey;
	
	
	
	/**
	 * 결제 페이지
	 * @throws Exception 
	 */
	@PostMapping("/mpay")
	public ModelAndView mpay(HttpServletRequest req) throws Exception {
		ModelAndView mav = new ModelAndView("mpay/mpay");
		mav.addObject("resultCd", "S");
		
		String accountId = req.getParameter("accountId");
		
		/*********************************************************************************
		* 가맹점에서 입력하셔야 하는 정보입니다. 테스트를 위해 발급받은 mid 와 가맹점키를 세팅해주세요. 
		**********************************************************************************/
	    // 가맹점에서 사용하실 주문번호를 설정하세요.
		String moid = req.getParameter("orderCd");
	    /*********************************************************************************/
		String payMethod = req.getParameter("payMethod");
		String goodsName = req.getParameter("goodsName");
		
		AppUser appUser = null;
		
		// 계정 없을 시
		if (StringUtils.isEmpty(accountId)) {
			mav.addObject("resultCd", "E");
			mav.addObject("resultMsg", "accountId 사용자 아이디는 필수 입력입니다.");
		} else {
			appUser = appUserService.findOne(accountId);
			
			if (ObjectUtils.isEmpty(appUser)) {
				mav.addObject("resultCd", "E");
				mav.addObject("resultMsg", "accountId 해당하는 계정 정보가 존재 하지 않습니다.");
			}
		}
		
		String amt = "0";
		
		if (StringUtils.isEmpty(payMethod)) {
			mav.addObject("resultCd", "E");
			mav.addObject("resultMsg", "payMethod 결제 타입 정보는 필수 입력입니다.");
		}
		
		if (StringUtils.isEmpty(goodsName)) {
			mav.addObject("resultCd", "E");
			mav.addObject("resultMsg", "goodsName 상품명은 필수 입력입니다.");
		}
		
		if (StringUtils.isEmpty(moid)) {
			mav.addObject("resultCd", "E");
			mav.addObject("resultMsg", "orderCd 주문코드 정보는 필수 입력입니다.");
		} else {
			try {
				Order order = orderService.findOne(Integer.parseInt(moid));
			
				if (ObjectUtils.isEmpty(moid)) {
					mav.addObject("resultCd", "E");
					mav.addObject("resultMsg", "orderCd 주문코드 해다되는 주문내역이 없습니다.");
				} else {
					amt = String.valueOf(order.getAmt());
				}
			} catch (Exception e) {
				mav.addObject("resultCd", "E");
				mav.addObject("resultMsg", "orderCd 주문코드 해다되는 주문내역이 없습니다.");
			}
		}
		
		
		// 
		if ("S".equals(mav.getModelMap().get("resultCd"))) {
			InetAddress inet = InetAddress.getLocalHost(); // 가맹점 클라이언트 IP 가져오기
			
		    //String ediDate, String mid, String merchantKey, String amt
		    Encryptor encryptor = new Encryptor(merchantKey);
		    
		    mav.addObject("encryptData", encryptor.encData(amt + mid + moid));
		    mav.addObject("ediDate", encryptor.getEdiDate());
		    mav.addObject("vbankExpDate", encryptor.getVBankExpDate());
		    mav.addObject("payMethod", payMethod);
		    mav.addObject("goodsName", goodsName);
		    // 가맹점에서 사용하실 주문번호를 설정하세요.
		    mav.addObject("mid", mid);
		    mav.addObject("moid", moid);
		    mav.addObject("amt", amt);
		    mav.addObject("mallUserId", appUser.getAccountId());
		    mav.addObject("buyerName", appUser.getAccountNm());
		    mav.addObject("buyerTel", appUser.getTel());
		    mav.addObject("buyerEmail", appUser.getEmail());
		    mav.addObject("mallIp", inet.getHostAddress());
		    mav.addObject("returnUrl", "http://34.97.200.142/api/app/rpay");
		    mav.addObject("cancelUrl", "http://34.97.200.142/api/app/cpay");
			   
		    // 122.43.37.145:1004
		    // 34.97.200.142
//			CARD">[신용카드] BANK">[계좌이체] VBANK">[가상계좌] CELLPHONE">[휴대폰결제]</option>
		    // test: http://122.43.37.145:1004/api/app/mpay?payMethod=CARD&goodsName=goodsName&amt=1004&orderCd=559&accountId=AU2020011900020
		}
		
		return mav;
	}
	
	
	/**
	 * 결제 리턴 페이지
	 * @throws Exception 
	 */
	@PostMapping("/rpay")
	public ModelAndView rpay(HttpServletRequest req) throws Exception {
		ModelAndView mav = new ModelAndView("mpay/rpay");
		
		req.setCharacterEncoding("utf-8");
		
		String payMethod       = req.getParameter("payMethod");
		String authDate        = req.getParameter("authDate");
		String resultMsg       = req.getParameter("resultMsg");
		String amt             = req.getParameter("amt");
//		String cardNo          = req.getParameter("cardNo");
		String fnName          = req.getParameter("fnName");
		String tid             = req.getParameter("tid");
		String moid            = req.getParameter("moid");
		String authCode        = req.getParameter("authCode");
		String resultCd        = req.getParameter("resultCd");
		String ediDate         = req.getParameter("ediDate");
		String errorCd         = req.getParameter("errorCd");
		String errorMsg        = req.getParameter("errorMsg");
		
		String result = ""; 
		
		String resultFlag = "0";
		
		Encryptor encryptor = new Encryptor(merchantKey, ediDate);
		
		amt = encryptor.decData(amt);
		moid = encryptor.decData(moid);
		
		// 금액 체크 함
		if (payMethod.equals("CARD")) {
			if (resultCd.equals("3001")) {
				//[성공]카드 결제 성공
				result = "[성공]카드 결제 성공";
				resultFlag = "1";
	
			} else {
				//[실패]카드 결제 실패, 실패 정보 확인하세요.
				result = "[실패]카드 결제 실패, 실패 정보 확인하세요.";
			}
		} else if (payMethod.equals("BANK")) {
			if (resultCd.equals("4000")) {
				//[성공]계좌이체 성공
				result = "[성공]계좌이체 성공";
				resultFlag = "1";
			} else {
				//[실패]계좌이체 실패, 실패 정보 확인하세요.
				result = "[실패]계좌이체 실패, 실패 정보 확인하세요.";
			}
		} else if (payMethod.equals("VBANK")) {
			if (resultCd.equals("4100")) {
				//[성공]가상계좌 발급 성공
				result = "[성공]가상계좌 발급 성공";
			} else if (resultCd.equals("4110")) {
				//[성공]가상계좌 입금 성공
				result = "[성공]가상계좌 입금 성공";
				resultFlag = "1";
			} else {
				//[실패]가상계좌 발급 실패, 실패 정보 확인하세요
				result = "[실패]가상계좌 발급 실패, 실패 정보 확인하세요";
			}
		} else if (payMethod.equals("CELLPHONE")) {
			if (resultCd.equals("A000")) {
				//휴대폰 결제 성공
				result = "휴대폰 결제 성공";
				resultFlag = "1";
			} else {
				//[실패]결제 실패, 실패 정보 확인하세요.
				result = "[실패]결제 실패, 실패 정보 확인하세요.";
			}
		}
		
		if ("1".equals(resultFlag)) {
			// 취소 시 필요 한 데이타만 걸러서 저장함
			Pay pay = new Pay();
			
			pay.setPayComp("TPay");
			pay.setReplyCd(resultCd);
			pay.setReplyMsg(resultMsg);
			pay.setOrderNo(moid);
			pay.setAmt(amt);
			pay.setPayType(payMethod);
			pay.setApprovalYmdHms(authDate);
			pay.setSeqNo(authCode);
			pay.setApprovalNo(tid);
			pay.setCardNm(fnName);
			
			pay = service.save(pay);
			
			Order order = orderService.findOne(Integer.parseInt(moid));
			// 주문에 결제 정보 연결
			order.setPay(pay);
			order.setPayYn("Y");
			
			// 결제 끝나면 사용가능 및 주문코드 설정
			order.setUseYn("Y");
//			orderService.setOrderId(order);
			
			orderService.save(order);
			
			// 후결제는 결제 끝나면 주문 통지 보내기
			webSocketAsync.sendMsg(order.getStore().getStoreId());
		}
		
		mav.addObject("result", result);
		mav.addObject("resultFlag", resultFlag);
		
		Map<String, Object> errors = new HashMap<>();
		errors.put("result", result);
		errors.put("errorCd", errorCd);
		errors.put("errorMsg", errorMsg);
		
		mav.addObject("errors", errors);
		
		return mav;
	}
	
	
	/**
	 * 결제 취소 페이지
	 * @throws Exception 
	 */
	@GetMapping("/cpay")
	public ModelAndView cpay(Principal principal, HttpServletRequest req) throws Exception {
		ModelAndView mav = new ModelAndView("mpay/rpay");
		mav.addObject("resultFlag", 2);
		mav.addObject("result", "사용자 취소");
		
		return mav;
	}
	
	
	
}
