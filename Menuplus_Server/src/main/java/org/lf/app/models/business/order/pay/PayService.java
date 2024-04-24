package org.lf.app.models.business.order.pay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.models.BaseService;
import org.lf.app.utils.system.HttpUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.tpay.webtx.Encryptor;


/**
 * 결제 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class PayService extends BaseService<Pay> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private PayRepository repository;
	
	// AllAt
	@Value("${sShopId}")
    private String sShopId;
	// AllAt
	@Value("${sCrossKey}")
    private String sCrossKey;
	
	// TPay 가맹점 ID (mid, 꼭 해당 mid 로 바꿔주세요)
	@Value("${mid}")
    private String mid;
	
	// TPay 가맹점 서명키 (꼭 해당 가맹점 서명키로 바꿔주세요)
	@Value("${merchantKey}")
    private String merchantKey;
		
	
	/**
	 * 결제 취소
	 * @param pay
	 * @return
	 * @throws Exception 
	 */
	public Map<String, String> cancelPay(Pay pay, HttpServletRequest req) throws Exception {
		if ("AllAt".equals(pay.getPayComp())) {
			return cancelAllPay(pay);
		} else {
			return cancelTPay(pay, req);
		}
	}
	
	
	/**
	 * AllAt 결제 취소
	 * @param pay
	 * @return
	 */
	public Map<String, String> cancelAllPay(Pay pay) {
		Map<String, String> result = new HashMap<>();
		
		HashMap reqHm = new HashMap();
		Map<String, String> resHm = null;
		String szReqMsg="";
		String szAllatEncData="";
		String szCrossKey="";

		// 취소 요청 정보
		//------------------------------------------------------------------------
		String szShopId ="";
		String szAmt    ="";
		String szOrderNo="";
		String szPayType="";
		String szSeqNo  ="";

		// 정보 입력
		//------------------------------------------------------------------------
		szCrossKey      ="cad398760d5a8b71c21a26f97dc9d3c5";	// 해당 CrossKey값
		szShopId        ="welcome44";	// ShopId 값(최대 20Byte)
		szAmt           =pay.getAmt();	// 취소 금액(최대 10Byte)
		szOrderNo       =pay.getOrderNo();	// 주문번호(최대 80Byte)
		szPayType       =pay.getPayType();	// 원거래건의 결제방식[카드:CARD,계좌이체:ABANK]
		szSeqNo         =pay.getSeqNo();	// 거래일련번호:옵션필드(최대 10Byte)

		reqHm.put("allat_shop_id" ,  szShopId );
		reqHm.put("allat_order_no",  szOrderNo);
		reqHm.put("allat_amt"     ,  szAmt    );
		reqHm.put("allat_pay_type",  szPayType);
		reqHm.put("allat_test_yn" ,  "N"      );	//테스트 :Y, 서비스 :N
		reqHm.put("allat_opt_pin" ,  "NOUSE"  );	//수정금지(올앳 참조 필드)
		reqHm.put("allat_opt_mod" ,  "APP"    );	//수정금지(올앳 참조 필드)
		reqHm.put("allat_seq_no"  ,  szSeqNo  );	//옵션 필드( 삭제 가능함 )

		AllatUtil util = new AllatUtil();

		szAllatEncData=util.setValue(reqHm);
		szReqMsg  = "allat_shop_id="   + szShopId
					+ "&allat_amt="      + szAmt
					+ "&allat_enc_data=" + szAllatEncData
					+ "&allat_cross_key="+ szCrossKey;

		resHm = util.cancelReq(szReqMsg, "SSL");

		String sReplyCd   = (String)resHm.get("reply_cd");
		String sReplyMsg  = (String)resHm.get("reply_msg");
		
//		result.put("sReplyCd", sReplyCd);
//		result.put("sReplyMsg", sReplyMsg);
//		result.put("cancel_ymdhms", cancel_ymdhms);
//		result.put("part_cancel_flag", part_cancel_flag);
//		result.put("sReplyMsg", sReplyMsg);
//		result.put("sReplyMsg", sReplyMsg);
//		
//		log.print(sReplyCd);
//		log.print(sReplyMsg);
//		
//		// 0001 테스트 설공 결과 값, 0505 이미 최소됨
		if(sReplyCd.equals("0000") || sReplyCd.equals("0505")) {
			result.put("resultCd", "S");
			result.put("resultMsg", "");
			
			String sCancelYMDHMS    = (String)resHm.get("cancel_ymdhms");
			
			pay.setCancelYn("Y");
			pay.setCancelYmdHms(sCancelYMDHMS);
			
			repository.save(pay);
//			// reply_cd "0000" 일때만 성공
//			String sCancelYMDHMS    = (String)resHm.get("cancel_ymdhms");
//			String sPartCancelFlag  = (String)resHm.get("part_cancel_flag");
//			String sRemainAmt       = (String)resHm.get("remain_amt");
//			String sPayType         = (String)resHm.get("pay_type");
//
//			System.out.println("결과코드		: " + sReplyCd);
//			System.out.println("결과메세지	: " + sReplyMsg);
//			System.out.println("취소일시		: " + sCancelYMDHMS);
//			System.out.println("취소구분		: " + sPartCancelFlag);
//			System.out.println("잔액			: " + sRemainAmt);
//			System.out.println("거래방식구분	: " + sPayType);
//		} else if (sReplyCd.equals("0505")) {
		} else {
			result.put("resultCd", "E");
			result.put("resultMsg", sReplyCd + ": " + sReplyMsg);
//			// reply_cd 가 "0000" 아닐때는 에러 (자세한 내용은 매뉴얼참조)
//			// reply_msg 가 실패에 대한 메세지
//			System.out.println("결과코드		: " + sReplyCd);
//			System.out.println("결과메세지	: " + sReplyMsg);
		}
		
		return result;
	}
	
	
	/**
	 * TPay 결제 취소
	 * @param pay
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> cancelTPay(Pay pay, HttpServletRequest req) throws Exception {
		Map<String, String> result = new HashMap<>();
		
		String cancelAmt = pay.getAmt();

		Encryptor encryptor = new Encryptor(merchantKey);
		String moid = pay.getOrderNo();
		String encryptData = encryptor.encData(cancelAmt + mid + moid);
		String ediDate = encryptor.getEdiDate();

		String payActionUrl = "https://webtx.tpay.co.kr/payCancel";
		
		String param = "encryptData=" + encryptData +
				"&ediDate=" + ediDate +
				"&cc_ip=" + req.getHeader("X-Real-IP") +
				"&mid=" + mid +
				"&tid=" + pay.getApprovalNo() +
				"&moid=" + moid +
				"&cancelPw=" +
				"&cancelAmt=" + cancelAmt +
				"&cancelMsg=사용자취소" +
				"&partialCancelCode=0" +
				"&partialCancelId=" +
				"&dataType=json";
		
		String resultJson = HttpUtil.doPostNoJson(payActionUrl, param);
		
		ObjectMapper objectMapper = new ObjectMapper();
		
		Map<String, String> resultMap = objectMapper.readValue(resultJson, Map.class);
		
		if (MapUtils.getString(resultMap, "resultCd").equals("000")) {
			Map<String, String> resultSubMap = (Map<String, String>) MapUtils.getMap(resultMap, "result");
			if (MapUtils.getString(resultSubMap, "resultCd").equals("0000")) {
				result.put("resultCd", "S");
				result.put("resultMsg", "");
				
				pay.setCancelYn("Y");
				pay.setCancelYmdHms(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				
				repository.save(pay);
			} else {
				result.put("resultCd", "E");
				result.put("resultMsg", MapUtils.getString(resultSubMap, "resultCd") + ": " + MapUtils.getString(resultSubMap, "resultMsg"));
			}
		} else {
			result.put("resultCd", "E");
			result.put("resultMsg", MapUtils.getString(resultMap, "resultCd") + ": " + MapUtils.getString(resultMap, "resultMsg"));
		}
		
		return result;
	}
	
	
}
