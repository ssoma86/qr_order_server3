package org.lf.app.models.business.appUser;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 회원
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class AppUserService extends BaseService<AppUser> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AppUserRepository repository;
	
	
	
	/**
	 * 회원 리스트 조회
	 * 
	 * @param user
	 * @param custCd
	 * @return 회원 리스트
	 */
	public List<AppUser> findUserList(AppUser user) {
		return repository.findUserList(user.getAccountId(), user.getAccountNm(),
				user.getUseYn(), user.getNonExpired(), "appUser");
	}
	
	/**
	 * 
	 * @param socialId
	 * @return 앱 회원 정보
	 */
	public AppUser findOneBySocialId(String socialId) {
		return repository.findOneBySocialId(socialId);
	}

	/**
	 * 이메일를 통해 계정 정보 조회
	 * 
	 * @param email 이메일
	 * @return 계정 정보
	 */
	public List<AppUser> findOneByEmail(String email) {
		return repository.findOneByEmail(email);
	}
	
	/**
	 * 이메일를 통해 계정 정보 조회
	 * 
	 * @param email 이메일
	 * @return 계정 정보
	 */
	public List<AppUser> findUserList(String useYn) {
		return repository.findByUseYnAndPushTokenIsNotNull(useYn);
	}
	
	
}
