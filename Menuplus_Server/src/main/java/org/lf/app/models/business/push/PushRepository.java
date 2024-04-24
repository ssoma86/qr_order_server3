package org.lf.app.models.business.push;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 푸시
 * 
 * @author LF
 * 
 */
public interface PushRepository extends BaseRepository<Push, Serializable> {
	
	
	/**
	 * 날자를 통해서 푸시 가져오기
	 * 
	 * @param startDtm 시작 일시
	 * @param endDtm 종료 일시
	 * @param pushTpCd 푸시 구분
	 * @return 푸시 정보 리스트
	 */
	@Query("SELECT p "
			+ "FROM Push p "
			+ "    LEFT JOIN p.pushTp pt "
			+ "WHERE p.sendDtm >= :startDtm "
			+ "  AND p.sendDtm <= :endDtm "
			+ "  AND (:pushTpCd = 0 OR pt.cd = :pushTpCd) "
			+ "ORDER BY p.sendDtm DESC"
	)
	public List<Push> findPushList(
			@Param("startDtm") Date startDtm, @Param("endDtm") Date endDtm,
			@Param("pushTpCd") Integer pushTpCd);
	
	
}
