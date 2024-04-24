package org.lf.app.models.business.smenuOptTp;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.smenuOpt.SmenuOptService;
import org.lf.app.models.business.smenuOpt.SmenuOptController.SmenuOptControllerJsonView;
import org.lf.app.models.business.smenuOptTp.SmenuOptTp.SmenuOptTpValid;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpController.SmenuOptTpControllerJsonView;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpInfo.SmenuOptTpInfoValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
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
 * 메뉴 옵션 그룹 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/smenuOptTp/store")
public class SmenuOptTpControllerForStore {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(SmenuOptTpControllerForStore.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SmenuOptTpService service;
	
	@Autowired
	private SmenuOptService smenuOptService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	
	/**
	 * 추가
	 * 
	 * @param smenuOptTp
	 */
	@PostMapping
	public void add(@Validated({ SmenuOptTpValid.class, SmenuOptTpInfoValid.class }) @RequestBody SmenuOptTp smenuOptTp,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		smenuOptTp.setStore(user.getStore());
		smenuOptTp.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(smenuOptTp);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param smenuOptTpCd 옵션 그룹 코드
	 * @return 옵션 그룹 정보
	 */
	@GetMapping("/{smenuOptTpCd:\\d+}")
	@JsonView(SmenuOptTpControllerJsonView.class)
	public SmenuOptTp get(@PathVariable Integer smenuOptTpCd) {
		return service.findOne(smenuOptTpCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param smenuOptTp
	 * @param smenuOptTpCd
	 */
	@PutMapping("/{smenuOptTpCd:\\d+}")
	public void up(@Validated({ SmenuOptTpValid.class, SmenuOptTpInfoValid.class }) @RequestBody SmenuOptTp smenuOptTp, @PathVariable Integer smenuOptTpCd,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		smenuOptTp.setSmenuOptTpCd(smenuOptTpCd);
		
		User user = userService.findOne(account.getAccountId());
		
		smenuOptTp.setStore(user.getStore());
		smenuOptTp.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(smenuOptTp);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param smenuOptTpCd
	 */
	@DeleteMapping("/{smenuOptTpCds}")
	public void del(@PathVariable Integer[] smenuOptTpCds) {
		service.useYn(smenuOptTpCds, "N");
	}
	
	
	/**
	 * 옵션그룹에 해당하는 옵션 개수 가져오기(23.10.13)
	 * @param account
	 * @param smenuOptTpCds
	 * @return
	 */
	@GetMapping("/optCnt4OptTp/{smenuOptTpCds}")
	@JsonView(SmenuOptControllerJsonView.class)
	public Integer[] getSmenuOptCnt4OptTp(@SessionAttribute(value = "sa_account") Account account, @PathVariable Integer[] smenuOptTpCds) {
		
		logger.info("=============getSmenuOptCnt4OptTp start=============");
		
		User user = userService.findOne(account.getAccountId());		
		Integer[] iaMenuOptCnt = null;
		
		if(null != smenuOptTpCds && smenuOptTpCds.length > 0) {		
			
			iaMenuOptCnt = new Integer[smenuOptTpCds.length];
			int iTempOrd = 0;
			
			Integer tempCnt = null;
			
			for(Integer smenuOptTpCd : smenuOptTpCds) {
				tempCnt = smenuOptService.findSmenuOptList(user.getStore().getStoreCd(),  "Y", smenuOptTpCd).size();
				iaMenuOptCnt[iTempOrd] = tempCnt;
				iTempOrd = iTempOrd + 1;
			}
			
		}
		
		return iaMenuOptCnt;
	}
	
	
	
	
	
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param smenuOptTp
	 * @param nationCd
	 * @return 메뉴 옵션 그룹정보 리스트
	 */
	@GetMapping
	@JsonView(SmenuOptTpControllerJsonView.class)
	public List<SmenuOptTp> search(String smenuOptTpNm, String useYn,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		List<SmenuOptTp> smenuOptTpList = service.findSmenuOptTpList(user.getStore().getStoreCd(), smenuOptTpNm, useYn);
		
		smenuOptTpList.forEach(smenuOptTp -> {
			if (!ObjectUtils.isEmpty(smenuOptTp.getOptTp())) {
				smenuOptTp.getOptTp().setNmLan(lanDataService.getLanData(smenuOptTp.getOptTp().getNm(), LocaleContextHolder.getLocale()));
			}
		});
		
		return smenuOptTpList;
	}
	
	
	/**
	 * 메뉴옵션 그룹 리스트 가져오기(23.08.09)
	 * @param account
	 * @return
	 */
	@GetMapping("/optGroupList")
	@JsonView(SmenuOptTpControllerJsonView.class)
	public List<SmenuOptTp> search(@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());		
		List<SmenuOptTp> smenuOptTpList = service.findSmenuOptTpList(user.getStore().getStoreCd());
		
		smenuOptTpList.forEach(smenuOptTp -> {
			if (!ObjectUtils.isEmpty(smenuOptTp.getOptTp())) {
				smenuOptTp.getOptTp().setNmLan(lanDataService.getLanData(smenuOptTp.getOptTp().getNm(), LocaleContextHolder.getLocale()));
			}
		});
		
		return smenuOptTpList;
	}
	
}
