package org.lf.app.models.business.order.pay;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.config.socket.WebSocketAsync;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 결제
 * 
 * @author LF
 *
 */
@RestController
@Transactional
public class PayController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private PayService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private WebSocketAsync webSocketAsync;
	
	@Value("${sShopId}")
    private String sShopId;
	
	@Value("${sCrossKey}")
    private String sCrossKey;
	
	
	
	/**
	 * 결제후 들어오는 부분
	 * @param params
	 * @throws IOException 
	 */
	@RequestMapping(value = "/pay/receive", method = { RequestMethod.GET, RequestMethod.POST }, produces={ "text/html;charset=EUC-KR;" })
	public void receive(HttpServletRequest req, HttpServletResponse res) throws IOException {
		res.setCharacterEncoding("EUC-KR");
		
		// 결과값
		String sResultCd  = req.getParameter("allat_result_cd");
		String sResultMsg = req.getParameter("allat_result_msg");
		String sEncData   = req.getParameter("allat_enc_data");
		
		PrintWriter out = res.getWriter();
		out.println("<script>");
		
		// 카드 사 결제 완료 0000: 결제 성공, 9999: 사용자 취소
//		if ("0000".equals(sResultCd) || "9999".equals(sResultCd)) {
			out.println("if (window.opener != undefined) {");
			out.println("	opener.result_submit('" + sResultCd + "','" + sResultMsg + "','" + sEncData + "');");
			out.println("	window.close();");
			out.println("} else {");
			out.println("	parent.result_submit('" + sResultCd + "','" + sResultMsg + "','" + sEncData + "');");
			out.println("	window.close(); ");
			out.println("}");
//		} else {
//			out.println("	alert('" + sResultMsg + "'); ");
//			out.println("	window.close(); ");
//		}
		
		out.println("</script>");
	}
	
	
	/**
	 * 결제 매입 부분
	 * @param params
	 * @throws IOException 
	 */
	@RequestMapping(value = "/api/pay/approval", method = { RequestMethod.GET, RequestMethod.POST })
	public Map<String, Object> approval(@RequestBody Map<String, Object> params, HttpServletResponse res) throws IOException {
		Map<String, Object> result = new HashMap<>();
		
		String sEncData = MapUtils.getString(params, "enc_data");
		
		String orderCd = MapUtils.getString(params, "orderCd");
		
		// 카드사에서 보내주는 결제 정보가 있는지 체크
		if (!StringUtils.isEmpty(sEncData)) {
			// 주문 번호 체크
			if (!StringUtils.isEmpty(orderCd)) {
				Order order = orderService.findOne(Integer.parseInt(orderCd));
				
				// 주문 정보 체크
				if (!ObjectUtils.isEmpty(order)) {
					// Service Code
					String sAmount   = String.valueOf(order.getAmt());			//결제 금액을 다시 계산해서 만들어야 함(해킹방지)  ( session, DB 사용 )
					
					// 요청 데이터 설정
					String strReq = "";
					
					strReq  ="allat_shop_id="   +sShopId;
					strReq +="&allat_amt="      +sAmount;
					strReq +="&allat_enc_data=" +sEncData;
					strReq +="&allat_cross_key="+sCrossKey;
	
					// 올앳 결제 서버와 통신  : AllatUtil.approvalReq->통신함수, HashMap->결과값
					//-----------------------------------------------------------------------------
					AllatUtil util = new AllatUtil();
					Map hm = util.approvalReq(strReq, "SSL");
					
					// 결제 결과 값 확인
					//------------------
					String sReplyCd     = (String)hm.get("reply_cd");
					String sReplyMsg    = (String)hm.get("reply_msg");
	
					/* 결과값 처리
					--------------------------------------------------------------------------
					   결과 값이 '0000'이면 정상임. 단, allat_test_yn=Y 일경우 '0001'이 정상임.
					   실제 결제   : allat_test_yn=N 일 경우 reply_cd=0000 이면 정상
					   테스트 결제 : allat_test_yn=Y 일 경우 reply_cd=0001 이면 정상
					--------------------------------------------------------------------------*/
//					if (sReplyCd.equals("0001")) {
					if (sReplyCd.equals("0000")) {
						// reply_cd "0000" 일때만 성공
						String sOrderNo        = (String)hm.get("order_no");
						String sAmt            = (String)hm.get("amt");
						String sPayType        = (String)hm.get("pay_type");
						String sApprovalYmdHms = (String)hm.get("approval_ymdhms");
						String sSeqNo          = (String)hm.get("seq_no");
						String sApprovalNo     = (String)hm.get("approval_no");
//						String sCardId         = (String)hm.get("card_id");
					    String sCardNm         = (String)hm.get("card_nm");
					    
						// 취소 시 필요 한 데이타만 걸러서 저장함
						Pay pay = new Pay();
						
						pay.setPayComp("AllAt");
						pay.setReplyCd(sReplyCd);
						pay.setReplyMsg(sReplyMsg);
						pay.setOrderNo(sOrderNo);
						pay.setAmt(sAmt);
						pay.setPayType(sPayType);
						pay.setApprovalYmdHms(sApprovalYmdHms);
						pay.setSeqNo(sSeqNo);
						pay.setApprovalNo(sApprovalNo);
//						pay.setCardId(sCardId);
						pay.setCardNm(sCardNm);
						
						pay = service.save(pay);
						
						// 주문에 결제 정보 연결
						order.setPay(pay);
						order.setPayYn("Y");
						
						// 결제 끝나면 사용가능 및 주문코드 설정
						order.setUseYn("Y");
						orderService.setOrderId(order);
						
						orderService.save(order);
						
						// 후결제는 결제 끝나면 주문 통지 보내기
						webSocketAsync.sendMsg(order.getStore().getStoreId());
						
						result.put("resultCd", "Y");
						result.put("orderId", order.getOrderId());
					} else {
						// reply_cd 가 "0000" 아닐때는 에러 (자세한 내용은 매뉴얼참조)
						// reply_msg 가 실패에 대한 메세지
						result.put("resultCd", "N");
						result.put("resultMsg", lanDataService.getLanData("결제 실패 하였습니다. 관리자에게 문의 하십시오.", LocaleContextHolder.getLocale()));
					}
				} else {
					result.put("resultCd", "N");
					result.put("resultMsg", lanDataService.getLanData("주문 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
				}
			} else {
				result.put("resultCd", "N");
				result.put("resultMsg", lanDataService.getLanData("주문 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
			}
		} else {
			result.put("resultCd", "N");
			result.put("resultMsg", lanDataService.getLanData("카드사 결제 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
		}
		
		return result;
	}
	
	
	
}
