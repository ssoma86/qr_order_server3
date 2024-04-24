package org.lf.app.models.business.store;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.category.Category;
import org.lf.app.utils.system.LogUtil;


/**
 * 객실정보 서비스
 * 
 * @author 박영근
 *
 */
@Service
@Transactional
public class StoreRoomService extends BaseService<StoreRoom> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StoreRoomRepository repository;
	
	/**
	 * 
	 * 매장 객실정보 리스트 조회
	 * @param storeCd
	 * @return
	 */
	public List<StoreRoom> findStoreRoomList(int storeCd){
		return repository.findStoreRoomList(storeCd);
	}
	
	/**
	 * 객실정보 가져오기
	 * @param storeCd
	 * @param storeRoomNm
	 * @param useYn
	 * @return
	 */
	public List<StoreRoom> findStoreRoomList(int storeCd, String storeRoomNm, String useYn){
		return repository.findStoreRoomList(storeCd, storeRoomNm, useYn);
	}
	
	/**
	 * 객실정보 가져오기
	 * @param storeCd
	 * @param storeRoomNm
	 * @param useYn
	 * @return
	 */
	public StoreRoom findStoreRoom(int storeCd, String storeRoomNm, String useYn) {
		return repository.findStoreRoom(storeCd, storeRoomNm, useYn);
	}
	
	
	/**
	 * 객실키 값 가져오기(23.12.27)
	 * @param storeCd
	 * @param storeRoomNm
	 * @return
	 */
	public int getStoreRoomCd(int storeCd, String storeRoomNm, String useYn) {
		return repository.getStoreRoomCd(storeCd, storeRoomNm, useYn);
	}
	
}
