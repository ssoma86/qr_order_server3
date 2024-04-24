package org.lf.app.models.system.account;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account.AccountPwValid;
import org.lf.app.models.system.account.Account.AccountValid;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthController.AuthControllerCommonJsonView;
import org.lf.app.models.system.auth.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService service;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AppUserService appUserService;
	
	// JsonView
	public interface AccountControllerJsonView extends BaseJsonView, AuthControllerCommonJsonView {}
	public interface AccountControllerCommonJsonView {}
		
	
	/**
	 * 추가
	 * 
	 * @param account 계정 정보
	 */
	@PostMapping
	public void add(@Validated({AccountValid.class, AccountPwValid.class}) @RequestBody Account account) {
		// 관리자 권한으로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("mng");
		auths.add(auth);
		account.setAuths(auths);
		
		// 비번 암호화
//		account.setAccountPw(new BCryptPasswordEncoder().encode(account.getAccountPw()));
		account.setAccountPw(new InnopayPasswordEncoder().encode(account.getAccountPw()));
		service.save(account);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param accountId 계정ID
	 * @return 계정 정보
	 */
	@GetMapping("/{accountId}")
	@JsonView(AccountControllerJsonView.class)
	public Account get(@PathVariable String accountId) {
		return service.findOne(accountId);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param account 계정 정보
	 * @param accountId 계정 코드
	 */
	@PutMapping("/{accountId}")
	public void up(@Validated({AccountValid.class, AccountPwValid.class}) @RequestBody Account account, @PathVariable String accountId) {
		account.setAccountId(accountId);
		
		// 비번 수정 되었으면 암호화 해줌
		if (!service.findOne(accountId).getAccountPw().equals(account.getAccountPw())) {
//			account.setAccountPw(new BCryptPasswordEncoder().encode(account.getAccountPw()));
			account.setAccountPw(new InnopayPasswordEncoder().encode(account.getAccountPw()));
		}
					
		// 사용자 구분이 회원인지 체크함, JPA에서 하위 클래스는 수정 안됨 오류남
		User user = userService.findOne(accountId);
		
		if (ObjectUtils.isEmpty(user)) {
			// 앱 회원 인지 체크함, JPA에서 하위 클래스는 수정 안됨 오류남
			AppUser appUser = appUserService.findOne(accountId);
			
			if (ObjectUtils.isEmpty(appUser)) {
				service.save(account);
			} else {
				BeanUtils.copyProperties(account, appUser);
				appUserService.save(appUser);
			}
		} else {
			BeanUtils.copyProperties(account, user);
			userService.save(user);
		}
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param accountId 계정 코드 리스트
	 */
	@DeleteMapping("/{accountIds}")
	public void del(@PathVariable String[] accountIds) {
		service.useYn(accountIds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param account 계정 정보
	 * @return 계정 정보 리스트
	 */
	@GetMapping
	@JsonView(AccountControllerJsonView.class)
	public List<Account> search(Account account) {
		return service.findAccountList(account);
	}
	
	
	
	/**
	 * 계정 아이디 사용여부 확인
	 * 
	 * @param accountId 계정 아이디
	 * @return 계정 정보
	 */
	@PatchMapping("/chk/{accountId}")
	@JsonView(AccountControllerCommonJsonView.class)
	public Account chkAccountId(@PathVariable String accountId) {
		return service.findOne(accountId);
	}
	
	
	/**
	 * 리스트 조회(자동검색기능에서 사용)
	 * 
	 * @param account 계정 정보
	 * @return 계정 정보 리스트
	 */
	@GetMapping("/searchByIdOrNm/{accountNm}")
	@JsonView(AccountControllerCommonJsonView.class)
	public List<Account> searchByIdAndNm(@PathVariable String accountNm) {
		return service.findAccountList(accountNm);
	}
	
	
}
