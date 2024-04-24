package org.lf.app.models.business.discount;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 할인 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class DiscountService extends BaseService<Discount> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private DiscountRepository repository;
	
	@Autowired
	private CodeService codeService;
	
	/**
	 * 할인 리스트 조회
	 * 
	 * @param storeCd
	 * @param discountNm
	 * @param useYn
	 * @return 할인 리스트 조회
	 */
	public List<Discount> findDiscountList(Integer storeCd, String discountNm, String useYn) {
		return repository.findDiscountList(storeCd, discountNm, useYn);
	}
	
	/**
	 * 할인 리스트 조회
	 * 
	 * @param discountNm
	 * @return 할인 리스트 조회
	 */
	public List<Discount> findDiscountList(String discountNm) {
		return repository.findDiscountList(discountNm);
	}
	
	/**
	 * 타겟 별 할인 리스트 조회
	 * 
	 * @param storeCd
	 * @param discountTargetCd
	 * @return 할인 리스트
	 */
	public List<Discount> findByTarget(Integer storeCd, Integer discountTargetCd) {
		return repository.findByTarget(storeCd, discountTargetCd);
	}
	
	
	/**
	 * 타겟, 단말 구분 별 할인 리스트 조회
	 * 
	 * @param storeCd
	 * @param discountTargetCd
	 * @param discountClientCd
	 * @return 할인 리스트
	 */
	public List<Discount> findByTargetAndClient(Integer storeCd, String clientVal) {
		Code target = codeService.findOneByTopCodeValAndVal("DISCOUNT_TARGET", "order");
		
		return repository.findByTarget(storeCd, target.getCd());
	}
	
	
}
