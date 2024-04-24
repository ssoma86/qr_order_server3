package org.lf.app.models.business.event;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 이벤트
 * 
 * @author LF
 * 
 */
public interface EventRepository extends BaseRepository<Event, Serializable> {
	
	
	/**
	 * 이벤트 정보 조회
	 * 
	 * @param useYn 사용여부
	 * @param lanId 언어 코드
	 * @return 이벤트 정보 리스트
	 */
	@Query("SELECT DISTINCT e "
			+ "FROM Event e "
			+ "    LEFT JOIN e.lan el "
			+ "WHERE e.useYn LIKE :useYn% "
			+ "  AND el.id LIKE :lanId% "
			+ "ORDER BY e.startDtm DESC"
	)
	public List<Event> findEventList(@Param("useYn") String useYn,
			@Param("lanId") String lanId);
	
	
	/**
	 * 언어, 날자를 통해서 공지사항 가져오기
	 * 
	 * @param lanId 언어 코드
	 * @param nowDtm 날자
	 * @return 이벤트 정보 리스트
	 */
	@Query("SELECT DISTINCT e "
			+ "FROM Event e "
			+ "    LEFT JOIN e.lan el "
			+ "WHERE e.useYn = 'Y' "
			+ "  AND el.id = :lanId "
			+ "  AND e.startDtm <= :nowDtm "
			+ "  AND e.endDtm >= :nowDtm "
			+ "ORDER BY e.startDtm DESC"
	)
	public List<Event> findOneByLan(@Param("lanId") String lanId,
			@Param("nowDtm") Date nowDtm);
	
	
}
