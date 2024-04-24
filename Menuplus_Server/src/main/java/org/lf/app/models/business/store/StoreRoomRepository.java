package org.lf.app.models.business.store;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 객실
 * 
 * @author 박영근
 * 
 */
public interface StoreRoomRepository extends BaseRepository<StoreRoom, Serializable> {
	
	/**
	 * 객실정보 리스트 가져오기
	 * @param storeId
	 * @return
	 */	
	@Query("SELECT a "
			+ "FROM StoreRoom a "
			+ "		LEFT JOIN a.store s "
			+ "WHERE s.storeCd = :storeCd "
			+ "ORDER BY a.storeRoomCd "
	)
	public List<StoreRoom> findStoreRoomList(@Param("storeCd") int storeCd);
	
	/**
	 * 객실정보 리스트 가져오기
	 * @param storeCd
	 * @param storeRoomNm
	 * @param useYn
	 * @return
	 */
	@Query("SELECT DISTINCT a "
			+ "FROM StoreRoom a"
			+ "    LEFT JOIN a.store s "
			+ "WHERE (a.storeRoomNm LIKE %:storeRoomNm%) "
			+ "  AND a.useYn = :useYn "
			+ "  AND s.storeCd = :storeCd "
			+ "ORDER BY a.storeRoomCd "
	)
	public List<StoreRoom> findStoreRoomList(@Param("storeCd") int storeCd, @Param("storeRoomNm") String storeRoomNm, @Param("useYn") String useYn);
	
	/**
	 * 객실정보 가져오기
	 * @param storeCd
	 * @param storeRoomNm
	 * @return
	 */
	@Query("SELECT a "
			+ "FROM StoreRoom a "
			+ "		LEFT JOIN a.store s "
			+ "WHERE s.storeCd = :storeCd "
			+ "  AND a.useYn = :useYn"
			+ "  AND a.storeRoomNm = :storeRoomNm "
	)
	public StoreRoom findStoreRoom(@Param("storeCd") int storeCd, @Param("storeRoomNm") String storeRoomNm, @Param("useYn") String useYn);
	
	/**
	 * 객실키 값 가져오기(23.12.27)
	 * @param storeCd
	 * @param storeRoomNm
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "select store_room_cd "
			+ "from tbl_store_room "
			+ "where store_store_cd = :storeCd "
			+ "and store_room_nm = :storeRoomNm "
			+ "and use_yn = :useYn"
	)
	public int getStoreRoomCd(@Param("storeCd") int storeCd, @Param("storeRoomNm") String storeRoomNm, @Param("useYn") String useYn);
	
	
}
