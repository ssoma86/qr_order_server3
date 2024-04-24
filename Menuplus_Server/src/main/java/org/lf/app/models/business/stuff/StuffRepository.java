package org.lf.app.models.business.stuff;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.stuff.Stuff;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 재료
 * 
 * @author LF
 * 
 */
public interface StuffRepository extends BaseRepository<Stuff, Serializable> {
	
	/**
	 * 재료 리스트 조회
	 * 
	 * @param storeCd
	 * @param stuffNm
	 * @param useYn
	 * @return 재료 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Stuff s "
			+ "    LEFT JOIN s.stuffInfos si "
			+ "    LEFT JOIN s.store ss "
			+ "WHERE ((si.stuffInfoNm IS NOT NULL AND si.stuffInfoNm LIKE %:stuffNm%) OR s.stuffNm LIKE %:stuffNm%) "
			+ "  AND s.useYn LIKE :useYn% "
			+ "  AND ss.storeCd = :storeCd "
			+ "ORDER BY s.stuffNm, si.stuffInfoNm "
	)
	public List<Stuff> findStuffList(@Param("storeCd") Integer storeCd,
			@Param("stuffNm") String stuffNm, @Param("useYn") String useYn);
	
	/**
	 * 재료 리스트 조회
	 * 
	 * @param stuffNm
	 * @return 재료 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Stuff s "
			+ "    LEFT JOIN s.stuffInfos si "
			+ "WHERE ((si.stuffInfoNm IS NOT NULL AND si.stuffInfoNm LIKE %:stuffNm%) OR s.stuffNm LIKE %:stuffNm%) "
			+ "ORDER BY s.stuffNm "
	)
	public List<Stuff> findStuffList(@Param("stuffNm") String stuffNm);
	
}
