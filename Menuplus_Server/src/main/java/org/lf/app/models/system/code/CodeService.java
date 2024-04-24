package org.lf.app.models.system.code;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 공통 코드
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class CodeService extends BaseService<Code> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private CodeRepository repository;
	
	
	
	/**
	 * 상위 코드 및 코드값을 통해서 코드 조회
	 * 
	 * @param topCode
	 * @param val
	 * @return 코드
	 */
	public Code findOneByTopCodeAndVal(Code topCode, String val) {
		return repository.findOneByTopCodeAndVal(topCode, val);
	}
	
	/**
	 * 상위 코드 값 및 코드값을 통해서 코드 조회
	 * 
	 * @param topCodeVal
	 * @param val
	 * @return 코드
	 */
	public Code findOneByTopCodeValAndVal(String topCodeVal, String val) {
		return repository.findOneByTopCodeAndVal(repository.findOneByTopCodeAndVal(null, topCodeVal), val);
	}
	
	/**
	 * 상위 코드 값을 통해서 코드 조회
	 * 
	 * @param topCode
	 * @return 코드 리스트
	 */
	public List<Code> findByTopCode(String topCodeVal) {
		if (null == topCodeVal || "".equals(topCodeVal)) {
			return null;
		} else {
			Code topCode = repository.findOneByTopCodeAndVal(null, topCodeVal);
			if (null == topCode) {
				return null;
			} else {
				return repository.findByTopCodeAndUseYnStartingWithOrderByOrd(repository.findOneByTopCodeAndVal(null, topCodeVal), "Y");
			}
		}
	}
	
	/**
	 * 코드 값, 명, 사용여부를 통해 코드 리스트 조회
	 * 
	 * @param nm
	 * @param useYn
	 * @return 코드 리스트
	 */
	public List<Code> findCodeList() {
		return repository.findByTopCodeAndUseYnStartingWithOrderByOrd(null, "");
	}

	
}
