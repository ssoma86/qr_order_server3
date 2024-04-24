package org.lf.app.models.system.action;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.lf.app.models.BaseRepository;

/**
 * 이벤트
 * 
 * @author LF
 *
 */
@Repository
public interface ActionRepository extends BaseRepository<Action, Serializable> {
	
	/**
	 * 이벤트ID를 통해서 이벤트 정보 조회
	 * 
	 * @param actionId
	 * @return 이벤트 정보
	 */
	public Action findOneByActionId(String actionId);
	
	/**
	 * 이벤트명을 통해서 이벤트 정보 조회
	 * 
	 * @param actionNm
	 * @return 이벤트 정보
	 */
	public Action findOneByActionNm(String actionNm);
	
	/**
	 * 이벤트ID&명, 사용여부, 타입을 통해서 이벤트 정보 리스트 조회
	 * 
	 * @param actionNm
	 * @param useYn
	 * @param actionTpCd
	 * @return 이벤트 정보 리스트
	 */
	@Query("SELECT a "
			+ "FROM Action a "
			+ "    LEFT JOIN a.actionTp atp "
			+ "WHERE ( a.actionId LIKE %:actionNm% OR a.actionNm LIKE %:actionNm% ) "
			+ "  AND a.useYn LIKE :useYn% "
			+ "  AND (atp.cd = :actionTpCd OR 0 = :actionTpCd) "
			+ "ORDER BY a.url, atp.cd "
	)
	public List<Action> findActionList(@Param("actionNm") String actionNm,
			@Param("useYn") String useYn, @Param("actionTpCd") Integer actionTpCd);
	
	
}
