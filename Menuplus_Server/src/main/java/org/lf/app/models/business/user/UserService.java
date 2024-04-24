package org.lf.app.models.business.user;

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
public class UserService extends BaseService<User> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserRepository repository;
	
	
	
	/**
	 * 회원 리스트 조회
	 * 
	 * @param user
	 * @param custCd
	 * @return 회원 리스트
	 */
	public List<User> findUserList(User user, Integer custCd) {
		return repository.findUserList(user.getAccountId(), user.getAccountNm(),
				user.getUseYn(), user.getNonExpired(), custCd, "custMng");
	}
	
	/**
	 * 사업장 하위 매장 회원 리스트 조회
	 * 
	 * @param user
	 * @param custCd
	 * @return 회원 리스트
	 */
	public List<User> findUserListForCust(User user, Integer custCd) {
		return repository.findUserList(user.getAccountId(), user.getAccountNm(),
				user.getUseYn(), user.getNonExpired(), custCd, "storeMng");
	}
	
}
