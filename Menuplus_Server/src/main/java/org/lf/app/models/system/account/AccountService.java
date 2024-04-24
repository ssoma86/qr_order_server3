package org.lf.app.models.system.account;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.lf.app.config.security.IAccountService;
import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


/**
 * 계정관리 서비스
 * IAccountService 상속 받아서 Spring security에 제공됨
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class AccountService extends BaseService<Account> implements IAccountService {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AccountRepository repository;
	
	
	
	/**
	 * 계정 리스트 조회
	 * 
	 * @param account 계정 정보
	 * @return 계정 정보 리스트
	 */
	public List<Account> findAccountList(Account account) {
		return repository.findAccountList(account.getAccountId(), account.getAccountNm(),
				account.getUseYn(), account.getNonLocked(), 
				account.getNonExpired(), account.getCertificateNonExpired());
	}
	
	
	/**
	 * 계정 리스트 조회
	 * 
	 * @param account 계정 정보
	 * @return 계정 정보 리스트
	 */
	public List<Account> findAccountList(String accountNm) {
		return repository.findAccountList(accountNm);
	}
	
	
	/**
	 * Spring security에서 계정조회
	 * @param username = accountId
	 * @return UserDetails = Account
	 */
	@Override
	public UserDetails getUserDetails(String username) {
		// 이메일 로그인
		if (ValidUtil.isEmail(username)) {
			List<Account> accountList = this.findOneByEmail(username);
			return !ObjectUtils.isEmpty(accountList) && accountList.size() > 0 ? accountList.get(0) : null;
		} else {
			Optional<Account> optional = repository.findById(username);
			return optional.orElse(null);
		}
	}
	
	
	/**
	 * 전화번호를 통해 계정 정보 조회
	 * 
	 * @param tel 전화번호
	 * @return 계정 정보
	 */
	public Account findOneByTel(String tel) {
		return repository.findOneByTel(tel);
	}
	
	
	/**
	 * 이메일를 통해 계정 정보 조회
	 * 
	 * @param email 이메일
	 * @return 계정 정보
	 */
	public List<Account> findOneByEmail(String email) {
		return repository.findOneByEmail(email);
	}
	
	
	/**
	 * 임시 코드를 통해 계정 정보 조회
	 * 
	 * @param tmpPassword
	 * @return 계정 정보
	 */
	public Account findOneByTmpPassword(String tmpPassword) {
		return repository.findOneByTmpPassword(tmpPassword);
	}
	
	/**
	 * accountId를 통해 계정 정보 조회(24.02.08)
	 * @param accountId
	 * @return
	 */
	public Account findAccountByAccountId(String accountId) {
		return repository.findAccountByAccountId(accountId);
	}

	/**
	 * 가맹점관리자 로그인정보와 일치하는지 비교(아이디,비밀번호)
	 * @param id
	 * @param pw
	 * @return
	 */
	public int findByMerchantId(String id, String pw){
		return repository.findOneByMerchantId(id, pw);
	}
	
	/**
	 * accountId에 해당하는 token값 update(23.11.20)
	 * @param token
	 * @param accountId
	 */
	public void updateToken4AccountId(String token, String accountId) {
		repository.updateToken4AccountId(token, accountId);
	}
	
	/**
	 * accountId에 해당하는 push info값 update(23.11.23)
	 * @param token
	 * @param osType
	 * @param accountId
	 */
	public void updatePushInfo4AccountId(String token, String osType, String accountId) {
		repository.updatePushInfo4AccountId(token, osType, accountId);
	}
	
	/**
	 * accountId 리스트 얻어오기(23.11.28)
	 * @param storeCd
	 * @return
	 */
	public List<String> getAccountIdList(Integer storeCd) {
		return repository.getAccountIdList(storeCd);
	}
	
}
