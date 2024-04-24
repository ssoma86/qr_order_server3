package org.lf.app.models.business.smenuOptTp;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 메뉴 옵션 그룹 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class SmenuOptTpService extends BaseService<SmenuOptTp> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private SmenuOptTpRepository repository;
	
	/**
	 * 메뉴 옵션 그룹 리스트 조회
	 * 
	 * @param storeCd
	 * @param smenuOptTpNm
	 * @param useYn
	 * @return 메뉴 옵션 그룹 리스트
	 */
	public List<SmenuOptTp> findSmenuOptTpList(Integer storeCd, String smenuOptTpNm, String useYn) {
		return repository.findSmenuOptTpList(storeCd, smenuOptTpNm, useYn);
	}
	
	/**
	 * 메뉴 옵션 그룹 리스트 조회
	 * 
	 * @param smenuOptTpNm
	 * @return 메뉴 옵션 그룹 리스트
	 */
	public List<SmenuOptTp> findSmenuOptTpList(String smenuOptTpNm) {
		return repository.findSmenuOptTpList(smenuOptTpNm);
	}
	
	/**
	 * 메뉴 옵션 그룹 리스트 조회
	 * @param storeCd
	 * @return
	 */
	public List<SmenuOptTp> findSmenuOptTpList(Integer storeCd) {
		return repository.findSmenuOptTpList(storeCd);
	}
}
