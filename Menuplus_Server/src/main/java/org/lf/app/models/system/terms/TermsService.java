package org.lf.app.models.system.terms;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 약관 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class TermsService extends BaseService<Terms> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private TermsRepository repository;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
    private CodeService codeService;
	
	
	
	/**
	 * 언어 값을 통해서 약관정보 가져오기
	 * 
	 * @param lanId 언어
	 * @return 약관 정보
	 */
	public List<Terms> findByLan(String lanId) {
		// 언어 체크
		Lan lan = lanService.findOne(lanId);
		if(null == lan) {
			lan = lanService.findOne("en");
		}
				
		return repository.findByLanAndUseYn(lan, "Y");
	}
	
	
	
	/**
	 * 언어 값을 통해서 약관정보 가져오기
	 * 
	 * @param lanId 언어
	 * @param termsTpVal 약관 타입
	 * @return 약관 정보
	 */
	public Terms findOneByLanAndTermsTp(String lanId, String termsTpVal) {
		// 언어 체크
		Lan lan = lanService.findOne(lanId);
		if(null == lan) {
			lan = lanService.findOne("en");
		}
		
		// 약관 타입
		Code termsTp = codeService.findOneByTopCodeValAndVal("TERMS_TP", termsTpVal);
		if(null == termsTp) {
			termsTp = codeService.findOneByTopCodeValAndVal("TERMS_TP", "UseTerms");
		}
				
		return repository.findOneByLanAndTermsTp(lan, termsTp);
	}
	
	
	
	/**
	 * 언어 값을 통해서 약관정보 가져오기
	 * 
	 * @param lanId 언어
	 * @param termsTpVal 약관 타입
	 * @return 약관 정보
	 */
	public Terms findOneByLanIdAndTermsTp(String lanId, Integer termsTpCd) {
		// 언어 체크
		Lan lan = lanService.findOne(lanId);
		// 약관 타입
		Code termsTp = codeService.findOne(termsTpCd);
				
		return repository.findOneByLanAndTermsTp(lan, termsTp);
	}
	
	
	
	/**
	 * 약관정보 정보 조회
	 * 
	 * @param terms 약관정보
	 * @param lanId 언어 값
	 * @return 약관정보 정보 리스트
	 */
	public List<Terms> findTermsList(Terms terms, String lanId, Integer termsTpCd) {
		return repository.findTermsList(terms.getUseYn(), lanId, termsTpCd);
	}
	
	
}
