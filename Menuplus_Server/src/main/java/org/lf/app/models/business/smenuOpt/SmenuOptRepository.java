package org.lf.app.models.business.smenuOpt;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 메뉴 옵션
 * 
 * @author LF
 * 
 */
public interface SmenuOptRepository extends BaseRepository<SmenuOpt, Serializable> {
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * @param storeCd
	 * @param smenuOptNm
	 * @param useYn
	 * @param smenuOptTpCd
	 * @return
	 */
	@Query("SELECT DISTINCT so "
			+ "FROM SmenuOpt so "
			+ "    LEFT JOIN so.smenuOptInfos soi "
			+ "    LEFT JOIN so.store sos "
			+ "    LEFT JOIN so.smenuOptTp sot "
			+ "WHERE ((soi.smenuOptInfoNm IS NOT NULL AND soi.smenuOptInfoNm LIKE %:smenuOptNm%) OR so.smenuOptNm LIKE %:smenuOptNm%) "
			+ "  AND so.useYn LIKE :useYn% "
			+ "  AND sot.useYn LIKE :useYn% "
			+ "  AND sos.storeCd = :storeCd "
			+ "  AND (:smenuOptTpCd IS NULL OR sot.smenuOptTpCd = :smenuOptTpCd )"
			+ "ORDER BY sot.ord, so.ord "
	)
	public List<SmenuOpt> findSmenuOptList(@Param("storeCd") Integer storeCd
											, @Param("smenuOptNm") String smenuOptNm
											, @Param("useYn") String useYn
											, @Param("smenuOptTpCd") Integer smenuOptTpCd);
	
	
	
	/**
	 * 메뉴 옵션 리스트 조회(23.10.13)
	 * @param storeCd
	 * @param useYn
	 * @param smenuOptTpCd
	 * @return
	 */
	@Query("SELECT DISTINCT so "
			+ "FROM SmenuOpt so "
			+ "    LEFT JOIN so.smenuOptInfos soi "
			+ "    LEFT JOIN so.store sos "
			+ "    LEFT JOIN so.smenuOptTp sot "
			+ "WHERE so.useYn LIKE :useYn% "
			+ "  AND sot.useYn LIKE :useYn% "
			+ "  AND sos.storeCd = :storeCd "
			+ "  AND (:smenuOptTpCd IS NULL OR sot.smenuOptTpCd = :smenuOptTpCd )"
			+ "ORDER BY sot.ord, so.ord "
	)
	public List<SmenuOpt> findSmenuOptList(@Param("storeCd") Integer storeCd
											, @Param("useYn") String useYn
											, @Param("smenuOptTpCd") Integer smenuOptTpCd);
	
	
	
	
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * 
	 * @param storeCd
	 * @param smenuOptNm
	 * @param useYn
	 * @return 메뉴 옵션 리스트
	 */
	@Query("SELECT DISTINCT so "
			+ "FROM SmenuOpt so "
			+ "    LEFT JOIN so.smenuOptInfos soi "
			+ "    LEFT JOIN so.store sos "
			+ "    LEFT JOIN so.smenuOptTp sot "
			+ "WHERE ((soi.smenuOptInfoNm IS NOT NULL AND soi.smenuOptInfoNm LIKE %:smenuOptNm%) OR so.smenuOptNm LIKE %:smenuOptNm%) "
			+ "  AND so.useYn LIKE :useYn% "
			+ "  AND sos.storeCd = :storeCd "
			+ "ORDER BY sot.ord, so.ord "
	)
	public List<SmenuOpt> findSmenuOptList(@Param("storeCd") Integer storeCd
											, @Param("smenuOptNm") String smenuOptNm
											, @Param("useYn") String useYn);
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * 
	 * @param smenuOptNm
	 * @return 메뉴 옵션 리스트
	 */
	@Query("SELECT DISTINCT so "
			+ "FROM SmenuOpt so "
			+ "    LEFT JOIN so.smenuOptInfos soi "
			+ "WHERE ((soi.smenuOptInfoNm IS NOT NULL AND soi.smenuOptInfoNm LIKE %:smenuOptNm%) OR so.smenuOptNm LIKE %:smenuOptNm%) "
			+ "ORDER BY so.smenuOptNm "
	)
	public List<SmenuOpt> findSmenuOptList(@Param("smenuOptNm") String smenuOptNm);
	
}
