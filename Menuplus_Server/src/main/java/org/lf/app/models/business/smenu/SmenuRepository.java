package org.lf.app.models.business.smenu;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.smenu.Smenu;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메뉴
 * 
 * @author LF
 * 
 */
public interface SmenuRepository extends BaseRepository<Smenu, Serializable> {
	
	/**
	 * 메뉴 리스트 조회
	 * 
	 * @param smenuNm
	 * @param storeCd
	 * @param categoryCd
	 * @param useYn
	 * @return 메뉴 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Smenu s "
			+ "    LEFT JOIN s.smenuInfos si "
			+ "    LEFT JOIN s.store ss " 
			+ "    LEFT JOIN s.category sc "   //메뉴는 한개의 카테고리와 연계된다. 기존은 여러개 였슴.(23.09.08)
			//+ "    LEFT JOIN s.categorys sc " 
			+ "    LEFT JOIN OrdSmenuInCategory os on s.smenuCd = os.smenuCd "
			+ "WHERE ((si.smenuInfoNm IS NOT NULL AND si.smenuInfoNm LIKE %:smenuNm%) OR s.smenuNm LIKE %:smenuNm%) "
			+ "  AND s.useYn LIKE :useYn% "
			+ "  AND ss.storeCd = :storeCd "
			+ "  AND (:categoryCd IS NULL OR sc.categoryCd = :categoryCd) "
			+ "ORDER BY ss.storeCd, os.ord, s.ord desc "
			//+ "ORDER BY ss.storeCd, sc.ord, s.ord, s.smenuCd "
	)
	public List<Smenu> findSmenuList(@Param("smenuNm") String smenuNm,
			@Param("storeCd") Integer storeCd, @Param("categoryCd") Integer categoryCd,
			@Param("useYn") String useYn);
	
	/**
	 * 메뉴 리스트 조회
	 * 
	 * @param smenuNm
	 * @param storeCd
	 * @param categoryCd
	 * @param useYn
	 * @return 메뉴 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Smenu s "
			+ "    LEFT JOIN s.smenuInfos si "
			+ "WHERE ((si.smenuInfoNm IS NOT NULL AND si.smenuInfoNm LIKE %:smenuNm%) OR s.smenuNm LIKE %:smenuNm%) "
			+ "ORDER BY s.smenuNm "
	)
	public List<Smenu> findSmenuList(@Param("smenuNm") String smenuNm);
	
	
	
	
	
	/**
	 * 메뉴 리스트 조회
	 * 
	 * @param storeCd
	 * @param categoryCd
	 * @param useYn
	 * @return 메뉴 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Smenu s "
			+ "    LEFT JOIN s.smenuInfos si "
			+ "    LEFT JOIN s.store ss " 
			+ "    LEFT JOIN s.category sc "   //메뉴는 한개의 카테고리와 연계된다. 기존은 여러개 였슴.(23.09.08)
			//+ "    LEFT JOIN s.categorys sc " 
			+ "    LEFT JOIN OrdSmenuInCategory os on s.smenuCd = os.smenuCd "
			+ "WHERE s.useYn LIKE :useYn% "
			+ "  AND ss.storeCd = :storeCd "
			+ "  AND (:categoryCd IS NULL OR sc.categoryCd = :categoryCd) "
			+ "ORDER BY ss.storeCd, os.ord, s.ord desc "
	)
	public List<Smenu> findSmenuList(@Param("storeCd") Integer storeCd, @Param("categoryCd") Integer categoryCd,@Param("useYn") String useYn);
	
	
	
	
	@Transactional
	@Modifying	
	@Query("update Smenu s set s.category = :category "
			+ "WHERE s.smenuCd = :smenuCd ")
	public void updateCategoryInSmenu(@Param("category") Category category, @Param("smenuCd") Integer smenuCd);
	
	
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "DELETE FROM tbl_smenu_x_category "
			+ "WHERE smenu_cd = :smenuCd "
	)
	public void deleteSmenuXCategory(@Param("smenuCd") Integer smenuCd);
	
	
}
