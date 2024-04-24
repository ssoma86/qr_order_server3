package org.lf.app.models.system.noti;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 공지사항
 * 
 * @author LF
 * 
 */
public interface NotiRepository extends BaseRepository<Noti, Serializable> {
	
	
	/**
	 * 언어, 단말 타입, 날자를 통해서 공지사항 가져오기
	 * 
	 * @param lanId 언어 코드
	 * @param clientTpCd 단말 타입 코드
	 * @param nowDtm 날자
	 * @return 공지사항 정보 리스트
	 */
	@Query("SELECT DISTINCT n "
			+ "FROM Noti n "
			+ "    LEFT JOIN n.lan nl "
			+ "    LEFT JOIN n.notiTarget nnt "
			+ "WHERE n.useYn = 'Y' "
			+ "  AND nl.id = :lanId "
			+ "  AND nnt.cd = :notiTargetCd "
			+ "  AND n.startDtm <= :nowDtm "
			+ "  AND n.endDtm >= :nowDtm "
			+ "ORDER BY n.startDtm DESC"
	)
	public List<Noti> findOneByLanAndNotiTarget(@Param("lanId") String lanId,
			@Param("notiTargetCd") Integer notiTargetCd, @Param("nowDtm") Date nowDtm);
	
	
	/**
	 * 언어, 단말 타입, 날자를 통해서 공지사항 가져오기
	 * 
	 * @param lanId 언어 코드
	 * @param clientTpCd 단말 타입 코드
	 * @param nowDtm 날자
	 * @return 공지사항 정보 리스트
	 */
	@Query("SELECT DISTINCT n "
			+ "FROM Noti n "
			+ "    LEFT JOIN n.lan nl "
			+ "    LEFT JOIN n.notiTarget nnt "
			+ "WHERE n.useYn = 'Y' "
			+ "  AND nl.id = :lanId "
			+ "  AND nnt.ref1 = :notiTargetRef1 "
			+ "  AND n.popupYn LIKE :popupYn% "
			+ "  AND n.startDtm <= :nowDtm "
			+ "  AND n.endDtm >= :nowDtm "
			+ "ORDER BY n.startDtm DESC"
	)
	public List<Noti> findOneByLanAndNotiTargetRef1(@Param("lanId") String lanId,
			@Param("notiTargetRef1") String notiTargetRef1,
			@Param("popupYn") String popupYn, @Param("nowDtm") Date nowDtm);
	
	
	/**
	 * 공지사항 정보 조회
	 * 
	 * @param useYn 사용여부
	 * @param lanId 언어 코드
	 * @param notiTargetCd 공지 타겟
	 * @return 공지사항 정보 리스트
	 */
	@Query("SELECT DISTINCT n "
			+ "FROM Noti n "
			+ "    LEFT JOIN n.lan nl "
			+ "    LEFT JOIN n.notiTarget nnt "
			+ "WHERE n.useYn LIKE :useYn% "
			+ "  AND nl.id LIKE :lanId% "
			+ "  AND (:notiTargetCd = 0 OR nnt.cd = :notiTargetCd) "
			+ "ORDER BY n.startDtm DESC"
	)
	public List<Noti> findNotiList(@Param("useYn") String useYn,
			@Param("lanId") String lanId, @Param("notiTargetCd") Integer notiTargetCd);
	
	
}
