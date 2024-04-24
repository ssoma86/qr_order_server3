package org.lf.app.models.business.store;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.address.Address;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매장
 * 
 * @author LF
 * 
 */
public interface StoreRepository extends BaseRepository<Store, Serializable> {
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param custCd
	 * @param storeNm
	 * @param useYn
	 * @return 매장 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Store s "
			+ "    LEFT JOIN s.storeInfos si "
			+ "    LEFT JOIN s.cust sc "
			+ "WHERE ((si.storeInfoNm IS NOT NULL AND si.storeInfoNm LIKE %:storeNm%) OR s.storeNm LIKE %:storeNm%) "
			+ "  AND s.useYn LIKE :useYn% "
			+ "  AND sc.custCd = :custCd "
			+ "ORDER BY s.storeId "
	)
	public List<Store> findStoreList(@Param("custCd") Integer custCd,
			@Param("storeNm") String storeNm, @Param("useYn") String useYn);
	
	
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
	 * 매장 콤보 리스트 조회(관리자 용)
	 * 
	 * @param storeNm
	 * @return 매장 리스트
	 */
//	@Query("SELECT DISTINCT s "
//			+ "FROM Store s "
//			+ "    LEFT JOIN s.storeInfos si "
//			+ "WHERE (si.storeInfoNm IS NULL OR si.storeInfoNm LIKE %:storeNm% OR s.storeNm LIKE %:storeNm%) "
//			+ "ORDER BY s.storeId "
//	)
//	public List<Store> findStoreList(@Param("storeNm") String storeNm);
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param useYn
	 * @param storeTp
	 * @return 매장 리스트
	 */
	public List<Store> findByUseYnAndStoreTpStrContainingOrderByStoreId(String useYn, String StoreTpStr);
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param useYn
	 * @return 매장 리스트
	 */
	@Query("SELECT DISTINCT s "
			+ "FROM Store s "
			+ "WHERE s.useYn LIKE :useYn% "
			+ "ORDER BY s.storeId "
	)
	public List<Store> findStoreList(@Param("useYn") String useYn);
	
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param storeId
	 * @return 매장 정보
	 */
	public Store findOneByStoreIdAndUseYn(String storeId, String useYn);


	/**
	 * 지역주소 리스트 조회
	 *
	 * @param city
	 * @return 지역주소 리스트
	 */
	@Query("SELECT a "
			+ "FROM Address a "
			+ "WHERE a.city LIKE %:city% "
			+ "ORDER BY a.addressCd "
	)
	public List<Address> findAddrList(@Param("city") String city);
	
	
	/**
	 * 스토어에 해당하는 design(주문자화면 가로,세로 보기)값 가져오기 (23.08.09)
	 * @param storeCd
	 * @return
	 */
	@Query("SELECT s.design "
			+ "FROM Store s "
			+ "WHERE s.storeCd = :storeCd ")
	public String getDesign4Store(@Param("storeCd") int storeCd);
	
	
	/**
	 * 주문자화면 가로,세로 보기 update(23.08.09)
	 * @param storeCd
	 * @param design
	 */
	@Transactional
	@Modifying	
	@Query("update Store s set s.design = :design "
			+ "WHERE s.storeCd = :storeCd ")
	public void updateDesign(@Param("storeCd") int storeCd, @Param("design") String design );
	
}
