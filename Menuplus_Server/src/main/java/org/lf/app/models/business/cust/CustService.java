package org.lf.app.models.business.cust;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;


/**
 * 사업장 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class CustService extends BaseService<Cust> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private CustRepository repository;
	
	
	
	/**
	 * 사업장명을 통해 사업장 정보 조회
	 * 
	 * @param custNm
	 * @return 사업장 정보
	 */
	public Cust findOneByCustNm(String custNm) {
		return repository.findOneByCustNm(custNm);
	}
	
	/**
	 * 사업장 리스트 조회
	 * 
	 * @param custNm
	 * @param useYn
	 * @return 사업장 리스트
	 */
	public List<Cust> findCustList(Cust cust, Integer topCustCd) {
		return repository.findCustList(cust.getCustNm(), cust.getUseYn(), topCustCd);
	}

	/**
	 * 결제 방법 정보 조회(23.09.18)
	 * @param custCd
	 * @return
	 */
	public PayMethod selectPayMethods4CustCd(@Param("custCd") Integer custCd) {
		return repository.selectPayMethods4CustCd(custCd);
	}
	
	/**
	 * 결제방법 저장(24.03.25)
	 * 24.04.12 currency 추가
	 * @param mid
	 * @param mkey
	 * @param overseasMid
	 * @param overseasMkey
	 * @param methods
	 * @param currency
	 * @param custCd
	 * @param useYn
	 */
	public void insertPayMethod(@Param("mid") String mid
									, @Param("mkey") String mkey
									, @Param("overseasMid") String overseasMid
									, @Param("overseasMkey") String overseasMkey
									, @Param("methods") String methods
									, @Param("currency") String currency
									, @Param("custCd") Integer custCd
									, @Param("useYn") String useYn) {
		repository.insertPayMethod(mid, mkey, overseasMid, overseasMkey, methods, currency, custCd, useYn);
	}
	
	/**
	 * 결제방법 정보 삭제(23.09.18)
	 * @param custCd
	 */
	public void deletePayMethod(@Param("smenuCd") Integer custCd) {
		repository.deletePayMethod(custCd);
	}
	
}
