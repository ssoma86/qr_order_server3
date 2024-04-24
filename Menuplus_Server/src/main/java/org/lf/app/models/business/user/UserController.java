package org.lf.app.models.business.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.lf.app.models.business.cust.CustController.CustControllerCommonJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.user.User.UserValid;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.Account.AccountPwValid;
import org.lf.app.models.system.account.Account.AccountValid;
import org.lf.app.models.system.account.AccountController.AccountControllerJsonView;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 계정 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/user")
public class UserController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService service;
	
	// JsonView
	public interface UserControllerCustJsonView extends AccountControllerJsonView, CustControllerCommonJsonView {}
	public interface UserControllerStoreJsonView extends UserControllerCustJsonView, StoreControllerJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param user
	 * @return 회원 정보
	 */
	@PostMapping
	public void add(@Validated({UserValid.class, AccountValid.class, AccountPwValid.class }) @RequestBody User user) {
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("custMng");
		auths.add(auth);
		user.setAuths(auths);
		
		// 비번 암호화
//		user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
		user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
		service.save(user);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param accountId 계정ID
	 * @return 계정 정보
	 */
	@GetMapping("/{accountId}")
	@JsonView(UserControllerCustJsonView.class)
	public User get(@PathVariable String accountId) {
		return service.findOne(accountId);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param user
	 * @param accountId
	 * @return 회원 정보
	 */
	@PutMapping("/{accountId}")
	public void up(@Validated({UserValid.class, AccountValid.class, AccountPwValid.class }) @RequestBody User user, @PathVariable String accountId) {
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("custMng");
		auths.add(auth);
		user.setAuths(auths);
				
		if (!service.findOne(accountId).getAccountPw().equals(user.getAccountPw())) {
//			user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
			user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
		}
		
		user.setAccountId(accountId);
		
		service.save(user);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param accountId 회원 코드 리스트
	 */
	@DeleteMapping("/{accountIds}")
	public void del(@PathVariable String[] accountIds) {
		service.useYn(accountIds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param user
	 * @param custCd
	 * @return 회원 정보 리스트
	 */
	@GetMapping
	@JsonView(UserControllerCustJsonView.class)
	public List<User> search(User user, Integer custCd) {
		return service.findUserList(user, custCd);
	}
	
	
	
	/**
	 * 사업장 관리자 자기 회원 정보 조회
	 * 
	 * @return 회원 정보
	 */
	@GetMapping("/info/cust")
	@JsonView(UserControllerCustJsonView.class)
	public User getSelfCustUserInfo(@SessionAttribute(value = "sa_account", required = false) Account account) {
		return service.findOne(account.getAccountId());
	}
	
	
	/**
	 * 사업장 관리자 자기 회원 정보 수정
	 * 
	 * @param user
	 * @param accountId
	 * @return 회원 정보
	 */
	@PutMapping("/info/cust/{accountId}")
	public void upSelfCustUserInfo(@Validated({UserValid.class, AccountValid.class, AccountPwValid.class }) @RequestBody User user, @PathVariable String accountId) {
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("custMng");
		auths.add(auth);
		user.setAuths(auths);
				
				
		if (!service.findOne(accountId).getAccountPw().equals(user.getAccountPw())) {
//			user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
			user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
		}
		
		user.setAccountId(accountId);
		
		service.save(user);
	}
	
	
	
	/**
	 * 매장 관리나 자기 회원 정보 조회
	 * 
	 * @return 회원 정보
	 */
	@GetMapping("/info/store")
	@JsonView(UserControllerStoreJsonView.class)
	public User getSelfStoreUserInfo(@SessionAttribute(value = "sa_account", required = false) Account account) {
		return service.findOne(account.getAccountId());
	}
	
	
	/**
	 * 매장 관리나 자기 회원 정보 수정
	 * 
	 * @param user
	 * @param accountId
	 * @return 회원 정보
	 */
	@PutMapping("/info/store/{accountId}")
	public void upSelfStoreUserInfo(@Validated({UserValid.class, AccountValid.class, AccountPwValid.class }) @RequestBody User user, @PathVariable String accountId) {
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("storeMng");
		auths.add(auth);
		user.setAuths(auths);
				
		if (!service.findOne(accountId).getAccountPw().equals(user.getAccountPw())) {
//			user.setAccountPw(new BCryptPasswordEncoder().encode(user.getAccountPw()));
			user.setAccountPw(new InnopayPasswordEncoder().encode(user.getAccountPw()));
		}
		
		user.setAccountId(accountId);
		
		service.save(user);
	}
	
	
}
