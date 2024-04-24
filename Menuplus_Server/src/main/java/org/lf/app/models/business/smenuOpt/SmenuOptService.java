package org.lf.app.models.business.smenuOpt;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.utils.system.LogUtil;


/**
 * 메뉴 옵션 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class SmenuOptService extends BaseService<SmenuOpt> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private SmenuOptRepository repository;
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * 
	 * @param storeCd
	 * @param smenuOptNm
	 * @param useYn
	 * @return 메뉴 옵션 리스트
	 */
	public List<SmenuOpt> findSmenuOptList(Integer storeCd, String smenuOptNm, String useYn, Integer smenuOptTpCd) {
		return repository.findSmenuOptList(storeCd, smenuOptNm, useYn, smenuOptTpCd);
	}
	
	/**
	 * 메뉴 옵션 리스트 조회(23.10.13)
	 * @param storeCd
	 * @param useYn
	 * @param smenuOptTpCd
	 * @return
	 */
	public List<SmenuOpt> findSmenuOptList(Integer storeCd, String useYn, Integer smenuOptTpCd) {
		return repository.findSmenuOptList(storeCd, useYn, smenuOptTpCd);
	}
	
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * @param storeCd
	 * @param smenuOptNm
	 * @param useYn
	 * @return
	 */
	public List<SmenuOpt> findSmenuOptList(Integer storeCd, String smenuOptNm, String useYn) {
		return repository.findSmenuOptList(storeCd, smenuOptNm, useYn);
	}	
	
	/**
	 * 메뉴 옵션 리스트 조회
	 * 
	 * @param smenuOptNm
	 * @return 메뉴 옵션 리스트
	 */
	public List<SmenuOpt> findSmenuOptList(String smenuOptNm) {
		return repository.findSmenuOptList(smenuOptNm);
	}
	
}
