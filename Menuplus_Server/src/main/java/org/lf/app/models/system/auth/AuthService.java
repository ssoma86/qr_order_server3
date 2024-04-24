package org.lf.app.models.system.auth;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 권한
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class AuthService extends BaseService<Auth> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AuthRepository repository;

	
	
	/**
	 * 권한명을 통해 권한 정보 조회
	 * 
	 * @param authNm
	 * @return 권한 정보
	 */
	public Auth findOneByAuthNm(String authNm) {
		return repository.findOneByAuthNm(authNm);
	}
	
	/**
	 * 권한ID, 권한명, 사용여부를 통해 권한 정보 리스트 조회
	 * 
	 * @param authId
	 * @param authNm
	 * @param useYn
	 * @return 권한 정보 리스트
	 */
	public List<Auth> findAuthList(Auth role) {
		return repository.findAuthList(role.getAuthId(), role.getAuthNm(), role.getUseYn());
	}

	
}
