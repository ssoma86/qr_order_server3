package org.lf.app.models.business.store;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.cust.CustController.CustControllerCommonJsonView;
import org.lf.app.models.business.delivery.DeliveryService;
import org.lf.app.models.business.store.Store.StoreValid;
import org.lf.app.models.business.store.StoreInfo.StoreInfoValid;
import org.lf.app.models.business.store.StoreRoom.StoreRoomValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lan.LanController.LanControllerJsonView;
import org.lf.app.models.tools.fileMan.FileManController.FileManControllerJsonView;
import org.lf.app.models.tools.fileMan.FileManService;
import org.lf.app.models.tools.seqBuilder.SeqBuilderService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 매장 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/store")
public class StoreController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(StoreController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private StoreService service;
	
	@Autowired
	private DeliveryService deliveryService;

	@Autowired
	private SeqBuilderService seqBuilderService;
	
	@Autowired
	private FileUtil fileUtil;
	
	@Autowired
	private FileManService fileManService;
	
	// JsonView
	public interface StoreControllerJsonView extends CustControllerCommonJsonView,
		LanControllerJsonView, CodeControllerCommonJsonView, FileManControllerJsonView {}
	public interface StoreControllerCommonJsonView {}
		
	
	/**
	 * 추가
	 * 
	 * @param store
	 * @return 매장정보
	 * @throws Exception
	 */
//	@PostMapping
//	public void add(@Validated({ StoreValid.class, StoreInfoValid.class }) @RequestBody Store store,
//			@SessionAttribute(value = "sa_account") Account account) throws Exception {
	@PostMapping
	public void add(@Validated({ StoreValid.class }) @RequestBody Store store, @SessionAttribute(value = "sa_account") Account account) throws Exception {
		
		logger.info("========================add start=========================");		
		logger.info("======================account.getAccountId():" + account.getAccountId());
		
		User user = userService.findOne(account.getAccountId());
		store.setCust(user.getCust());
		
		logger.info("======================store.setStoreId:" + seqBuilderService.getStoreId());
		
		store.setStoreId(seqBuilderService.getStoreId());
		
		logger.info("여기아래 오류");
		
		if (!StringUtils.isEmpty(store.getStoreImgFile())) {
			store.setStoreImg(fileUtil.saveFile("store", "STORE", store.getStoreImgFile()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile1())) {
			store.setStoreImg1(fileUtil.saveFile("store", "STORE1", store.getStoreImgFile1()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile2())) {
			store.setStoreImg2(fileUtil.saveFile("store", "STORE2", store.getStoreImgFile2()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile3())) {
			store.setStoreImg3(fileUtil.saveFile("store", "STORE3", store.getStoreImgFile3()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile4())) {
			store.setStoreImg4(fileUtil.saveFile("store", "STORE4", store.getStoreImgFile4()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile5())) {
			store.setStoreImg5(fileUtil.saveFile("store", "STORE5", store.getStoreImgFile5()));
		}
		
		// 파일 저장
		//store.setFiles(fileManService.saveFiles("store", store.getFiles()));
		
		service.setWeekSniffling(store);
		
		store = service.save(store);
		 
		
		// 사용자 하나 자동 추가
		User newUser = new User();
		
		newUser.setCust(store.getCust());
		newUser.setStore(store);
		// 사업장 관리자 역할로 설정
		Set<Auth> auths = new HashSet<Auth>();
		Auth auth = authService.findOne("storeMng");
		auths.add(auth);
		newUser.setAuths(auths);
		
		newUser.setAccountId(store.getStoreId());
		newUser.setAccountNm(store.getCeoNm());
		// 비번 암호화
//		newUser.setAccountPw(new BCryptPasswordEncoder().encode("12345678"));
		newUser.setAccountPw(new InnopayPasswordEncoder().encode("ars123!@#"));
		newUser.setTel(store.getTel());
		newUser.setEmail(store.getStoreId() + "@mail.com");
		
		userService.save(newUser);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param storeCd 상점코드
	 * @return 상점 정보
	 */
	@GetMapping("/{storeCd:\\d+}")
	@JsonView(StoreControllerJsonView.class)
	public Store get(@PathVariable Integer storeCd) {
		Store store = service.findOne(storeCd);
		
		List<Object[]> list = deliveryService.getDeliveryAddressList(storeCd);
		
		service.setDelivery(list, store);
		service.setStoreImgList(store);	// 매장 이미지 리스트 설정
		service.setSniffling(store);

		return store;
	}
	
	
	/**
	 * 수정
	 * 
	 * @param store
	 * @param storeCd
	 * @return 매장정보
	 * @throws Exception
	 */
	@PostMapping("/updateStore/{storeCd:\\d+}")
	public void updateStore(@Validated({ StoreValid.class  }) @RequestBody Store store, @PathVariable("storeCd") Integer storeCd,
			@SessionAttribute(value = "sa_account") Account account) throws Exception {
		store.setStoreCd(storeCd);
		
		User user = userService.findOne(account.getAccountId());
		
		store.setCust(user.getCust());
		
		if (!StringUtils.isEmpty(store.getStoreImgFile()) && store.getStoreImgFile().contains("base64")) {
			store.setStoreImg(fileUtil.delAndSaveFile(store.getStoreImg(), "store", "STORE", store.getStoreImgFile()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile1()) && store.getStoreImgFile1().contains("base64")) {
			store.setStoreImg1(fileUtil.delAndSaveFile(store.getStoreImg1(), "store", "STORE1", store.getStoreImgFile1()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile2()) && store.getStoreImgFile2().contains("base64")) {
			store.setStoreImg2(fileUtil.delAndSaveFile(store.getStoreImg2(), "store", "STORE2", store.getStoreImgFile2()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile3()) && store.getStoreImgFile3().contains("base64")) {
			store.setStoreImg3(fileUtil.delAndSaveFile(store.getStoreImg3(), "store", "STORE3", store.getStoreImgFile3()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile4()) && store.getStoreImgFile4().contains("base64")) {
			store.setStoreImg4(fileUtil.delAndSaveFile(store.getStoreImg4(), "store", "STORE4", store.getStoreImgFile4()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile5()) && store.getStoreImgFile5().contains("base64")) {
			store.setStoreImg5(fileUtil.delAndSaveFile(store.getStoreImg5(), "store", "STORE5", store.getStoreImgFile5()));
		}

		//배달비 저장
		deliveryService.deleteDelivery(storeCd);
     	if (store.getDelivery().size() > 0) deliveryService.saveDeliveryList(store.getDelivery());

		// 파일 저장
//		store.setFiles(fileManService.saveFiles("store", store.getFiles()));
		
		service.setWeekSniffling(store);		
		service.save(store);
		
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param storeCd
	 */
	@DeleteMapping("/{storeCds}")
	public void del(@PathVariable Integer[] storeCds) {
		service.useYn(storeCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param store
	 * @param nationCd
	 * @return 매장정보 리스트
	 */
	@GetMapping
	@JsonView(StoreControllerJsonView.class)
	public List<Store> search(String storeNm, String useYn, HttpServletRequest req,
			@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		List<Store> storeList = service.findStoreList(user.getCust().getCustCd(), storeNm, useYn);
		
		storeList.forEach(store -> {
			service.settingStoreData(store);
		});
		
		return storeList;
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param store
	 * @param nationCd
	 * @return 매장정보 리스트
	 */
	@GetMapping("/combo")
	@JsonView(StoreControllerCommonJsonView.class)
	public List<Store> searchCombo(String storeNm) {
		return service.findStoreList(storeNm);
	}
	
	
	/**
	 * 사용중인 언어 리스트 조회
	 * 
	 * @return 사용중인 언어 리스트
	 */
	@GetMapping("/getStore")
	@JsonView(StoreControllerJsonView.class)
	public Store getStore(@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		return user.getStore();
	}
	
	
	/**
	 * 사용중인 언어 리스트 조회
	 * 
	 * @return 사용중인 언어 리스트
	 */
	@GetMapping("/getStoreLans")
	@JsonView(LanControllerCommonJsonView.class)
	public List<Lan> getLans(@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		return user.getStore().getLans();
	}
	
	
	/**
	 * 매장 정보 조회
	 * 
	 * @return 매장 정보
	 */
	@GetMapping("/info")
	@JsonView(StoreControllerJsonView.class)
	public Store getOne(@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		Store store = user.getStore();
		
		List<Object[]> list = deliveryService.getDeliveryAddressList(store.getStoreCd());

		service.setDelivery(list, store);
		service.setStoreImgList(store);	// 매장 이미지 리스트 설정
		service.setSniffling(store);
		
		return store;
	}
	
	
	/**
	 * 수정
	 * 
	 * @param store
	 * @param storeCd
	 * @return 매장정보
	 * @throws Exception
	 */
	@PostMapping("/info/updateSelfStore/{storeCd:\\d+}")
	public void updateSelfStore(@Validated({ StoreValid.class }) @RequestBody Store store, @PathVariable("storeCd") Integer storeCd,
			@SessionAttribute(value = "sa_account") Account account) throws Exception {
		store.setStoreCd(storeCd);
		
		User user = userService.findOne(account.getAccountId());
		
		store.setCust(user.getCust());
		
		if (!StringUtils.isEmpty(store.getStoreImgFile()) && store.getStoreImgFile().contains("base64")) {
			store.setStoreImg(fileUtil.delAndSaveFile(store.getStoreImg(), "store", "STORE", store.getStoreImgFile()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile1()) && store.getStoreImgFile1().contains("base64")) {
			store.setStoreImg1(fileUtil.delAndSaveFile(store.getStoreImg1(), "store", "STORE1", store.getStoreImgFile1()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile2()) && store.getStoreImgFile2().contains("base64")) {
			store.setStoreImg2(fileUtil.delAndSaveFile(store.getStoreImg2(), "store", "STORE2", store.getStoreImgFile2()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile3()) && store.getStoreImgFile3().contains("base64")) {
			store.setStoreImg3(fileUtil.delAndSaveFile(store.getStoreImg3(), "store", "STORE3", store.getStoreImgFile3()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile4()) && store.getStoreImgFile4().contains("base64")) {
			store.setStoreImg4(fileUtil.delAndSaveFile(store.getStoreImg4(), "store", "STORE4", store.getStoreImgFile4()));
		}
		
		if (!StringUtils.isEmpty(store.getStoreImgFile5()) && store.getStoreImgFile5().contains("base64")) {
			store.setStoreImg5(fileUtil.delAndSaveFile(store.getStoreImg5(), "store", "STORE5", store.getStoreImgFile5()));
		}

		//배달비 저장
		deliveryService.deleteDelivery(storeCd);
		if (store.getDelivery().size() > 0) deliveryService.saveDeliveryList(store.getDelivery());
		
		// 파일 저장
		//store.setFiles(fileManService.saveFiles("store", store.getFiles()));
		
		service.setWeekSniffling(store);
		
		service.save(store);
	}

	 /**
	  * 배달주소 조회
	  *
	  * @param address
	  * @return 배달주소 정보 리스트
	  */
	 @GetMapping("/searchAddr")
	 public List<Address> searchAddr(@RequestParam String city) {
	  return service.findAddrList(city);
	 }
	 
	 
	 /**
	  * 디자인 설정 저장
	  * @param store
	  */
	 @PostMapping("/design")
	 public void upateDesign4Store(@Validated({ StoreRoomValid.class }) @RequestBody Store store) {
		 service.updateDesign(store.getStoreCd(), store.getDesign());
	 }
	 
	
}
