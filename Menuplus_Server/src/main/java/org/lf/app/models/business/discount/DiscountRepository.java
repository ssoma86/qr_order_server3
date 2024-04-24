package org.lf.app.models.business.discount;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.discount.Discount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 할인
 * 
 * @author LF
 * 
 */
public interface DiscountRepository extends BaseRepository<Discount, Serializable> {
	
	/**
	 * 할인 리스트 조회
	 * 
	 * @param storeCd
	 * @param discountNm
	 * @param useYn
	 * @return 할인 리스트
	 */
	@Query("SELECT DISTINCT d "
			+ "FROM Discount d "
			+ "    LEFT JOIN d.discountInfos di "
			+ "    LEFT JOIN d.store ds "
			+ "WHERE ((di.discountInfoNm IS NOT NULL AND di.discountInfoNm LIKE %:discountNm%) OR d.discountNm LIKE %:discountNm%) "
			+ "  AND d.useYn LIKE :useYn% "
			+ "  AND ds.storeCd = :storeCd "
			+ "ORDER BY d.ord "
	)
	public List<Discount> findDiscountList(@Param("storeCd") Integer storeCd,
			@Param("discountNm") String discountNm, @Param("useYn") String useYn);
	
	/**
	 * 할인 리스트 조회
	 * 
	 * @param discountNm
	 * @return 할인 리스트
	 */
	@Query("SELECT DISTINCT d "
			+ "FROM Discount d "
			+ "    LEFT JOIN d.discountInfos di "
			+ "WHERE ((di.discountInfoNm IS NOT NULL AND di.discountInfoNm LIKE %:discountNm%) OR d.discountNm LIKE %:discountNm%) "
			+ "ORDER BY d.discountNm "
	)
	public List<Discount> findDiscountList(@Param("discountNm") String discountNm);
	
	
	/**
	 * 타겟 별 할인 리스트 조회
	 * 
	 * @param storeCd
	 * @param discountTargetCd
	 * @return 할인 리스트
	 */
	@Query("SELECT DISTINCT d "
			+ "FROM Discount d "
			+ "    LEFT JOIN d.discountTarget dt "
			+ "    LEFT JOIN d.store ds "
			+ "WHERE d.useYn = 'Y' "
			+ "  AND ds.storeCd = :storeCd "
			+ "  AND dt.cd = :discountTargetCd "
			+ "ORDER BY d.ord "
	)
	public List<Discount> findByTarget(@Param("storeCd") Integer storeCd,
			@Param("discountTargetCd") Integer discountTargetCd);
	
	
}
