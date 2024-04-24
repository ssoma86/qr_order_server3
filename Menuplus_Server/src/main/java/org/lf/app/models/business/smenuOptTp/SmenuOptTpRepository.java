package org.lf.app.models.business.smenuOptTp;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 메뉴 옵션 그룹
 * 
 * @author LF
 * 
 */
public interface SmenuOptTpRepository extends BaseRepository<SmenuOptTp, Serializable> {
	
	/**
	 * 메뉴 옵션 그룹 리스트 조회
	 * 
	 * @param storeCd
	 * @param smenuOptTpNm
	 * @param useYn
	 * @return 메뉴 옵션 그룹 리스트
	 */
	@Query("SELECT DISTINCT sot "
			+ "FROM SmenuOptTp sot "
			+ "    LEFT JOIN sot.smenuOptTpInfos soti "
			+ "    LEFT JOIN sot.store sots "
			+ "WHERE ((soti.smenuOptTpInfoNm IS NOT NULL AND soti.smenuOptTpInfoNm LIKE %:smenuOptTpNm%) OR sot.smenuOptTpNm LIKE %:smenuOptTpNm%) "
			+ "  AND sot.useYn LIKE :useYn% "
			+ "  AND sots.storeCd = :storeCd "
			+ "ORDER BY sot.ord "
	)
	public List<SmenuOptTp> findSmenuOptTpList(@Param("storeCd") Integer storeCd,
			@Param("smenuOptTpNm") String smenuOptNm, @Param("useYn") String useYn);
	
	
	/**
	 * 메뉴 옵션 그룹 리스트 조회
	 * 
	 * @param smenuOptTpNm
	 * @return 메뉴 옵션 그룹 리스트
	 */
	@Query("SELECT DISTINCT sot "
			+ "FROM SmenuOptTp sot "
			+ "    LEFT JOIN sot.smenuOptTpInfos soti "
			+ "WHERE ((soti.smenuOptTpInfoNm IS NOT NULL AND soti.smenuOptTpInfoNm LIKE %:smenuOptTpNm%) OR sot.smenuOptTpNm LIKE %:smenuOptTpNm%) "
			+ "ORDER BY sot.smenuOptTpNm "
	)
	public List<SmenuOptTp> findSmenuOptTpList(@Param("smenuOptTpNm") String smenuOptNm);
	
	/**
	 * 메뉴옵션 그룹 리스트 조회
	 * @param storeCd
	 * @return
	 */
	@Query("SELECT DISTINCT sot "
			+ "FROM SmenuOptTp sot "
			+ "    LEFT JOIN sot.store sots "
			+ "WHERE sots.storeCd = :storeCd "
			+ "  AND sot.useYn = 'Y' "
			+ "ORDER BY sot.ord "
	)
	public List<SmenuOptTp> findSmenuOptTpList(@Param("storeCd") Integer storeCd);
	
	
}
