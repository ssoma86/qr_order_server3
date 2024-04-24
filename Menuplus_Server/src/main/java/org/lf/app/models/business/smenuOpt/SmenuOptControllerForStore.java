package org.lf.app.models.business.smenuOpt;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.smenuOpt.SmenuOpt.SmenuOptValid;
import org.lf.app.models.business.smenuOpt.SmenuOptController.SmenuOptControllerJsonView;
import org.lf.app.models.business.smenuOpt.SmenuOptInfo.SmenuOptInfoValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
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

/**
 * 메뉴 옵션 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/smenuOpt/store")
public class SmenuOptControllerForStore {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SmenuOptService service;
	
	
	
	/**
	 * 추가
	 * 
	 * @param smenuOpt
	 */
	@PostMapping
	public void add(@Validated({ SmenuOptValid.class, SmenuOptInfoValid.class }) @RequestBody SmenuOpt smenuOpt,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		smenuOpt.setStore(user.getStore());
		smenuOpt.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(smenuOpt);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param smenuOptCd 옵션 코드
	 * @return 옵션 정보
	 */
	@GetMapping("/{smenuOptCd:\\d+}")
	@JsonView(SmenuOptControllerJsonView.class)
	public SmenuOpt get(@PathVariable Integer smenuOptCd) {
		return service.findOne(smenuOptCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param smenuOpt
	 * @param smenuOptCd
	 */
	@PutMapping("/{smenuOptCd:\\d+}")
	public void up(@Validated({ SmenuOptValid.class, SmenuOptInfoValid.class }) @RequestBody SmenuOpt smenuOpt, @PathVariable Integer smenuOptCd,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		smenuOpt.setSmenuOptCd(smenuOptCd);
		
		User user = userService.findOne(account.getAccountId());
		
		smenuOpt.setStore(user.getStore());
		smenuOpt.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(smenuOpt);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param smenuOptCd
	 */
	@DeleteMapping("/{smenuOptCds}")
	public void del(@PathVariable Integer[] smenuOptCds) {
		service.useYn(smenuOptCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param smenuOpt
	 * @param nationCd
	 * @return 메뉴 옵션정보 리스트
	 */
	@GetMapping
	@JsonView(SmenuOptControllerJsonView.class)
	public List<SmenuOpt> search(String smenuOptNm, String useYn, Integer smenuOptTpCd,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		return service.findSmenuOptList(user.getStore().getStoreCd(), smenuOptNm, useYn, smenuOptTpCd);
	}
	
	
}
