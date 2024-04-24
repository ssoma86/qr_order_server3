package org.lf.app.models.system.history;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 히스토리 관리
 * @author LF
 * 
 */
public interface HistoryRepository extends BaseRepository<History, Serializable> {
	
	/**
	 * 히스토리 리스트 조회
	 * 
	 * @param accountId
	 * @param resourcesCd
	 * @param sdtm
	 * @param edtm
	 * @return 히스토리 리스트
	 */
	@Query("SELECT h "
			+ "FROM History h "
			+ "    LEFT JOIN h.account ha "
			+ "    LEFT JOIN h.resources hr "
			+ "WHERE (:accountId IS NULL OR (:accountId IS NOT NULL AND ha.accountId = :accountId) ) "
			+ "  AND (:resourcesCd IS NULL OR (:resourcesCd IS NOT NULL AND hr.resourcesCd = :resourcesCd) ) "
			+ "  AND (:sdtm = '' OR (:sdtm != '' AND h.inDtm >= :sdtm) ) "
			+ "  AND (:edtm = '' OR (:edtm != '' AND h.inDtm <= :edtm) ) "
			+ "ORDER BY h.inDtm DESC "
	)
	public List<History> findHistoryList(@Param("accountId") String accountId,
			@Param("resourcesCd") Integer resourcesCd, @Param("sdtm") String sdtm, @Param("edtm") String edtm);
	
	
	
	/**
	 * qr 접속로그 저장 (24.02.21)
	 * @param useYn
	 * @param storeCd
	 * @param storeNm
	 * @param storeRoomCd
	 * @param storeRoomNm
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "insert into sys_qr_history "
			+ " (use_yn, store_cd, store_nm, store_room_cd, store_room_nm, in_dtm) "
			+ " values ( :useYn, :storeCd, :storeNm, :storeRoomCd, :storeRoomNm, sysdate())")
	public void insertQrLog(@Param("useYn") String useYn
								, @Param("storeCd") int storeCd
								, @Param("storeNm") String storeNm
								, @Param("storeRoomCd") int storeRoomCd
								, @Param("storeRoomNm") String storeRoomNm );
	
	
	
}