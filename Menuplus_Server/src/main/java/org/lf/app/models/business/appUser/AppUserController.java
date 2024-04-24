package org.lf.app.models.business.appUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.lf.app.models.business.appUser.AppUser.AppUserValid;
import org.lf.app.models.business.store.StoreController.StoreControllerCommonJsonView;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
@RequestMapping("/appUser")
public class AppUserController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private AppUserService service;
	
	// JsonView
	public interface AppUserControllerJsonView extends AccountControllerJsonView, StoreControllerCommonJsonView	 {}
	
	
	
	/**
	 * 정보 조회
	 * 
	 * @param accountId 계정ID
	 * @return 계정 정보
	 */
	@GetMapping("/{accountId}")
	@JsonView(AppUserControllerJsonView.class)
	public AppUser get(@PathVariable String accountId) {
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
	public void up(@Validated({AppUserValid.class, AccountValid.class, AccountPwValid.class }) @RequestBody AppUser user, @PathVariable String accountId) {
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("appUser");
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
	 * @return 회원 정보 리스트
	 */
	@GetMapping
	@JsonView(AppUserControllerJsonView.class)
	public List<AppUser> search(AppUser user) {
		return service.findUserList(user);
	}
	
	
	
}
