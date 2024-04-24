package org.lf.app.models.system.code;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;

/**
 * 공통 코드
 * 
 * @author LF
 * 
 */
public interface CodeRepository extends BaseRepository<Code, Serializable> {
	
	/**
	 * 상위 코드 및 코드값을 통해서 코드 조회
	 * 
	 * @param topCode
	 * @param val
	 * @return 코드
	 */
	public Code findOneByTopCodeAndVal(Code topCode, String val);
	
	/**
	 * 상위 코드, 사용여부를 통해서 코드 리스트 조회
	 * 
	 * @param topCode
	 * @param useYn
	 * @return 코드 리스트
	 */
	public List<Code> findByTopCodeAndUseYnStartingWithOrderByOrd(Code topCode, String useYn);
	
	
}
