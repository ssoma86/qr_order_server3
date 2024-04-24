package org.lf.app.models.system.terms;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 약관동의 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class TermsAcceptService extends BaseService<TermsAccept> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private TermsAcceptRepository repository;
	
	
	
	/**
	 * 약관 동의 정보 정보 조회
	 * 
	 * @param uuid 폰 고유 키
	 * @return 약관 동의 정보 정보 리스트
	 */
	public List<TermsAccept> findByUuid(String uuid) {
		return repository.findByUuid(uuid);
	}
	
	/**
	 * 약관동의정보 정보 조회
	 * 
	 * @param uuid 폰 고유 키
	 * @param termsTpCd 약관 구분
	 * @return 약관동의정보 정보 리스트
	 */
	public List<TermsAccept> findTermsAcceptList(String uuid, Integer termsTpCd) {
		return repository.findTermsAcceptList(uuid, termsTpCd);
	}
	
	
}
