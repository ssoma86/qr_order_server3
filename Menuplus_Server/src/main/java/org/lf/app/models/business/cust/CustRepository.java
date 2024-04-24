package org.lf.app.models.business.cust;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.lf.app.models.BaseRepository;

/**
 * 사업장
 * 
 * @author LF
 * 
 */
public interface CustRepository extends BaseRepository<Cust, Serializable> {
	
	/**
	 * 사업장명을 통해 사업장 정보 조회
	 * 
	 * @param custNm
	 * @return 사업장 정보
	 */
	public Cust findOneByCustNm(String custNm);
	
	/**
	 * 사업장 리스트 조회
	 * 
	 * @param custNm
	 * @param useYn
	 * @return 사업장 리스트
	 */
	@Query("SELECT c "
			+ "FROM Cust c "
			+ "    LEFT JOIN c.topCust ct "
			+ "WHERE c.custNm LIKE %:custNm% "
			+ "  AND c.useYn LIKE :useYn% "
			+ "  AND (:topCustCd IS NULL OR ct.custCd = :topCustCd) "
			+ "ORDER BY c.custId "
	)
	public List<Cust> findCustList(@Param("custNm") String custNm, @Param("useYn") String useYn, @Param("topCustCd") Integer topCustCd);
	
	
	/**
	 * 결제 방법 정보 가져오기(23.09.18)
	 * @param custCd
	 * @return
	 */
	@Query("SELECT p "
			+ "FROM PayMethod p "
			+ "WHERE p.custCd = :custCd "
	)
	public PayMethod selectPayMethods4CustCd(@Param("custCd") Integer custCd);
	
	
	/**
	 * 결제방법 저장(24.04.12 수정)
	 * @param mid
	 * @param mkey
	 * @param overseasMid
	 * @param overseasMkey
	 * @param methods
	 * @param currency
	 * @param custCd
	 * @param useYn
	 */
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "INSERT INTO tbl_pay_method (mid, mkey, overseas_mid, overseas_mkey, methods, currency, cust_cd, use_yn) "
			+ "values(:mid, :mkey, :overseasMid, :overseasMkey, :methods, :currency, :custCd, :useYn) "
	)
	public void insertPayMethod(@Param("mid") String mid
								, @Param("mkey") String mkey
								, @Param("overseasMid") String overseasMid
								, @Param("overseasMkey") String overseasMkey
								, @Param("methods") String methods
								, @Param("currency") String currency
								, @Param("custCd") Integer custCd
								, @Param("useYn") String useYn);
	
	
	/**
	 * 결제방법 삭제(23.09.18)
	 * @param custCd
	 */
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "DELETE FROM tbl_pay_method "
			+ "WHERE cust_cd = :custCd "
	)
	public void deletePayMethod(@Param("custCd") Integer custCd);
	
}
