package org.lf.app.models.business.cust;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.lf.app.models.business.cust.Cust.CustValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.tools.fileMan.FileManController.FileManControllerJsonView;
import org.lf.app.models.tools.fileMan.FileManService;
import org.lf.app.models.tools.seqBuilder.SeqBuilderService;
import org.lf.app.utils.system.LogUtil;
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

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 사업장 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/cust")
public class CustController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CustService service;
	
	@Autowired
	private SeqBuilderService seqBuilderService;
	
	@Autowired
	private FileManService fileManService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface CustControllerJsonView extends FileManControllerJsonView,
		CodeControllerCommonJsonView {}
	public interface CustControllerCommonJsonView {}
		
		
	/**
	 * 추가
	 * 
	 * @param cust
	 * @return 사업장 정보
	 * @throws Exception
	 */
	@PostMapping
	public void add(@Validated(CustValid.class) @RequestBody Cust cust) throws Exception {
		
		if (null == cust.getTopCust().getCustCd()) {
			cust.setTopCust(null);
		}
		
		cust.setCustId(seqBuilderService.getCustId());
		
		// 파일 저장
		cust.setFiles(fileManService.saveFiles("cust", cust.getFiles()));
		
		cust = service.save(cust);
		
		// 사용자 하나 자동 추가
		User user = new User();
		
		user.setCust(cust);
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("custMng");
		auths.add(auth);
		user.setAuths(auths);
		
		user.setAccountId(cust.getCustId());
		user.setAccountNm(cust.getCeoNm());
		// 비번 암호화
//		user.setAccountPw(new BCryptPasswordEncoder().encode("12345678"));
		user.setAccountPw(new InnopayPasswordEncoder().encode("ars123!@#"));
		user.setTel(cust.getTel());
		user.setEmail(cust.getCustId() + "@mail.com");
		
		userService.save(user);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param custCd 사업장 코드
	 * @return 사업장 정보
	 */
	@GetMapping("/{custCd:\\d+}")
	@JsonView(CustControllerJsonView.class)
	public Cust get(@PathVariable Integer custCd) {
		return service.findOne(custCd);
	}
	
	
	/**
	 * 결제방법 정보 조회(23.09.18)
	 * @param account
	 * @return
	 */
	@GetMapping("/payMethod")
	@JsonView(CustControllerJsonView.class)
	public PayMethod getPayInfo(@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		return service.selectPayMethods4CustCd(user.getCust().getCustCd());
		
	}
	
	
	/**
	 * 결제방법 정보 저장(23.09.18)
	 * overseas_mid, overseas_mkey (해외결제용 mid, mkey 추가  24.03.29)
	 * currency(해외결제 통화 추가 24.04.12)
	 * @param account
	 * @param payMethod
	 */
	@PostMapping("/payMethod")
	@JsonView(CustControllerJsonView.class)
	public void savePayInfo(@SessionAttribute(value = "sa_account", required = false) Account account, @RequestBody PayMethod payMethod) {
		
		User user = userService.findOne(account.getAccountId());
		
		service.deletePayMethod(user.getCust().getCustCd()); //기존정보 삭제
		service.insertPayMethod(payMethod.getMid()
									, payMethod.getMkey()
									, payMethod.getOverseasMid()
									, payMethod.getOverseasMkey()
									, payMethod.getMethods()
									, payMethod.getCurrency()
									, user.getCust().getCustCd()
									, "Y"); //결제방법 정보 저장
		
	}
	
	
	/**
	 * 수정
	 * 
	 * @param cust
	 * @param custCd
	 * @return 사업장 정보
	 * @throws Exception
	 */
	@PutMapping("/{custCd:\\d+}")
	public void up(@Validated(CustValid.class) @RequestBody Cust cust, @PathVariable Integer custCd) throws Exception {
		cust.setCustCd(custCd);
		
		if (null == cust.getTopCust().getCustCd()) {
			cust.setTopCust(null);
		}
		
		// 파일 저장
		cust.setFiles(fileManService.saveFiles("cust", cust.getFiles()));
		
		service.save(cust);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param custCd
	 */
	@DeleteMapping("/{custCds}")
	public void del(@PathVariable Integer[] custCds) {
		service.useYn(custCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param cust
	 * @return 사업장 정보 리스트
	 */
	@GetMapping
	@JsonView(CustControllerJsonView.class)
	public List<Cust> search(Cust cust, Integer topCustCd) {
		List<Cust> custList = service.findCustList(cust, topCustCd);
		
		custList.forEach(tmp -> {
			if (!ObjectUtils.isEmpty(tmp.getBankTp())) {
				tmp.getBankTp().setNmLan(lanDataService.getLanData(tmp.getBankTp().getNm(), LocaleContextHolder.getLocale()));
			}
		});
		
		return custList;
	}
	
	
	/**
	 * 사업장 관리나 자기 사업장 정보 조회
	 * 
	 * @return 사업장 정보
	 */
	@GetMapping("/info")
	@JsonView(CustControllerJsonView.class)
	public Cust getSelfCustInfo(@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		return service.findOne(user.getCust().getCustCd());
	}
	
	
	/**
	 * 사업장 관리나 자기 사업장 정보 수정
	 * 
	 * @param cust
	 * @param custCd
	 * @return 사업장 정보
	 * @throws Exception
	 */
	@PutMapping("/info/{custCd:\\d+}")
	public void upSelfCustInfo(@Validated(CustValid.class) @RequestBody Cust cust, @PathVariable Integer custCd) throws Exception {
		cust.setCustCd(custCd);
		
		// 파일 저장
		cust.setFiles(fileManService.saveFiles("cust", cust.getFiles()));
		
		service.save(cust);
	}
	
	
}
