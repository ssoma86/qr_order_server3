package org.lf.app.models.business.stuff;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.stuff.Stuff;
import org.lf.app.utils.system.LogUtil;


/**
 * 재료 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class StuffService extends BaseService<Stuff> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StuffRepository repository;
	
	
	/**
	 * 재료 리스트 조회
	 * 
	 * @param storeCd
	 * @param stuffNm
	 * @param useYn
	 * @return 재료 리스트
	 */
	public List<Stuff> findStuffList(Integer storeCd, String stuffNm, String useYn) {
		return repository.findStuffList(storeCd, stuffNm, useYn);
	}
	
	/**
	 * 재료 리스트 조회
	 * 
	 * @param stuffNm
	 * @return 재료 리스트
	 */
	public List<Stuff> findStuffList(String stuffNm) {
		return repository.findStuffList(stuffNm);
	}
	
}
