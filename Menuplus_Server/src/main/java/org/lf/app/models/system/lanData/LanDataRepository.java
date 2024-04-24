package org.lf.app.models.system.lanData;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 언어 데이타
 * 
 * @author LF
 * 
 */
public interface LanDataRepository extends BaseRepository<LanData, Serializable> {
	
	/**
	 * 언어ID, 언어코드를 통해 언어 데이타 정보 조회
	 * 
	 * @param id
	 * @param lan
	 * @return 언어 데이타 정보
	 */
	public LanData findOneByIdAndLan(String id, Lan lan);
	
	/**
	 * 언어, 클라이언트 타입을 통해 언어 데이타 정보 조회(앱에서 사용)
	 * 
	 * @param lan 언어
	 * @param clientTp 클라이언트 타입
	 * @return 언어 데이타 정보
	 */
	public List<LanData> findByLanAndClientTp(Lan lan, Code clientTp);
	
	/**
	 * 클라이언트 타입을 통해 언어 데이타 정보 조회(웹앱에서 사용)
	 * 
	 * @param lan 언어
	 * @param clientTp 클라이언트 타입
	 * @return 언어 데이타 정보
	 */
	public List<LanData> findByClientTp(Code clientTp);
	
	/**
	 * 다국어 정보 리스트 조회
	 * 
	 * @param lanNm
	 * @return 다국어 정보 리스트
	 */
	@Query("SELECT DISTINCT l "
			+ "FROM LanData l "
			+ "    LEFT JOIN l.lanDatas ls "
			+ "    LEFT JOIN l.clientTp lc "
			+ "WHERE l.lan.id = 'ko' "
			+ "  AND (l.id LIKE %:nm% OR l.nm LIKE %:nm% "
			+ "       OR (ls.nm IS NOT NULL AND ls.nm LIKE %:nm%)) "
			+ "  AND (:clientTpCd = 0 OR lc.cd = :clientTpCd) "
			+ "ORDER BY l.id "
	)
	public List<LanData> findLanDataList(@Param("nm") String nm,
			@Param("clientTpCd") Integer clientTpCd);

	
	
	
}
