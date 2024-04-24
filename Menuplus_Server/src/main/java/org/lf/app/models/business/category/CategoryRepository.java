package org.lf.app.models.business.category;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.OrdSmenuInCategory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 카테고리
 * 
 * @author LF
 * 
 */
public interface CategoryRepository extends BaseRepository<Category, Serializable> {

	/**
	 * 카테고리 리스트 조회
	 * 
	 * @param storeCd
	 * @param categoryNm
	 * @param useYn
	 * @return 카테고리 리스트
	 */
	@Query("SELECT DISTINCT c "
			+ "FROM Category c "
			+ "    LEFT JOIN c.categoryInfos ci "
			+ "    LEFT JOIN c.store cs "
			+ "WHERE ((ci.categoryInfoNm IS NOT NULL AND ci.categoryInfoNm LIKE %:categoryNm%) OR c.categoryNm LIKE %:categoryNm%) "
			+ "  AND c.useYn LIKE :useYn% "
			+ "  AND cs.storeCd = :storeCd "
			+ "ORDER BY c.ord "
	)
	public List<Category> findCategoryList(@Param("storeCd") Integer storeCd,
			@Param("categoryNm") String categoryNm, @Param("useYn") String useYn);
	
	/**
	 * 카테고리 리스트 조회
	 * 
	 * @param categoryNm
	 * @return 카테고리 리스트
	 */
	@Query("SELECT DISTINCT c "
			+ "FROM Category c "
			+ "    LEFT JOIN c.categoryInfos ci "
			+ "WHERE ((ci.categoryInfoNm IS NOT NULL AND ci.categoryInfoNm LIKE %:categoryNm%) OR c.categoryNm LIKE %:categoryNm%) "
			+ "ORDER BY c.ord "
	)
	public List<Category> findCategoryList(@Param("categoryNm") String categoryNm);
	
	
	
	
	/**
	 * 카테고리에 해당하는 메뉴리스트 순번 가져오기
	 * @param categoryCd
	 * @return
	 */
	@Query("SELECT DISTINCT o "
			+ "FROM OrdSmenuInCategory o "
			+ "WHERE o.categoryCd = :categoryCd "
			+ "ORDER BY o.ord "
	)
	public List<OrdSmenuInCategory> findSmenuOrdList4Category(@Param("categoryCd") Integer categoryCd);
	
	/**
	 * 카테고리와 연관된 메뉴리스트의 순번정보 저장
	 * @param categoryCd
	 * @param smenuCd
	 * @param ord
	 */
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "INSERT INTO tbl_ord_smenu_in_category (category_cd, smenu_cd, smenu_nm_lan, use_yn, ord) "
			+ "values(:categoryCd, :smenuCd, :smenuNmLan, :useYn, :ord) "

	)
	public void insertOrdSmenuInCategory(@Param("categoryCd") Integer categoryCd
										, @Param("smenuCd") Integer smenuCd
										, @Param("smenuNmLan") String smenuNmLan
										, @Param("useYn") String useYn
										, @Param("ord") Integer ord);
	
	/**
	 * 
	 * @param smenuCd
	 */
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "DELETE FROM tbl_ord_smenu_in_category "
			+ "WHERE smenu_cd = :smenuCd "
	)
	public void deleteOrdSmenuInCategory4Smenu(@Param("smenuCd") Integer smenuCd);
	
	
	/**
	 * 
	 * @param categoryCd
	 */
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "DELETE FROM tbl_ord_smenu_in_category "
			+ "WHERE category_cd = :categoryCd "
	)
	public void deleteOrdSmenuInCategory4Category(@Param("categoryCd") Integer categoryCd);
	
	/**
	 * 카테고리 ord select(23.09.08)
	 * @param categoryCd
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT ord FROM tbl_category "
			+ "WHERE category_cd = :categoryCd ")
	public Integer selectCategoryOrd(@Param("categoryCd") Integer categoryCd);
	
	/**
	 * 카테고리 ord update(23.09.07)
	 * @param categoryCd
	 * @param ord
	 */
	@Transactional
	@Modifying	
	@Query("update Category c set c.ord = :ord "
			+ "WHERE c.categoryCd = :categoryCd ")
	public void updateOrdInCategory(@Param("categoryCd") Integer categoryCd, @Param("ord") Integer ord );
	
	
}
