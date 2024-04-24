package org.lf.app.config.init;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountRepository;
import org.lf.app.models.system.action.Action;
import org.lf.app.models.system.action.ActionRepository;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthRepository;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeRepository;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanRepository;
import org.lf.app.models.system.menu.Menu;
import org.lf.app.models.system.menu.MenuRepository;
import org.lf.app.models.system.resources.Resources;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 서비스 시작 시 실행 Runner, 기초 데이타 설정
 * 
 * 여기서는 Repository만 사용 가능 Service를 사용 하면 실행 시 오류가 남
 * 
 * @author LF
 * 
 * 1. 매장 유형 하드 코딩으로 사용
 * 2. 브레이크 타임 주일 별로 설정
 * 3. 매장 할인 메뉴처럼 수정
 * 4. 주문 리스트 30개씩 마지막 코드 이후부터 조회, 토탈 보여줌
 * 
 */
@Component
public class AppStartRunner implements ApplicationRunner {

	private LogUtil log = new LogUtil(getClass());

	@Value("${init_data}")
    private boolean initData;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private CodeRepository codeRepository;
	
	@Autowired
	private LanRepository lanRepository;
	
	@Autowired
	private AuthRepository authRepository;
	
	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private ActionRepository actionRepository;

	private Account account;
	
	private Account manager;
	
	
	/** 슈퍼 관리자 */
	// 리소스
	private Set<Resources> resourcess = new HashSet<>();
	
	/** 관리자 */
	// 리소스
	private Set<Resources> resourcessForMng = new HashSet<>();
	
	/** 사업장 관리자 */
	// 리소스
	private Set<Resources> resourcessForCust = new HashSet<>();
	
	/** 매장 관리자 */
	// 리소스
	private Set<Resources> resourcessForStore = new HashSet<>();
	
	/** 앱 회원 */
	// 리소스
	private Set<Resources> resourcessForAppUser = new HashSet<>();
	
	
	private Code codeResMenu;	// 리소스 메뉴
	private Code codeResAction;	// 리소스 액션
	
	private Code codeBtn;		// 이벤트 버튼
	private Code codeLink;		// 이벤트 링크
	private Code codeApi;		// 이벤트 API
	
	private int subMenuOrd = 1;	// 메뉴 등록 순번
	
	private Menu menuSystem;	// 시스템 관리
	private Menu menuLanMng;	// 언어 관리
	private Menu menuCommonMng; // 기본정보 관리
	
	private Menu menuCustMng;	// 사업장 정보 관리
	
	private Menu menuStoreMng;	// 매장 관리
	
	
	
	
	
	@Override
	@Transactional
	public void run(ApplicationArguments args) throws Exception {
		initData = false;
		if (initData) {
			log.print("시스템 기초 데이타 설정");
	
			/** ===== 슈퍼 관리자 및 관리자 정보 초기화  시작 ===== */
			// 슈퍼 관리자 등록
			Optional<Account> optionalAccount = accountRepository.findById("admin");
			
			if (optionalAccount.isPresent()) {
				account = optionalAccount.get();
			} else {
				account = new Account();
				account.setAccountId("admin");
//				account.setAccountPw(new BCryptPasswordEncoder().encode("12345678"));
				account.setAccountPw(new InnopayPasswordEncoder().encode("12345678"));
				account.setAccountNm("슈퍼 관리자");
				account.setTel("010-0000-0000");
				account.setEmail("admin@mail.com");
				accountRepository.save(account);
			}
			
			// 관리자 등록
			Optional<Account> optionalManager = accountRepository.findById("manager");
			
			if (optionalManager.isPresent()) {
				manager = optionalManager.get();
			} else {
				manager = new Account();
				manager.setAccountId("manager");
//				manager.setAccountPw(new BCryptPasswordEncoder().encode("12345678"));
				manager.setAccountPw(new InnopayPasswordEncoder().encode("12345678"));
				manager.setAccountNm("관리자");
				manager.setTel("010-0000-0001");
				manager.setEmail("manager@mail.com");
				accountRepository.save(manager);
			}
			
			initLan();				// 언어 등록
			initCodes();			// 공통 코드 등록
			
			initSystemAction();		// 시스템 기본 정보 등록
			
			initTopMenu();			// 최상위 메뉴 등록

			subMenuOrd = 1;
			initAccountMenu();		// 계정 관리 메뉴 등록
			initAuthMenu();			// 권한 관리 메뉴 등록
			initMenuMenu();			// 메뉴 관리 메뉴 등록
			initActionMenu();		// 이벤트 관리 메뉴 등록
			initCodeMenu();			// 코드 관리 메뉴 등록
			initSystemOptMenu();	// 시스템 관리 메뉴 등록
			initHistoryMenu();		// 시스템 작업이력 조회 메뉴 등록
			
			subMenuOrd = 1;
			initLanMenu();			// 언어 관리 메뉴 등록
			
			subMenuOrd = 1;
			initTermsMenu();		// 약관 관리 메뉴 등록
			initNotiMenu();			// 공지사항 관리 메뉴 등록
			initAppUserMenu();		// 앱 사용자 관리 메뉴 등록
			initBannerMenu();		// 배너 관리 메뉴 등록
			initPushMenu();			// 푸시 관리 메뉴 등록
			initBizMenu();			// 알림 관리
			initInquiryMenu();		// 1:1 문의
			initEventMenu();		// 이벤트 관리
			
			subMenuOrd = 1;
			initCustMenu();			// 사업장 관리 메뉴 등록
			initUserMenu();			// 사업장 회원 관리 메뉴 등록
			
			
			// 권한 설정
			Auth auth = authRepository.findOneByAuthNm("슈퍼 관리자");
			if (null == auth) {
				auth = new Auth();
				auth.setAuthId("admin");
				auth.setAuthNm("슈퍼 관리자");
				auth.setAuthDesc("슈퍼 관리자");
			}
			auth.setResources(resourcess);
			authRepository.save(auth);
			
			Set<Auth> auths = new HashSet<>();
			auths.add(auth);
			account.setAuths(auths);
			
			accountRepository.save(account);
			
			
			/* 관리자 권한 */
			// 권한 설정
			Auth authForMng = authRepository.findOneByAuthNm("관리자");
			if (null == authForMng) {
				authForMng = new Auth();
				authForMng.setAuthId("mng");
				authForMng.setAuthNm("관리자");
				authForMng.setAuthDesc("관리자");
			}
			authForMng.setResources(resourcessForMng);
			authRepository.save(authForMng);
			
			Set<Auth> authsForMng = new HashSet<>();
			authsForMng.add(authForMng);
			manager.setAuths(authsForMng);
			
			accountRepository.save(manager);
			
			/** ===== 슈퍼 관리자 및 관리자 정보 초기화  끝 ===== */
			
			
			
			/** ===== 사업장 관리자 정보 초기화  시작 ===== */
			subMenuOrd = 1;
			initCustInfoMenu();		// 사업장 관리자 사업장 정보 수정
			initUserInfoMenu();		// 사업장 관리자 사용자 정보 수정
			
			// 사업장 관리자 매장 관리 메뉴 
			subMenuOrd = 1;
			initStoreMenuForCustMng();		// 매장 관리 메뉴 등록
			initUserMenuForCustMng();		// 매장 사용자 정보 관리
			initCategoryMenuForCustMng();	// 카테고리 관리 메뉴 등록
			initStuffMenuForCustMng();		// 재료관리 메뉴 등록
			initSmenuOptTpMenuForCustMng();	// 옵션 그룹관리 관리 메뉴 등록
			initSmenuOptMenuForCustMng();	// 옵션관리 관리 메뉴 등록
			initDiscountMenuForCustMng();	// 할인관리 관리 메뉴 등록
			initSmenuMenuForCustMng();		// 메뉴 관리 메뉴 등록
			initPayMethodMenuForCustMng();  // 결제방법 관리 메뉴 등록
			
			
			/* 사업장 관리자 권한 */
			// 권한 설정
			Auth authForCust = authRepository.findOneByAuthNm("사업장 관리자");
			if (null == authForCust) {
				authForCust = new Auth();
				authForCust.setAuthId("custMng");
				authForCust.setAuthNm("사업장 관리자");
				authForCust.setAuthDesc("사업장 관리자");
			}
			
			authForCust.setResources(resourcessForCust);
			authRepository.save(authForCust);
			/** ===== 사업장 관리자 정보 초기화  끝 ===== */
			
			/** ===== 매장 관리자 정보 초기화  시작 ===== */
			// 매장 관리자 매장 관리 메뉴
			subMenuOrd = 1;
			initStoreMenu();			// 매장 관리 메뉴 등록
			initStoreUserInfoMenu();	// 사업장 관리자 사용자 정보 수정
			
			subMenuOrd = 1;
			initCategoryMenu();	// 카테고리 관리 메뉴 등록
			initStuffMenu();	// 재료관리 메뉴 등록
			initSmenuOptTpMenu();	// 옵션 그룹 관리 관리 메뉴 등록
			initSmenuOptMenu();	// 옵션관리 관리 메뉴 등록
			initDiscountMenu();		// 할인관리 관리 메뉴 등록
			initSmenuMenu();	// 메뉴 관리 메뉴 등록
			
			initOrderMenu();	// 주문 관리 메뉴 등록
			
			initOrderMngApi();	// 주문 관리 API 등록
			initRoomMenu(); 	//객실 관리 메뉴 등록(2023.06.15)
			initStoreDesign();  // 주문자 화면 가로,세로 보기 설정(23.08.09)
			
			/* 매장 관리자 권한 */
			// 권한 설정
			Auth authForSotre = authRepository.findOneByAuthNm("매장 관리자");
			if (null == authForSotre) {
				authForSotre = new Auth();
				authForSotre.setAuthId("storeMng");
				authForSotre.setAuthNm("매장 관리자");
				authForSotre.setAuthDesc("매장 관리자");
			}
			authForSotre.setResources(resourcessForStore);
			authRepository.save(authForSotre);
			
			/** ===== 매장 관리자 정보 초기화  끝 ===== */
			
			subMenuOrd = 1;
			initApi();				// API 권한 등록
			
			/* 앱 회원 접속 권한 */
			// 앱 회원 접속 권한 설정
			Auth authForAppUser = authRepository.findOneByAuthNm("앱 회원");
			if (null == authForAppUser) {
				authForAppUser = new Auth();
				authForAppUser.setAuthId("appUser");
				authForAppUser.setAuthNm("앱 회원");
				authForAppUser.setAuthDesc("앱 회원");
			}
			authForAppUser.setResources(resourcessForAppUser);
			authRepository.save(authForAppUser);
			
		}
	}

	/**
	 * 언어 정보 등록
	 */
	private void initLan() {
		// 한국
		Optional<Lan> optKoLan = lanRepository.findById("ko");
		if (!optKoLan.isPresent()) {
			Lan koLan = new Lan();
			koLan.setId("ko");
			koLan.setNm("한국어");
			koLan.setOrd(1);
			koLan.setDefault_(true);
			koLan.setRef1("원");
			koLan.setRef2("원");
			lanRepository.save(koLan);
		}
		
		// 중국
		Optional<Lan> optZhLan = lanRepository.findById("zh");
		if (!optZhLan.isPresent()) {
			Lan zhLan = new Lan();
			zhLan.setId("zh");
			zhLan.setNm("简体中文");
			zhLan.setOrd(2);
			zhLan.setRef1("元");
			zhLan.setRef2("元");
			lanRepository.save(zhLan);
		}
		
		// 영문
		Optional<Lan> optEnLan = lanRepository.findById("en");
		if (!optEnLan.isPresent()) {
			Lan enLan = new Lan();
			enLan.setId("en");
			enLan.setNm("English");
			enLan.setOrd(3);
			enLan.setRef1("won");
			enLan.setRef2("$");
			lanRepository.save(enLan);
		}
		
		// 스페인어
		Optional<Lan> optEsLan = lanRepository.findById("es");
		if (!optEsLan.isPresent()) {
			Lan esLan = new Lan();
			esLan.setId("es");
			esLan.setNm("Español");
			esLan.setOrd(4);
			esLan.setRef1("won");
			esLan.setRef2("€");
			lanRepository.save(esLan);
		}
		
		// 일본어
		Optional<Lan> optJaLan = lanRepository.findById("ja");
		if (!optJaLan.isPresent()) {
			Lan jaLan = new Lan();
			jaLan.setId("ja");
			jaLan.setNm("日本語");
			jaLan.setOrd(5);
			jaLan.setRef1("won");
			jaLan.setRef2("$");
			lanRepository.save(jaLan);
		}
		
	}
	
	/**
	 * 공통코드 등록
	 */
	private void initCodes() {
		int ord = 1;
        // 리소스 타입 상위 코드 등록
		Code codeRes = codeRepository.findOneByTopCodeAndVal(null, "RES_TP");
		if (null == codeRes) {
			codeRes = new Code();
			codeRes.setVal("RES_TP");
			codeRes.setNm("리소스 타입 유형");
			codeRes.setOrd(ord++);
			codeRes.setCodeDesc("시스템 중 모든 리소스 타입 유형");
			codeRepository.save(codeRes);
		}
	
		// 메뉴
		codeResMenu = codeRepository.findOneByTopCodeAndVal(codeRes, "menu");
		if (null == codeResMenu) {
			codeResMenu = new Code();
			codeResMenu.setNm("메뉴");
			codeResMenu.setVal("menu");
			codeResMenu.setOrd(1);
			codeResMenu.setCodeDesc("모든 메뉴");
			codeResMenu.setTopCode(codeRes);
			codeRepository.save(codeResMenu);
		}
				
		// 이벤트
		codeResAction = codeRepository.findOneByTopCodeAndVal(codeRes, "action");
		if (null == codeResAction) {
			codeResAction = new Code();
			codeResAction.setNm("이벤트");
			codeResAction.setVal("action");
			codeResAction.setOrd(2);
			codeResAction.setCodeDesc("모든 이벤트");
			codeResAction.setTopCode(codeRes);
			codeRepository.save(codeResAction);
		}
		
     		
		// 이벤트 상위 코드 등록
		Code codeAction = codeRepository.findOneByTopCodeAndVal(null, "EVENT");
		if (null == codeAction) {
			codeAction = new Code();
			codeAction.setVal("EVENT");
			codeAction.setNm("이벤트 유형");
			codeAction.setOrd(ord++);
			codeAction.setCodeDesc("시스템 중 모든 이벤트 유형");
			codeRepository.save(codeAction);
		}

		// 버튼 이벤트
		codeBtn = codeRepository.findOneByTopCodeAndVal(codeAction, "btn");
		if (null == codeBtn) {
			codeBtn = new Code();
			codeBtn.setNm("버튼");
			codeBtn.setVal("btn");
			codeBtn.setOrd(1);
			codeBtn.setCodeDesc("모든 버튼");
			codeBtn.setTopCode(codeAction);
			codeRepository.save(codeBtn);
		}
		
		// 링크 이벤트
		codeLink = codeRepository.findOneByTopCodeAndVal(codeAction, "link");
		if (null == codeLink) {
			codeLink = new Code();
			codeLink.setNm("링크");
			codeLink.setVal("link");
			codeLink.setOrd(2);
			codeLink.setCodeDesc("모든 링크");
			codeLink.setTopCode(codeAction);
			codeRepository.save(codeLink);
		}
		
		// API
		codeApi = codeRepository.findOneByTopCodeAndVal(codeAction, "api");
		if (null == codeApi) {
			codeApi = new Code();
			codeApi.setNm("API");
			codeApi.setVal("api");
			codeApi.setOrd(3);
			codeApi.setCodeDesc("모든 API");
			codeApi.setTopCode(codeAction);
			codeRepository.save(codeApi);
		}
				
		// OS구분 상위 코드 등록
		Code codeAppOs = codeRepository.findOneByTopCodeAndVal(null, "OS_TP");
		if (null == codeAppOs) {
			codeAppOs = new Code();
			codeAppOs.setVal("OS_TP");
			codeAppOs.setNm("OS구분");
			codeAppOs.setOrd(ord++);
			codeAppOs.setCodeDesc("시스템 중 모든 OS구분 정보");
			codeRepository.save(codeAppOs);
		}

		// 안드로이드
		Code codeAppOsAnd = codeRepository.findOneByTopCodeAndVal(codeAppOs, "And");
		if (null == codeAppOsAnd) {
			codeAppOsAnd = new Code();
			codeAppOsAnd.setNm("안드로이드");
			codeAppOsAnd.setVal("And");
			codeAppOsAnd.setOrd(1);
			codeAppOsAnd.setCodeDesc("안드로이드");
			codeAppOsAnd.setTopCode(codeAppOs);
			codeRepository.save(codeAppOsAnd);
		}
		
		// 아이폰
		Code codeAppTpIos = codeRepository.findOneByTopCodeAndVal(codeAppOs, "IOS");
		if (null == codeAppTpIos) {
			codeAppTpIos = new Code();
			codeAppTpIos.setNm("아이폰");
			codeAppTpIos.setVal("IOS");
			codeAppTpIos.setOrd(2);
			codeAppTpIos.setCodeDesc("아이폰");
			codeAppTpIos.setTopCode(codeAppOs);
			codeRepository.save(codeAppTpIos);
		}
				
		// 단말 타입 상위 코드 등록
		Code codeClientTp = codeRepository.findOneByTopCodeAndVal(null, "CLIENT_TP");
		if (null == codeClientTp) {
			codeClientTp = new Code();
			codeClientTp.setVal("CLIENT_TP");
			codeClientTp.setNm("단말 타입");
			codeClientTp.setOrd(ord++);
			codeClientTp.setCodeDesc("시스템 중 모든 단말 타입 정보");
			codeRepository.save(codeClientTp);
		}
		
		// 전체 포함
		Code codeClientTpAll = codeRepository.findOneByTopCodeAndVal(codeClientTp, "ALL");
		if (null == codeClientTpAll) {
			codeClientTpAll = new Code();
			codeClientTpAll.setNm("전부 포함");
			codeClientTpAll.setVal("ALL");
			codeClientTpAll.setOrd(1);
			codeClientTpAll.setCodeDesc("전부 포함");
			codeClientTpAll.setTopCode(codeClientTp);
			codeRepository.save(codeClientTpAll);
		}
		
		// 웹
		Code codeClientTpWeb = codeRepository.findOneByTopCodeAndVal(codeClientTp, "WEB");
		if (null == codeClientTpWeb) {
			codeClientTpWeb = new Code();
			codeClientTpWeb.setNm("웹");
			codeClientTpWeb.setVal("WEB");
			codeClientTpWeb.setOrd(2);
			codeClientTpWeb.setCodeDesc("웹");
			codeClientTpWeb.setTopCode(codeClientTp);
			codeRepository.save(codeClientTpWeb);
		}
		
		// 앱
		Code codeClientTpApp = codeRepository.findOneByTopCodeAndVal(codeClientTp, "APP");
		if (null == codeClientTpApp) {
			codeClientTpApp = new Code();
			codeClientTpApp.setNm("앱");
			codeClientTpApp.setVal("APP");
			codeClientTpApp.setOrd(3);
			codeClientTpApp.setCodeDesc("앱");
			codeClientTpApp.setTopCode(codeClientTp);
			codeRepository.save(codeClientTpApp);
		}
				
		// 웹 앱
		Code codeClientTpWebApp = codeRepository.findOneByTopCodeAndVal(codeClientTp, "WEB_APP");
		if (null == codeClientTpWebApp) {
			codeClientTpWebApp = new Code();
			codeClientTpWebApp.setNm("웹 앱");
			codeClientTpWebApp.setVal("WEB_APP");
			codeClientTpWebApp.setOrd(4);
			codeClientTpWebApp.setCodeDesc("웹");
			codeClientTpWebApp.setTopCode(codeClientTp);
			codeRepository.save(codeClientTpWebApp);
		}
		
		// 약관 타입 상위 코드 등록
		Code codeTermsTp = codeRepository.findOneByTopCodeAndVal(null, "TERMS_TP");
		if (null == codeTermsTp) {
			codeTermsTp = new Code();
			codeTermsTp.setVal("TERMS_TP");
			codeTermsTp.setNm("약관 타입");
			codeTermsTp.setOrd(ord++);
			codeTermsTp.setCodeDesc("시스템 중 모든 약관 타입 정보");
			codeRepository.save(codeTermsTp);
		}

		// 이용약관
		Code codeUseTerms = codeRepository.findOneByTopCodeAndVal(codeTermsTp, "UseTerms");
		if (null == codeUseTerms) {
			codeUseTerms = new Code();
			codeUseTerms.setNm("이용약관");
			codeUseTerms.setVal("UseTerms");
			codeUseTerms.setOrd(1);
			codeUseTerms.setCodeDesc("이용약관");
			codeUseTerms.setTopCode(codeTermsTp);
			codeRepository.save(codeUseTerms);
		}
		
		// 개인정보
		Code codePersonalTerms = codeRepository.findOneByTopCodeAndVal(codeTermsTp, "PersonalTerms");
		if (null == codePersonalTerms) {
			codePersonalTerms = new Code();
			codePersonalTerms.setNm("개인정보");
			codePersonalTerms.setVal("PersonalTerms");
			codePersonalTerms.setOrd(2);
			codePersonalTerms.setCodeDesc("개인정보");
			codePersonalTerms.setTopCode(codeTermsTp);
			codeRepository.save(codePersonalTerms);
		}
		
		// 3자 정보 제공
		Code codeThreePartyTerms = codeRepository.findOneByTopCodeAndVal(codeTermsTp, "ThreePartyTerms");
		if (null == codeThreePartyTerms) {
			codeThreePartyTerms = new Code();
			codeThreePartyTerms.setNm("3자 정보 제공");
			codeThreePartyTerms.setVal("ThreePartyTerms");
			codeThreePartyTerms.setOrd(3);
			codeThreePartyTerms.setCodeDesc("3자 정보 제공");
			codeThreePartyTerms.setTopCode(codeTermsTp);
			codeRepository.save(codeThreePartyTerms);
		}
		
		
		// 옵션 선택 구분 상위 코드 등록
		Code codeOptTp = codeRepository.findOneByTopCodeAndVal(null, "OPT_TP");
		if (null == codeOptTp) {
			codeOptTp = new Code();
			codeOptTp.setVal("OPT_TP");
			codeOptTp.setNm("옵션 선택 구분");
			codeOptTp.setOrd(ord++);
			codeOptTp.setCodeDesc("옵션 선택 구분(필수, 선택, 수량설정)");
			codeRepository.save(codeOptTp);
		}

		// 필수 선택 사항 옵션
		Code codeRequiredOpt = codeRepository.findOneByTopCodeAndVal(codeOptTp, "REQUIRED_OPT");
		if (null == codeRequiredOpt) {
			codeRequiredOpt = new Code();
			codeRequiredOpt.setNm("필수 선택 옵션");
			codeRequiredOpt.setVal("REQUIRED_OPT");
			codeRequiredOpt.setOrd(1);
			codeRequiredOpt.setCodeDesc("필수 설택 옵션");
			codeRequiredOpt.setTopCode(codeOptTp);
			codeRepository.save(codeRequiredOpt);
		}
		
		// 선택 사항 옵션
		Code codeSelectOpt = codeRepository.findOneByTopCodeAndVal(codeOptTp, "SELECT_OPT");
		if (null == codeSelectOpt) {
			codeSelectOpt = new Code();
			codeSelectOpt.setNm("선택 사항 옵션");
			codeSelectOpt.setVal("SELECT_OPT");
			codeSelectOpt.setOrd(2);
			codeSelectOpt.setCodeDesc("선택 사항 옵션");
			codeSelectOpt.setTopCode(codeOptTp);
			codeRepository.save(codeSelectOpt);
		}
		
		// 수량 설정 가능 옵션
		Code codeCountOpt = codeRepository.findOneByTopCodeAndVal(codeOptTp, "COUNT_OPT");
		if (null == codeCountOpt) {
			codeCountOpt = new Code();
			codeCountOpt.setNm("수량 설정 가능 옵션");
			codeCountOpt.setVal("COUNT_OPT");
			codeCountOpt.setOrd(3);
			codeCountOpt.setCodeDesc("수량 설정 가능 옵션");
			codeCountOpt.setTopCode(codeOptTp);
			codeRepository.save(codeCountOpt);
		}
		
		
		// 결제 구분 상위 코드 등록
		Code codePayTp = codeRepository.findOneByTopCodeAndVal(null, "PAY_TP");
		if (null == codePayTp) {
			codePayTp = new Code();
			codePayTp.setVal("PAY_TP");
			codePayTp.setNm("결제 구분");
			codePayTp.setOrd(ord++);
			codePayTp.setCodeDesc("결제 구분");
			codeRepository.save(codePayTp);
		}

		// 선결제
		Code codePayFirst = codeRepository.findOneByTopCodeAndVal(codePayTp, "PAY_FIRST");
		if (null == codePayFirst) {
			codePayFirst = new Code();
			codePayFirst.setNm("선결제");
			codePayFirst.setVal("PAY_FIRST");
			codePayFirst.setOrd(1);
			codePayFirst.setCodeDesc("선결제");
			codePayFirst.setTopCode(codePayTp);
			codeRepository.save(codePayFirst);
		}
		
		// 후결제
		Code codePayAfter = codeRepository.findOneByTopCodeAndVal(codePayTp, "PAY_AFTER");
		if (null == codePayAfter) {
			codePayAfter = new Code();
			codePayAfter.setNm("후결제");
			codePayAfter.setVal("PAY_AFTER");
			codePayAfter.setOrd(2);
			codePayAfter.setCodeDesc("후결제");
			codePayAfter.setTopCode(codePayTp);
			codeRepository.save(codePayAfter);
		}
		
		
		// 결제 방법 상위 코드 등록
		Code codePayMethod = codeRepository.findOneByTopCodeAndVal(null, "PAY_METHOD");
		if (null == codePayMethod) {
			codePayTp = new Code();
			codePayTp.setVal("PAY_METHOD");
			codePayTp.setNm("결제 방법");
			codePayTp.setOrd(ord++);
			codePayTp.setCodeDesc("결제 방법");
			codeRepository.save(codePayMethod);
		}

		// 인증 결제
		Code codePay0108 = codeRepository.findOneByTopCodeAndVal(codePayTp, "0108");
		if (null == codePay0108) {
			codePayFirst = new Code();
			codePayFirst.setNm("인증결제");
			codePayFirst.setVal("0108");
			codePayFirst.setOrd(1);
			codePayFirst.setCodeDesc("인증결제");
			codePayFirst.setTopCode(codePayMethod);
			codeRepository.save(codePay0108);
		}
		
		// 수기결제
		Code codePay0101 = codeRepository.findOneByTopCodeAndVal(codePayTp, "0101");
		if (null == codePay0101) {
			codePayFirst = new Code();
			codePayFirst.setNm("수기결제");
			codePayFirst.setVal("0101");
			codePayFirst.setOrd(2);
			codePayFirst.setCodeDesc("수기결제");
			codePayFirst.setTopCode(codePayMethod);
			codeRepository.save(codePay0101);
		}
		
		// 간편결제
		Code codePay0116 = codeRepository.findOneByTopCodeAndVal(codePayTp, "0116");
		if (null == codePay0116) {
			codePayFirst = new Code();
			codePayFirst.setNm("간편결제");
			codePayFirst.setVal("0116");
			codePayFirst.setOrd(3);
			codePayFirst.setCodeDesc("간편결제");
			codePayFirst.setTopCode(codePayMethod);
			codeRepository.save(codePay0116);
		}
		
		
		// 주문 상태 상위 코드 등록
		Code codeOrderStatus = codeRepository.findOneByTopCodeAndVal(null, "ORDER_STATUS");
		if (null == codeOrderStatus) {
			codeOrderStatus = new Code();
			codeOrderStatus.setVal("ORDER_STATUS");
			codeOrderStatus.setNm("주문 상태");
			codeOrderStatus.setOrd(ord++);
			codeOrderStatus.setCodeDesc("주문 상태 ");
			codeRepository.save(codeOrderStatus);
		}

		// 주문
		Code codeOrder = codeRepository.findOneByTopCodeAndVal(codeOrderStatus, "ORDER");
		if (null == codeOrder) {
			codeOrder = new Code();
			codeOrder.setNm("접수요청");
			codeOrder.setVal("ORDER");
			codeOrder.setOrd(1);
			codeOrder.setRef1("주문 등록 되었습니다.");
			codeOrder.setCodeDesc("접수요청");
			codeOrder.setTopCode(codeOrderStatus);
			codeRepository.save(codeOrder);
		}
				
		// 접수
		Code codeReceipt = codeRepository.findOneByTopCodeAndVal(codeOrderStatus, "RECEIPT");
		if (null == codeReceipt) {
			codeReceipt = new Code();
			codeReceipt.setNm("접수");
			codeReceipt.setVal("RECEIPT");
			codeReceipt.setOrd(2);
			codeReceipt.setRef1("접수 되었습니다.");
			codeReceipt.setCodeDesc("접수");
			codeReceipt.setTopCode(codeOrderStatus);
			codeRepository.save(codeReceipt);
		}
		
		// 준비중
		Code codeReady = codeRepository.findOneByTopCodeAndVal(codeOrderStatus, "READY");
		if (null == codeReady) {
			codeReady = new Code();
			codeReady.setNm("준비중");
			codeReady.setVal("READY");
			codeReady.setOrd(3);
			codeReady.setRef1("준비중입니다...");
			codeReady.setCodeDesc("준비중");
			codeReady.setTopCode(codeOrderStatus);
			codeRepository.save(codeReady);
		}
		
		// 완료
		Code codeDeliveryStatu = codeRepository.findOneByTopCodeAndVal(codeOrderStatus, "DELIVERY");
		if (null == codeDeliveryStatu) {
			codeDeliveryStatu = new Code();
			codeDeliveryStatu.setNm("배달중");
			codeDeliveryStatu.setVal("DELIVERY");
			codeDeliveryStatu.setOrd(4);
			codeDeliveryStatu.setRef1("배달중입니다...");
			codeDeliveryStatu.setCodeDesc("배달중");
			codeDeliveryStatu.setTopCode(codeOrderStatus);
			codeRepository.save(codeDeliveryStatu);
		}
		
		// 완료
		Code codeOver = codeRepository.findOneByTopCodeAndVal(codeOrderStatus, "OVER");
		if (null == codeOver) {
			codeOver = new Code();
			codeOver.setNm("배달완료");
			codeOver.setVal("OVER");
			codeOver.setOrd(5);
			codeOver.setRef1("방문 감사 드립니다.");
			codeOver.setRef2("배달 완료 되었습니다.");
			codeOver.setCodeDesc("배달완료");
			codeOver.setTopCode(codeOrderStatus);
			codeRepository.save(codeOver);
		}
		
		// 공지 사항 타겟 상위 코드 등록
		Code codeNotiTarget = codeRepository.findOneByTopCodeAndVal(null, "NotiTarget");
		if (null == codeNotiTarget) {
			codeNotiTarget = new Code();
			codeNotiTarget.setVal("NotiTarget");
			codeNotiTarget.setNm("공지 사항 타겟");
			codeNotiTarget.setOrd(ord++);
			codeNotiTarget.setCodeDesc("공지 사항 타겟");
			codeRepository.save(codeNotiTarget);
		}

		// 사업장
		Code codeNotiTargetCust = codeRepository.findOneByTopCodeAndVal(codeNotiTarget, "NotiTargetCust");
		if (null == codeNotiTargetCust) {
			codeNotiTargetCust = new Code();
			codeNotiTargetCust.setNm("사업장");
			codeNotiTargetCust.setVal("NotiTargetCust");
			codeNotiTargetCust.setOrd(1);
			codeNotiTargetCust.setCodeDesc("사업장 공지");
			codeNotiTargetCust.setTopCode(codeNotiTarget);
			codeNotiTargetCust.setRef1("custMng");
			codeRepository.save(codeNotiTargetCust);
		}
		
		// 매장
		Code codeNotiTargetStore = codeRepository.findOneByTopCodeAndVal(codeNotiTarget, "NotiTargetStore");
		if (null == codeNotiTargetStore) {
			codeNotiTargetStore = new Code();
			codeNotiTargetStore.setNm("매장");
			codeNotiTargetStore.setVal("NotiTargetStore");
			codeNotiTargetStore.setOrd(2);
			codeNotiTargetStore.setCodeDesc("매장 공지");
			codeNotiTargetStore.setTopCode(codeNotiTarget);
			codeNotiTargetStore.setRef1("storeMng");
			codeRepository.save(codeNotiTargetStore);
		}
		
		// 사용자 공지 앱
		Code codeNotiTargetApp = codeRepository.findOneByTopCodeAndVal(codeNotiTarget, "NotiTargetApp");
		if (null == codeNotiTargetApp) {
			codeNotiTargetApp = new Code();
			codeNotiTargetApp.setNm("사용자");
			codeNotiTargetApp.setVal("NotiTargetApp");
			codeNotiTargetApp.setOrd(3);
			codeNotiTargetApp.setCodeDesc("사용자 공지");
			codeNotiTargetApp.setTopCode(codeNotiTarget);
			codeNotiTargetApp.setRef1("appUser");
			codeRepository.save(codeNotiTargetApp);
		}
		
		// 판매 방식 상위 코드 등록
		Code codeOrderTp = codeRepository.findOneByTopCodeAndVal(null, "SALES_TP");
		if (null == codeOrderTp) {
			codeOrderTp = new Code();
			codeOrderTp.setVal("SALES_TP");
			codeOrderTp.setNm("판매 방식");
			codeOrderTp.setOrd(ord++);
			codeOrderTp.setCodeDesc("판매 방식");
			codeRepository.save(codeOrderTp);
		}
		
		// 배달 
		Code codeDelivery = codeRepository.findOneByTopCodeAndVal(codeOrderTp, "Delivery");
		if (null == codeDelivery) {
			codeDelivery = new Code();
			codeDelivery.setNm("배달");
			codeDelivery.setVal("Delivery");
			codeDelivery.setOrd(1);
			codeDelivery.setCodeDesc("배달");
			codeDelivery.setTopCode(codeOrderTp);
			codeDelivery.setRef2("app");
			codeDelivery.setRef3("D");
			codeRepository.save(codeDelivery);
		}
		
		// 정산 은행 상위 코드 등록
		Code codeBankTp = codeRepository.findOneByTopCodeAndVal(null, "BANK_TP");
		if (null == codeBankTp) {
			codeBankTp = new Code();
			codeBankTp.setVal("BANK_TP");
			codeBankTp.setNm("은행 구분");
			codeBankTp.setOrd(ord++);
			codeBankTp.setCodeDesc("은행 구분");
			codeRepository.save(codeBankTp);
		}

		// 없음
		Code codeNon = codeRepository.findOneByTopCodeAndVal(codeBankTp, "없음");
		if (null == codeNon) {
			codeNon = new Code();
			codeNon.setNm("없음");
			codeNon.setVal("없음");
			codeNon.setOrd(1);
			codeNon.setCodeDesc("없음");
			codeNon.setTopCode(codeBankTp);
			codeRepository.save(codeNon);
		}
		
		// 국민 은행
		Code codeKb = codeRepository.findOneByTopCodeAndVal(codeBankTp, "국민 은행");
		if (null == codeKb) {
			codeKb = new Code();
			codeKb.setNm("국민 은행");
			codeKb.setVal("국민 은행");
			codeKb.setOrd(2);
			codeKb.setCodeDesc("국민 은행");
			codeKb.setTopCode(codeBankTp);
			codeRepository.save(codeKb);
		}
		
		// 신한 은행
		Code codeSh = codeRepository.findOneByTopCodeAndVal(codeBankTp, "신한 은행");
		if (null == codeSh) {
			codeSh = new Code();
			codeSh.setNm("신한 은행");
			codeSh.setVal("신한 은행");
			codeSh.setOrd(3);
			codeSh.setCodeDesc("신한 은행");
			codeSh.setTopCode(codeBankTp);
			codeRepository.save(codeSh);
		}
		
		// 기업 은행
		Code codeIbk = codeRepository.findOneByTopCodeAndVal(codeBankTp, "기업 은행");
		if (null == codeIbk) {
			codeIbk = new Code();
			codeIbk.setNm("기업 은행");
			codeIbk.setVal("기업 은행");
			codeIbk.setOrd(4);
			codeIbk.setCodeDesc("기업 은행");
			codeIbk.setTopCode(codeBankTp);
			codeRepository.save(codeIbk);
		}
		
		// 하나 은행
		Code codeHn = codeRepository.findOneByTopCodeAndVal(codeBankTp, "하나 은행");
		if (null == codeHn) {
			codeHn = new Code();
			codeHn.setNm("하나 은행");
			codeHn.setVal("하나 은행");
			codeHn.setOrd(5);
			codeHn.setCodeDesc("하나 은행");
			codeHn.setTopCode(codeBankTp);
			codeRepository.save(codeHn);
		}
		
		
		// 판매 정지 사유
		Code codeStopReason = codeRepository.findOneByTopCodeAndVal(null, "STOP_REASON");
		if (null == codeStopReason) {
			codeStopReason = new Code();
			codeStopReason.setVal("STOP_REASON");
			codeStopReason.setNm("판매 정지 사유");
			codeStopReason.setOrd(ord++);
			codeStopReason.setCodeDesc("판매 정지 사유");
			codeRepository.save(codeStopReason);
		}

		// 계절메뉴
		Code codeReason1 = codeRepository.findOneByTopCodeAndVal(codeStopReason, "계절메뉴");
		if (null == codeReason1) {
			codeReason1 = new Code();
			codeReason1.setNm("계절메뉴");
			codeReason1.setVal("계절메뉴");
			codeReason1.setOrd(1);
			codeReason1.setCodeDesc("계절메뉴");
			codeReason1.setTopCode(codeStopReason);
			codeRepository.save(codeReason1);
		}
				
		// 기타
		Code codeReason99 = codeRepository.findOneByTopCodeAndVal(codeStopReason, "other");
		if (null == codeReason99) {
			codeReason99 = new Code();
			codeReason99.setNm("기타");
			codeReason99.setVal("other");
			codeReason99.setOrd(99);
			codeReason99.setCodeDesc("기타");
			codeReason99.setTopCode(codeStopReason);
			codeRepository.save(codeReason99);
		}
				
		
		// 재료 원산지
		Code codeStuffNation = codeRepository.findOneByTopCodeAndVal(null, "STUFF_NATION");
		if (null == codeStuffNation) {
			codeStuffNation = new Code();
			codeStuffNation.setVal("STUFF_NATION");
			codeStuffNation.setNm("재료 원산지");
			codeStuffNation.setOrd(ord++);
			codeStuffNation.setCodeDesc("재료 원산지");
			codeRepository.save(codeStuffNation);
		}

		// 국내산
		Code codeStuffNationKo = codeRepository.findOneByTopCodeAndVal(codeStuffNation, "ko");
		if (null == codeStuffNationKo) {
			codeStuffNationKo = new Code();
			codeStuffNationKo.setNm("국내산");
			codeStuffNationKo.setVal("ko");
			codeStuffNationKo.setOrd(1);
			codeStuffNationKo.setCodeDesc("국내산");
			codeStuffNationKo.setTopCode(codeStuffNation);
			codeRepository.save(codeStuffNationKo);
		}
				
		// 중국산
		Code codeStuffNationZh = codeRepository.findOneByTopCodeAndVal(codeStuffNation, "zh");
		if (null == codeStuffNationZh) {
			codeStuffNationZh = new Code();
			codeStuffNationZh.setNm("중국산");
			codeStuffNationZh.setVal("zh");
			codeStuffNationZh.setOrd(2);
			codeStuffNationZh.setCodeDesc("중국산");
			codeStuffNationZh.setTopCode(codeStuffNation);
			codeRepository.save(codeStuffNationZh);
		}
		
		// 호주산
		Code codeStuffNationAu = codeRepository.findOneByTopCodeAndVal(codeStuffNation, "au");
		if (null == codeStuffNationAu) {
			codeStuffNationAu = new Code();
			codeStuffNationAu.setNm("호주산");
			codeStuffNationAu.setVal("au");
			codeStuffNationAu.setOrd(3);
			codeStuffNationAu.setCodeDesc("호주산");
			codeStuffNationAu.setTopCode(codeStuffNation);
			codeRepository.save(codeStuffNationAu);
		}
		
		// 미국산
		Code codeStuffNationUs = codeRepository.findOneByTopCodeAndVal(codeStuffNation, "us");
		if (null == codeStuffNationUs) {
			codeStuffNationUs = new Code();
			codeStuffNationUs.setNm("미국산");
			codeStuffNationUs.setVal("us");
			codeStuffNationUs.setOrd(4);
			codeStuffNationUs.setCodeDesc("미국산");
			codeStuffNationUs.setTopCode(codeStuffNation);
			codeRepository.save(codeStuffNationUs);
		}
		
		// 베트남산
		Code codeStuffNationVn = codeRepository.findOneByTopCodeAndVal(codeStuffNation, "vn");
		if (null == codeStuffNationVn) {
			codeStuffNationVn = new Code();
			codeStuffNationVn.setNm("베트남산");
			codeStuffNationVn.setVal("vn");
			codeStuffNationVn.setOrd(5);
			codeStuffNationVn.setCodeDesc("베트남산");
			codeStuffNationVn.setTopCode(codeStuffNation);
			codeRepository.save(codeStuffNationVn);
		}
		
		
		
		// 할인 대상 관리
		Code codeDiscountTarget = codeRepository.findOneByTopCodeAndVal(null, "DISCOUNT_TARGET");
		if (null == codeDiscountTarget) {
			codeDiscountTarget = new Code();
			codeDiscountTarget.setVal("DISCOUNT_TARGET");
			codeDiscountTarget.setNm("할인 대상");
			codeDiscountTarget.setOrd(ord++);
			codeDiscountTarget.setCodeDesc("할인 대상");
			codeRepository.save(codeDiscountTarget);
		}

		// 주문
		Code codeDiscountTargetOrder = codeRepository.findOneByTopCodeAndVal(codeDiscountTarget, "order");
		if (null == codeDiscountTargetOrder) {
			codeDiscountTargetOrder = new Code();
			codeDiscountTargetOrder.setNm("주문별 할인 적용");
			codeDiscountTargetOrder.setVal("order");
			codeDiscountTargetOrder.setOrd(1);
			codeDiscountTargetOrder.setCodeDesc("주문별 할인 적용");
			codeDiscountTargetOrder.setTopCode(codeDiscountTarget);
			codeRepository.save(codeDiscountTargetOrder);
		}
		
		// 제품
		Code codeDiscountTargetMenu = codeRepository.findOneByTopCodeAndVal(codeDiscountTarget, "menu");
		if (null == codeDiscountTargetMenu) {
			codeDiscountTargetMenu = new Code();
			codeDiscountTargetMenu.setNm("메뉴별 할인 적용");
			codeDiscountTargetMenu.setVal("menu");
			codeDiscountTargetMenu.setOrd(2);
			codeDiscountTargetMenu.setCodeDesc("메뉴별 할인 적용");
			codeDiscountTargetMenu.setTopCode(codeDiscountTarget);
			codeRepository.save(codeDiscountTargetMenu);
		}
		
		// 할인 대상 관리
		Code codeDiscountClient = codeRepository.findOneByTopCodeAndVal(null, "DISCOUNT_CLIENT");
		if (null == codeDiscountClient) {
			codeDiscountClient = new Code();
			codeDiscountClient.setVal("DISCOUNT_CLIENT");
			codeDiscountClient.setNm("할인 단말 구분");
			codeDiscountClient.setOrd(ord++);
			codeDiscountClient.setCodeDesc("할인 단말 구분");
			codeRepository.save(codeDiscountClient);
		}

		// QR주문
		Code codeDiscountTargetQr = codeRepository.findOneByTopCodeAndVal(codeDiscountClient, "qr");
		if (null == codeDiscountTargetQr) {
			codeDiscountTargetQr = new Code();
			codeDiscountTargetQr.setNm("QR주문");
			codeDiscountTargetQr.setVal("qr");
			codeDiscountTargetQr.setOrd(1);
			codeDiscountTargetQr.setCodeDesc("QR주문");
			codeDiscountTargetQr.setTopCode(codeDiscountClient);
			codeRepository.save(codeDiscountTargetQr);
		}
		
		// 엡에서 주문
		Code codeDiscountTargetApp = codeRepository.findOneByTopCodeAndVal(codeDiscountClient, "app");
		if (null == codeDiscountTargetApp) {
			codeDiscountTargetApp = new Code();
			codeDiscountTargetApp.setNm("앱 주문");
			codeDiscountTargetApp.setVal("app");
			codeDiscountTargetApp.setOrd(2);
			codeDiscountTargetApp.setCodeDesc("앱 주문");
			codeDiscountTargetApp.setTopCode(codeDiscountClient);
			codeRepository.save(codeDiscountTargetApp);
		}
				
		// 할인 유형
		Code codeDiscountTp = codeRepository.findOneByTopCodeAndVal(null, "DISCOUNT_TP");
		if (null == codeDiscountTp) {
			codeDiscountTp = new Code();
			codeDiscountTp.setVal("DISCOUNT_TP");
			codeDiscountTp.setNm("할인 유형");
			codeDiscountTp.setOrd(ord++);
			codeDiscountTp.setCodeDesc("할인 유형");
			codeRepository.save(codeDiscountTp);
		}

		// 할인 금액
		Code codeDiscountTpPrice = codeRepository.findOneByTopCodeAndVal(codeDiscountTp, "price");
		if (null == codeDiscountTpPrice) {
			codeDiscountTpPrice = new Code();
			codeDiscountTpPrice.setNm("금액 할인");
			codeDiscountTpPrice.setVal("price");
			codeDiscountTpPrice.setOrd(1);
			codeDiscountTpPrice.setCodeDesc("금액 할인");
			codeDiscountTpPrice.setTopCode(codeDiscountTp);
			codeRepository.save(codeDiscountTpPrice);
		}
		
		// 할인율
		Code codeDiscountTpPercent = codeRepository.findOneByTopCodeAndVal(codeDiscountTp, "percent");
		if (null == codeDiscountTpPercent) {
			codeDiscountTpPercent = new Code();
			codeDiscountTpPercent.setNm("할인율");
			codeDiscountTpPercent.setVal("percent");
			codeDiscountTpPercent.setOrd(2);
			codeDiscountTpPercent.setCodeDesc("할인율");
			codeDiscountTpPercent.setTopCode(codeDiscountTp);
			codeRepository.save(codeDiscountTpPercent);
		}
		
		// 다국어 사전 구분
		Code codeDictionaryTp = codeRepository.findOneByTopCodeAndVal(null, "DICTIONARY_TP");
		if (null == codeDictionaryTp) {
			codeDictionaryTp = new Code();
			codeDictionaryTp.setVal("DICTIONARY_TP");
			codeDictionaryTp.setNm("다국어 사전 구분");
			codeDictionaryTp.setOrd(ord++);
			codeDictionaryTp.setCodeDesc("다국어 사전 구분");
			codeRepository.save(codeDictionaryTp);
		}

		// 카테고리
		Code codeDiscountTpCategory = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "category");
		if (null == codeDiscountTpCategory) {
			codeDiscountTpCategory = new Code();
			codeDiscountTpCategory.setNm("카테고리");
			codeDiscountTpCategory.setVal("category");
			codeDiscountTpCategory.setOrd(1);
			codeDiscountTpCategory.setCodeDesc("카테고리");
			codeDiscountTpCategory.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpCategory);
		}
		
		// 메뉴
		Code codeDiscountTpMenu = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "menu");
		if (null == codeDiscountTpMenu) {
			codeDiscountTpMenu = new Code();
			codeDiscountTpMenu.setNm("메뉴");
			codeDiscountTpMenu.setVal("menu");
			codeDiscountTpMenu.setOrd(2);
			codeDiscountTpMenu.setCodeDesc("메뉴");
			codeDiscountTpMenu.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpMenu);
		}
		
		// 옵션 그룹
		Code codeDiscountTpOptTp = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "optTp");
		if (null == codeDiscountTpOptTp) {
			codeDiscountTpOptTp = new Code();
			codeDiscountTpOptTp.setNm("옵션 그룹");
			codeDiscountTpOptTp.setVal("optTp");
			codeDiscountTpOptTp.setOrd(3);
			codeDiscountTpOptTp.setCodeDesc("옵션 그룹");
			codeDiscountTpOptTp.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpOptTp);
		}
		
		// 옵션
		Code codeDiscountTpOpt = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "opt");
		if (null == codeDiscountTpOpt) {
			codeDiscountTpOpt = new Code();
			codeDiscountTpOpt.setNm("옵션");
			codeDiscountTpOpt.setVal("opt");
			codeDiscountTpOpt.setOrd(4);
			codeDiscountTpOpt.setCodeDesc("옵션");
			codeDiscountTpOpt.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpOpt);
		}
		
		// 할인
		Code codeDiscountTpDiscount = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "discount");
		if (null == codeDiscountTpDiscount) {
			codeDiscountTpDiscount = new Code();
			codeDiscountTpDiscount.setNm("할인");
			codeDiscountTpDiscount.setVal("discount");
			codeDiscountTpDiscount.setOrd(5);
			codeDiscountTpDiscount.setCodeDesc("할인");
			codeDiscountTpDiscount.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpDiscount);
		}
		
		// 재료
		Code codeDiscountTpStuff = codeRepository.findOneByTopCodeAndVal(codeDictionaryTp, "stuff");
		if (null == codeDiscountTpStuff) {
			codeDiscountTpStuff = new Code();
			codeDiscountTpStuff.setNm("재료");
			codeDiscountTpStuff.setVal("stuff");
			codeDiscountTpStuff.setOrd(6);
			codeDiscountTpStuff.setCodeDesc("재료");
			codeDiscountTpStuff.setTopCode(codeDictionaryTp);
			codeRepository.save(codeDiscountTpStuff);
		}
		
		
		// 통계 조회 기간 구분
		Code codeDateSearchTp = codeRepository.findOneByTopCodeAndVal(null, "DATE_SEARCH_TP");
		if (null == codeDateSearchTp) {
			codeDateSearchTp = new Code();
			codeDateSearchTp.setVal("DATE_SEARCH_TP");
			codeDateSearchTp.setNm("통계 조회 기간 구분");
			codeDateSearchTp.setOrd(ord++);
			codeDateSearchTp.setCodeDesc("통계 조회 기간 구분");
			codeRepository.save(codeDateSearchTp);
		}

		// 시간
		Code codeDateSearchTpTime = codeRepository.findOneByTopCodeAndVal(codeDateSearchTp, "time");
		if (null == codeDateSearchTpTime) {
			codeDateSearchTpTime = new Code();
			codeDateSearchTpTime.setNm("시간별");
			codeDateSearchTpTime.setVal("time");
			codeDateSearchTpTime.setOrd(1);
			codeDateSearchTpTime.setCodeDesc("시간별");
			codeDateSearchTpTime.setTopCode(codeDateSearchTp);
			codeRepository.save(codeDateSearchTpTime);
		}
		
		// 일
		Code codeDateSearchTpDate = codeRepository.findOneByTopCodeAndVal(codeDateSearchTp, "date");
		if (null == codeDateSearchTpDate) {
			codeDateSearchTpDate = new Code();
			codeDateSearchTpDate.setNm("일별");
			codeDateSearchTpDate.setVal("date");
			codeDateSearchTpDate.setOrd(2);
			codeDateSearchTpDate.setCodeDesc("일별");
			codeDateSearchTpDate.setTopCode(codeDateSearchTp);
			codeRepository.save(codeDateSearchTpDate);
		}
		
		// 월
		Code codeDateSearchTpMonth = codeRepository.findOneByTopCodeAndVal(codeDateSearchTp, "month");
		if (null == codeDateSearchTpMonth) {
			codeDateSearchTpMonth = new Code();
			codeDateSearchTpMonth.setNm("월별");
			codeDateSearchTpMonth.setVal("month");
			codeDateSearchTpMonth.setOrd(3);
			codeDateSearchTpMonth.setCodeDesc("월별");
			codeDateSearchTpMonth.setTopCode(codeDateSearchTp);
			codeRepository.save(codeDateSearchTpMonth);
		}
		
		// 년
		Code codeDateSearchTpYear = codeRepository.findOneByTopCodeAndVal(codeDateSearchTp, "year");
		if (null == codeDateSearchTpYear) {
			codeDateSearchTpYear = new Code();
			codeDateSearchTpYear.setNm("년별");
			codeDateSearchTpYear.setVal("year");
			codeDateSearchTpYear.setOrd(4);
			codeDateSearchTpYear.setCodeDesc("년별");
			codeDateSearchTpYear.setTopCode(codeDateSearchTp);
			codeRepository.save(codeDateSearchTpYear);
		}
				
		
		// 필터 구분
		Code codeFilterTp = codeRepository.findOneByTopCodeAndVal(null, "FILTER_TP");
		if (null == codeFilterTp) {
			codeFilterTp = new Code();
			codeFilterTp.setVal("FILTER_TP");
			codeFilterTp.setNm("웹 주문 필터 구분");
			codeFilterTp.setOrd(ord++);
			codeFilterTp.setCodeDesc("웹 주문 필터 구분");
			codeRepository.save(codeFilterTp);
		}

		// 금액 정열
		Code codeFilterPrice = codeRepository.findOneByTopCodeAndVal(codeFilterTp, "price");
		if (null == codeFilterPrice) {
			codeFilterPrice = new Code();
			codeFilterPrice.setNm("금액 정열");
			codeFilterPrice.setVal("price");
			codeFilterPrice.setOrd(1);
			codeFilterPrice.setCodeDesc("금액 정열");
			codeFilterPrice.setTopCode(codeFilterTp);
			codeRepository.save(codeFilterPrice);
		}
		
		// 인기 정열
		Code codeFilterFame = codeRepository.findOneByTopCodeAndVal(codeFilterTp, "fame");
		if (null == codeFilterFame) {
			codeFilterFame = new Code();
			codeFilterFame.setNm("인기 정열");
			codeFilterFame.setVal("fame");
			codeFilterFame.setOrd(2);
			codeFilterFame.setCodeDesc("인기 정열");
			codeFilterFame.setTopCode(codeFilterTp);
			codeRepository.save(codeFilterFame);
		}
		
		// 재료 필터
		Code codeFilterStuff = codeRepository.findOneByTopCodeAndVal(codeFilterTp, "stuff");
		if (null == codeFilterStuff) {
			codeFilterStuff = new Code();
			codeFilterStuff.setNm("재료 필터");
			codeFilterStuff.setVal("stuff");
			codeFilterStuff.setOrd(3);
			codeFilterStuff.setCodeDesc("재료 필터");
			codeFilterStuff.setTopCode(codeFilterTp);
			codeRepository.save(codeFilterStuff);
		}
				
				
		// 푸시 구분
		Code codePushTp = codeRepository.findOneByTopCodeAndVal(null, "PUSH_TP");
		if (null == codePushTp) {
			codePushTp = new Code();
			codePushTp.setVal("PUSH_TP");
			codePushTp.setNm("푸시 구분");
			codePushTp.setOrd(ord++);
			codePushTp.setCodeDesc("푸시 구분");
			codeRepository.save(codePushTp);
		}

		// 공지
		Code codePushNoti = codeRepository.findOneByTopCodeAndVal(codePushTp, "notice");
		if (null == codePushNoti) {
			codePushNoti = new Code();
			codePushNoti.setNm("공지");
			codePushNoti.setVal("notice");
			codePushNoti.setOrd(1);
			codePushNoti.setCodeDesc("공지");
			codePushNoti.setTopCode(codePushTp);
			codeRepository.save(codePushNoti);
		}
		
		// 이벤트
		Code codePushEvent = codeRepository.findOneByTopCodeAndVal(codePushTp, "event");
		if (null == codePushEvent) {
			codePushEvent = new Code();
			codePushEvent.setNm("이벤트");
			codePushEvent.setVal("event");
			codePushEvent.setOrd(2);
			codePushEvent.setCodeDesc("이벤트");
			codePushEvent.setTopCode(codePushTp);
			codeRepository.save(codePushEvent);
		}
		
		// 긴급
		Code codePushAlert = codeRepository.findOneByTopCodeAndVal(codePushTp, "alert");
		if (null == codePushAlert) {
			codePushAlert = new Code();
			codePushAlert.setNm("긴급");
			codePushAlert.setVal("alert");
			codePushAlert.setOrd(3);
			codePushAlert.setCodeDesc("긴급");
			codePushAlert.setTopCode(codePushTp);
			codeRepository.save(codePushAlert);
		}
		
				
//		// 매장 유형
//		Code codeStoreType = codeRepository.findOneByTopCodeAndVal(null, "STORE_TP");
//		if (null == codeStoreType) {
//			codeStoreType = new Code();
//			codeStoreType.setVal("STORE_TP");
//			codeStoreType.setNm("매장 유형");
//			codeStoreType.setOrd(ord++);
//			codeStoreType.setCodeDesc("매장 유형");
//			codeRepository.save(codeStoreType);
//		}
//
//		// 카페,디저트
//		Code codeSC01 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC01");
//		if (null == codeSC01) {
//			codeSC01 = new Code();
//			codeSC01.setNm("카페-디저트");
//			codeSC01.setVal("SC01");
//			codeSC01.setOrd(1);
//			codeSC01.setCodeDesc("카페-디저트");
//			codeSC01.setTopCode(codeStoreType);
//			codeRepository.save(codeSC01);
//		}
//		
//		// 한식
//		Code codeSC02 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC02");
//		if (null == codeSC02) {
//			codeSC02 = new Code();
//			codeSC02.setNm("한식");
//			codeSC02.setVal("SC02");
//			codeSC02.setOrd(2);
//			codeSC02.setCodeDesc("한식");
//			codeSC02.setTopCode(codeStoreType);
//			codeRepository.save(codeSC02);
//		}
//				
//		// 중국집
//		Code codeSC03 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC03");
//		if (null == codeSC03) {
//			codeSC03 = new Code();
//			codeSC03.setNm("중국집");
//			codeSC03.setVal("SC03");
//			codeSC03.setOrd(3);
//			codeSC03.setCodeDesc("중국집");
//			codeSC03.setTopCode(codeStoreType);
//			codeRepository.save(codeSC03);
//		}
//		
//		// 일식
//		Code codeSC04 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC04");
//		if (null == codeSC04) {
//			codeSC04 = new Code();
//			codeSC04.setNm("일식");
//			codeSC04.setVal("SC04");
//			codeSC04.setOrd(4);
//			codeSC04.setCodeDesc("일식");
//			codeSC04.setTopCode(codeStoreType);
//			codeRepository.save(codeSC04);
//		}
//		
//		// 분식
//		Code codeSC05 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC05");
//		if (null == codeSC05) {
//			codeSC05 = new Code();
//			codeSC05.setNm("분식");
//			codeSC05.setVal("SC05");
//			codeSC05.setOrd(5);
//			codeSC05.setCodeDesc("분식");
//			codeSC05.setTopCode(codeStoreType);
//			codeRepository.save(codeSC05);
//		}
//		
//		// 치킨
//		Code codeSC06 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC06");
//		if (null == codeSC06) {
//			codeSC06 = new Code();
//			codeSC06.setNm("치킨");
//			codeSC06.setVal("SC06");
//			codeSC06.setOrd(6);
//			codeSC06.setCodeDesc("치킨");
//			codeSC06.setTopCode(codeStoreType);
//			codeRepository.save(codeSC06);
//		}
//		
//		// 피자
//		Code codeSC07 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC07");
//		if (null == codeSC07) {
//			codeSC07 = new Code();
//			codeSC07.setNm("피자");
//			codeSC07.setVal("SC07");
//			codeSC07.setOrd(7);
//			codeSC07.setCodeDesc("피자");
//			codeSC07.setTopCode(codeStoreType);
//			codeRepository.save(codeSC07);
//		}
//		
//		// 야식
//		Code codeSC08 = codeRepository.findOneByTopCodeAndVal(codeStoreType, "SC08");
//		if (null == codeSC08) {
//			codeSC08 = new Code();
//			codeSC08.setNm("야식");
//			codeSC08.setVal("SC08");
//			codeSC08.setOrd(8);
//			codeSC08.setCodeDesc("야식");
//			codeSC08.setTopCode(codeStoreType);
//			codeRepository.save(codeSC08);
//		}
	}
	
	
	
	/**
	 * 최상위 메뉴 등록
	 */
	private void initTopMenu() {
		// 시스템 메뉴 등록
		menuSystem = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "시스템 관리", 1);
		if (null == menuSystem) {
			menuSystem = new Menu();
			menuSystem.setMenuNm("시스템 관리");
			menuSystem.setMenuIcon("icon-system");
			menuSystem.setMlevel(1);
			menuSystem.setOrd(1);
			menuSystem.setResTp(codeResMenu);
			menuSystem.setUrl("system");
			menuSystem.setMethod("NONE");
			menuSystem.setResDesc(menuSystem.getMenuNm());
			menuRepository.save(menuSystem);
		}
		resourcess.add(menuSystem);
		
		menuLanMng = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "다국어 관리", 1);
		if (null == menuLanMng) {
			menuLanMng = new Menu();
			menuLanMng.setMenuNm("다국어 관리");
			menuLanMng.setMenuIcon("icon-lan");
			menuLanMng.setMlevel(1);
			menuLanMng.setOrd(2);
			menuLanMng.setResTp(codeResMenu);
			menuLanMng.setUrl("lan");
			menuLanMng.setMethod("NONE");
			menuLanMng.setResDesc(menuLanMng.getMenuNm());
			menuRepository.save(menuLanMng);
		}
		resourcess.add(menuLanMng);
		resourcessForMng.add(menuLanMng);
		
		// 기본 정보 메뉴 등록
		menuCommonMng = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "기본 정보 관리", 1);
		if (null == menuCommonMng) {
			menuCommonMng = new Menu();
			menuCommonMng.setMenuNm("기본 정보 관리");
			menuCommonMng.setMenuIcon("icon-common");
			menuCommonMng.setMlevel(1);
			menuCommonMng.setOrd(3);
			menuCommonMng.setResTp(codeResMenu);
			menuCommonMng.setUrl("common");
			menuCommonMng.setMethod("NONE");
			menuCommonMng.setResDesc(menuCommonMng.getMenuNm());
			menuRepository.save(menuCommonMng);
		}
		resourcess.add(menuCommonMng);
		resourcessForMng.add(menuCommonMng);
		
		// 사업장 관리 메뉴 등록
		menuCustMng = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "사업장 관리", 1);
		if (null == menuCustMng) {
			menuCustMng = new Menu();
			menuCustMng.setMenuNm("사업장 관리");
			menuCustMng.setMenuIcon("icon-cust");
			menuCustMng.setMlevel(1);
			menuCustMng.setOrd(4);
			menuCustMng.setResTp(codeResMenu);
			menuCustMng.setUrl("cust");
			menuCustMng.setMethod("NONE");
			menuCustMng.setResDesc(menuCustMng.getMenuNm());
			menuRepository.save(menuCustMng);
		}
		resourcess.add(menuCustMng);
		resourcessForMng.add(menuCustMng);
		
		// 하위 매장 관리 메뉴 등록
		menuStoreMng = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "매장 관리", 1);
		if (null == menuStoreMng) {
			menuStoreMng = new Menu();
			menuStoreMng.setMenuNm("매장 관리");
			menuStoreMng.setMenuIcon("icon-store");
			menuStoreMng.setMlevel(1);
			menuStoreMng.setOrd(6);
			menuStoreMng.setResTp(codeResMenu);
			menuStoreMng.setUrl("store");
			menuStoreMng.setMethod("NONE");
			menuStoreMng.setResDesc(menuStoreMng.getMenuNm());
			menuRepository.save(menuStoreMng);
		}
		resourcessForCust.add(menuStoreMng);
		
	}
	
	
	
	/**
	 * 시스템 기본 정보 등록
	 */
	private void initSystemAction() {
		// 시스템 기본 권한
		Action actionLoginSuccess = actionRepository.findOneByActionId("LoginSuccess");
		if (null == actionLoginSuccess) {
			actionLoginSuccess = new Action();
			actionLoginSuccess.setActionId("LoginSuccess");
			actionLoginSuccess.setActionNm("링크 - 로그인 성공");
			actionLoginSuccess.setActionTp(codeLink);
			actionLoginSuccess.setResTp(codeResAction);
			actionLoginSuccess.setUrl("/loginSuccess");
			actionLoginSuccess.setMethod("GET");
			actionLoginSuccess.setResDesc(actionLoginSuccess.getActionNm());
			actionRepository.save(actionLoginSuccess);
		}
		resourcess.add(actionLoginSuccess);
		resourcessForMng.add(actionLoginSuccess);
		resourcessForCust.add(actionLoginSuccess);
		resourcessForStore.add(actionLoginSuccess);
		
		Action actionLoginFailure = actionRepository.findOneByActionId("LoginFailure");
		if (null == actionLoginFailure) {
			actionLoginFailure = new Action();
			actionLoginFailure.setActionId("LoginFailure");
			actionLoginFailure.setActionNm("링크 - 로그인 실패");
			actionLoginFailure.setActionTp(codeLink);
			actionLoginFailure.setResTp(codeResAction);
			actionLoginFailure.setUrl("/loginFailure");
			actionLoginFailure.setMethod("GET");
			actionLoginFailure.setResDesc(actionLoginFailure.getActionNm());
			actionRepository.save(actionLoginFailure);
		}
		resourcess.add(actionLoginFailure);
		resourcessForMng.add(actionLoginFailure);
		resourcessForCust.add(actionLoginFailure);
		resourcessForStore.add(actionLoginFailure);
		
		Action actionMain = actionRepository.findOneByActionId("Main");
		if (null == actionMain) {
			actionMain = new Action();
			actionMain.setActionId("Main");
			actionMain.setActionNm("링크 - 메인 페이지");
			actionMain.setActionTp(codeLink);
			actionMain.setResTp(codeResAction);
			actionMain.setUrl("/main");
			actionMain.setMethod("GET");
			actionMain.setResDesc(actionMain.getActionNm());
			actionRepository.save(actionMain);
		}
		resourcess.add(actionMain);
		resourcessForMng.add(actionMain);
		resourcessForCust.add(actionMain);
		resourcessForStore.add(actionMain);
		
		Action actionMainMenu = actionRepository.findOneByActionId("MainMenu");
		if (null == actionMainMenu) {
			actionMainMenu = new Action();
			actionMainMenu.setActionId("MainMenu");
			actionMainMenu.setActionNm("링크 - 메인 페이지 메뉴 조회");
			actionMainMenu.setActionTp(codeLink);
			actionMainMenu.setResTp(codeResAction);
			actionMainMenu.setUrl("/mainMenu");
			actionMainMenu.setMethod("GET");
			actionMainMenu.setResDesc(actionMainMenu.getActionNm());
			actionRepository.save(actionMainMenu);
		}
		resourcess.add(actionMainMenu);
		resourcessForMng.add(actionMainMenu);
		resourcessForCust.add(actionMainMenu);
		resourcessForStore.add(actionMainMenu);
		
		Action actionUpUserInfo = actionRepository.findOneByActionId("UpUserInfo");
		if (null == actionUpUserInfo) {
			actionUpUserInfo = new Action();
			actionUpUserInfo.setActionId("UpUserInfo");
			actionUpUserInfo.setActionNm("버튼 - 계정 정보 수정");
			actionUpUserInfo.setActionTp(codeBtn);
			actionUpUserInfo.setResTp(codeResAction);
			actionUpUserInfo.setUrl("/v/system/account/info");
			actionUpUserInfo.setMethod("GET");
			actionUpUserInfo.setResDesc(actionUpUserInfo.getActionNm());
			actionRepository.save(actionUpUserInfo);
		}
		resourcess.add(actionUpUserInfo);
		resourcessForMng.add(actionUpUserInfo);
		
		Action actionUpCustUserInfo = actionRepository.findOneByActionId("UpCustUserInfo");
		if (null == actionUpCustUserInfo) {
			actionUpCustUserInfo = new Action();
			actionUpCustUserInfo.setActionId("UpCustUserInfo");
			actionUpCustUserInfo.setActionNm("버튼 - 사업장 회원 계정 정보 수정");
			actionUpCustUserInfo.setActionTp(codeBtn);
			actionUpCustUserInfo.setResTp(codeResAction);
			actionUpCustUserInfo.setUrl("/v/business/user/cust");
			actionUpCustUserInfo.setMethod("GET");
			actionUpCustUserInfo.setResDesc(actionUpCustUserInfo.getActionNm());
			actionRepository.save(actionUpCustUserInfo);
		}
		resourcessForCust.add(actionUpCustUserInfo);
		
		Action actionUpStoreUserInfo = actionRepository.findOneByActionId("UpStoreUserInfo");
		if (null == actionUpStoreUserInfo) {
			actionUpStoreUserInfo = new Action();
			actionUpStoreUserInfo.setActionId("UpStoreUserInfo");
			actionUpStoreUserInfo.setActionNm("버튼 - 매장 회원 계정 정보 수정");
			actionUpStoreUserInfo.setActionTp(codeBtn);
			actionUpStoreUserInfo.setResTp(codeResAction);
			actionUpStoreUserInfo.setUrl("/v/business/user/store");
			actionUpStoreUserInfo.setMethod("GET");
			actionUpStoreUserInfo.setResDesc(actionUpStoreUserInfo.getActionNm());
			actionRepository.save(actionUpStoreUserInfo);
		}
		resourcessForStore.add(actionUpStoreUserInfo);
	}
	
	
	/**
	 * 계정 관리 메뉴 등록
	 */
	private void initAccountMenu() {
		Menu menuAccount = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "계정 관리", 2);
		if (null == menuAccount) {
			menuAccount = new Menu();
			menuAccount.setTopMenu(menuSystem);
			menuAccount.setMenuNm("계정 관리");
			menuAccount.setMenuIcon("icon-account");
			menuAccount.setMlevel(2);
			menuAccount.setOrd(subMenuOrd++);
			menuAccount.setResTp(codeResMenu);
			menuAccount.setUrl("/v/system/account/list");
			menuAccount.setMethod("GET");
			menuAccount.setResDesc(menuAccount.getMenuNm());
			menuRepository.save(menuAccount);
		}
		resourcess.add(menuAccount);
		
		Action actionAccountAdd = actionRepository.findOneByActionId("AccountAdd");
		if (null == actionAccountAdd) {
			actionAccountAdd = new Action();
			actionAccountAdd.setActionId("AccountAdd");
			actionAccountAdd.setActionNm("버튼 - 계정 등록");
			actionAccountAdd.setActionTp(codeBtn);
			actionAccountAdd.setResTp(codeResAction);
			actionAccountAdd.setUrl("/account");
			actionAccountAdd.setMethod("POST");
			actionAccountAdd.setResDesc(actionAccountAdd.getActionNm());
			actionRepository.save(actionAccountAdd);
		}
		resourcess.add(actionAccountAdd);
		
		Action actionAccountGet = actionRepository.findOneByActionId("AccountGet");
		if (null == actionAccountGet) {
			actionAccountGet = new Action();
			actionAccountGet.setActionId("AccountGet");
			actionAccountGet.setActionNm("링크 - 계정 정보 조회");
			actionAccountGet.setActionTp(codeLink);
			actionAccountGet.setResTp(codeResAction);
			actionAccountGet.setUrl("/account/*");
			actionAccountGet.setMethod("GET");
			actionAccountGet.setResDesc(actionAccountGet.getActionNm());
			actionRepository.save(actionAccountGet);
		}
		resourcess.add(actionAccountGet);
		
		Action actionAccountUp = actionRepository.findOneByActionId("AccountUp");
		if (null == actionAccountUp) {
			actionAccountUp = new Action();
			actionAccountUp.setActionId("AccountUp");
			actionAccountUp.setActionNm("버튼 - 계정 수정");
			actionAccountUp.setActionTp(codeBtn);
			actionAccountUp.setResTp(codeResAction);
			actionAccountUp.setUrl("/account/*");
			actionAccountUp.setMethod("PUT");
			actionAccountUp.setResDesc(actionAccountUp.getActionNm());
			actionRepository.save(actionAccountUp);
		}
		resourcess.add(actionAccountUp);
		
		Action actionAccountDel = actionRepository.findOneByActionId("AccountDel");
		if (null == actionAccountDel) {
			actionAccountDel = new Action();
			actionAccountDel.setActionId("AccountDel");
			actionAccountDel.setActionNm("버튼 - 계정 삭제");
			actionAccountDel.setActionTp(codeBtn);
			actionAccountDel.setResTp(codeResAction);
			actionAccountDel.setUrl("/account/*");
			actionAccountDel.setMethod("DELETE");
			actionAccountDel.setResDesc(actionAccountDel.getActionNm());
			actionRepository.save(actionAccountDel);
		}
		resourcess.add(actionAccountDel);
		
		Action actionAccountAuth = actionRepository.findOneByActionId("AccountAuth");
		if (null == actionAccountAuth) {
			actionAccountAuth = new Action();
			actionAccountAuth.setActionId("AccountAuth");
			actionAccountAuth.setActionNm("버튼 - 계정 권한 설정");
			actionAccountAuth.setActionTp(codeBtn);
			actionAccountAuth.setResTp(codeResAction);
			actionAccountAuth.setUrl("accountAuthBtn");
			actionAccountAuth.setMethod("NONE");
			actionAccountAuth.setResDesc(actionAccountAuth.getActionNm());
			actionRepository.save(actionAccountAuth);
		}
		resourcess.add(actionAccountAuth);
		
		Action actionAccountSearch = actionRepository.findOneByActionId("AccountSearch");
		if (null == actionAccountSearch) {
			actionAccountSearch = new Action();
			actionAccountSearch.setActionId("AccountSearch");
			actionAccountSearch.setActionNm("버튼 - 계정 리스트 조회");
			actionAccountSearch.setActionTp(codeBtn);
			actionAccountSearch.setResTp(codeResAction);
			actionAccountSearch.setUrl("/account");
			actionAccountSearch.setMethod("GET");
			actionAccountSearch.setResDesc(actionAccountSearch.getActionNm());
			actionRepository.save(actionAccountSearch);
		}
		resourcess.add(actionAccountSearch);
		
		Action actionAccountIdChk = actionRepository.findOneByActionId("AccountIdChk");
		if (null == actionAccountIdChk) {
			actionAccountIdChk = new Action();
			actionAccountIdChk.setActionId("AccountIdChk");
			actionAccountIdChk.setActionNm("링크 - 계정 아이디 중복 체크");
			actionAccountIdChk.setActionTp(codeLink);
			actionAccountIdChk.setResTp(codeResAction);
			actionAccountIdChk.setUrl("/account/chk/*");
			actionAccountIdChk.setMethod("PATCH");
			actionAccountIdChk.setResDesc(actionAccountIdChk.getActionNm());
			actionRepository.save(actionAccountIdChk);
		}
		resourcess.add(actionAccountIdChk);
		
		Action actionAccountGetByNm = actionRepository.findOneByActionId("AccountGetByNm");
		if (null == actionAccountGetByNm) {
			actionAccountGetByNm = new Action();
			actionAccountGetByNm.setActionId("AccountGetByNm");
			actionAccountGetByNm.setActionNm("링크 - 계정 정보 콤보 조회");
			actionAccountGetByNm.setActionTp(codeLink);
			actionAccountGetByNm.setResTp(codeResAction);
			actionAccountGetByNm.setUrl("/account/searchByIdOrNm/*");
			actionAccountGetByNm.setMethod("GET");
			actionAccountGetByNm.setResDesc(actionAccountGetByNm.getActionNm());
			actionRepository.save(actionAccountGetByNm);
		}
		resourcess.add(actionAccountGetByNm);
	}
	
	
	/**
	 * 권한 관리 메뉴 등록
	 */
	private void initAuthMenu() {
		Menu menuAuth = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "권한 관리", 2);
		if (null == menuAuth) {
			menuAuth = new Menu();
			menuAuth.setTopMenu(menuSystem);
			menuAuth.setMenuNm("권한 관리");
			menuAuth.setMenuIcon("icon-auth");
			menuAuth.setMlevel(2);
			menuAuth.setOrd(subMenuOrd++);
			menuAuth.setResTp(codeResMenu);
			menuAuth.setUrl("/v/system/auth/list");
			menuAuth.setMethod("GET");
			menuAuth.setResDesc(menuAuth.getMenuNm());
			menuRepository.save(menuAuth);
		}
		resourcess.add(menuAuth);
		
		Action actionAuthAdd = actionRepository.findOneByActionId("AuthAdd");
		if (null == actionAuthAdd) {
			actionAuthAdd = new Action();
			actionAuthAdd.setActionId("AuthAdd");
			actionAuthAdd.setActionNm("버튼 - 권한 등록");
			actionAuthAdd.setActionTp(codeBtn);
			actionAuthAdd.setResTp(codeResAction);
			actionAuthAdd.setUrl("/auth");
			actionAuthAdd.setMethod("POST");
			actionAuthAdd.setResDesc(actionAuthAdd.getActionNm());
			actionRepository.save(actionAuthAdd);
		}
		resourcess.add(actionAuthAdd);
		
		Action actionAuthGet = actionRepository.findOneByActionId("AuthGet");
		if (null == actionAuthGet) {
			actionAuthGet = new Action();
			actionAuthGet.setActionId("AuthGet");
			actionAuthGet.setActionNm("링크 - 권한 정보 조회");
			actionAuthGet.setActionTp(codeLink);
			actionAuthGet.setResTp(codeResAction);
			actionAuthGet.setUrl("/auth/*");
			actionAuthGet.setMethod("GET");
			actionAuthGet.setResDesc(actionAuthGet.getActionNm());
			actionRepository.save(actionAuthGet);
		}
		resourcess.add(actionAuthGet);
		
		Action actionAuthUp = actionRepository.findOneByActionId("AuthUp");
		if (null == actionAuthUp) {
			actionAuthUp = new Action();
			actionAuthUp.setActionId("AuthUp");
			actionAuthUp.setActionNm("버튼 - 권한 수정");
			actionAuthUp.setActionTp(codeBtn);
			actionAuthUp.setResTp(codeResAction);
			actionAuthUp.setUrl("/auth/*");
			actionAuthUp.setMethod("PUT");
			actionAuthUp.setResDesc(actionAuthUp.getActionNm());
			actionRepository.save(actionAuthUp);
		}
		resourcess.add(actionAuthUp);
		
		Action actionAuthDel = actionRepository.findOneByActionId("AuthDel");
		if (null == actionAuthDel) {
			actionAuthDel = new Action();
			actionAuthDel.setActionId("AuthDel");
			actionAuthDel.setActionNm("버튼 - 권한 삭제");
			actionAuthDel.setActionTp(codeBtn);
			actionAuthDel.setResTp(codeResAction);
			actionAuthDel.setUrl("/auth/*");
			actionAuthDel.setMethod("DELETE");
			actionAuthDel.setResDesc(actionAuthDel.getActionNm());
			actionRepository.save(actionAuthDel);
		}
		resourcess.add(actionAuthDel);
		
		Action actionAuthAuth = actionRepository.findOneByActionId("AuthAuth");
		if (null == actionAuthAuth) {
			actionAuthAuth = new Action();
			actionAuthAuth.setActionId("AuthAuth");
			actionAuthAuth.setActionNm("버튼 - 권한 리소스 설정");
			actionAuthAuth.setActionTp(codeBtn);
			actionAuthAuth.setResTp(codeResAction);
			actionAuthAuth.setUrl("authResBtn");
			actionAuthAuth.setMethod("NONE");
			actionAuthAuth.setResDesc(actionAuthAuth.getActionNm());
			actionRepository.save(actionAuthAuth);
		}
		resourcess.add(actionAuthAuth);
		
		Action actionAuthSearch = actionRepository.findOneByActionId("AuthSearch");
		if (null == actionAuthSearch) {
			actionAuthSearch = new Action();
			actionAuthSearch.setActionId("AuthSearch");
			actionAuthSearch.setActionNm("버튼 - 권한 리스트 조회");
			actionAuthSearch.setActionTp(codeBtn);
			actionAuthSearch.setResTp(codeResAction);
			actionAuthSearch.setUrl("/auth");
			actionAuthSearch.setMethod("GET");
			actionAuthSearch.setResDesc(actionAuthSearch.getActionNm());
			actionRepository.save(actionAuthSearch);
		}
		resourcess.add(actionAuthSearch);
		
		Action actionAuthIdChk = actionRepository.findOneByActionId("AuthIdChk");
		if (null == actionAuthIdChk) {
			actionAuthIdChk = new Action();
			actionAuthIdChk.setActionId("AuthIdChk");
			actionAuthIdChk.setActionNm("링크 - 권한ID 중복 체크");
			actionAuthIdChk.setActionTp(codeLink);
			actionAuthIdChk.setResTp(codeResAction);
			actionAuthIdChk.setUrl("/auth/chk/*");
			actionAuthIdChk.setMethod("PATCH");
			actionAuthIdChk.setResDesc(actionAuthIdChk.getActionNm());
			actionRepository.save(actionAuthIdChk);
		}
		resourcess.add(actionAuthIdChk);
		
		Action actionAuthNmChk = actionRepository.findOneByActionId("AuthNmChk");
		if (null == actionAuthNmChk) {
			actionAuthNmChk = new Action();
			actionAuthNmChk.setActionId("AuthNmChk");
			actionAuthNmChk.setActionNm("링크 - 권한명 중복 체크");
			actionAuthNmChk.setActionTp(codeLink);
			actionAuthNmChk.setResTp(codeResAction);
			actionAuthNmChk.setUrl("/auth/chkNm/*");
			actionAuthNmChk.setMethod("PATCH");
			actionAuthNmChk.setResDesc(actionAuthNmChk.getActionNm());
			actionRepository.save(actionAuthNmChk);
		}
		resourcess.add(actionAuthNmChk);
	}
	
	
	/**
	 * 메뉴 관리 메뉴 등록
	 */
	private void initMenuMenu() {
		Menu menuMenu = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "상품 관리", 2);
		if (null == menuMenu) {
			menuMenu = new Menu();
			menuMenu.setTopMenu(menuSystem);
			menuMenu.setMenuNm("상품 관리");
			menuMenu.setMenuIcon("icon-menu");
			menuMenu.setMlevel(2);
			menuMenu.setOrd(subMenuOrd++);
			menuMenu.setResTp(codeResMenu);
			menuMenu.setUrl("/v/system/menu/list");
			menuMenu.setMethod("GET");
			menuMenu.setResDesc(menuMenu.getMenuNm());
			menuRepository.save(menuMenu);
		}
		resourcess.add(menuMenu);
		
		Action actionMenuAdd = actionRepository.findOneByActionId("MenuAdd");
		if (null == actionMenuAdd) {
			actionMenuAdd = new Action();
			actionMenuAdd.setActionId("MenuAdd");
			actionMenuAdd.setActionNm("버튼 - 메뉴 등록");
			actionMenuAdd.setActionTp(codeBtn);
			actionMenuAdd.setResTp(codeResAction);
			actionMenuAdd.setUrl("/menu");
			actionMenuAdd.setMethod("POST");
			actionMenuAdd.setResDesc(actionMenuAdd.getActionNm());
			actionRepository.save(actionMenuAdd);
		}
		resourcess.add(actionMenuAdd);
		
		Action actionMenuGet = actionRepository.findOneByActionId("MenuGet");
		if (null == actionMenuGet) {
			actionMenuGet = new Action();
			actionMenuGet.setActionId("MenuGet");
			actionMenuGet.setActionNm("버튼 - 메뉴 정보 조회");
			actionMenuGet.setActionTp(codeBtn);
			actionMenuGet.setResTp(codeResAction);
			actionMenuGet.setUrl("/menu/*");
			actionMenuGet.setMethod("GET");
			actionMenuGet.setResDesc(actionMenuGet.getActionNm());
			actionRepository.save(actionMenuGet);
		}
		resourcess.add(actionMenuGet);
		
		Action actionMenuUp = actionRepository.findOneByActionId("MenuUp");
		if (null == actionMenuUp) {
			actionMenuUp = new Action();
			actionMenuUp.setActionId("MenuUp");
			actionMenuUp.setActionNm("버튼 - 메뉴 수정");
			actionMenuUp.setActionTp(codeBtn);
			actionMenuUp.setResTp(codeResAction);
			actionMenuUp.setUrl("/menu/*");
			actionMenuUp.setMethod("PUT");
			actionMenuUp.setResDesc(actionMenuUp.getActionNm());
			actionRepository.save(actionMenuUp);
		}
		resourcess.add(actionMenuUp);
		
		Action actionMenuDel = actionRepository.findOneByActionId("MenuDel");
		if (null == actionMenuDel) {
			actionMenuDel = new Action();
			actionMenuDel.setActionId("MenuDel");
			actionMenuDel.setActionNm("버튼 - 메뉴 삭제");
			actionMenuDel.setActionTp(codeBtn);
			actionMenuDel.setResTp(codeResAction);
			actionMenuDel.setUrl("/menu/*");
			actionMenuDel.setMethod("DELETE");
			actionMenuDel.setResDesc(actionMenuDel.getActionNm());
			actionRepository.save(actionMenuDel);
		}
		resourcess.add(actionMenuDel);
		
		Action actionMenuSearch = actionRepository.findOneByActionId("MenuSearch");
		if (null == actionMenuSearch) {
			actionMenuSearch = new Action();
			actionMenuSearch.setActionId("MenuSearch");
			actionMenuSearch.setActionNm("버튼 - 메뉴 리스트 조회");
			actionMenuSearch.setActionTp(codeBtn);
			actionMenuSearch.setResTp(codeResAction);
			actionMenuSearch.setUrl("/menu");
			actionMenuSearch.setMethod("GET");
			actionMenuSearch.setResDesc(actionMenuSearch.getActionNm());
			actionRepository.save(actionMenuSearch);
		}
		resourcess.add(actionMenuSearch);
		
		
	}
	
	
	/**
	 * 이벤트 메뉴 등록
	 */
	private void initActionMenu() {
		Menu menuAction = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "이벤트 관리", 2);
		if (null == menuAction) {
			menuAction = new Menu();
			menuAction.setTopMenu(menuSystem);
			menuAction.setMenuNm("이벤트 관리");
			menuAction.setMenuIcon("icon-action");
			menuAction.setMlevel(2);
			menuAction.setOrd(subMenuOrd++);
			menuAction.setResTp(codeResMenu);
			menuAction.setUrl("/v/system/action/list");
			menuAction.setMethod("GET");
			menuAction.setResDesc(menuAction.getMenuNm());
			menuRepository.save(menuAction);
		}
		resourcess.add(menuAction);
		
		Action actionActionAdd = actionRepository.findOneByActionId("ActionAdd");
		if (null == actionActionAdd) {
			actionActionAdd = new Action();
			actionActionAdd.setActionId("ActionAdd");
			actionActionAdd.setActionNm("버튼 - 이벤트 등록");
			actionActionAdd.setActionTp(codeBtn);
			actionActionAdd.setResTp(codeResAction);
			actionActionAdd.setUrl("/action");
			actionActionAdd.setMethod("POST");
			actionActionAdd.setResDesc(actionActionAdd.getActionNm());
			actionRepository.save(actionActionAdd);
		}
		resourcess.add(actionActionAdd);
		
		Action actionActionGet = actionRepository.findOneByActionId("ActionGet");
		if (null == actionActionGet) {
			actionActionGet = new Action();
			actionActionGet.setActionId("ActionGet");
			actionActionGet.setActionNm("버튼 - 이벤트 정보 조회");
			actionActionGet.setActionTp(codeLink);
			actionActionGet.setResTp(codeResAction);
			actionActionGet.setUrl("/action/*");
			actionActionGet.setMethod("GET");
			actionActionGet.setResDesc(actionActionGet.getActionNm());
			actionRepository.save(actionActionGet);
		}
		resourcess.add(actionActionGet);
		
		Action actionActionUp = actionRepository.findOneByActionId("ActionUp");
		if (null == actionActionUp) {
			actionActionUp = new Action();
			actionActionUp.setActionId("ActionUp");
			actionActionUp.setActionNm("버튼 - 이벤트 수정");
			actionActionUp.setActionTp(codeBtn);
			actionActionUp.setResTp(codeResAction);
			actionActionUp.setUrl("/action/*");
			actionActionUp.setMethod("PUT");
			actionActionUp.setResDesc(actionActionUp.getActionNm());
			actionRepository.save(actionActionUp);
		}
		resourcess.add(actionActionUp);
		
		Action actionActionDel = actionRepository.findOneByActionId("ActionDel");
		if (null == actionActionDel) {
			actionActionDel = new Action();
			actionActionDel.setActionId("ActionDel");
			actionActionDel.setActionNm("버튼 - 이벤트 삭제");
			actionActionDel.setActionTp(codeBtn);
			actionActionDel.setResTp(codeResAction);
			actionActionDel.setUrl("/action/*");
			actionActionDel.setMethod("DELETE");
			actionActionDel.setResDesc(actionActionDel.getActionNm());
			actionRepository.save(actionActionDel);
		}
		resourcess.add(actionActionDel);
		
		Action actionActionSearch = actionRepository.findOneByActionId("ActionSearch");
		if (null == actionActionSearch) {
			actionActionSearch = new Action();
			actionActionSearch.setActionId("ActionSearch");
			actionActionSearch.setActionNm("버튼 - 이벤트 리스트 조회");
			actionActionSearch.setActionTp(codeBtn);
			actionActionSearch.setResTp(codeResAction);
			actionActionSearch.setUrl("/action");
			actionActionSearch.setMethod("GET");
			actionActionSearch.setResDesc(actionActionSearch.getActionNm());
			actionRepository.save(actionActionSearch);
		}
		resourcess.add(actionActionSearch);
		
		Action actionActionIdChk = actionRepository.findOneByActionId("ActionIdChk");
		if (null == actionActionIdChk) {
			actionActionIdChk = new Action();
			actionActionIdChk.setActionId("ActionIdChk");
			actionActionIdChk.setActionNm("링크 - 이벤트 코드 중복 체크");
			actionActionIdChk.setActionTp(codeBtn);
			actionActionIdChk.setResTp(codeResAction);
			actionActionIdChk.setUrl("/action/chk/*");
			actionActionIdChk.setMethod("PATCH");
			actionActionIdChk.setResDesc(actionActionIdChk.getActionNm());
			actionRepository.save(actionActionIdChk);
		}
		resourcess.add(actionActionIdChk);
		
		Action actionActionNmChk = actionRepository.findOneByActionId("ActionNmChk");
		if (null == actionActionNmChk) {
			actionActionNmChk = new Action();
			actionActionNmChk.setActionId("ActionNmChk");
			actionActionNmChk.setActionNm("링크 - 이벤트명 중복 체크");
			actionActionNmChk.setActionTp(codeBtn);
			actionActionNmChk.setResTp(codeResAction);
			actionActionNmChk.setUrl("/action/chkNm/*");
			actionActionNmChk.setMethod("PATCH");
			actionActionNmChk.setResDesc(actionActionNmChk.getActionNm());
			actionRepository.save(actionActionNmChk);
		}
		resourcess.add(actionActionNmChk);
		
		Action urlMethodChk = actionRepository.findOneByActionId("UrlMethodChk");
		if (null == urlMethodChk) {
			urlMethodChk = new Action();
			urlMethodChk.setActionId("UrlMethodChk");
			urlMethodChk.setActionNm("링크 - 주소, 호출 타입 중복 체크");
			urlMethodChk.setActionTp(codeLink);
			urlMethodChk.setResTp(codeResAction);
			urlMethodChk.setUrl("/resources/chk/*/*");
			urlMethodChk.setMethod("PATCH");
			urlMethodChk.setResDesc(urlMethodChk.getActionNm());
			actionRepository.save(urlMethodChk);
		}
		resourcess.add(urlMethodChk);
		
//		Action actionResourcesSearch = actionRepository.findOneByActionId("ResourcesSearch");
//		if (null == actionResourcesSearch) {
//			actionResourcesSearch = new Action();
//			actionResourcesSearch.setActionId("ResourcesSearch");
//			actionResourcesSearch.setActionNm("링크 - 리소스 조회");
//			actionResourcesSearch.setActionTp(codeLink);
//			actionResourcesSearch.setResTp(codeResAction);
//			actionResourcesSearch.setUrl("/resources/*");
//			actionResourcesSearch.setMethod("GET");
//			actionResourcesSearch.setResDesc(actionResourcesSearch.getActionNm());
//			actionRepository.save(actionResourcesSearch);
//		}
//		resourcess.add(actionResourcesSearch);
		
	}
	
	
	/**
	 * 코드 관리 메뉴 등록
	 */
	private void initCodeMenu() {
		Menu menuCode = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "코드 관리", 2);
		if (null == menuCode) {
			menuCode = new Menu();
			menuCode.setTopMenu(menuSystem);
			menuCode.setMenuNm("코드 관리");
			menuCode.setMenuIcon("icon-code");
			menuCode.setMlevel(2);
			menuCode.setOrd(subMenuOrd++);
			menuCode.setResTp(codeResMenu);
			menuCode.setUrl("/v/system/code/list");
			menuCode.setMethod("GET");
			menuCode.setResDesc(menuCode.getMenuNm());
			menuRepository.save(menuCode);
		}
		resourcess.add(menuCode);
		
		Action actionCodeAdd = actionRepository.findOneByActionId("CodeAdd");
		if (null == actionCodeAdd) {
			actionCodeAdd = new Action();
			actionCodeAdd.setActionId("CodeAdd");
			actionCodeAdd.setActionNm("버튼 - 코드 등록");
			actionCodeAdd.setActionTp(codeBtn);
			actionCodeAdd.setResTp(codeResAction);
			actionCodeAdd.setUrl("/code");
			actionCodeAdd.setMethod("POST");
			actionCodeAdd.setResDesc(actionCodeAdd.getActionNm());
			actionRepository.save(actionCodeAdd);
		}
		resourcess.add(actionCodeAdd);
		
		Action actionCodeGet = actionRepository.findOneByActionId("CodeGet");
		if (null == actionCodeGet) {
			actionCodeGet = new Action();
			actionCodeGet.setActionId("CodeGet");
			actionCodeGet.setActionNm("버튼 - 코드 정보 조회");
			actionCodeGet.setActionTp(codeLink);
			actionCodeGet.setResTp(codeResAction);
			actionCodeGet.setUrl("/code/*");
			actionCodeGet.setMethod("GET");
			actionCodeGet.setResDesc(actionCodeGet.getActionNm());
			actionRepository.save(actionCodeGet);
		}
		resourcess.add(actionCodeGet);
		
		Action actionCodeUp = actionRepository.findOneByActionId("CodeUp");
		if (null == actionCodeUp) {
			actionCodeUp = new Action();
			actionCodeUp.setActionId("CodeUp");
			actionCodeUp.setActionNm("버튼 - 코드 수정");
			actionCodeUp.setActionTp(codeBtn);
			actionCodeUp.setResTp(codeResAction);
			actionCodeUp.setUrl("/code/*");
			actionCodeUp.setMethod("PUT");
			actionCodeUp.setResDesc(actionCodeUp.getActionNm());
			actionRepository.save(actionCodeUp);
		}
		resourcess.add(actionCodeUp);
		
		Action actionCodeDel = actionRepository.findOneByActionId("CodeDel");
		if (null == actionCodeDel) {
			actionCodeDel = new Action();
			actionCodeDel.setActionId("CodeDel");
			actionCodeDel.setActionNm("버튼 - 코드 삭제");
			actionCodeDel.setActionTp(codeBtn);
			actionCodeDel.setResTp(codeResAction);
			actionCodeDel.setUrl("/code/*");
			actionCodeDel.setMethod("DELETE");
			actionCodeDel.setResDesc(actionCodeDel.getActionNm());
			actionRepository.save(actionCodeDel);
		}
		resourcess.add(actionCodeDel);
		
		Action actionCodeSearch = actionRepository.findOneByActionId("CodeSearch");
		if (null == actionCodeSearch) {
			actionCodeSearch = new Action();
			actionCodeSearch.setActionId("CodeSearch");
			actionCodeSearch.setActionNm("버튼 - 코드 리스트 조회");
			actionCodeSearch.setActionTp(codeBtn);
			actionCodeSearch.setResTp(codeResAction);
			actionCodeSearch.setUrl("/code");
			actionCodeSearch.setMethod("GET");
			actionCodeSearch.setResDesc(actionCodeSearch.getActionNm());
			actionRepository.save(actionCodeSearch);
		}
		resourcess.add(actionCodeSearch);
		
		Action actionGetCode = actionRepository.findOneByActionId("GetCode");
		if (null == actionGetCode) {
			actionGetCode = new Action();
			actionGetCode.setActionId("GetCode");
			actionGetCode.setActionNm("API - 공통코드 리스트 조회");
			actionGetCode.setActionTp(codeApi);
			actionGetCode.setResTp(codeResAction);
			actionGetCode.setUrl("/code/getCode/*");
			actionGetCode.setMethod("GET");
			actionGetCode.setResDesc(actionGetCode.getActionNm());
			actionRepository.save(actionGetCode);
		}
		resourcess.add(actionGetCode);
		resourcessForMng.add(actionGetCode);
		resourcessForCust.add(actionGetCode);
		resourcessForStore.add(actionGetCode);
		resourcessForAppUser.add(actionGetCode);
		
		Action actionCodeValChk = actionRepository.findOneByActionId("CodeValChk");
		if (null == actionCodeValChk) {
			actionCodeValChk = new Action();
			actionCodeValChk.setActionId("CodeValChk");
			actionCodeValChk.setActionNm("링크 - 코드값 중복 체크");
			actionCodeValChk.setActionTp(codeLink);
			actionCodeValChk.setResTp(codeResAction);
			actionCodeValChk.setUrl("/code/chk/*/*");
			actionCodeValChk.setMethod("PATCH");
			actionCodeValChk.setResDesc(actionCodeValChk.getActionNm());
			actionRepository.save(actionCodeValChk);
		}
		resourcess.add(actionCodeValChk);
		
	}
	
	
	/**
	 * 시스템 옵션 메뉴 등록
	 */
	private void initSystemOptMenu() {
		Menu menuOption = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "시스템 옵션", 2);
		if (null == menuOption) {
			menuOption = new Menu();
			menuOption.setTopMenu(menuSystem);
			menuOption.setMenuNm("시스템 옵션");
			menuOption.setMenuIcon("icon-options");
			menuOption.setMlevel(2);
			menuOption.setOrd(subMenuOrd++);
			menuOption.setResTp(codeResMenu);
			menuOption.setUrl("/v/system/option/list");
			menuOption.setMethod("GET");
			menuOption.setResDesc(menuOption.getMenuNm());
			menuRepository.save(menuOption);
		}
		resourcess.add(menuOption);
		
		Action actionOptionAdd = actionRepository.findOneByActionId("OptionAdd");
		if (null == actionOptionAdd) {
			actionOptionAdd = new Action();
			actionOptionAdd.setActionId("OptionAdd");
			actionOptionAdd.setActionNm("버튼 - 시스템 옵션 등록");
			actionOptionAdd.setActionTp(codeBtn);
			actionOptionAdd.setResTp(codeResAction);
			actionOptionAdd.setUrl("/option");
			actionOptionAdd.setMethod("POST");
			actionOptionAdd.setResDesc(actionOptionAdd.getActionNm());
			actionRepository.save(actionOptionAdd);
		}
		resourcess.add(actionOptionAdd);
		
		Action actionOptionGet = actionRepository.findOneByActionId("OptionGet");
		if (null == actionOptionGet) {
			actionOptionGet = new Action();
			actionOptionGet.setActionId("OptionGet");
			actionOptionGet.setActionNm("버튼 - 시스템 옵션 정보 조회");
			actionOptionGet.setActionTp(codeLink);
			actionOptionGet.setResTp(codeResAction);
			actionOptionGet.setUrl("/option/*");
			actionOptionGet.setMethod("GET");
			actionOptionGet.setResDesc(actionOptionGet.getActionNm());
			actionRepository.save(actionOptionGet);
		}
		resourcess.add(actionOptionGet);
		
		Action actionOptionUp = actionRepository.findOneByActionId("OptionUp");
		if (null == actionOptionUp) {
			actionOptionUp = new Action();
			actionOptionUp.setActionId("OptionUp");
			actionOptionUp.setActionNm("버튼 - 시스템 옵션 수정");
			actionOptionUp.setActionTp(codeBtn);
			actionOptionUp.setResTp(codeResAction);
			actionOptionUp.setUrl("/option/*");
			actionOptionUp.setMethod("PUT");
			actionOptionUp.setResDesc(actionOptionUp.getActionNm());
			actionRepository.save(actionOptionUp);
		}
		resourcess.add(actionOptionUp);
		
		Action actionOptionDel = actionRepository.findOneByActionId("OptionDel");
		if (null == actionOptionDel) {
			actionOptionDel = new Action();
			actionOptionDel.setActionId("OptionDel");
			actionOptionDel.setActionNm("버튼 - 시스템 옵션 삭제");
			actionOptionDel.setActionTp(codeBtn);
			actionOptionDel.setResTp(codeResAction);
			actionOptionDel.setUrl("/option/*");
			actionOptionDel.setMethod("DELETE");
			actionOptionDel.setResDesc(actionOptionDel.getActionNm());
			actionRepository.save(actionOptionDel);
		}
		resourcess.add(actionOptionDel);
		
		Action actionOptionSearch = actionRepository.findOneByActionId("OptionSearch");
		if (null == actionOptionSearch) {
			actionOptionSearch = new Action();
			actionOptionSearch.setActionId("OptionSearch");
			actionOptionSearch.setActionNm("버튼 - 시스템 옵션 리스트 조회");
			actionOptionSearch.setActionTp(codeBtn);
			actionOptionSearch.setResTp(codeResAction);
			actionOptionSearch.setUrl("/option");
			actionOptionSearch.setMethod("GET");
			actionOptionSearch.setResDesc(actionOptionSearch.getActionNm());
			actionRepository.save(actionOptionSearch);
		}
		resourcess.add(actionOptionSearch);
		
		Action actionOptionIdChk = actionRepository.findOneByActionId("OptionIdChk");
		if (null == actionOptionIdChk) {
			actionOptionIdChk = new Action();
			actionOptionIdChk.setActionId("OptionIdChk");
			actionOptionIdChk.setActionNm("링크 - 시스템 옵션값 중복 체크");
			actionOptionIdChk.setActionTp(codeLink);
			actionOptionIdChk.setResTp(codeResAction);
			actionOptionIdChk.setUrl("/option/chk/*");
			actionOptionIdChk.setMethod("PATCH");
			actionOptionIdChk.setResDesc(actionOptionIdChk.getActionNm());
			actionRepository.save(actionOptionIdChk);
		}
		resourcess.add(actionOptionIdChk);
		
	}
	
	
	/**
	 * 작업이력 조회 메뉴 등록
	 */
	private void initHistoryMenu() {
		Menu menuHistory = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuSystem, "작업이력 조회", 2);
		if (null == menuHistory) {
			menuHistory = new Menu();
			menuHistory.setTopMenu(menuSystem);
			menuHistory.setMenuNm("작업이력 조회");
			menuHistory.setMenuIcon("icon-history");
			menuHistory.setMlevel(2);
			menuHistory.setOrd(subMenuOrd++);
			menuHistory.setResTp(codeResMenu);
			menuHistory.setUrl("/v/system/history/list");
			menuHistory.setMethod("GET");
			menuHistory.setResDesc(menuHistory.getMenuNm());
			menuRepository.save(menuHistory);
		}
		resourcess.add(menuHistory);
		resourcessForMng.add(menuHistory);
		
		Action actionHistorySearch = actionRepository.findOneByActionId("HistorySearch");
		if (null == actionHistorySearch) {
			actionHistorySearch = new Action();
			actionHistorySearch.setActionId("HistorySearch");
			actionHistorySearch.setActionNm("버튼 - 시스템 작업이력 리스트 조회");
			actionHistorySearch.setActionTp(codeBtn);
			actionHistorySearch.setResTp(codeResAction);
			actionHistorySearch.setUrl("/history");
			actionHistorySearch.setMethod("GET");
			actionHistorySearch.setResDesc(actionHistorySearch.getActionNm());
			actionRepository.save(actionHistorySearch);
		}
		resourcess.add(actionHistorySearch);
		resourcessForMng.add(actionHistorySearch);
		
	}
	
	
	/**
	 * 언어 관리 메뉴 등록
	 */
	private void initLanMenu() {
		Menu menuLan = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuLanMng, "언어 관리", 2);
		if (null == menuLan) {
			menuLan = new Menu();
			menuLan.setTopMenu(menuLanMng);
			menuLan.setMenuNm("언어 관리");
			menuLan.setMenuIcon("icon-lan");
			menuLan.setMlevel(2);
			menuLan.setOrd(subMenuOrd++);
			menuLan.setResTp(codeResMenu);
			menuLan.setUrl("/v/system/lan/list");
			menuLan.setMethod("GET");
			menuLan.setResDesc(menuLan.getMenuNm());
			menuRepository.save(menuLan);
		}
		resourcess.add(menuLan);
		resourcessForMng.add(menuLan);
		
		Action actionLanAdd = actionRepository.findOneByActionId("LanAdd");
		if (null == actionLanAdd) {
			actionLanAdd = new Action();
			actionLanAdd.setActionId("LanAdd");
			actionLanAdd.setActionNm("버튼 - 언어 등록");
			actionLanAdd.setActionTp(codeBtn);
			actionLanAdd.setResTp(codeResAction);
			actionLanAdd.setUrl("/lan");
			actionLanAdd.setMethod("POST");
			actionLanAdd.setResDesc(actionLanAdd.getActionNm());
			actionRepository.save(actionLanAdd);
		}
		resourcess.add(actionLanAdd);
		resourcessForMng.add(actionLanAdd);
		
		Action actionLanGet = actionRepository.findOneByActionId("LanGet");
		if (null == actionLanGet) {
			actionLanGet = new Action();
			actionLanGet.setActionId("LanGet");
			actionLanGet.setActionNm("버튼 - 언어 정보 조회");
			actionLanGet.setActionTp(codeLink);
			actionLanGet.setResTp(codeResAction);
			actionLanGet.setUrl("/lan/*");
			actionLanGet.setMethod("GET");
			actionLanGet.setResDesc(actionLanGet.getActionNm());
			actionRepository.save(actionLanGet);
		}
		resourcess.add(actionLanGet);
		resourcessForMng.add(actionLanGet);
		
		Action actionLanUp = actionRepository.findOneByActionId("LanUp");
		if (null == actionLanUp) {
			actionLanUp = new Action();
			actionLanUp.setActionId("LanUp");
			actionLanUp.setActionNm("버튼 - 언어 수정");
			actionLanUp.setActionTp(codeBtn);
			actionLanUp.setResTp(codeResAction);
			actionLanUp.setUrl("/lan/*");
			actionLanUp.setMethod("PUT");
			actionLanUp.setResDesc(actionLanUp.getActionNm());
			actionRepository.save(actionLanUp);
		}
		resourcess.add(actionLanUp);
		resourcessForMng.add(actionLanUp);
		
		Action actionLanDel = actionRepository.findOneByActionId("LanDel");
		if (null == actionLanDel) {
			actionLanDel = new Action();
			actionLanDel.setActionId("LanDel");
			actionLanDel.setActionNm("버튼 - 언어 삭제");
			actionLanDel.setActionTp(codeBtn);
			actionLanDel.setResTp(codeResAction);
			actionLanDel.setUrl("/lan/*");
			actionLanDel.setMethod("DELETE");
			actionLanDel.setResDesc(actionLanDel.getActionNm());
			actionRepository.save(actionLanDel);
		}
		resourcess.add(actionLanDel);
		resourcessForMng.add(actionLanDel);
		
		Action actionLanSearch = actionRepository.findOneByActionId("LanSearch");
		if (null == actionLanSearch) {
			actionLanSearch = new Action();
			actionLanSearch.setActionId("LanSearch");
			actionLanSearch.setActionNm("버튼 - 언어 리스트 조회");
			actionLanSearch.setActionTp(codeBtn);
			actionLanSearch.setResTp(codeResAction);
			actionLanSearch.setUrl("/lan");
			actionLanSearch.setMethod("GET");
			actionLanSearch.setResDesc(actionLanSearch.getActionNm());
			actionRepository.save(actionLanSearch);
		}
		resourcess.add(actionLanSearch);
		resourcessForMng.add(actionLanSearch);
		
		Action actionLanIdChk = actionRepository.findOneByActionId("LanIdChk");
		if (null == actionLanIdChk) {
			actionLanIdChk = new Action();
			actionLanIdChk.setActionId("LanIdChk");
			actionLanIdChk.setActionNm("링크 - 언어 코드 중복 체크");
			actionLanIdChk.setActionTp(codeLink);
			actionLanIdChk.setResTp(codeResAction);
			actionLanIdChk.setUrl("/lan/chk/*");
			actionLanIdChk.setMethod("PATCH");
			actionLanIdChk.setResDesc(actionLanIdChk.getActionNm());
			actionRepository.save(actionLanIdChk);
		}
		resourcess.add(actionLanIdChk);
		resourcessForMng.add(actionLanIdChk);
		
		
		
		Menu menuLanData = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuLanMng, "키워드 관리", 2);
		if (null == menuLanData) {
			menuLanData = new Menu();
			menuLanData.setTopMenu(menuLanMng);
			menuLanData.setMenuNm("키워드 관리");
			menuLanData.setMenuIcon("icon-text");
			menuLanData.setMlevel(2);
			menuLanData.setOrd(subMenuOrd++);
			menuLanData.setResTp(codeResMenu);
			menuLanData.setUrl("/v/system/lanData/list");
			menuLanData.setMethod("GET");
			menuLanData.setResDesc(menuLanData.getMenuNm());
			menuRepository.save(menuLanData);
		}
		resourcess.add(menuLanData);
		resourcessForMng.add(menuLanData);
		
		Action actionLanDataAdd = actionRepository.findOneByActionId("LanDataAdd");
		if (null == actionLanDataAdd) {
			actionLanDataAdd = new Action();
			actionLanDataAdd.setActionId("LanDataAdd");
			actionLanDataAdd.setActionNm("버튼 - 키워드 등록");
			actionLanDataAdd.setActionTp(codeBtn);
			actionLanDataAdd.setResTp(codeResAction);
			actionLanDataAdd.setUrl("/lanData");
			actionLanDataAdd.setMethod("POST");
			actionLanDataAdd.setResDesc(actionLanDataAdd.getActionNm());
			actionRepository.save(actionLanDataAdd);
		}
		resourcess.add(actionLanDataAdd);
		resourcessForMng.add(actionLanDataAdd);
		
		Action actionLanDataGet = actionRepository.findOneByActionId("LanDataGet");
		if (null == actionLanDataGet) {
			actionLanDataGet = new Action();
			actionLanDataGet.setActionId("LanDataGet");
			actionLanDataGet.setActionNm("버튼 - 키워드 정보 조회");
			actionLanDataGet.setActionTp(codeLink);
			actionLanDataGet.setResTp(codeResAction);
			actionLanDataGet.setUrl("/lanData/*");
			actionLanDataGet.setMethod("GET");
			actionLanDataGet.setResDesc(actionLanDataGet.getActionNm());
			actionRepository.save(actionLanDataGet);
		}
		resourcess.add(actionLanDataGet);
		resourcessForMng.add(actionLanDataGet);
		
		Action actionLanDataUp = actionRepository.findOneByActionId("LanDataUp");
		if (null == actionLanDataUp) {
			actionLanDataUp = new Action();
			actionLanDataUp.setActionId("LanDataUp");
			actionLanDataUp.setActionNm("버튼 - 키워드 수정");
			actionLanDataUp.setActionTp(codeBtn);
			actionLanDataUp.setResTp(codeResAction);
			actionLanDataUp.setUrl("/lanData/*");
			actionLanDataUp.setMethod("PUT");
			actionLanDataUp.setResDesc(actionLanDataUp.getActionNm());
			actionRepository.save(actionLanDataUp);
		}
		resourcess.add(actionLanDataUp);
		resourcessForMng.add(actionLanDataUp);
		
		Action actionLanDataSearch = actionRepository.findOneByActionId("LanDataSearch");
		if (null == actionLanDataSearch) {
			actionLanDataSearch = new Action();
			actionLanDataSearch.setActionId("LanDataSearch");
			actionLanDataSearch.setActionNm("버튼 - 키워드 리스트 조회");
			actionLanDataSearch.setActionTp(codeBtn);
			actionLanDataSearch.setResTp(codeResAction);
			actionLanDataSearch.setUrl("/lanData");
			actionLanDataSearch.setMethod("GET");
			actionLanDataSearch.setResDesc(actionLanDataSearch.getActionNm());
			actionRepository.save(actionLanDataSearch);
		}
		resourcess.add(actionLanDataSearch);
		resourcessForMng.add(actionLanDataSearch);
		
		Action actionLanDataDown = actionRepository.findOneByActionId("LanDataDown");
		if (null == actionLanDataDown) {
			actionLanDataDown = new Action();
			actionLanDataDown.setActionId("LanDataDown");
			actionLanDataDown.setActionNm("버튼 - 키워드 엑셀로 다운로드");
			actionLanDataDown.setActionTp(codeBtn);
			actionLanDataDown.setResTp(codeResAction);
			actionLanDataDown.setUrl("/lanData/down");
			actionLanDataDown.setMethod("GET");
			actionLanDataDown.setResDesc(actionLanDataDown.getActionNm());
			actionRepository.save(actionLanDataDown);
		}
		resourcess.add(actionLanDataDown);
		resourcessForMng.add(actionLanDataDown);
		
		Action actionLanDataUpload = actionRepository.findOneByActionId("LanDataUpload");
		if (null == actionLanDataUpload) {
			actionLanDataUpload = new Action();
			actionLanDataUpload.setActionId("LanDataUpload");
			actionLanDataUpload.setActionNm("버튼 - 키워드 엑셀로 업로드");
			actionLanDataUpload.setActionTp(codeBtn);
			actionLanDataUpload.setResTp(codeResAction);
			actionLanDataUpload.setUrl("/lanData/up");
			actionLanDataUpload.setMethod("POST");
			actionLanDataUpload.setResDesc(actionLanDataUpload.getActionNm());
			actionRepository.save(actionLanDataUpload);
		}
		resourcess.add(actionLanDataUpload);
		resourcessForMng.add(actionLanDataUpload);
		
		Action actionLanDataIdChk = actionRepository.findOneByActionId("LanDataIdChk");
		if (null == actionLanDataIdChk) {
			actionLanDataIdChk = new Action();
			actionLanDataIdChk.setActionId("LanDataIdChk");
			actionLanDataIdChk.setActionNm("링크 - 키워드 중복 체크");
			actionLanDataIdChk.setActionTp(codeLink);
			actionLanDataIdChk.setResTp(codeResAction);
			actionLanDataIdChk.setUrl("/lanData/chk/*");
			actionLanDataIdChk.setMethod("PATCH");
			actionLanDataIdChk.setResDesc(actionLanDataIdChk.getActionNm());
			actionRepository.save(actionLanDataIdChk);
		}
		resourcess.add(actionLanDataIdChk);
		resourcessForMng.add(actionLanDataIdChk);
		
		
		Menu menuDictionary = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuLanMng, "다국어 사전 관리", 2);
		if (null == menuDictionary) {
			menuDictionary = new Menu();
			menuDictionary.setTopMenu(menuLanMng);
			menuDictionary.setMenuNm("다국어 사전 관리");
			menuDictionary.setMenuIcon("icon-text");
			menuDictionary.setMlevel(2);
			menuDictionary.setOrd(subMenuOrd++);
			menuDictionary.setResTp(codeResMenu);
			menuDictionary.setUrl("/v/system/dictionary/list");
			menuDictionary.setMethod("GET");
			menuDictionary.setResDesc(menuDictionary.getMenuNm());
			menuRepository.save(menuDictionary);
		}
		resourcess.add(menuDictionary);
		resourcessForMng.add(menuDictionary);
		
		Action actionDictionaryAdd = actionRepository.findOneByActionId("DictionaryAdd");
		if (null == actionDictionaryAdd) {
			actionDictionaryAdd = new Action();
			actionDictionaryAdd.setActionId("DictionaryAdd");
			actionDictionaryAdd.setActionNm("버튼 - 사전 등록");
			actionDictionaryAdd.setActionTp(codeBtn);
			actionDictionaryAdd.setResTp(codeResAction);
			actionDictionaryAdd.setUrl("/dictionary");
			actionDictionaryAdd.setMethod("POST");
			actionDictionaryAdd.setResDesc(actionDictionaryAdd.getActionNm());
			actionRepository.save(actionDictionaryAdd);
		}
		resourcess.add(actionDictionaryAdd);
		resourcessForMng.add(actionDictionaryAdd);
		
		Action actionDictionaryGet = actionRepository.findOneByActionId("DictionaryGet");
		if (null == actionDictionaryGet) {
			actionDictionaryGet = new Action();
			actionDictionaryGet.setActionId("DictionaryGet");
			actionDictionaryGet.setActionNm("버튼 - 사전 정보 조회");
			actionDictionaryGet.setActionTp(codeLink);
			actionDictionaryGet.setResTp(codeResAction);
			actionDictionaryGet.setUrl("/dictionary/*");
			actionDictionaryGet.setMethod("GET");
			actionDictionaryGet.setResDesc(actionDictionaryGet.getActionNm());
			actionRepository.save(actionDictionaryGet);
		}
		resourcess.add(actionDictionaryGet);
		resourcessForMng.add(actionDictionaryGet);
		
		Action actionDictionaryUp = actionRepository.findOneByActionId("DictionaryUp");
		if (null == actionDictionaryUp) {
			actionDictionaryUp = new Action();
			actionDictionaryUp.setActionId("DictionaryUp");
			actionDictionaryUp.setActionNm("버튼 - 사전 수정");
			actionDictionaryUp.setActionTp(codeBtn);
			actionDictionaryUp.setResTp(codeResAction);
			actionDictionaryUp.setUrl("/dictionary/*");
			actionDictionaryUp.setMethod("PUT");
			actionDictionaryUp.setResDesc(actionDictionaryUp.getActionNm());
			actionRepository.save(actionDictionaryUp);
		}
		resourcess.add(actionDictionaryUp);
		resourcessForMng.add(actionDictionaryUp);
		
		Action actionDictionarySearch = actionRepository.findOneByActionId("DictionarySearch");
		if (null == actionDictionarySearch) {
			actionDictionarySearch = new Action();
			actionDictionarySearch.setActionId("DictionarySearch");
			actionDictionarySearch.setActionNm("버튼 - 사전 리스트 조회");
			actionDictionarySearch.setActionTp(codeBtn);
			actionDictionarySearch.setResTp(codeResAction);
			actionDictionarySearch.setUrl("/dictionary");
			actionDictionarySearch.setMethod("GET");
			actionDictionarySearch.setResDesc(actionDictionarySearch.getActionNm());
			actionRepository.save(actionDictionarySearch);
		}
		resourcess.add(actionDictionarySearch);
		resourcessForMng.add(actionDictionarySearch);
		
		Action actionDictionaryDown = actionRepository.findOneByActionId("DictionaryDown");
		if (null == actionDictionaryDown) {
			actionDictionaryDown = new Action();
			actionDictionaryDown.setActionId("DictionaryDown");
			actionDictionaryDown.setActionNm("버튼 - 사전 엑셀로 다운로드");
			actionDictionaryDown.setActionTp(codeBtn);
			actionDictionaryDown.setResTp(codeResAction);
			actionDictionaryDown.setUrl("/dictionary/down");
			actionDictionaryDown.setMethod("GET");
			actionDictionaryDown.setResDesc(actionDictionaryDown.getActionNm());
			actionRepository.save(actionDictionaryDown);
		}
		resourcess.add(actionDictionaryDown);
		resourcessForMng.add(actionDictionaryDown);
		
		Action actionDictionaryUpload = actionRepository.findOneByActionId("DictionaryUpload");
		if (null == actionDictionaryUpload) {
			actionDictionaryUpload = new Action();
			actionDictionaryUpload.setActionId("DictionaryUpload");
			actionDictionaryUpload.setActionNm("버튼 - 사전 엑셀로 업로드");
			actionDictionaryUpload.setActionTp(codeBtn);
			actionDictionaryUpload.setResTp(codeResAction);
			actionDictionaryUpload.setUrl("/dictionary/up");
			actionDictionaryUpload.setMethod("POST");
			actionDictionaryUpload.setResDesc(actionDictionaryUpload.getActionNm());
			actionRepository.save(actionDictionaryUpload);
		}
		resourcess.add(actionDictionaryUpload);
		resourcessForMng.add(actionDictionaryUpload);
		
		Action actionDictionaryIdChk = actionRepository.findOneByActionId("DictionaryIdChk");
		if (null == actionDictionaryIdChk) {
			actionDictionaryIdChk = new Action();
			actionDictionaryIdChk.setActionId("DictionaryIdChk");
			actionDictionaryIdChk.setActionNm("링크 - 사전 중복 체크");
			actionDictionaryIdChk.setActionTp(codeLink);
			actionDictionaryIdChk.setResTp(codeResAction);
			actionDictionaryIdChk.setUrl("/dictionary/chk/*");
			actionDictionaryIdChk.setMethod("PATCH");
			actionDictionaryIdChk.setResDesc(actionDictionaryIdChk.getActionNm());
			actionRepository.save(actionDictionaryIdChk);
		}
		resourcess.add(actionDictionaryIdChk);
		resourcessForMng.add(actionDictionaryIdChk);
		
		Action actionDictionaryPopSearch = actionRepository.findOneByActionId("DictionaryPopSearch");
		if (null == actionDictionaryPopSearch) {
			actionDictionaryPopSearch = new Action();
			actionDictionaryPopSearch.setActionId("DictionaryPopSearch");
			actionDictionaryPopSearch.setActionNm("버튼 - 사전 팝업 조회");
			actionDictionaryPopSearch.setActionTp(codeBtn);
			actionDictionaryPopSearch.setResTp(codeResAction);
			actionDictionaryPopSearch.setUrl("/dictionary/search");
			actionDictionaryPopSearch.setMethod("GET");
			actionDictionaryPopSearch.setResDesc(actionDictionaryPopSearch.getActionNm());
			actionRepository.save(actionDictionaryPopSearch);
		}
		resourcess.add(actionDictionaryPopSearch);
		resourcessForMng.add(actionDictionaryPopSearch);
		resourcessForCust.add(actionDictionaryPopSearch);
		resourcessForStore.add(actionDictionaryPopSearch);
	}
	
	
	
	/**
	 * 약관 관리 메뉴 등록
	 */
	private void initTermsMenu() {
		Menu menuTerms = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "약관 관리", 2);
		if (null == menuTerms) {
			menuTerms = new Menu();
			menuTerms.setTopMenu(menuCommonMng);
			menuTerms.setMenuNm("약관 관리");
			menuTerms.setMenuIcon("icon-terms");
			menuTerms.setMlevel(2);
			menuTerms.setOrd(subMenuOrd++);
			menuTerms.setResTp(codeResMenu);
			menuTerms.setUrl("/v/system/terms/list");
			menuTerms.setMethod("GET");
			menuTerms.setResDesc(menuTerms.getMenuNm());
			menuRepository.save(menuTerms);
		}
		resourcess.add(menuTerms);
		resourcessForMng.add(menuTerms);
				
		Action actionTermsAdd = actionRepository.findOneByActionId("TermsAdd");
		if (null == actionTermsAdd) {
			actionTermsAdd = new Action();
			actionTermsAdd.setActionId("TermsAdd");
			actionTermsAdd.setActionNm("버튼 - 약관 정보 등록");
			actionTermsAdd.setActionTp(codeBtn);
			actionTermsAdd.setResTp(codeResAction);
			actionTermsAdd.setUrl("/terms");
			actionTermsAdd.setMethod("POST");
			actionTermsAdd.setResDesc(actionTermsAdd.getActionNm());
			actionRepository.save(actionTermsAdd);
		}
		resourcess.add(actionTermsAdd);
		resourcessForMng.add(actionTermsAdd);
		
		Action actionTermsGet = actionRepository.findOneByActionId("TermsGet");
		if (null == actionTermsGet) {
			actionTermsGet = new Action();
			actionTermsGet.setActionId("TermsGet");
			actionTermsGet.setActionNm("버튼 - 약관 정보 조회");
			actionTermsGet.setActionTp(codeLink);
			actionTermsGet.setResTp(codeResAction);
			actionTermsGet.setUrl("/terms/*");
			actionTermsGet.setMethod("GET");
			actionTermsGet.setResDesc(actionTermsGet.getActionNm());
			actionRepository.save(actionTermsGet);
		}
		resourcess.add(actionTermsGet);
		resourcessForMng.add(actionTermsGet);
		
		Action actionTermsUp = actionRepository.findOneByActionId("TermsUp");
		if (null == actionTermsUp) {
			actionTermsUp = new Action();
			actionTermsUp.setActionId("TermsUp");
			actionTermsUp.setActionNm("버튼 - 약관 정보 수정");
			actionTermsUp.setActionTp(codeBtn);
			actionTermsUp.setResTp(codeResAction);
			actionTermsUp.setUrl("/terms/*");
			actionTermsUp.setMethod("PUT");
			actionTermsUp.setResDesc(actionTermsUp.getActionNm());
			actionRepository.save(actionTermsUp);
		}
		resourcess.add(actionTermsUp);
		resourcessForMng.add(actionTermsUp);
		
		Action actionTermsDel = actionRepository.findOneByActionId("TermsDel");
		if (null == actionTermsDel) {
			actionTermsDel = new Action();
			actionTermsDel.setActionId("TermsDel");
			actionTermsDel.setActionNm("버튼 - 약관 정보 삭제");
			actionTermsDel.setActionTp(codeBtn);
			actionTermsDel.setResTp(codeResAction);
			actionTermsDel.setUrl("/terms/*");
			actionTermsDel.setMethod("DELETE");
			actionTermsDel.setResDesc(actionTermsDel.getActionNm());
			actionRepository.save(actionTermsDel);
		}
		resourcess.add(actionTermsDel);
		resourcessForMng.add(actionTermsDel);
		
		Action actionTermsSearch = actionRepository.findOneByActionId("TermsSearch");
		if (null == actionTermsSearch) {
			actionTermsSearch = new Action();
			actionTermsSearch.setActionId("TermsSearch");
			actionTermsSearch.setActionNm("버튼 - 약관 정보 리스트 조회");
			actionTermsSearch.setActionTp(codeBtn);
			actionTermsSearch.setResTp(codeResAction);
			actionTermsSearch.setUrl("/terms");
			actionTermsSearch.setMethod("GET");
			actionTermsSearch.setResDesc(actionTermsSearch.getActionNm());
			actionRepository.save(actionTermsSearch);
		}
		resourcess.add(actionTermsSearch);
		resourcessForMng.add(actionTermsSearch);
		
		Action actionTermsForLanChk = actionRepository.findOneByActionId("TermsForLanChk");
		if (null == actionTermsForLanChk) {
			actionTermsForLanChk = new Action();
			actionTermsForLanChk.setActionId("TermsForLanChk");
			actionTermsForLanChk.setActionNm("링크 - 언어별 약관 등록 여부 체크");
			actionTermsForLanChk.setActionTp(codeLink);
			actionTermsForLanChk.setResTp(codeResAction);
			actionTermsForLanChk.setUrl("/terms/chk/*/*");
			actionTermsForLanChk.setMethod("PATCH");
			actionTermsForLanChk.setResDesc(actionTermsForLanChk.getActionNm());
			actionRepository.save(actionTermsForLanChk);
		}
		resourcess.add(actionTermsForLanChk);
		resourcessForMng.add(actionTermsForLanChk);
		
		Action actionTermsAcceptSearch = actionRepository.findOneByActionId("TermsAcceptSearch");
		if (null == actionTermsAcceptSearch) {
			actionTermsAcceptSearch = new Action();
			actionTermsAcceptSearch.setActionId("TermsAcceptSearch");
			actionTermsAcceptSearch.setActionNm("버튼 - 약관 동의 정보 조회");
			actionTermsAcceptSearch.setActionTp(codeBtn);
			actionTermsAcceptSearch.setResTp(codeResAction);
			actionTermsAcceptSearch.setUrl("/terms/accept");
			actionTermsAcceptSearch.setMethod("GET");
			actionTermsAcceptSearch.setResDesc(actionTermsAcceptSearch.getActionNm());
			actionRepository.save(actionTermsAcceptSearch);
		}
		resourcess.add(actionTermsAcceptSearch);
		resourcessForMng.add(actionTermsAcceptSearch);
	}
	
	
	/**
	 * 공지사항 관리 메뉴 등록
	 */
	private void initNotiMenu() {
		Menu menuNoti = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "공지사항", 2);
		if (null == menuNoti) {
			menuNoti = new Menu();
			menuNoti.setTopMenu(menuCommonMng);
			menuNoti.setMenuNm("공지사항");
			menuNoti.setMenuIcon("icon-noti");
			menuNoti.setMlevel(2);
			menuNoti.setOrd(subMenuOrd++);
			menuNoti.setResTp(codeResMenu);
			menuNoti.setUrl("/v/system/noti/list");
			menuNoti.setMethod("GET");
			menuNoti.setResDesc(menuNoti.getMenuNm());
			menuRepository.save(menuNoti);
		}
		resourcess.add(menuNoti);
		resourcessForMng.add(menuNoti);
				
		Action actionNotiAdd = actionRepository.findOneByActionId("NotiAdd");
		if (null == actionNotiAdd) {
			actionNotiAdd = new Action();
			actionNotiAdd.setActionId("NotiAdd");
			actionNotiAdd.setActionNm("버튼 - 공지사항 등록");
			actionNotiAdd.setActionTp(codeBtn);
			actionNotiAdd.setResTp(codeResAction);
			actionNotiAdd.setUrl("/noti");
			actionNotiAdd.setMethod("POST");
			actionNotiAdd.setResDesc(actionNotiAdd.getActionNm());
			actionRepository.save(actionNotiAdd);
		}
		resourcess.add(actionNotiAdd);
		resourcessForMng.add(actionNotiAdd);
		
		Action actionNotiGet = actionRepository.findOneByActionId("NotiGet");
		if (null == actionNotiGet) {
			actionNotiGet = new Action();
			actionNotiGet.setActionId("NotiGet");
			actionNotiGet.setActionNm("버튼 - 공지사항 정보 조회");
			actionNotiGet.setActionTp(codeLink);
			actionNotiGet.setResTp(codeResAction);
			actionNotiGet.setUrl("/noti/*");
			actionNotiGet.setMethod("GET");
			actionNotiGet.setResDesc(actionNotiGet.getActionNm());
			actionRepository.save(actionNotiGet);
		}
		resourcess.add(actionNotiGet);
		resourcessForMng.add(actionNotiGet);
		
		Action actionNotiUp = actionRepository.findOneByActionId("NotiUp");
		if (null == actionNotiUp) {
			actionNotiUp = new Action();
			actionNotiUp.setActionId("NotiUp");
			actionNotiUp.setActionNm("버튼 - 공지사항 수정");
			actionNotiUp.setActionTp(codeBtn);
			actionNotiUp.setResTp(codeResAction);
			actionNotiUp.setUrl("/noti/*");
			actionNotiUp.setMethod("PUT");
			actionNotiUp.setResDesc(actionNotiUp.getActionNm());
			actionRepository.save(actionNotiUp);
		}
		resourcess.add(actionNotiUp);
		resourcessForMng.add(actionNotiUp);
		
		Action actionNotiDel = actionRepository.findOneByActionId("NotiDel");
		if (null == actionNotiDel) {
			actionNotiDel = new Action();
			actionNotiDel.setActionId("NotiDel");
			actionNotiDel.setActionNm("버튼 - 공지사항 삭제");
			actionNotiDel.setActionTp(codeBtn);
			actionNotiDel.setResTp(codeResAction);
			actionNotiDel.setUrl("/noti/*");
			actionNotiDel.setMethod("DELETE");
			actionNotiDel.setResDesc(actionNotiDel.getActionNm());
			actionRepository.save(actionNotiDel);
		}
		resourcess.add(actionNotiDel);
		resourcessForMng.add(actionNotiDel);
		
		Action actionNotiSearch = actionRepository.findOneByActionId("NotiSearch");
		if (null == actionNotiSearch) {
			actionNotiSearch = new Action();
			actionNotiSearch.setActionId("NotiSearch");
			actionNotiSearch.setActionNm("버튼 - 공지사항 리스트 조회");
			actionNotiSearch.setActionTp(codeBtn);
			actionNotiSearch.setResTp(codeResAction);
			actionNotiSearch.setUrl("/noti");
			actionNotiSearch.setMethod("GET");
			actionNotiSearch.setResDesc(actionNotiSearch.getActionNm());
			actionRepository.save(actionNotiSearch);
		}
		resourcess.add(actionNotiSearch);
		resourcessForMng.add(actionNotiSearch);
		
		Action actionNotiGetShowList = actionRepository.findOneByActionId("NotiGetShowList");
		if (null == actionNotiGetShowList) {
			actionNotiGetShowList = new Action();
			actionNotiGetShowList.setActionId("NotiGetShowList");
			actionNotiGetShowList.setActionNm("링크 - 공지사항 리스트 조회(사업장, 매장)");
			actionNotiGetShowList.setActionTp(codeLink);
			actionNotiGetShowList.setResTp(codeResAction);
			actionNotiGetShowList.setUrl("/noti/showNotiList");
			actionNotiGetShowList.setMethod("GET");
			actionNotiGetShowList.setResDesc(actionNotiGetShowList.getActionNm());
			actionRepository.save(actionNotiGetShowList);
		}
		resourcess.add(actionNotiGetShowList);
		resourcessForMng.add(actionNotiGetShowList);
		resourcessForCust.add(actionNotiGetShowList);
		resourcessForStore.add(actionNotiGetShowList);
		
		Action actionNotiGetShowData = actionRepository.findOneByActionId("NotiGetShowData");
		if (null == actionNotiGetShowData) {
			actionNotiGetShowData = new Action();
			actionNotiGetShowData.setActionId("NotiGetShowData");
			actionNotiGetShowData.setActionNm("링크 - 공지사항 팝업 조회");
			actionNotiGetShowData.setActionTp(codeLink);
			actionNotiGetShowData.setResTp(codeResAction);
			actionNotiGetShowData.setUrl("/noti/showNotiData");
			actionNotiGetShowData.setMethod("GET");
			actionNotiGetShowData.setResDesc(actionNotiGetShowData.getActionNm());
			actionRepository.save(actionNotiGetShowData);
		}
		resourcess.add(actionNotiGetShowData);
		resourcessForMng.add(actionNotiGetShowData);
		resourcessForCust.add(actionNotiGetShowData);
		resourcessForStore.add(actionNotiGetShowData);
	}
	
	
	/**
	 * 앱 사용자 관리 메뉴 등록
	 */
	private void initAppUserMenu() {
		Menu menuAppUser = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "앱 회원 관리", 2);
		if (null == menuAppUser) {
			menuAppUser = new Menu();
			menuAppUser.setTopMenu(menuCommonMng);
			menuAppUser.setMenuNm("앱 회원 관리");
			menuAppUser.setMenuIcon("icon-people");
			menuAppUser.setMlevel(2);
			menuAppUser.setOrd(subMenuOrd++);
			menuAppUser.setResTp(codeResMenu);
			menuAppUser.setUrl("/v/business/appUser/list");
			menuAppUser.setMethod("GET");
			menuAppUser.setResDesc(menuAppUser.getMenuNm());
			menuRepository.save(menuAppUser);
		}
		resourcess.add(menuAppUser);
		resourcessForMng.add(menuAppUser);
		
		Action actionAppUserGet = actionRepository.findOneByActionId("AppUserGet");
		if (null == actionAppUserGet) {
			actionAppUserGet = new Action();
			actionAppUserGet.setActionId("AppUserGet");
			actionAppUserGet.setActionNm("버튼 - 앱 회원 정보 조회");
			actionAppUserGet.setActionTp(codeLink);
			actionAppUserGet.setResTp(codeResAction);
			actionAppUserGet.setUrl("/appUser/*");
			actionAppUserGet.setMethod("GET");
			actionAppUserGet.setResDesc(actionAppUserGet.getActionNm());
			actionRepository.save(actionAppUserGet);
		}
		resourcess.add(actionAppUserGet);
		resourcessForMng.add(actionAppUserGet);
		
		Action actionAppUserUp = actionRepository.findOneByActionId("AppUserUp");
		if (null == actionAppUserUp) {
			actionAppUserUp = new Action();
			actionAppUserUp.setActionId("AppUserUp");
			actionAppUserUp.setActionNm("버튼 - 앱 회원 수정");
			actionAppUserUp.setActionTp(codeBtn);
			actionAppUserUp.setResTp(codeResAction);
			actionAppUserUp.setUrl("/appUser/*");
			actionAppUserUp.setMethod("PUT");
			actionAppUserUp.setResDesc(actionAppUserUp.getActionNm());
			actionRepository.save(actionAppUserUp);
		}
		resourcess.add(actionAppUserUp);
		resourcessForMng.add(actionAppUserUp);
		
		Action actionAppUserDel = actionRepository.findOneByActionId("AppUserDel");
		if (null == actionAppUserDel) {
			actionAppUserDel = new Action();
			actionAppUserDel.setActionId("AppUserDel");
			actionAppUserDel.setActionNm("버튼 - 앱 회원 삭제");
			actionAppUserDel.setActionTp(codeBtn);
			actionAppUserDel.setResTp(codeResAction);
			actionAppUserDel.setUrl("/appUser/*");
			actionAppUserDel.setMethod("DELETE");
			actionAppUserDel.setResDesc(actionAppUserDel.getActionNm());
			actionRepository.save(actionAppUserDel);
		}
		resourcess.add(actionAppUserDel);
		resourcessForMng.add(actionAppUserDel);
		
		Action actionAppUserSearch = actionRepository.findOneByActionId("AppUserSearch");
		if (null == actionAppUserSearch) {
			actionAppUserSearch = new Action();
			actionAppUserSearch.setActionId("AppUserSearch");
			actionAppUserSearch.setActionNm("버튼 -앱 회원 리스트 조회");
			actionAppUserSearch.setActionTp(codeBtn);
			actionAppUserSearch.setResTp(codeResAction);
			actionAppUserSearch.setUrl("/appUser");
			actionAppUserSearch.setMethod("GET");
			actionAppUserSearch.setResDesc(actionAppUserSearch.getActionNm());
			actionRepository.save(actionAppUserSearch);
		}
		resourcess.add(actionAppUserSearch);
		resourcessForMng.add(actionAppUserSearch);
		
	}
	
	
	/**
	 * 배너 관리 메뉴 등록
	 */
	private void initBannerMenu() {
		Menu menuBanner = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "배너 관리", 2);
		if (null == menuBanner) {
			menuBanner = new Menu();
			menuBanner.setTopMenu(menuCommonMng);
			menuBanner.setMenuNm("배너 관리");
			menuBanner.setMenuIcon("icon-banner");
			menuBanner.setMlevel(2);
			menuBanner.setOrd(subMenuOrd++);
			menuBanner.setResTp(codeResMenu);
			menuBanner.setUrl("/v/system/banner/list");
			menuBanner.setMethod("GET");
			menuBanner.setResDesc(menuBanner.getMenuNm());
			menuRepository.save(menuBanner);
		}
		resourcess.add(menuBanner);
		resourcessForMng.add(menuBanner);
		
		Action actionBannerAdd = actionRepository.findOneByActionId("BannerAdd");
		if (null == actionBannerAdd) {
			actionBannerAdd = new Action();
			actionBannerAdd.setActionId("BannerAdd");
			actionBannerAdd.setActionNm("버튼 - 배너 등록");
			actionBannerAdd.setActionTp(codeBtn);
			actionBannerAdd.setResTp(codeResAction);
			actionBannerAdd.setUrl("/banner");
			actionBannerAdd.setMethod("POST");
			actionBannerAdd.setResDesc(actionBannerAdd.getActionNm());
			actionRepository.save(actionBannerAdd);
		}
		resourcess.add(actionBannerAdd);
		resourcessForMng.add(actionBannerAdd);
		
		Action actionBannerGet = actionRepository.findOneByActionId("BannerGet");
		if (null == actionBannerGet) {
			actionBannerGet = new Action();
			actionBannerGet.setActionId("BannerGet");
			actionBannerGet.setActionNm("버튼 - 배너 정보 조회");
			actionBannerGet.setActionTp(codeLink);
			actionBannerGet.setResTp(codeResAction);
			actionBannerGet.setUrl("/banner/*");
			actionBannerGet.setMethod("GET");
			actionBannerGet.setResDesc(actionBannerGet.getActionNm());
			actionRepository.save(actionBannerGet);
		}
		resourcess.add(actionBannerGet);
		resourcessForMng.add(actionBannerGet);
		
		Action actionBannerUp = actionRepository.findOneByActionId("BannerUp");
		if (null == actionBannerUp) {
			actionBannerUp = new Action();
			actionBannerUp.setActionId("BannerUp");
			actionBannerUp.setActionNm("버튼 - 배너 수정");
			actionBannerUp.setActionTp(codeBtn);
			actionBannerUp.setResTp(codeResAction);
			actionBannerUp.setUrl("/banner/*");
			actionBannerUp.setMethod("PUT");
			actionBannerUp.setResDesc(actionBannerUp.getActionNm());
			actionRepository.save(actionBannerUp);
		}
		resourcess.add(actionBannerUp);
		resourcessForMng.add(actionBannerUp);
		
		Action actionBannerDel = actionRepository.findOneByActionId("BannerDel");
		if (null == actionBannerDel) {
			actionBannerDel = new Action();
			actionBannerDel.setActionId("BannerDel");
			actionBannerDel.setActionNm("버튼 - 배너 삭제");
			actionBannerDel.setActionTp(codeBtn);
			actionBannerDel.setResTp(codeResAction);
			actionBannerDel.setUrl("/banner/*");
			actionBannerDel.setMethod("DELETE");
			actionBannerDel.setResDesc(actionBannerDel.getActionNm());
			actionRepository.save(actionBannerDel);
		}
		resourcess.add(actionBannerDel);
		resourcessForMng.add(actionBannerDel);
		
		Action actionBannerSetting = actionRepository.findOneByActionId("BannerSetting");
		if (null == actionBannerSetting) {
			actionBannerSetting = new Action();
			actionBannerSetting.setActionId("BannerSetting");
			actionBannerSetting.setActionNm("버튼 - 배너 설정");
			actionBannerSetting.setActionTp(codeBtn);
			actionBannerSetting.setResTp(codeResAction);
			actionBannerSetting.setUrl("/banner/*/*");
			actionBannerSetting.setMethod("PATCH");
			actionBannerSetting.setResDesc(actionBannerSetting.getActionNm());
			actionRepository.save(actionBannerSetting);
		}
		resourcess.add(actionBannerSetting);
		resourcessForMng.add(actionBannerSetting);
		
		Action actionBannerSearch = actionRepository.findOneByActionId("BannerSearch");
		if (null == actionBannerSearch) {
			actionBannerSearch = new Action();
			actionBannerSearch.setActionId("BannerSearch");
			actionBannerSearch.setActionNm("버튼 - 배너 리스트 조회");
			actionBannerSearch.setActionTp(codeBtn);
			actionBannerSearch.setResTp(codeResAction);
			actionBannerSearch.setUrl("/banner");
			actionBannerSearch.setMethod("GET");
			actionBannerSearch.setResDesc(actionBannerSearch.getActionNm());
			actionRepository.save(actionBannerSearch);
		}
		resourcess.add(actionBannerSearch);
		resourcessForMng.add(actionBannerSearch);
		
		
	}
	
	
	/**
	 * 푸시 메뉴 등록
	 */
	private void initPushMenu() {
		Menu menuPush = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "푸시 관리", 2);
		if (null == menuPush) {
			menuPush = new Menu();
			menuPush.setTopMenu(menuCommonMng);
			menuPush.setMenuNm("푸시 관리");
			menuPush.setMenuIcon("icon-push");
			menuPush.setMlevel(2);
			menuPush.setOrd(subMenuOrd++);
			menuPush.setResTp(codeResMenu);
			menuPush.setUrl("/v/business/push/list");
			menuPush.setMethod("GET");
			menuPush.setResDesc(menuPush.getMenuNm());
			menuRepository.save(menuPush);
		}
		resourcess.add(menuPush);
		resourcessForMng.add(menuPush);
		
		Action actionPushAdd = actionRepository.findOneByActionId("PushAdd");
		if (null == actionPushAdd) {
			actionPushAdd = new Action();
			actionPushAdd.setActionId("PushAdd");
			actionPushAdd.setActionNm("버튼 - 푸시 등록");
			actionPushAdd.setActionTp(codeBtn);
			actionPushAdd.setResTp(codeResAction);
			actionPushAdd.setUrl("/push");
			actionPushAdd.setMethod("POST");
			actionPushAdd.setResDesc(actionPushAdd.getActionNm());
			actionRepository.save(actionPushAdd);
		}
		resourcess.add(actionPushAdd);
		resourcessForMng.add(actionPushAdd);
		
		Action actionPushGet = actionRepository.findOneByActionId("PushGet");
		if (null == actionPushGet) {
			actionPushGet = new Action();
			actionPushGet.setActionId("PushGet");
			actionPushGet.setActionNm("버튼 - 푸시 정보 조회");
			actionPushGet.setActionTp(codeLink);
			actionPushGet.setResTp(codeResAction);
			actionPushGet.setUrl("/push/*");
			actionPushGet.setMethod("GET");
			actionPushGet.setResDesc(actionPushGet.getActionNm());
			actionRepository.save(actionPushGet);
		}
		resourcess.add(actionPushGet);
		resourcessForMng.add(actionPushGet);
		
		Action actionPushSearch = actionRepository.findOneByActionId("PushSearch");
		if (null == actionPushSearch) {
			actionPushSearch = new Action();
			actionPushSearch.setActionId("PushSearch");
			actionPushSearch.setActionNm("버튼 - 푸시 리스트 조회");
			actionPushSearch.setActionTp(codeBtn);
			actionPushSearch.setResTp(codeResAction);
			actionPushSearch.setUrl("/push");
			actionPushSearch.setMethod("GET");
			actionPushSearch.setResDesc(actionPushSearch.getActionNm());
			actionRepository.save(actionPushSearch);
		}
		resourcess.add(actionPushSearch);
		resourcessForMng.add(actionPushSearch);
		
	}
	
	
	
	/**
	 * 알림 메뉴 등록
	 */
	private void initBizMenu() {
		Menu menuBiz = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "알림 관리", 2);
		if (null == menuBiz) {
			menuBiz = new Menu();
			menuBiz.setTopMenu(menuCommonMng);
			menuBiz.setMenuNm("알림 관리");
			menuBiz.setMenuIcon("icon-push");
			menuBiz.setMlevel(2);
			menuBiz.setOrd(subMenuOrd++);
			menuBiz.setResTp(codeResMenu);
			menuBiz.setUrl("/v/business/biz/list");
			menuBiz.setMethod("GET");
			menuBiz.setResDesc(menuBiz.getMenuNm());
			menuRepository.save(menuBiz);
		}
		resourcess.add(menuBiz);
		resourcessForMng.add(menuBiz);
		
		Action actionBizAdd = actionRepository.findOneByActionId("BizAdd");
		if (null == actionBizAdd) {
			actionBizAdd = new Action();
			actionBizAdd.setActionId("BizAdd");
			actionBizAdd.setActionNm("버튼 - 알림 등록");
			actionBizAdd.setActionTp(codeBtn);
			actionBizAdd.setResTp(codeResAction);
			actionBizAdd.setUrl("/biz");
			actionBizAdd.setMethod("POST");
			actionBizAdd.setResDesc(actionBizAdd.getActionNm());
			actionRepository.save(actionBizAdd);
		}
		resourcess.add(actionBizAdd);
		resourcessForMng.add(actionBizAdd);
		
		Action actionBizSearch = actionRepository.findOneByActionId("BizSearch");
		if (null == actionBizSearch) {
			actionBizSearch = new Action();
			actionBizSearch.setActionId("BizSearch");
			actionBizSearch.setActionNm("버튼 - 알림 리스트 조회");
			actionBizSearch.setActionTp(codeBtn);
			actionBizSearch.setResTp(codeResAction);
			actionBizSearch.setUrl("/biz");
			actionBizSearch.setMethod("GET");
			actionBizSearch.setResDesc(actionBizSearch.getActionNm());
			actionRepository.save(actionBizSearch);
		}
		resourcess.add(actionBizSearch);
		resourcessForMng.add(actionBizSearch);
		
	}
	
	
	/**
	 * 1:1 문의 메뉴 등록
	 */
	private void initInquiryMenu() {
		Menu menuInquiry = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "1:1 문의 관리", 2);
		if (null == menuInquiry) {
			menuInquiry = new Menu();
			menuInquiry.setTopMenu(menuCommonMng);
			menuInquiry.setMenuNm("1:1 문의 관리");
			menuInquiry.setMenuIcon("icon-inquiry");
			menuInquiry.setMlevel(2);
			menuInquiry.setOrd(subMenuOrd++);
			menuInquiry.setResTp(codeResMenu);
			menuInquiry.setUrl("/v/business/inquiry/list");
			menuInquiry.setMethod("GET");
			menuInquiry.setResDesc(menuInquiry.getMenuNm());
			menuRepository.save(menuInquiry);
		}
		resourcess.add(menuInquiry);
		resourcessForMng.add(menuInquiry);
		
		Action actionInquiryGet = actionRepository.findOneByActionId("InquiryGet");
		if (null == actionInquiryGet) {
			actionInquiryGet = new Action();
			actionInquiryGet.setActionId("InquiryGet");
			actionInquiryGet.setActionNm("버튼 - 1:1 문의 정보 조회");
			actionInquiryGet.setActionTp(codeLink);
			actionInquiryGet.setResTp(codeResAction);
			actionInquiryGet.setUrl("/inquiry/*");
			actionInquiryGet.setMethod("GET");
			actionInquiryGet.setResDesc(actionInquiryGet.getActionNm());
			actionRepository.save(actionInquiryGet);
		}
		resourcess.add(actionInquiryGet);
		resourcessForMng.add(actionInquiryGet);
		
		Action actionInquiryUp = actionRepository.findOneByActionId("InquiryUp");
		if (null == actionInquiryUp) {
			actionInquiryUp = new Action();
			actionInquiryUp.setActionId("InquiryUp");
			actionInquiryUp.setActionNm("버튼 - 1:1 문의 답변");
			actionInquiryUp.setActionTp(codeBtn);
			actionInquiryUp.setResTp(codeResAction);
			actionInquiryUp.setUrl("/inquiry/*");
			actionInquiryUp.setMethod("PUT");
			actionInquiryUp.setResDesc(actionInquiryUp.getActionNm());
			actionRepository.save(actionInquiryUp);
		}
		resourcess.add(actionInquiryUp);
		resourcessForMng.add(actionInquiryUp);
		
		Action actionInquirySearch = actionRepository.findOneByActionId("InquirySearch");
		if (null == actionInquirySearch) {
			actionInquirySearch = new Action();
			actionInquirySearch.setActionId("InquirySearch");
			actionInquirySearch.setActionNm("버튼 - 1:1 문의 리스트 조회");
			actionInquirySearch.setActionTp(codeBtn);
			actionInquirySearch.setResTp(codeResAction);
			actionInquirySearch.setUrl("/inquiry");
			actionInquirySearch.setMethod("GET");
			actionInquirySearch.setResDesc(actionInquirySearch.getActionNm());
			actionRepository.save(actionInquirySearch);
		}
		resourcess.add(actionInquirySearch);
		resourcessForMng.add(actionInquirySearch);
		
	}
	
	
	/**
	 * 이벤트 메뉴 등록
	 */
	private void initEventMenu() {
		Menu menuEvent = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCommonMng, "앱 이벤트 관리", 2);
		if (null == menuEvent) {
			menuEvent = new Menu();
			menuEvent.setTopMenu(menuCommonMng);
			menuEvent.setMenuNm("앱 이벤트 관리");
			menuEvent.setMenuIcon("icon-event");
			menuEvent.setMlevel(2);
			menuEvent.setOrd(subMenuOrd++);
			menuEvent.setResTp(codeResMenu);
			menuEvent.setUrl("/v/business/event/list");
			menuEvent.setMethod("GET");
			menuEvent.setResDesc(menuEvent.getMenuNm());
			menuRepository.save(menuEvent);
		}
		resourcess.add(menuEvent);
		resourcessForMng.add(menuEvent);
		
		Action actionEventAdd = actionRepository.findOneByActionId("EventAdd");
		if (null == actionEventAdd) {
			actionEventAdd = new Action();
			actionEventAdd.setActionId("EventAdd");
			actionEventAdd.setActionNm("버튼 - 앱 이벤트 등록");
			actionEventAdd.setActionTp(codeBtn);
			actionEventAdd.setResTp(codeResAction);
			actionEventAdd.setUrl("/event");
			actionEventAdd.setMethod("POST");
			actionEventAdd.setResDesc(actionEventAdd.getActionNm());
			actionRepository.save(actionEventAdd);
		}
		resourcess.add(actionEventAdd);
		resourcessForMng.add(actionEventAdd);
		
		Action actionEventGet = actionRepository.findOneByActionId("EventGet");
		if (null == actionEventGet) {
			actionEventGet = new Action();
			actionEventGet.setActionId("EventGet");
			actionEventGet.setActionNm("버튼 - 앱 이벤트 정보 조회");
			actionEventGet.setActionTp(codeLink);
			actionEventGet.setResTp(codeResAction);
			actionEventGet.setUrl("/event/*");
			actionEventGet.setMethod("GET");
			actionEventGet.setResDesc(actionEventGet.getActionNm());
			actionRepository.save(actionEventGet);
		}
		resourcess.add(actionEventGet);
		resourcessForMng.add(actionEventGet);
		
		Action actionEventUp = actionRepository.findOneByActionId("EventUp");
		if (null == actionEventUp) {
			actionEventUp = new Action();
			actionEventUp.setActionId("EventUp");
			actionEventUp.setActionNm("버튼 - 앱 이벤트 수정");
			actionEventUp.setActionTp(codeBtn);
			actionEventUp.setResTp(codeResAction);
			actionEventUp.setUrl("/event/*");
			actionEventUp.setMethod("PUT");
			actionEventUp.setResDesc(actionEventUp.getActionNm());
			actionRepository.save(actionEventUp);
		}
		resourcess.add(actionEventUp);
		resourcessForMng.add(actionEventUp);
		
		Action actionEventDel = actionRepository.findOneByActionId("EventDel");
		if (null == actionEventDel) {
			actionEventDel = new Action();
			actionEventDel.setActionId("EventDel");
			actionEventDel.setActionNm("버튼 - 앱 이벤트 삭제");
			actionEventDel.setActionTp(codeBtn);
			actionEventDel.setResTp(codeResAction);
			actionEventDel.setUrl("/event/*");
			actionEventDel.setMethod("DELETE");
			actionEventDel.setResDesc(actionEventDel.getActionNm());
			actionRepository.save(actionEventDel);
		}
		resourcess.add(actionEventDel);
		resourcessForMng.add(actionEventDel);
		
		Action actionEventSearch = actionRepository.findOneByActionId("EventSearch");
		if (null == actionEventSearch) {
			actionEventSearch = new Action();
			actionEventSearch.setActionId("EventSearch");
			actionEventSearch.setActionNm("버튼 - 앱 이벤트 리스트 조회");
			actionEventSearch.setActionTp(codeBtn);
			actionEventSearch.setResTp(codeResAction);
			actionEventSearch.setUrl("/event");
			actionEventSearch.setMethod("GET");
			actionEventSearch.setResDesc(actionEventSearch.getActionNm());
			actionRepository.save(actionEventSearch);
		}
		resourcess.add(actionEventSearch);
		resourcessForMng.add(actionEventSearch);
		
	}
	
	
	
	/**
	 * 사업장 관리 메뉴 등록
	 */
	private void initCustMenu() {
		Menu menuCust = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCustMng, "사업장 관리", 2);
		if (null == menuCust) {
			menuCust = new Menu();
			menuCust.setTopMenu(menuCustMng);
			menuCust.setMenuNm("사업장 관리");
			menuCust.setMenuIcon("icon-cust");
			menuCust.setMlevel(2);
			menuCust.setOrd(subMenuOrd++);
			menuCust.setResTp(codeResMenu);
			menuCust.setUrl("/v/business/cust/list");
			menuCust.setMethod("GET");
			menuCust.setResDesc(menuCust.getMenuNm());
			menuRepository.save(menuCust);
		}
		resourcess.add(menuCust);
		resourcessForMng.add(menuCust);
		
		Action actionCustAdd = actionRepository.findOneByActionId("CustAdd");
		if (null == actionCustAdd) {
			actionCustAdd = new Action();
			actionCustAdd.setActionId("CustAdd");
			actionCustAdd.setActionNm("버튼 - 사업장 등록");
			actionCustAdd.setActionTp(codeBtn);
			actionCustAdd.setResTp(codeResAction);
			actionCustAdd.setUrl("/cust");
			actionCustAdd.setMethod("POST");
			actionCustAdd.setResDesc(actionCustAdd.getActionNm());
			actionRepository.save(actionCustAdd);
		}
		resourcess.add(actionCustAdd);
		resourcessForMng.add(actionCustAdd);
		
		Action actionCustGet = actionRepository.findOneByActionId("CustGet");
		if (null == actionCustGet) {
			actionCustGet = new Action();
			actionCustGet.setActionId("CustGet");
			actionCustGet.setActionNm("버튼 - 사업장 정보 조회");
			actionCustGet.setActionTp(codeLink);
			actionCustGet.setResTp(codeResAction);
			actionCustGet.setUrl("/cust/*");
			actionCustGet.setMethod("GET");
			actionCustGet.setResDesc(actionCustGet.getActionNm());
			actionRepository.save(actionCustGet);
		}
		resourcess.add(actionCustGet);
		resourcessForMng.add(actionCustGet);
		
		Action actionCustUp = actionRepository.findOneByActionId("CustUp");
		if (null == actionCustUp) {
			actionCustUp = new Action();
			actionCustUp.setActionId("CustUp");
			actionCustUp.setActionNm("버튼 - 사업장 수정");
			actionCustUp.setActionTp(codeBtn);
			actionCustUp.setResTp(codeResAction);
			actionCustUp.setUrl("/cust/*");
			actionCustUp.setMethod("PUT");
			actionCustUp.setResDesc(actionCustUp.getActionNm());
			actionRepository.save(actionCustUp);
		}
		resourcess.add(actionCustUp);
		resourcessForMng.add(actionCustUp);
		
		Action actionCustDel = actionRepository.findOneByActionId("CustDel");
		if (null == actionCustDel) {
			actionCustDel = new Action();
			actionCustDel.setActionId("CustDel");
			actionCustDel.setActionNm("버튼 - 사업장 삭제");
			actionCustDel.setActionTp(codeBtn);
			actionCustDel.setResTp(codeResAction);
			actionCustDel.setUrl("/cust/*");
			actionCustDel.setMethod("DELETE");
			actionCustDel.setResDesc(actionCustDel.getActionNm());
			actionRepository.save(actionCustDel);
		}
		resourcess.add(actionCustDel);
		resourcessForMng.add(actionCustDel);
		
		Action actionCustSearch = actionRepository.findOneByActionId("CustSearch");
		if (null == actionCustSearch) {
			actionCustSearch = new Action();
			actionCustSearch.setActionId("CustSearch");
			actionCustSearch.setActionNm("버튼 - 사업장 리스트 조회");
			actionCustSearch.setActionTp(codeBtn);
			actionCustSearch.setResTp(codeResAction);
			actionCustSearch.setUrl("/cust");
			actionCustSearch.setMethod("GET");
			actionCustSearch.setResDesc(actionCustSearch.getActionNm());
			actionRepository.save(actionCustSearch);
		}
		resourcess.add(actionCustSearch);
		resourcessForMng.add(actionCustSearch);
	}
	
	
	/**
	 * 사업장 계정 관리 메뉴 등록
	 */
	private void initUserMenu() {
		Menu menuUser = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuCustMng, "계정 관리", 2);
		if (null == menuUser) {
			menuUser = new Menu();
			menuUser.setTopMenu(menuCustMng);
			menuUser.setMenuNm("계정 관리");
			menuUser.setMenuIcon("icon-people");
			menuUser.setMlevel(2);
			menuUser.setOrd(subMenuOrd++);
			menuUser.setResTp(codeResMenu);
			menuUser.setUrl("/v/business/user/list");
			menuUser.setMethod("GET");
			menuUser.setResDesc(menuUser.getMenuNm());
			menuRepository.save(menuUser);
		}
		resourcess.add(menuUser);
		resourcessForMng.add(menuUser);
		
		Action actionUserAdd = actionRepository.findOneByActionId("UserAdd");
		if (null == actionUserAdd) {
			actionUserAdd = new Action();
			actionUserAdd.setActionId("UserAdd");
			actionUserAdd.setActionNm("버튼 - 사업장 계정 등록");
			actionUserAdd.setActionTp(codeBtn);
			actionUserAdd.setResTp(codeResAction);
			actionUserAdd.setUrl("/user");
			actionUserAdd.setMethod("POST");
			actionUserAdd.setResDesc(actionUserAdd.getActionNm());
			actionRepository.save(actionUserAdd);
		}
		resourcess.add(actionUserAdd);
		resourcessForMng.add(actionUserAdd);
		
		Action actionUserGet = actionRepository.findOneByActionId("UserGet");
		if (null == actionUserGet) {
			actionUserGet = new Action();
			actionUserGet.setActionId("UserGet");
			actionUserGet.setActionNm("버튼 - 사업장 계정 정보 조회");
			actionUserGet.setActionTp(codeLink);
			actionUserGet.setResTp(codeResAction);
			actionUserGet.setUrl("/user/*");
			actionUserGet.setMethod("GET");
			actionUserGet.setResDesc(actionUserGet.getActionNm());
			actionRepository.save(actionUserGet);
		}
		resourcess.add(actionUserGet);
		resourcessForMng.add(actionUserGet);
		
		Action actionUserUp = actionRepository.findOneByActionId("UserUp");
		if (null == actionUserUp) {
			actionUserUp = new Action();
			actionUserUp.setActionId("UserUp");
			actionUserUp.setActionNm("버튼 - 사업장 계정 수정");
			actionUserUp.setActionTp(codeBtn);
			actionUserUp.setResTp(codeResAction);
			actionUserUp.setUrl("/user/*");
			actionUserUp.setMethod("PUT");
			actionUserUp.setResDesc(actionUserUp.getActionNm());
			actionRepository.save(actionUserUp);
		}
		resourcess.add(actionUserUp);
		resourcessForMng.add(actionUserUp);
		
		Action actionUserDel = actionRepository.findOneByActionId("UserDel");
		if (null == actionUserDel) {
			actionUserDel = new Action();
			actionUserDel.setActionId("UserDel");
			actionUserDel.setActionNm("버튼 - 사업장 계정 삭제");
			actionUserDel.setActionTp(codeBtn);
			actionUserDel.setResTp(codeResAction);
			actionUserDel.setUrl("/user/*");
			actionUserDel.setMethod("DELETE");
			actionUserDel.setResDesc(actionUserDel.getActionNm());
			actionRepository.save(actionUserDel);
		}
		resourcess.add(actionUserDel);
		resourcessForMng.add(actionUserDel);
		
		Action actionUserSearch = actionRepository.findOneByActionId("UserSearch");
		if (null == actionUserSearch) {
			actionUserSearch = new Action();
			actionUserSearch.setActionId("UserSearch");
			actionUserSearch.setActionNm("버튼 - 사업장 계정 리스트 조회");
			actionUserSearch.setActionTp(codeBtn);
			actionUserSearch.setResTp(codeResAction);
			actionUserSearch.setUrl("/user");
			actionUserSearch.setMethod("GET");
			actionUserSearch.setResDesc(actionUserSearch.getActionNm());
			actionRepository.save(actionUserSearch);
		}
		resourcess.add(actionUserSearch);
		resourcessForMng.add(actionUserSearch);
		
		
		Action actionAccountIdChk = actionRepository.findOneByActionId("AccountIdChk");
		resourcessForMng.add(actionAccountIdChk);
		
	}
	
	
	
	
	
	/** ===== 관리자 부분 끝 ===== */
	
	
	/**
	 * 사업장 담당자용 사업장 정보 수정
	 */
	private void initCustInfoMenu() {
		Menu menuCust = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "사업장 정보 관리", 1);
		if (null == menuCust) {
			menuCust = new Menu();
			menuCust.setMenuNm("사업장 정보 관리");
			menuCust.setMenuIcon("icon-cust");
			menuCust.setMlevel(1);
			menuCust.setOrd(5);
			menuCust.setResTp(codeResMenu);
			menuCust.setUrl("/v/business/cust/info");
			menuCust.setMethod("GET");
			menuCust.setResDesc(menuCust.getMenuNm());
			menuRepository.save(menuCust);
		}
		resourcessForCust.add(menuCust);
		
		Action actionGetSelfCustInfo = actionRepository.findOneByActionId("GetSelfCustInfo");
		if (null == actionGetSelfCustInfo) {
			actionGetSelfCustInfo = new Action();
			actionGetSelfCustInfo.setActionId("GetSelfCustInfo");
			actionGetSelfCustInfo.setActionNm("링크 - 사업장 정보 조회(사업장 관리자)");
			actionGetSelfCustInfo.setActionTp(codeLink);
			actionGetSelfCustInfo.setResTp(codeResAction);
			actionGetSelfCustInfo.setUrl("/cust/info");
			actionGetSelfCustInfo.setMethod("GET");
			actionGetSelfCustInfo.setResDesc(actionGetSelfCustInfo.getActionNm());
			actionRepository.save(actionGetSelfCustInfo);
		}
		resourcessForCust.add(actionGetSelfCustInfo);
		
		Action actionUpSelfCustInfo = actionRepository.findOneByActionId("UpSelfCustInfo");
		if (null == actionUpSelfCustInfo) {
			actionUpSelfCustInfo = new Action();
			actionUpSelfCustInfo.setActionId("UpSelfCustInfo");
			actionUpSelfCustInfo.setActionNm("버튼 - 사업장 정보 수정(사업장 관리자)");
			actionUpSelfCustInfo.setActionTp(codeBtn);
			actionUpSelfCustInfo.setResTp(codeResAction);
			actionUpSelfCustInfo.setUrl("/cust/info/*");
//			actionUpSelfCustInfo.setMethod("PUT");
			actionUpSelfCustInfo.setMethod("UPDATE");
			actionUpSelfCustInfo.setResDesc(actionUpSelfCustInfo.getActionNm());
			actionRepository.save(actionUpSelfCustInfo);
		}
		resourcessForCust.add(actionUpSelfCustInfo);
	}
	
	
	/**
	 * 사업장 담당자용 계정 정보 수정
	 */
	private void initUserInfoMenu() {
		Action actionGetSelfUserInfo = actionRepository.findOneByActionId("GetSelfUserInfo");
		if (null == actionGetSelfUserInfo) {
			actionGetSelfUserInfo = new Action();
			actionGetSelfUserInfo.setActionId("GetSelfUserInfo");
			actionGetSelfUserInfo.setActionNm("링크 - 계정 정보 조회(사업장 관리자)");
			actionGetSelfUserInfo.setActionTp(codeLink);
			actionGetSelfUserInfo.setResTp(codeResAction);
			actionGetSelfUserInfo.setUrl("/user/info/cust");
			actionGetSelfUserInfo.setMethod("GET");
			actionGetSelfUserInfo.setResDesc(actionGetSelfUserInfo.getActionNm());
			actionRepository.save(actionGetSelfUserInfo);
		}
		resourcessForCust.add(actionGetSelfUserInfo);
		
		Action actionUpSelfUserInfo = actionRepository.findOneByActionId("UpSelfUserInfo");
		if (null == actionUpSelfUserInfo) {
			actionUpSelfUserInfo = new Action();
			actionUpSelfUserInfo.setActionId("UpSelfUserInfo");
			actionUpSelfUserInfo.setActionNm("버튼 - 계정 정보 수정(사업장 관리자)");
			actionUpSelfUserInfo.setActionTp(codeBtn);
			actionUpSelfUserInfo.setResTp(codeResAction);
			actionUpSelfUserInfo.setUrl("/user/info/cust/*");
			actionUpSelfUserInfo.setMethod("PUT");
			actionUpSelfUserInfo.setResDesc(actionUpSelfUserInfo.getActionNm());
			actionRepository.save(actionUpSelfUserInfo);
		}
		resourcessForCust.add(actionUpSelfUserInfo);
		
		Action actionAccountIdChk = actionRepository.findOneByActionId("AccountIdChk");
		resourcessForCust.add(actionAccountIdChk);
		
	}
	
	
	/**
	 * 매장 관리 메뉴 등록
	 */
	private void initStoreMenuForCustMng() {
		Menu menuStore = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "매장 관리", 2);
		if (null == menuStore) {
			menuStore = new Menu();
			menuStore.setTopMenu(menuStoreMng);
			menuStore.setMenuNm("매장 관리");
			menuStore.setMenuIcon("icon-store");
			menuStore.setMlevel(2);
			menuStore.setOrd(subMenuOrd++);
			menuStore.setResTp(codeResMenu);
			menuStore.setUrl("/v/business/store/list");
			menuStore.setMethod("GET");
			menuStore.setResDesc(menuStore.getMenuNm());
			menuRepository.save(menuStore);
		}
		resourcessForCust.add(menuStore);
		
		Action actionStoreAdd = actionRepository.findOneByActionId("StoreAdd");
		if (null == actionStoreAdd) {
			actionStoreAdd = new Action();
			actionStoreAdd.setActionId("StoreAdd");
			actionStoreAdd.setActionNm("버튼 - 매장 등록");
			actionStoreAdd.setActionTp(codeBtn);
			actionStoreAdd.setResTp(codeResAction);
			actionStoreAdd.setUrl("/store");
			actionStoreAdd.setMethod("POST");
			actionStoreAdd.setResDesc(actionStoreAdd.getActionNm());
			actionRepository.save(actionStoreAdd);
		}
		resourcessForCust.add(actionStoreAdd);
		
		Action actionStoreGet = actionRepository.findOneByActionId("StoreGet");
		if (null == actionStoreGet) {
			actionStoreGet = new Action();
			actionStoreGet.setActionId("StoreGet");
			actionStoreGet.setActionNm("버튼 - 매장 정보 조회");
			actionStoreGet.setActionTp(codeLink);
			actionStoreGet.setResTp(codeResAction);
			actionStoreGet.setUrl("/store/*");
			actionStoreGet.setMethod("GET");
			actionStoreGet.setResDesc(actionStoreGet.getActionNm());
			actionRepository.save(actionStoreGet);
		}
		resourcessForCust.add(actionStoreGet);
		
		Action actionStoreUp = actionRepository.findOneByActionId("StoreUp");
		if (null == actionStoreUp) {
			actionStoreUp = new Action();
			actionStoreUp.setActionId("StoreUp");
			actionStoreUp.setActionNm("버튼 - 매장 수정");
			actionStoreUp.setActionTp(codeBtn);
			actionStoreUp.setResTp(codeResAction);
			actionStoreUp.setUrl("/store/*");
			actionStoreUp.setMethod("PUT");
			actionStoreUp.setResDesc(actionStoreUp.getActionNm());
			actionRepository.save(actionStoreUp);
		}
		resourcessForCust.add(actionStoreUp);
		
		Action actionStoreDel = actionRepository.findOneByActionId("StoreDel");
		if (null == actionStoreDel) {
			actionStoreDel = new Action();
			actionStoreDel.setActionId("StoreDel");
			actionStoreDel.setActionNm("버튼 - 매장 삭제");
			actionStoreDel.setActionTp(codeBtn);
			actionStoreDel.setResTp(codeResAction);
			actionStoreDel.setUrl("/store/*");
			actionStoreDel.setMethod("DELETE");
			actionStoreDel.setResDesc(actionStoreDel.getActionNm());
			actionRepository.save(actionStoreDel);
		}
		resourcessForCust.add(actionStoreDel);
		
		Action actionStoreSearch = actionRepository.findOneByActionId("StoreSearch");
		if (null == actionStoreSearch) {
			actionStoreSearch = new Action();
			actionStoreSearch.setActionId("StoreSearch");
			actionStoreSearch.setActionNm("버튼 - 매장 리스트 조회");
			actionStoreSearch.setActionTp(codeBtn);
			actionStoreSearch.setResTp(codeResAction);
			actionStoreSearch.setUrl("/store");
			actionStoreSearch.setMethod("GET");
			actionStoreSearch.setResDesc(actionStoreSearch.getActionNm());
			actionRepository.save(actionStoreSearch);
		}
		resourcessForCust.add(actionStoreSearch);
		
		Action actionStoreInfoGet = actionRepository.findOneByActionId("StoreInfoGet");
		if (null == actionStoreInfoGet) {
			actionStoreInfoGet = new Action();
			actionStoreInfoGet.setActionId("StoreInfoGet");
			actionStoreInfoGet.setActionNm("링크 - 로그인 매장 정보 조회");
			actionStoreInfoGet.setActionTp(codeLink);
			actionStoreInfoGet.setResTp(codeResAction);
			actionStoreInfoGet.setUrl("/store/getStore");
			actionStoreInfoGet.setMethod("GET");
			actionStoreInfoGet.setResDesc(actionStoreInfoGet.getActionNm());
			actionRepository.save(actionStoreInfoGet);
		}
		resourcessForCust.add(actionStoreInfoGet);
		
		Action actionStoreLansGet = actionRepository.findOneByActionId("StoreLansGet");
		if (null == actionStoreLansGet) {
			actionStoreLansGet = new Action();
			actionStoreLansGet.setActionId("StoreLansGet");
			actionStoreLansGet.setActionNm("링크 - 매장 추가 언어 리스트 조회");
			actionStoreLansGet.setActionTp(codeLink);
			actionStoreLansGet.setResTp(codeResAction);
			actionStoreLansGet.setUrl("/store/getStoreLans");
			actionStoreLansGet.setMethod("GET");
			actionStoreLansGet.setResDesc(actionStoreLansGet.getActionNm());
			actionRepository.save(actionStoreLansGet);
		}
		resourcessForCust.add(actionStoreLansGet);
		
	}
	
	
	/**
	 * 매장 사용자 정보 관리
	 */
	private void initUserMenuForCustMng() {
		Menu menuUser = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "매장 회원 관리", 2);
		if (null == menuUser) {
			menuUser = new Menu();
			menuUser.setTopMenu(menuStoreMng);
			menuUser.setMenuNm("매장 회원 관리");
			menuUser.setMenuIcon("icon-account");
			menuUser.setMlevel(2);
			menuUser.setOrd(subMenuOrd++);
			menuUser.setResTp(codeResMenu);
			menuUser.setUrl("/v/business/user/list_for_cust");
			menuUser.setMethod("GET");
			menuUser.setResDesc(menuUser.getMenuNm());
			menuRepository.save(menuUser);
		}
		resourcessForCust.add(menuUser);
		
		Action actionUserAdd = actionRepository.findOneByActionId("UserForCustAdd");
		if (null == actionUserAdd) {
			actionUserAdd = new Action();
			actionUserAdd.setActionId("UserForCustAdd");
			actionUserAdd.setActionNm("버튼 - 매장 회원 등록");
			actionUserAdd.setActionTp(codeBtn);
			actionUserAdd.setResTp(codeResAction);
			actionUserAdd.setUrl("/userCust");
			actionUserAdd.setMethod("POST");
			actionUserAdd.setResDesc(actionUserAdd.getActionNm());
			actionRepository.save(actionUserAdd);
		}
		resourcessForCust.add(actionUserAdd);
		
		Action actionUserGet = actionRepository.findOneByActionId("UserForCustGet");
		if (null == actionUserGet) {
			actionUserGet = new Action();
			actionUserGet.setActionId("UserForCustGet");
			actionUserGet.setActionNm("버튼 - 매장 회원 정보 조회");
			actionUserGet.setActionTp(codeLink);
			actionUserGet.setResTp(codeResAction);
			actionUserGet.setUrl("/userCust/*");
			actionUserGet.setMethod("GET");
			actionUserGet.setResDesc(actionUserGet.getActionNm());
			actionRepository.save(actionUserGet);
		}
		resourcessForCust.add(actionUserGet);
		
		Action actionUserUp = actionRepository.findOneByActionId("UserForCustUp");
		if (null == actionUserUp) {
			actionUserUp = new Action();
			actionUserUp.setActionId("UserForCustUp");
			actionUserUp.setActionNm("버튼 - 매장 회원 수정");
			actionUserUp.setActionTp(codeBtn);
			actionUserUp.setResTp(codeResAction);
			actionUserUp.setUrl("/userCust/*");
			actionUserUp.setMethod("PUT");
			actionUserUp.setResDesc(actionUserUp.getActionNm());
			actionRepository.save(actionUserUp);
		}
		resourcessForCust.add(actionUserUp);
		
		Action actionUserDel = actionRepository.findOneByActionId("UserForCustDel");
		if (null == actionUserDel) {
			actionUserDel = new Action();
			actionUserDel.setActionId("UserForCustDel");
			actionUserDel.setActionNm("버튼 - 매장 회원 삭제");
			actionUserDel.setActionTp(codeBtn);
			actionUserDel.setResTp(codeResAction);
			actionUserDel.setUrl("/userCust/*");
			actionUserDel.setMethod("DELETE");
			actionUserDel.setResDesc(actionUserDel.getActionNm());
			actionRepository.save(actionUserDel);
		}
		resourcessForCust.add(actionUserDel);
		
		Action actionUserSearch = actionRepository.findOneByActionId("UserForCustSearch");
		if (null == actionUserSearch) {
			actionUserSearch = new Action();
			actionUserSearch.setActionId("UserForCustSearch");
			actionUserSearch.setActionNm("버튼 - 매장 회원 리스트 조회");
			actionUserSearch.setActionTp(codeBtn);
			actionUserSearch.setResTp(codeResAction);
			actionUserSearch.setUrl("/userCust");
			actionUserSearch.setMethod("GET");
			actionUserSearch.setResDesc(actionUserSearch.getActionNm());
			actionRepository.save(actionUserSearch);
		}
		resourcessForCust.add(actionUserSearch);
		
	}
	
	
	/**
	 * 카테고리 관리 메뉴 등록
	 */
	private void initCategoryMenuForCustMng() {
		Menu menuCategory = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "상품 그룹 관리", 2);
		if (null == menuCategory) {
			menuCategory = new Menu();
			menuCategory.setTopMenu(menuStoreMng);
			menuCategory.setMenuNm("상품 그룹 관리");
			menuCategory.setMenuIcon("icon-category");
			menuCategory.setMlevel(2);
			menuCategory.setOrd(subMenuOrd++);
			menuCategory.setResTp(codeResMenu);
			menuCategory.setUrl("/v/business/category/list");
			menuCategory.setMethod("GET");
			menuCategory.setResDesc(menuCategory.getMenuNm());
			menuRepository.save(menuCategory);
		}
		resourcessForCust.add(menuCategory);
		
		Action actionCategoryAdd = actionRepository.findOneByActionId("CategoryAdd");
		if (null == actionCategoryAdd) {
			actionCategoryAdd = new Action();
			actionCategoryAdd.setActionId("CategoryAdd");
			actionCategoryAdd.setActionNm("버튼 - 상품그룹 등록");
			actionCategoryAdd.setActionTp(codeBtn);
			actionCategoryAdd.setResTp(codeResAction);
			actionCategoryAdd.setUrl("/category");
			actionCategoryAdd.setMethod("POST");
			actionCategoryAdd.setResDesc(actionCategoryAdd.getActionNm());
			actionRepository.save(actionCategoryAdd);
		}
		resourcessForCust.add(actionCategoryAdd);
		
		Action actionCategoryGet = actionRepository.findOneByActionId("CategoryGet");
		if (null == actionCategoryGet) {
			actionCategoryGet = new Action();
			actionCategoryGet.setActionId("CategoryGet");
			actionCategoryGet.setActionNm("버튼 - 상품그룹 정보 조회");
			actionCategoryGet.setActionTp(codeLink);
			actionCategoryGet.setResTp(codeResAction);
			actionCategoryGet.setUrl("/category/*");
			actionCategoryGet.setMethod("GET");
			actionCategoryGet.setResDesc(actionCategoryGet.getActionNm());
			actionRepository.save(actionCategoryGet);
		}
		resourcessForCust.add(actionCategoryGet);
		
		Action actionCategoryUp = actionRepository.findOneByActionId("CategoryUp");
		if (null == actionCategoryUp) {
			actionCategoryUp = new Action();
			actionCategoryUp.setActionId("CategoryUp");
			actionCategoryUp.setActionNm("버튼 - 상품그룹 수정");
			actionCategoryUp.setActionTp(codeBtn);
			actionCategoryUp.setResTp(codeResAction);
			actionCategoryUp.setUrl("/category/*");
			actionCategoryUp.setMethod("PUT");
			actionCategoryUp.setResDesc(actionCategoryUp.getActionNm());
			actionRepository.save(actionCategoryUp);
		}
		resourcessForCust.add(actionCategoryUp);
		
		Action actionCategoryDel = actionRepository.findOneByActionId("CategoryDel");
		if (null == actionCategoryDel) {
			actionCategoryDel = new Action();
			actionCategoryDel.setActionId("CategoryDel");
			actionCategoryDel.setActionNm("버튼 - 상품그룹 삭제");
			actionCategoryDel.setActionTp(codeBtn);
			actionCategoryDel.setResTp(codeResAction);
			actionCategoryDel.setUrl("/category/*");
			actionCategoryDel.setMethod("DELETE");
			actionCategoryDel.setResDesc(actionCategoryDel.getActionNm());
			actionRepository.save(actionCategoryDel);
		}
		resourcessForCust.add(actionCategoryDel);
		
		Action actionCategorySearch = actionRepository.findOneByActionId("CategorySearch");
		if (null == actionCategorySearch) {
			actionCategorySearch = new Action();
			actionCategorySearch.setActionId("CategorySearch");
			actionCategorySearch.setActionNm("버튼 - 상품그룹 리스트 조회");
			actionCategorySearch.setActionTp(codeBtn);
			actionCategorySearch.setResTp(codeResAction);
			actionCategorySearch.setUrl("/category");
			actionCategorySearch.setMethod("GET");
			actionCategorySearch.setResDesc(actionCategorySearch.getActionNm());
			actionRepository.save(actionCategorySearch);
		}
		resourcessForCust.add(actionCategorySearch);
	}
	
	
	/**
	 * 재료관리 메뉴 등록
	 */
	private void initStuffMenuForCustMng() {
		Menu menuStuff = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "재료 관리", 2);
		if (null == menuStuff) {
			menuStuff = new Menu();
			menuStuff.setTopMenu(menuStoreMng);
			menuStuff.setMenuNm("재료 관리");
			menuStuff.setMenuIcon("icon-stuff");
			menuStuff.setMlevel(2);
			menuStuff.setOrd(subMenuOrd++);
			menuStuff.setResTp(codeResMenu);
			menuStuff.setUrl("/v/business/stuff/list");
			menuStuff.setMethod("GET");
			menuStuff.setResDesc(menuStuff.getMenuNm());
			menuRepository.save(menuStuff);
		}
		resourcessForCust.add(menuStuff);
		
		Action actionStuffAdd = actionRepository.findOneByActionId("StuffAdd");
		if (null == actionStuffAdd) {
			actionStuffAdd = new Action();
			actionStuffAdd.setActionId("StuffAdd");
			actionStuffAdd.setActionNm("버튼 - 재료 등록");
			actionStuffAdd.setActionTp(codeBtn);
			actionStuffAdd.setResTp(codeResAction);
			actionStuffAdd.setUrl("/stuff");
			actionStuffAdd.setMethod("POST");
			actionStuffAdd.setResDesc(actionStuffAdd.getActionNm());
			actionRepository.save(actionStuffAdd);
		}
		resourcessForCust.add(actionStuffAdd);
		
		Action actionStuffGet = actionRepository.findOneByActionId("StuffGet");
		if (null == actionStuffGet) {
			actionStuffGet = new Action();
			actionStuffGet.setActionId("StuffGet");
			actionStuffGet.setActionNm("버튼 - 재료 정보 조회");
			actionStuffGet.setActionTp(codeBtn);
			actionStuffGet.setResTp(codeResAction);
			actionStuffGet.setUrl("/stuff/*");
			actionStuffGet.setMethod("GET");
			actionStuffGet.setResDesc(actionStuffGet.getActionNm());
			actionRepository.save(actionStuffGet);
		}
		resourcessForCust.add(actionStuffGet);
		
		Action actionStuffUp = actionRepository.findOneByActionId("StuffUp");
		if (null == actionStuffUp) {
			actionStuffUp = new Action();
			actionStuffUp.setActionId("StuffUp");
			actionStuffUp.setActionNm("버튼 - 재료 수정");
			actionStuffUp.setActionTp(codeBtn);
			actionStuffUp.setResTp(codeResAction);
			actionStuffUp.setUrl("/stuff/*");
			actionStuffUp.setMethod("PUT");
			actionStuffUp.setResDesc(actionStuffUp.getActionNm());
			actionRepository.save(actionStuffUp);
		}
		resourcessForCust.add(actionStuffUp);
		
		Action actionStuffDel = actionRepository.findOneByActionId("StuffDel");
		if (null == actionStuffDel) {
			actionStuffDel = new Action();
			actionStuffDel.setActionId("StuffDel");
			actionStuffDel.setActionNm("버튼 - 재료 삭제");
			actionStuffDel.setActionTp(codeBtn);
			actionStuffDel.setResTp(codeResAction);
			actionStuffDel.setUrl("/stuff/*");
			actionStuffDel.setMethod("DELETE");
			actionStuffDel.setResDesc(actionStuffDel.getActionNm());
			actionRepository.save(actionStuffDel);
		}
		resourcessForCust.add(actionStuffDel);
		
		Action actionStuffSearch = actionRepository.findOneByActionId("StuffSearch");
		if (null == actionStuffSearch) {
			actionStuffSearch = new Action();
			actionStuffSearch.setActionId("StuffSearch");
			actionStuffSearch.setActionNm("버튼 - 재료 리스트 조회");
			actionStuffSearch.setActionTp(codeBtn);
			actionStuffSearch.setResTp(codeResAction);
			actionStuffSearch.setUrl("/stuff");
			actionStuffSearch.setMethod("GET");
			actionStuffSearch.setResDesc(actionStuffSearch.getActionNm());
			actionRepository.save(actionStuffSearch);
		}
		resourcessForCust.add(actionStuffSearch);
		
		Action actionStuffDown = actionRepository.findOneByActionId("StuffDown");
		if (null == actionStuffDown) {
			actionStuffDown = new Action();
			actionStuffDown.setActionId("StuffDown");
			actionStuffDown.setActionNm("버튼 - 매장 재료 리스트 양식 다운로드");
			actionStuffDown.setActionTp(codeBtn);
			actionStuffDown.setResTp(codeResAction);
			actionStuffDown.setUrl("/stuff/down/*");
			actionStuffDown.setMethod("GET");
			actionStuffDown.setResDesc(actionStuffDown.getActionNm());
			actionRepository.save(actionStuffDown);
		}
		resourcessForCust.add(actionStuffDown);
		
		Action actionStuffUpData = actionRepository.findOneByActionId("StuffUpData");
		if (null == actionStuffUpData) {
			actionStuffUpData = new Action();
			actionStuffUpData.setActionId("StuffUpData");
			actionStuffUpData.setActionNm("버튼 - 매장 재료 데이타 업로드");
			actionStuffUpData.setActionTp(codeBtn);
			actionStuffUpData.setResTp(codeResAction);
			actionStuffUpData.setUrl("/stuff/up/*");
			actionStuffUpData.setMethod("POST");
			actionStuffUpData.setResDesc(actionStuffUpData.getActionNm());
			actionRepository.save(actionStuffUpData);
		}
		resourcessForCust.add(actionStuffUpData);
	}
	
	
	
	
	/**
	 * 옵션 그룹 관리 관리 메뉴 등록
	 */
	private void initSmenuOptTpMenuForCustMng() {
		Menu menuSmenuOptTp = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "옵션 그룹 관리", 2);
		if (null == menuSmenuOptTp) {
			menuSmenuOptTp = new Menu();
			menuSmenuOptTp.setTopMenu(menuStoreMng);
			menuSmenuOptTp.setMenuNm("옵션 그룹 관리");
			menuSmenuOptTp.setMenuIcon("icon-options");
			menuSmenuOptTp.setMlevel(2);
			menuSmenuOptTp.setOrd(subMenuOrd++);
			menuSmenuOptTp.setResTp(codeResMenu);
			menuSmenuOptTp.setUrl("/v/business/smenuOptTp/list");
			menuSmenuOptTp.setMethod("GET");
			menuSmenuOptTp.setResDesc(menuSmenuOptTp.getMenuNm());
			menuRepository.save(menuSmenuOptTp);
		}
		resourcessForCust.add(menuSmenuOptTp);
		
		Action actionSmenuOptTpAdd = actionRepository.findOneByActionId("SmenuOptTpAdd");
		if (null == actionSmenuOptTpAdd) {
			actionSmenuOptTpAdd = new Action();
			actionSmenuOptTpAdd.setActionId("SmenuOptTpAdd");
			actionSmenuOptTpAdd.setActionNm("버튼 - 옵션 그룹 등록");
			actionSmenuOptTpAdd.setActionTp(codeBtn);
			actionSmenuOptTpAdd.setResTp(codeResAction);
			actionSmenuOptTpAdd.setUrl("/smenuOptTp");
			actionSmenuOptTpAdd.setMethod("POST");
			actionSmenuOptTpAdd.setResDesc(actionSmenuOptTpAdd.getActionNm());
			actionRepository.save(actionSmenuOptTpAdd);
		}
		resourcessForCust.add(actionSmenuOptTpAdd);
		
		Action actionSmenuOptTpGet = actionRepository.findOneByActionId("SmenuOptTpGet");
		if (null == actionSmenuOptTpGet) {
			actionSmenuOptTpGet = new Action();
			actionSmenuOptTpGet.setActionId("SmenuOptTpGet");
			actionSmenuOptTpGet.setActionNm("링크 - 옵션 그룹 정보 조회");
			actionSmenuOptTpGet.setActionTp(codeLink);
			actionSmenuOptTpGet.setResTp(codeResAction);
			actionSmenuOptTpGet.setUrl("/smenuOptTp/*");
			actionSmenuOptTpGet.setMethod("GET");
			actionSmenuOptTpGet.setResDesc(actionSmenuOptTpGet.getActionNm());
			actionRepository.save(actionSmenuOptTpGet);
		}
		resourcessForCust.add(actionSmenuOptTpGet);
		
		Action actionSmenuOptTpUp = actionRepository.findOneByActionId("SmenuOptTpUp");
		if (null == actionSmenuOptTpUp) {
			actionSmenuOptTpUp = new Action();
			actionSmenuOptTpUp.setActionId("SmenuOptTpUp");
			actionSmenuOptTpUp.setActionNm("버튼 - 옵션 그룹 수정");
			actionSmenuOptTpUp.setActionTp(codeBtn);
			actionSmenuOptTpUp.setResTp(codeResAction);
			actionSmenuOptTpUp.setUrl("/smenuOptTp/*");
			actionSmenuOptTpUp.setMethod("PUT");
			actionSmenuOptTpUp.setResDesc(actionSmenuOptTpUp.getActionNm());
			actionRepository.save(actionSmenuOptTpUp);
		}
		resourcessForCust.add(actionSmenuOptTpUp);
		
		Action actionSmenuOptTpDel = actionRepository.findOneByActionId("SmenuOptTpDel");
		if (null == actionSmenuOptTpDel) {
			actionSmenuOptTpDel = new Action();
			actionSmenuOptTpDel.setActionId("SmenuOptTpDel");
			actionSmenuOptTpDel.setActionNm("버튼 - 옵션 그룹 삭제");
			actionSmenuOptTpDel.setActionTp(codeBtn);
			actionSmenuOptTpDel.setResTp(codeResAction);
			actionSmenuOptTpDel.setUrl("/smenuOptTp/*");
			actionSmenuOptTpDel.setMethod("DELETE");
			actionSmenuOptTpDel.setResDesc(actionSmenuOptTpDel.getActionNm());
			actionRepository.save(actionSmenuOptTpDel);
		}
		resourcessForCust.add(actionSmenuOptTpDel);
		
		Action actionSmenuOptTpSearch = actionRepository.findOneByActionId("SmenuOptTpSearch");
		if (null == actionSmenuOptTpSearch) {
			actionSmenuOptTpSearch = new Action();
			actionSmenuOptTpSearch.setActionId("SmenuOptTpSearch");
			actionSmenuOptTpSearch.setActionNm("버튼 - 옵션 그룹 리스트 조회");
			actionSmenuOptTpSearch.setActionTp(codeBtn);
			actionSmenuOptTpSearch.setResTp(codeResAction);
			actionSmenuOptTpSearch.setUrl("/smenuOptTp");
			actionSmenuOptTpSearch.setMethod("GET");
			actionSmenuOptTpSearch.setResDesc(actionSmenuOptTpSearch.getActionNm());
			actionRepository.save(actionSmenuOptTpSearch);
		}
		resourcessForCust.add(actionSmenuOptTpSearch);
	}
	
	
	/**
	 * 옵션관리 관리 메뉴 등록
	 */
	private void initSmenuOptMenuForCustMng() {
		Menu menuSmenuOpt = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "옵션 관리", 2);
		if (null == menuSmenuOpt) {
			menuSmenuOpt = new Menu();
			menuSmenuOpt.setTopMenu(menuStoreMng);
			menuSmenuOpt.setMenuNm("옵션 관리");
			menuSmenuOpt.setMenuIcon("icon-options");
			menuSmenuOpt.setMlevel(2);
			menuSmenuOpt.setOrd(subMenuOrd++);
			menuSmenuOpt.setResTp(codeResMenu);
			menuSmenuOpt.setUrl("/v/business/smenuOpt/list");
			menuSmenuOpt.setMethod("GET");
			menuSmenuOpt.setResDesc(menuSmenuOpt.getMenuNm());
			menuRepository.save(menuSmenuOpt);
		}
		resourcessForCust.add(menuSmenuOpt);
		
		Action actionSmenuOptAdd = actionRepository.findOneByActionId("SmenuOptAdd");
		if (null == actionSmenuOptAdd) {
			actionSmenuOptAdd = new Action();
			actionSmenuOptAdd.setActionId("SmenuOptAdd");
			actionSmenuOptAdd.setActionNm("버튼 - 옵션 등록");
			actionSmenuOptAdd.setActionTp(codeBtn);
			actionSmenuOptAdd.setResTp(codeResAction);
			actionSmenuOptAdd.setUrl("/smenuOpt");
			actionSmenuOptAdd.setMethod("POST");
			actionSmenuOptAdd.setResDesc(actionSmenuOptAdd.getActionNm());
			actionRepository.save(actionSmenuOptAdd);
		}
		resourcessForCust.add(actionSmenuOptAdd);
		
		Action actionSmenuOptGet = actionRepository.findOneByActionId("SmenuOptGet");
		if (null == actionSmenuOptGet) {
			actionSmenuOptGet = new Action();
			actionSmenuOptGet.setActionId("SmenuOptGet");
			actionSmenuOptGet.setActionNm("버튼 - 옵션 정보 조회");
			actionSmenuOptGet.setActionTp(codeLink);
			actionSmenuOptGet.setResTp(codeResAction);
			actionSmenuOptGet.setUrl("/smenuOpt/*");
			actionSmenuOptGet.setMethod("GET");
			actionSmenuOptGet.setResDesc(actionSmenuOptGet.getActionNm());
			actionRepository.save(actionSmenuOptGet);
		}
		resourcessForCust.add(actionSmenuOptGet);
		
		Action actionSmenuOptUp = actionRepository.findOneByActionId("SmenuOptUp");
		if (null == actionSmenuOptUp) {
			actionSmenuOptUp = new Action();
			actionSmenuOptUp.setActionId("SmenuOptUp");
			actionSmenuOptUp.setActionNm("버튼 - 옵션 수정");
			actionSmenuOptUp.setActionTp(codeBtn);
			actionSmenuOptUp.setResTp(codeResAction);
			actionSmenuOptUp.setUrl("/smenuOpt/*");
			actionSmenuOptUp.setMethod("PUT");
			actionSmenuOptUp.setResDesc(actionSmenuOptUp.getActionNm());
			actionRepository.save(actionSmenuOptUp);
		}
		resourcessForCust.add(actionSmenuOptUp);
		
		Action actionSmenuOptDel = actionRepository.findOneByActionId("SmenuOptDel");
		if (null == actionSmenuOptDel) {
			actionSmenuOptDel = new Action();
			actionSmenuOptDel.setActionId("SmenuOptDel");
			actionSmenuOptDel.setActionNm("버튼 - 옵션 삭제");
			actionSmenuOptDel.setActionTp(codeBtn);
			actionSmenuOptDel.setResTp(codeResAction);
			actionSmenuOptDel.setUrl("/smenuOpt/*");
			actionSmenuOptDel.setMethod("DELETE");
			actionSmenuOptDel.setResDesc(actionSmenuOptDel.getActionNm());
			actionRepository.save(actionSmenuOptDel);
		}
		resourcessForCust.add(actionSmenuOptDel);
		
		Action actionSmenuOptSearch = actionRepository.findOneByActionId("SmenuOptSearch");
		if (null == actionSmenuOptSearch) {
			actionSmenuOptSearch = new Action();
			actionSmenuOptSearch.setActionId("SmenuOptSearch");
			actionSmenuOptSearch.setActionNm("버튼 - 옵션 리스트 조회");
			actionSmenuOptSearch.setActionTp(codeBtn);
			actionSmenuOptSearch.setResTp(codeResAction);
			actionSmenuOptSearch.setUrl("/smenuOpt");
			actionSmenuOptSearch.setMethod("GET");
			actionSmenuOptSearch.setResDesc(actionSmenuOptSearch.getActionNm());
			actionRepository.save(actionSmenuOptSearch);
		}
		resourcessForCust.add(actionSmenuOptSearch);
	}
	
	
	/**
	 * 할인관리 관리 메뉴 등록
	 */
	private void initDiscountMenuForCustMng() {
		Menu menuDiscount = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "할인 관리", 2);
		if (null == menuDiscount) {
			menuDiscount = new Menu();
			menuDiscount.setTopMenu(menuStoreMng);
			menuDiscount.setMenuNm("할인 관리");
			menuDiscount.setMenuIcon("icon-discount");
			menuDiscount.setMlevel(2);
			menuDiscount.setOrd(subMenuOrd++);
			menuDiscount.setResTp(codeResMenu);
			menuDiscount.setUrl("/v/business/discount/list");
			menuDiscount.setMethod("GET");
			menuDiscount.setResDesc(menuDiscount.getMenuNm());
			menuRepository.save(menuDiscount);
		}
		resourcessForCust.add(menuDiscount);
		
		Action actionDiscountAdd = actionRepository.findOneByActionId("DiscountAdd");
		if (null == actionDiscountAdd) {
			actionDiscountAdd = new Action();
			actionDiscountAdd.setActionId("DiscountAdd");
			actionDiscountAdd.setActionNm("버튼 - 할인 등록");
			actionDiscountAdd.setActionTp(codeBtn);
			actionDiscountAdd.setResTp(codeResAction);
			actionDiscountAdd.setUrl("/discount");
			actionDiscountAdd.setMethod("POST");
			actionDiscountAdd.setResDesc(actionDiscountAdd.getActionNm());
			actionRepository.save(actionDiscountAdd);
		}
		resourcessForCust.add(actionDiscountAdd);
		
		Action actionDiscountGet = actionRepository.findOneByActionId("DiscountGet");
		if (null == actionDiscountGet) {
			actionDiscountGet = new Action();
			actionDiscountGet.setActionId("DiscountGet");
			actionDiscountGet.setActionNm("버튼 - 할인 정보 조회");
			actionDiscountGet.setActionTp(codeLink);
			actionDiscountGet.setResTp(codeResAction);
			actionDiscountGet.setUrl("/discount/*");
			actionDiscountGet.setMethod("GET");
			actionDiscountGet.setResDesc(actionDiscountGet.getActionNm());
			actionRepository.save(actionDiscountGet);
		}
		resourcessForCust.add(actionDiscountGet);
		
		Action actionDiscountUp = actionRepository.findOneByActionId("DiscountUp");
		if (null == actionDiscountUp) {
			actionDiscountUp = new Action();
			actionDiscountUp.setActionId("DiscountUp");
			actionDiscountUp.setActionNm("버튼 - 할인 수정");
			actionDiscountUp.setActionTp(codeBtn);
			actionDiscountUp.setResTp(codeResAction);
			actionDiscountUp.setUrl("/discount/*");
			actionDiscountUp.setMethod("PUT");
			actionDiscountUp.setResDesc(actionDiscountUp.getActionNm());
			actionRepository.save(actionDiscountUp);
		}
		resourcessForCust.add(actionDiscountUp);
		
		Action actionDiscountDel = actionRepository.findOneByActionId("DiscountDel");
		if (null == actionDiscountDel) {
			actionDiscountDel = new Action();
			actionDiscountDel.setActionId("DiscountDel");
			actionDiscountDel.setActionNm("버튼 - 할인 삭제");
			actionDiscountDel.setActionTp(codeBtn);
			actionDiscountDel.setResTp(codeResAction);
			actionDiscountDel.setUrl("/discount/*");
			actionDiscountDel.setMethod("DELETE");
			actionDiscountDel.setResDesc(actionDiscountDel.getActionNm());
			actionRepository.save(actionDiscountDel);
		}
		resourcessForCust.add(actionDiscountDel);
		
		Action actionDiscountSearch = actionRepository.findOneByActionId("DiscountSearch");
		if (null == actionDiscountSearch) {
			actionDiscountSearch = new Action();
			actionDiscountSearch.setActionId("DiscountSearch");
			actionDiscountSearch.setActionNm("버튼 - 할인 리스트 조회");
			actionDiscountSearch.setActionTp(codeBtn);
			actionDiscountSearch.setResTp(codeResAction);
			actionDiscountSearch.setUrl("/discount");
			actionDiscountSearch.setMethod("GET");
			actionDiscountSearch.setResDesc(actionDiscountSearch.getActionNm());
			actionRepository.save(actionDiscountSearch);
		}
		resourcessForCust.add(actionDiscountSearch);
	}
	
	
	/**
	 * 메뉴 관리 메뉴 등록
	 */
	private void initSmenuMenuForCustMng() {
		Menu menuSmenu = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "메뉴 관리", 2);
		if (null == menuSmenu) {
			menuSmenu = new Menu();
			menuSmenu.setTopMenu(menuStoreMng);
			menuSmenu.setMenuNm("메뉴 관리");
			menuSmenu.setMenuIcon("icon-menu");
			menuSmenu.setMlevel(2);
			menuSmenu.setOrd(subMenuOrd++);
			menuSmenu.setResTp(codeResMenu);
			menuSmenu.setUrl("/v/business/smenu/list");
			menuSmenu.setMethod("GET");
			menuSmenu.setResDesc(menuSmenu.getMenuNm());
			menuRepository.save(menuSmenu);
		}
		resourcessForCust.add(menuSmenu);
		
		Action actionSmenuAdd = actionRepository.findOneByActionId("SmenuAdd");
		if (null == actionSmenuAdd) {
			actionSmenuAdd = new Action();
			actionSmenuAdd.setActionId("SmenuAdd");
			actionSmenuAdd.setActionNm("버튼 - 매장 메뉴 등록");
			actionSmenuAdd.setActionTp(codeBtn);
			actionSmenuAdd.setResTp(codeResAction);
			actionSmenuAdd.setUrl("/smenu");
			actionSmenuAdd.setMethod("POST");
			actionSmenuAdd.setResDesc(actionSmenuAdd.getActionNm());
			actionRepository.save(actionSmenuAdd);
		}
		resourcessForCust.add(actionSmenuAdd);
		
		Action actionSmenuGet = actionRepository.findOneByActionId("SmenuGet");
		if (null == actionSmenuGet) {
			actionSmenuGet = new Action();
			actionSmenuGet.setActionId("SmenuGet");
			actionSmenuGet.setActionNm("버튼 - 매장 메뉴 정보 조회");
			actionSmenuGet.setActionTp(codeBtn);
			actionSmenuGet.setResTp(codeResAction);
			actionSmenuGet.setUrl("/smenu/*");
			actionSmenuGet.setMethod("GET");
			actionSmenuGet.setResDesc(actionSmenuGet.getActionNm());
			actionRepository.save(actionSmenuGet);
		}
		resourcessForCust.add(actionSmenuGet);
		
		Action actionSmenuUp = actionRepository.findOneByActionId("SmenuUp");
		if (null == actionSmenuUp) {
			actionSmenuUp = new Action();
			actionSmenuUp.setActionId("SmenuUp");
			actionSmenuUp.setActionNm("버튼 - 매장 메뉴 수정");
			actionSmenuUp.setActionTp(codeBtn);
			actionSmenuUp.setResTp(codeResAction);
			actionSmenuUp.setUrl("/smenu/*");
			actionSmenuUp.setMethod("PUT");
			actionSmenuUp.setResDesc(actionSmenuUp.getActionNm());
			actionRepository.save(actionSmenuUp);
		}
		resourcessForCust.add(actionSmenuUp);
		
		Action actionSmenuDel = actionRepository.findOneByActionId("SmenuDel");
		if (null == actionSmenuDel) {
			actionSmenuDel = new Action();
			actionSmenuDel.setActionId("SmenuDel");
			actionSmenuDel.setActionNm("버튼 - 매장 메뉴 삭제");
			actionSmenuDel.setActionTp(codeBtn);
			actionSmenuDel.setResTp(codeResAction);
			actionSmenuDel.setUrl("/smenu/*");
			actionSmenuDel.setMethod("DELETE");
			actionSmenuDel.setResDesc(actionSmenuDel.getActionNm());
			actionRepository.save(actionSmenuDel);
		}
		resourcessForCust.add(actionSmenuDel);
		
		Action actionSmenuUse = actionRepository.findOneByActionId("SmenuUse");
		if (null == actionSmenuUse) {
			actionSmenuUse = new Action();
			actionSmenuUse.setActionId("SmenuUse");
			actionSmenuUse.setActionNm("버튼 - 매장 메뉴 반영");
			actionSmenuUse.setActionTp(codeBtn);
			actionSmenuUse.setResTp(codeResAction);
			actionSmenuUse.setUrl("/smenu/use/*");
			actionSmenuUse.setMethod("PUT");
			actionSmenuUse.setResDesc(actionSmenuUse.getActionNm());
			actionRepository.save(actionSmenuUse);
		}
		resourcessForCust.add(actionSmenuUse);
		
		Action actionSmenuSearch = actionRepository.findOneByActionId("SmenuSearch");
		if (null == actionSmenuSearch) {
			actionSmenuSearch = new Action();
			actionSmenuSearch.setActionId("SmenuSearch");
			actionSmenuSearch.setActionNm("버튼 - 매장 메뉴 리스트 조회");
			actionSmenuSearch.setActionTp(codeBtn);
			actionSmenuSearch.setResTp(codeResAction);
			actionSmenuSearch.setUrl("/smenu");
			actionSmenuSearch.setMethod("GET");
			actionSmenuSearch.setResDesc(actionSmenuSearch.getActionNm());
			actionRepository.save(actionSmenuSearch);
		}
		resourcessForCust.add(actionSmenuSearch);
		
		Action actionSmenuDown = actionRepository.findOneByActionId("SmenuDown");
		if (null == actionSmenuDown) {
			actionSmenuDown = new Action();
			actionSmenuDown.setActionId("SmenuDown");
			actionSmenuDown.setActionNm("버튼 - 매장 메뉴 리스트 양식 다운로드");
			actionSmenuDown.setActionTp(codeBtn);
			actionSmenuDown.setResTp(codeResAction);
			actionSmenuDown.setUrl("/smenu/down/*");
			actionSmenuDown.setMethod("GET");
			actionSmenuDown.setResDesc(actionSmenuDown.getActionNm());
			actionRepository.save(actionSmenuDown);
		}
		resourcessForCust.add(actionSmenuDown);
		
		Action actionSmenuUpData = actionRepository.findOneByActionId("SmenuUpData");
		if (null == actionSmenuUpData) {
			actionSmenuUpData = new Action();
			actionSmenuUpData.setActionId("SmenuUpData");
			actionSmenuUpData.setActionNm("버튼 - 매장 메뉴 데이타 업로드");
			actionSmenuUpData.setActionTp(codeBtn);
			actionSmenuUpData.setResTp(codeResAction);
			actionSmenuUpData.setUrl("/smenu/up/*");
			actionSmenuUpData.setMethod("POST");
			actionSmenuUpData.setResDesc(actionSmenuUpData.getActionNm());
			actionRepository.save(actionSmenuUpData);
		}
		resourcessForCust.add(actionSmenuUpData);
		
		Action actionSmenuUpImg = actionRepository.findOneByActionId("SmenuUpImg");
		if (null == actionSmenuUpImg) {
			actionSmenuUpImg = new Action();
			actionSmenuUpImg.setActionId("SmenuUpImg");
			actionSmenuUpImg.setActionNm("버튼 - 매장 메뉴 이미지 업로드");
			actionSmenuUpImg.setActionTp(codeBtn);
			actionSmenuUpImg.setResTp(codeResAction);
			actionSmenuUpImg.setUrl("/smenu/upImg/*");
			actionSmenuUpImg.setMethod("POST");
			actionSmenuUpImg.setResDesc(actionSmenuUpImg.getActionNm());
			actionRepository.save(actionSmenuUpImg);
		}
		resourcessForCust.add(actionSmenuUpImg);
		
		Action actionSmenuUpOrd = actionRepository.findOneByActionId("SmenuUpOrd");
		if (null == actionSmenuUpOrd) {
			actionSmenuUpOrd = new Action();
			actionSmenuUpOrd.setActionId("SmenuUpOrd");
			actionSmenuUpOrd.setActionNm("버튼 - 매장 메뉴 순번 수정");
			actionSmenuUpOrd.setActionTp(codeBtn);
			actionSmenuUpOrd.setResTp(codeResAction);
			actionSmenuUpOrd.setUrl("/smenu/*/*");
			actionSmenuUpOrd.setMethod("PATCH");
			actionSmenuUpOrd.setResDesc(actionSmenuUpOrd.getActionNm());
			actionRepository.save(actionSmenuUpOrd);
		}
		resourcessForCust.add(actionSmenuUpOrd);
		
	}
	
	
	
	/**
	 * 결제방법 관리 메뉴 등록
	 */
	private void initPayMethodMenuForCustMng() {
		Menu payMethodSmenu = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(menuStoreMng, "결제방법 관리", 2);
		if (null == payMethodSmenu) {
			payMethodSmenu = new Menu();
			payMethodSmenu.setTopMenu(menuStoreMng);
			payMethodSmenu.setMenuNm("결제방법 관리");
			payMethodSmenu.setMenuIcon("icon-menu");
			payMethodSmenu.setMlevel(2);
			payMethodSmenu.setOrd(subMenuOrd++);
			payMethodSmenu.setResTp(codeResMenu);
			payMethodSmenu.setUrl("/v/business/payInfo/for_store");
			payMethodSmenu.setMethod("GET");
			payMethodSmenu.setResDesc(payMethodSmenu.getMenuNm());
			menuRepository.save(payMethodSmenu);
		}
		
		resourcessForCust.add(payMethodSmenu);
		
	}
	
	
	/* ===== 사업장 관리자 부분 끝 ===== */
	
	
	/**
	 * 매장 관리 메뉴 등록
	 */
	private void initStoreMenu() {
		Menu menuStore = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "매장 정보 관리", 1);
		if (null == menuStore) {
			menuStore = new Menu();
			menuStore.setMenuNm("매장 정보 관리");
			menuStore.setMenuIcon("icon-store");
			menuStore.setMlevel(1);
			menuStore.setOrd(7);
			menuStore.setResTp(codeResMenu);
			menuStore.setUrl("/v/business/store/info");
			menuStore.setMethod("GET");
			menuStore.setResDesc("매장 관리자 " + menuStore.getMenuNm());
			menuRepository.save(menuStore);
		}
		resourcessForStore.add(menuStore);
		
		Action actionGetSelfStoreInfo = actionRepository.findOneByActionId("GetSelfStoreInfo");
		if (null == actionGetSelfStoreInfo) {
			actionGetSelfStoreInfo = new Action();
			actionGetSelfStoreInfo.setActionId("GetSelfStoreInfo");
			actionGetSelfStoreInfo.setActionNm("링크 - 매장 관리자 자기 매장 정보 조회");
			actionGetSelfStoreInfo.setActionTp(codeLink);
			actionGetSelfStoreInfo.setResTp(codeResAction);
			actionGetSelfStoreInfo.setUrl("/store/info");
			actionGetSelfStoreInfo.setMethod("GET");
			actionGetSelfStoreInfo.setResDesc(actionGetSelfStoreInfo.getActionNm());
			actionRepository.save(actionGetSelfStoreInfo);
		}
		resourcessForStore.add(actionGetSelfStoreInfo);
		
		Action actionUpSelfStoreInfo = actionRepository.findOneByActionId("UpSelfStoreInfo");
		if (null == actionUpSelfStoreInfo) {
			actionUpSelfStoreInfo = new Action();
			actionUpSelfStoreInfo.setActionId("UpSelfStoreInfo");
			actionUpSelfStoreInfo.setActionNm("버튼 - 매장 관리자 자기 매장 정보 수정");
			actionUpSelfStoreInfo.setActionTp(codeBtn);
			actionUpSelfStoreInfo.setResTp(codeResAction);
			actionUpSelfStoreInfo.setUrl("/store/info/*");
			actionUpSelfStoreInfo.setMethod("PUT");
			actionUpSelfStoreInfo.setResDesc(actionUpSelfStoreInfo.getActionNm());
			actionRepository.save(actionUpSelfStoreInfo);
		}
		resourcessForStore.add(actionUpSelfStoreInfo);
		
		Action actionStoreLansGet = actionRepository.findOneByActionId("StoreLansGet");
		resourcessForStore.add(actionStoreLansGet);
		
		Action actionStoreInfoGet = actionRepository.findOneByActionId("StoreInfoGet");
		resourcessForStore.add(actionStoreInfoGet);
		
		
		Action actionOrderSocket = actionRepository.findOneByActionId("OrderSocket");
		if (null == actionOrderSocket) {
			actionOrderSocket = new Action();
			actionOrderSocket.setActionId("OrderSocket");
			actionOrderSocket.setActionNm("API - 주문 통지 API");
			actionOrderSocket.setActionTp(codeLink);
			actionOrderSocket.setResTp(codeResAction);
			actionOrderSocket.setUrl("/websocket/*/*");
			actionOrderSocket.setMethod("GET");
			actionOrderSocket.setResDesc(actionOrderSocket.getActionNm());
			actionRepository.save(actionOrderSocket);
		}
		resourcessForStore.add(actionOrderSocket);
		
	}
	
	
	/**
	 * 매장 담당자용 계정 정보 수정
	 */
	private void initStoreUserInfoMenu() {
		Action actionGetSelfUserInfo = actionRepository.findOneByActionId("GetSelfUserInfoForStore");
		if (null == actionGetSelfUserInfo) {
			actionGetSelfUserInfo = new Action();
			actionGetSelfUserInfo.setActionId("GetSelfUserInfoForStore");
			actionGetSelfUserInfo.setActionNm("링크 - 계정 정보 조회(매장 회원)");
			actionGetSelfUserInfo.setActionTp(codeLink);
			actionGetSelfUserInfo.setResTp(codeResAction);
			actionGetSelfUserInfo.setUrl("/user/info/store");
			actionGetSelfUserInfo.setMethod("GET");
			actionGetSelfUserInfo.setResDesc(actionGetSelfUserInfo.getActionNm());
			actionRepository.save(actionGetSelfUserInfo);
		}
		resourcessForStore.add(actionGetSelfUserInfo);
		
		Action actionUpSelfUserInfo = actionRepository.findOneByActionId("UpSelfUserInfoForStore");
		if (null == actionUpSelfUserInfo) {
			actionUpSelfUserInfo = new Action();
			actionUpSelfUserInfo.setActionId("UpSelfUserInfoForStore");
			actionUpSelfUserInfo.setActionNm("버튼 - 계정 정보 수정(매장 회원)");
			actionUpSelfUserInfo.setActionTp(codeBtn);
			actionUpSelfUserInfo.setResTp(codeResAction);
			actionUpSelfUserInfo.setUrl("/user/info/store/*");
			actionUpSelfUserInfo.setMethod("PUT");
			actionUpSelfUserInfo.setResDesc(actionUpSelfUserInfo.getActionNm());
			actionRepository.save(actionUpSelfUserInfo);
		}
		resourcessForStore.add(actionUpSelfUserInfo);
	}
	
	
	/**
	 * 카테고리 관리 메뉴 등록
	 */
	private void initCategoryMenu() {
		Menu menuCategory = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "카테고리 관리", 1);
		if (null == menuCategory) {
			menuCategory = new Menu();
			menuCategory.setMenuNm("카테고리 관리");
			menuCategory.setMenuIcon("icon-category");
			menuCategory.setMlevel(1);
			menuCategory.setOrd(8);
			menuCategory.setResTp(codeResMenu);
			menuCategory.setUrl("/v/business/category/list_for_store");
			menuCategory.setMethod("GET");
			menuCategory.setResDesc("매장 관리자 " + menuCategory.getMenuNm());
			menuRepository.save(menuCategory);
		}
		resourcessForStore.add(menuCategory);
		
		Action actionCategoryAdd = actionRepository.findOneByActionId("CategoryForSotreAdd");
		if (null == actionCategoryAdd) {
			actionCategoryAdd = new Action();
			actionCategoryAdd.setActionId("CategoryForSotreAdd");
			actionCategoryAdd.setActionNm("버튼 - 카테고리 등록(매장 관리자)");
			actionCategoryAdd.setActionTp(codeBtn);
			actionCategoryAdd.setResTp(codeResAction);
			actionCategoryAdd.setUrl("/category/store");
			actionCategoryAdd.setMethod("POST");
			actionCategoryAdd.setResDesc(actionCategoryAdd.getActionNm());
			actionRepository.save(actionCategoryAdd);
		}
		resourcessForStore.add(actionCategoryAdd);
		
		Action actionCategoryGet = actionRepository.findOneByActionId("CategoryForSotreGet");
		if (null == actionCategoryGet) {
			actionCategoryGet = new Action();
			actionCategoryGet.setActionId("CategoryForSotreGet");
			actionCategoryGet.setActionNm("버튼 - 카테고리 정보 조회(매장 관리자)");
			actionCategoryGet.setActionTp(codeLink);
			actionCategoryGet.setResTp(codeResAction);
			actionCategoryGet.setUrl("/category/store/*");
			actionCategoryGet.setMethod("GET");
			actionCategoryGet.setResDesc(actionCategoryGet.getActionNm());
			actionRepository.save(actionCategoryGet);
		}
		resourcessForStore.add(actionCategoryGet);
		
		Action actionCategoryUp = actionRepository.findOneByActionId("CategoryForSotreUp");
		if (null == actionCategoryUp) {
			actionCategoryUp = new Action();
			actionCategoryUp.setActionId("CategoryForSotreUp");
			actionCategoryUp.setActionNm("버튼 - 카테고리 수정(매장 관리자)");
			actionCategoryUp.setActionTp(codeBtn);
			actionCategoryUp.setResTp(codeResAction);
			actionCategoryUp.setUrl("/category/store/*");
			actionCategoryUp.setMethod("PUT");
			actionCategoryUp.setResDesc(actionCategoryUp.getActionNm());
			actionRepository.save(actionCategoryUp);
		}
		resourcessForStore.add(actionCategoryUp);
		
		Action actionCategoryDel = actionRepository.findOneByActionId("CategoryForSotreDel");
		if (null == actionCategoryDel) {
			actionCategoryDel = new Action();
			actionCategoryDel.setActionId("CategoryForSotreDel");
			actionCategoryDel.setActionNm("버튼 - 카테고리 삭제(매장 관리자)");
			actionCategoryDel.setActionTp(codeBtn);
			actionCategoryDel.setResTp(codeResAction);
			actionCategoryDel.setUrl("/category/store/*");
			actionCategoryDel.setMethod("DELETE");
			actionCategoryDel.setResDesc(actionCategoryDel.getActionNm());
			actionRepository.save(actionCategoryDel);
		}
		resourcessForStore.add(actionCategoryDel);
		
		Action actionCategorySearch = actionRepository.findOneByActionId("CategoryForSotreSearch");
		if (null == actionCategorySearch) {
			actionCategorySearch = new Action();
			actionCategorySearch.setActionId("CategoryForSotreSearch");
			actionCategorySearch.setActionNm("버튼 - 카테고리 리스트 조회(매장 관리자)");
			actionCategorySearch.setActionTp(codeBtn);
			actionCategorySearch.setResTp(codeResAction);
			actionCategorySearch.setUrl("/category/store");
			actionCategorySearch.setMethod("GET");
			actionCategorySearch.setResDesc(actionCategorySearch.getActionNm());
			actionRepository.save(actionCategorySearch);
		}
		resourcessForStore.add(actionCategorySearch);
	}
	
	/**
	 * 객실 정보 관리 메뉴 등록
	 */
	private void initRoomMenu() {
		Menu menuRoom = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "객실 관리", 1);
		if (null == menuRoom) {
			menuRoom = new Menu();
			menuRoom.setMenuNm("객실 관리");
			menuRoom.setMenuIcon("icon-calendar");
			menuRoom.setMlevel(1);
			menuRoom.setOrd(15);
			menuRoom.setResTp(codeResMenu);
			menuRoom.setUrl("/v/business/room/list_for_store");
			menuRoom.setMethod("GET");
			menuRoom.setResDesc("매장 관리자 " + menuRoom.getMenuNm());
			menuRepository.save(menuRoom);
		}
		resourcessForStore.add(menuRoom);
		
		Action actionStoreRoomAdd = actionRepository.findOneByActionId("StoreRoomAdd");
		if (null == actionStoreRoomAdd) {
			actionStoreRoomAdd = new Action();
			actionStoreRoomAdd.setActionId("StoreRoomAdd");
			actionStoreRoomAdd.setActionNm("버튼 - 객실 정보 등록");
			actionStoreRoomAdd.setActionTp(codeBtn);
			actionStoreRoomAdd.setResTp(codeResAction);
			actionStoreRoomAdd.setUrl("/room");
			actionStoreRoomAdd.setMethod("POST");
			actionStoreRoomAdd.setResDesc(actionStoreRoomAdd.getActionNm());
			actionRepository.save(actionStoreRoomAdd);
		}
		resourcessForStore.add(actionStoreRoomAdd);
		
		Action actionStoreRoomsAdd = actionRepository.findOneByActionId("StoreRoomsAdd");
		if (null == actionStoreRoomsAdd) {
			actionStoreRoomsAdd = new Action();
			actionStoreRoomsAdd.setActionId("StoreRoomsAdd");
			actionStoreRoomsAdd.setActionNm("버튼 - 객실 정보 일괄등록");
			actionStoreRoomsAdd.setActionTp(codeBtn);
			actionStoreRoomsAdd.setResTp(codeResAction);
			actionStoreRoomsAdd.setUrl("/rooms");
			actionStoreRoomsAdd.setMethod("POST");
			actionStoreRoomsAdd.setResDesc(actionStoreRoomsAdd.getActionNm());
			actionRepository.save(actionStoreRoomsAdd);
		}
		resourcessForStore.add(actionStoreRoomsAdd);
		
		Action actionStoreRoomDel = actionRepository.findOneByActionId("StoreRoomDel");
		if (null == actionStoreRoomDel) {
			actionStoreRoomDel = new Action();
			actionStoreRoomDel.setActionId("StoreRoomDel");
			actionStoreRoomDel.setActionNm("버튼 - 객실정보 삭제");
			actionStoreRoomDel.setActionTp(codeBtn);
			actionStoreRoomDel.setResTp(codeResAction);
			actionStoreRoomDel.setUrl("/room/*");
			actionStoreRoomDel.setMethod("DELETE");
			actionStoreRoomDel.setResDesc(actionStoreRoomDel.getActionNm());
			actionRepository.save(actionStoreRoomDel);
		}
		resourcessForStore.add(actionStoreRoomDel);
		
		Action actionStoreRoomSearch = actionRepository.findOneByActionId("StoreRoomSearch");
		if (null == actionStoreRoomSearch) {
			actionStoreRoomSearch = new Action();
			actionStoreRoomSearch.setActionId("StoreRoomSearch");
			actionStoreRoomSearch.setActionNm("버튼 - 객실정보 리스트 조회");
			actionStoreRoomSearch.setActionTp(codeBtn);
			actionStoreRoomSearch.setResTp(codeResAction);
			actionStoreRoomSearch.setUrl("/room");
			actionStoreRoomSearch.setMethod("GET");
			actionStoreRoomSearch.setResDesc(actionStoreRoomSearch.getActionNm());
			actionRepository.save(actionStoreRoomSearch);
		}
		resourcessForStore.add(actionStoreRoomSearch);
	}
	
	/**
	 * 주문자 화면 디자인 설정(23.08.09)
	 */
	private void initStoreDesign() {
		Menu menuDesign = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "디자인 설정", 1);
		if (null == menuDesign) {
			menuDesign = new Menu();
			menuDesign.setMenuNm("디자인 설정");
			menuDesign.setMenuIcon("icon-skin_light");
			menuDesign.setMlevel(1);
			menuDesign.setOrd(15);
			menuDesign.setResTp(codeResMenu);
			menuDesign.setUrl("/v/business/design/for_store");
			menuDesign.setMethod("GET");
			menuDesign.setResDesc("매장 관리자 " + menuDesign.getMenuNm());
			menuRepository.save(menuDesign);
		}
		resourcessForStore.add(menuDesign);
		
		Action actionStoreDesignAdd = actionRepository.findOneByActionId("StoreDesign");
		if (null == actionStoreDesignAdd) {
			actionStoreDesignAdd = new Action();
			actionStoreDesignAdd.setActionId("StoreDesign");
			actionStoreDesignAdd.setActionNm("버튼 - 디자인 설정");
			actionStoreDesignAdd.setActionTp(codeBtn);
			actionStoreDesignAdd.setResTp(codeResAction);
			actionStoreDesignAdd.setUrl("/store/design");
			actionStoreDesignAdd.setMethod("POST");
			actionStoreDesignAdd.setResDesc(actionStoreDesignAdd.getActionNm());
			actionRepository.save(actionStoreDesignAdd);
		}
		resourcessForStore.add(actionStoreDesignAdd);
	}
	
	/**
	 * 재료관리 메뉴 등록
	 */
	private void initStuffMenu() {
		Menu menuStuff = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "재료 관리", 1);
		if (null == menuStuff) {
			menuStuff = new Menu();
			menuStuff.setMenuNm("재료 관리");
			menuStuff.setMenuIcon("icon-stuff");
			menuStuff.setMlevel(1);
			menuStuff.setOrd(9);
			menuStuff.setResTp(codeResMenu);
			menuStuff.setUrl("/v/business/stuff/list_for_store");
			menuStuff.setMethod("GET");
			menuStuff.setResDesc("매장 관리자 " + menuStuff.getMenuNm());
			menuRepository.save(menuStuff);
		}
		resourcessForStore.add(menuStuff);
		
		Action actionStuffAdd = actionRepository.findOneByActionId("StuffForStoreAdd");
		if (null == actionStuffAdd) {
			actionStuffAdd = new Action();
			actionStuffAdd.setActionId("StuffForStoreAdd");
			actionStuffAdd.setActionNm("버튼 - 재료 등록(매장 관리자)");
			actionStuffAdd.setActionTp(codeBtn);
			actionStuffAdd.setResTp(codeResAction);
			actionStuffAdd.setUrl("/stuff/store");
			actionStuffAdd.setMethod("POST");
			actionStuffAdd.setResDesc(actionStuffAdd.getActionNm());
			actionRepository.save(actionStuffAdd);
		}
		resourcessForStore.add(actionStuffAdd);
		
		Action actionStuffGet = actionRepository.findOneByActionId("StuffForStoreGet");
		if (null == actionStuffGet) {
			actionStuffGet = new Action();
			actionStuffGet.setActionId("StuffForStoreGet");
			actionStuffGet.setActionNm("버튼 - 재료 정보 조회(매장 관리자)");
			actionStuffGet.setActionTp(codeLink);
			actionStuffGet.setResTp(codeResAction);
			actionStuffGet.setUrl("/stuff/store/*");
			actionStuffGet.setMethod("GET");
			actionStuffGet.setResDesc(actionStuffGet.getActionNm());
			actionRepository.save(actionStuffGet);
		}
		resourcessForStore.add(actionStuffGet);
		
		Action actionStuffUp = actionRepository.findOneByActionId("StuffForStoreUp");
		if (null == actionStuffUp) {
			actionStuffUp = new Action();
			actionStuffUp.setActionId("StuffForStoreUp");
			actionStuffUp.setActionNm("버튼 - 재료 수정(매장 관리자)");
			actionStuffUp.setActionTp(codeBtn);
			actionStuffUp.setResTp(codeResAction);
			actionStuffUp.setUrl("/stuff/store/*");
			actionStuffUp.setMethod("PUT");
			actionStuffUp.setResDesc(actionStuffUp.getActionNm());
			actionRepository.save(actionStuffUp);
		}
		resourcessForStore.add(actionStuffUp);
		
		Action actionStuffDel = actionRepository.findOneByActionId("StuffForStoreDel");
		if (null == actionStuffDel) {
			actionStuffDel = new Action();
			actionStuffDel.setActionId("StuffForStoreDel");
			actionStuffDel.setActionNm("버튼 - 재료 삭제(매장 관리자)");
			actionStuffDel.setActionTp(codeBtn);
			actionStuffDel.setResTp(codeResAction);
			actionStuffDel.setUrl("/stuff/store/*");
			actionStuffDel.setMethod("DELETE");
			actionStuffDel.setResDesc(actionStuffDel.getActionNm());
			actionRepository.save(actionStuffDel);
		}
		resourcessForStore.add(actionStuffDel);
		
		Action actionStuffSearch = actionRepository.findOneByActionId("StuffForStoreSearch");
		if (null == actionStuffSearch) {
			actionStuffSearch = new Action();
			actionStuffSearch.setActionId("StuffForStoreSearch");
			actionStuffSearch.setActionNm("버튼 - 재료 리스트 조회(매장 관리자)");
			actionStuffSearch.setActionTp(codeBtn);
			actionStuffSearch.setResTp(codeResAction);
			actionStuffSearch.setUrl("/stuff/store");
			actionStuffSearch.setMethod("GET");
			actionStuffSearch.setResDesc(actionStuffSearch.getActionNm());
			actionRepository.save(actionStuffSearch);
		}
		resourcessForStore.add(actionStuffSearch);
		
//		Action actionStuffAddNation = actionRepository.findOneByActionId("StuffForStoreAddNation");
//		if (null == actionStuffAddNation) {
//			actionStuffAddNation = new Action();
//			actionStuffAddNation.setActionId("StuffForStoreAddNation");
//			actionStuffAddNation.setActionNm("버튼 - 재료 원산지 등록(매장 관리자)");
//			actionStuffAddNation.setActionTp(codeBtn);
//			actionStuffAddNation.setResTp(codeResAction);
//			actionStuffAddNation.setUrl("/stuff/store/nation/*");
//			actionStuffAddNation.setMethod("POST");
//			actionStuffAddNation.setResDesc(actionStuffAddNation.getActionNm());
//			actionRepository.save(actionStuffAddNation);
//		}
//		resourcessForStore.add(actionStuffAddNation);
		
		Action actionStuffDown = actionRepository.findOneByActionId("StuffForStoreDown");
		if (null == actionStuffDown) {
			actionStuffDown = new Action();
			actionStuffDown.setActionId("StuffForStoreDown");
			actionStuffDown.setActionNm("버튼 - 매장 재료 리스트 양식 다운로드(매장 관리자)");
			actionStuffDown.setActionTp(codeBtn);
			actionStuffDown.setResTp(codeResAction);
			actionStuffDown.setUrl("/stuff/store/down/*");
			actionStuffDown.setMethod("GET");
			actionStuffDown.setResDesc(actionStuffDown.getActionNm());
			actionRepository.save(actionStuffDown);
		}
		resourcessForStore.add(actionStuffDown);
		
		Action actionStuffUpData = actionRepository.findOneByActionId("StuffForStoreUpData");
		if (null == actionStuffUpData) {
			actionStuffUpData = new Action();
			actionStuffUpData.setActionId("StuffForStoreUpData");
			actionStuffUpData.setActionNm("버튼 - 매장 재료 데이타 업로드(매장 관리자)");
			actionStuffUpData.setActionTp(codeBtn);
			actionStuffUpData.setResTp(codeResAction);
			actionStuffUpData.setUrl("/stuff/store/up/*");
			actionStuffUpData.setMethod("POST");
			actionStuffUpData.setResDesc(actionStuffUpData.getActionNm());
			actionRepository.save(actionStuffUpData);
		}
		resourcessForStore.add(actionStuffUpData);
	}
	
	
	
	
	/**
	 * 옵션 그룹 관리 메뉴 등록
	 */
	private void initSmenuOptTpMenu() {
		Menu menuSmenuOptTp = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "옵션 그룹 관리", 1);
		if (null == menuSmenuOptTp) {
			menuSmenuOptTp = new Menu();
			menuSmenuOptTp.setMenuNm("옵션 그룹 관리");
			menuSmenuOptTp.setMenuIcon("icon-options");
			menuSmenuOptTp.setMlevel(1);
			menuSmenuOptTp.setOrd(10);
			menuSmenuOptTp.setResTp(codeResMenu);
			menuSmenuOptTp.setUrl("/v/business/smenuOptTp/list_for_store");
			menuSmenuOptTp.setMethod("GET");
			menuSmenuOptTp.setResDesc("매장 관리자 " + menuSmenuOptTp.getMenuNm());
			menuRepository.save(menuSmenuOptTp);
		}
		resourcessForStore.add(menuSmenuOptTp);
		
		Action actionSmenuOptTpAdd = actionRepository.findOneByActionId("SmenuOptTpForStoreAdd");
		if (null == actionSmenuOptTpAdd) {
			actionSmenuOptTpAdd = new Action();
			actionSmenuOptTpAdd.setActionId("SmenuOptTpForStoreAdd");
			actionSmenuOptTpAdd.setActionNm("버튼 - 옵션 그룹 등록(매장 관리자)");
			actionSmenuOptTpAdd.setActionTp(codeBtn);
			actionSmenuOptTpAdd.setResTp(codeResAction);
			actionSmenuOptTpAdd.setUrl("/smenuOptTp/store");
			actionSmenuOptTpAdd.setMethod("POST");
			actionSmenuOptTpAdd.setResDesc(actionSmenuOptTpAdd.getActionNm());
			actionRepository.save(actionSmenuOptTpAdd);
		}
		resourcessForStore.add(actionSmenuOptTpAdd);
		
		Action actionSmenuOptTpGet = actionRepository.findOneByActionId("SmenuOptTpForStoreGet");
		if (null == actionSmenuOptTpGet) {
			actionSmenuOptTpGet = new Action();
			actionSmenuOptTpGet.setActionId("SmenuOptTpForStoreGet");
			actionSmenuOptTpGet.setActionNm("버튼 - 옵션 그룹 정보 조회(매장 관리자)");
			actionSmenuOptTpGet.setActionTp(codeLink);
			actionSmenuOptTpGet.setResTp(codeResAction);
			actionSmenuOptTpGet.setUrl("/smenuOptTp/store/*");
			actionSmenuOptTpGet.setMethod("GET");
			actionSmenuOptTpGet.setResDesc(actionSmenuOptTpGet.getActionNm());
			actionRepository.save(actionSmenuOptTpGet);
		}
		resourcessForStore.add(actionSmenuOptTpGet);
		
		Action actionSmenuOptTpUp = actionRepository.findOneByActionId("SmenuOptTpForStoreUp");
		if (null == actionSmenuOptTpUp) {
			actionSmenuOptTpUp = new Action();
			actionSmenuOptTpUp.setActionId("SmenuOptTpForStoreUp");
			actionSmenuOptTpUp.setActionNm("버튼 - 옵션 그룹 수정(매장 관리자)");
			actionSmenuOptTpUp.setActionTp(codeBtn);
			actionSmenuOptTpUp.setResTp(codeResAction);
			actionSmenuOptTpUp.setUrl("/smenuOptTp/store/*");
			actionSmenuOptTpUp.setMethod("PUT");
			actionSmenuOptTpUp.setResDesc(actionSmenuOptTpUp.getActionNm());
			actionRepository.save(actionSmenuOptTpUp);
		}
		resourcessForStore.add(actionSmenuOptTpUp);
		
		Action actionSmenuOptTpDel = actionRepository.findOneByActionId("SmenuOptTpForStoreDel");
		if (null == actionSmenuOptTpDel) {
			actionSmenuOptTpDel = new Action();
			actionSmenuOptTpDel.setActionId("SmenuOptTpForStoreDel");
			actionSmenuOptTpDel.setActionNm("버튼 - 옵션 그룹 삭제(매장 관리자)");
			actionSmenuOptTpDel.setActionTp(codeBtn);
			actionSmenuOptTpDel.setResTp(codeResAction);
			actionSmenuOptTpDel.setUrl("/smenuOptTp/store/*");
			actionSmenuOptTpDel.setMethod("DELETE");
			actionSmenuOptTpDel.setResDesc(actionSmenuOptTpDel.getActionNm());
			actionRepository.save(actionSmenuOptTpDel);
		}
		resourcessForStore.add(actionSmenuOptTpDel);
		
		Action actionSmenuOptTpSearch = actionRepository.findOneByActionId("SmenuOptTpForStoreSearch");
		if (null == actionSmenuOptTpSearch) {
			actionSmenuOptTpSearch = new Action();
			actionSmenuOptTpSearch.setActionId("SmenuOptTpForStoreSearch");
			actionSmenuOptTpSearch.setActionNm("버튼 - 옵션 그룹 리스트 조회(매장 관리자)");
			actionSmenuOptTpSearch.setActionTp(codeBtn);
			actionSmenuOptTpSearch.setResTp(codeResAction);
			actionSmenuOptTpSearch.setUrl("/smenuOptTp/store");
			actionSmenuOptTpSearch.setMethod("GET");
			actionSmenuOptTpSearch.setResDesc(actionSmenuOptTpSearch.getActionNm());
			actionRepository.save(actionSmenuOptTpSearch);
		}
		resourcessForStore.add(actionSmenuOptTpSearch);
	}
	
	
	/**
	 * 옵션 관리 메뉴 등록
	 */
	private void initSmenuOptMenu() {
		Menu menuSmenuOpt = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "옵션 관리", 1);
		if (null == menuSmenuOpt) {
			menuSmenuOpt = new Menu();
			menuSmenuOpt.setMenuNm("옵션 관리");
			menuSmenuOpt.setMenuIcon("icon-options");
			menuSmenuOpt.setMlevel(1);
			menuSmenuOpt.setOrd(11);
			menuSmenuOpt.setResTp(codeResMenu);
			menuSmenuOpt.setUrl("/v/business/smenuOpt/list_for_store");
			menuSmenuOpt.setMethod("GET");
			menuSmenuOpt.setResDesc("매장 관리자 " + menuSmenuOpt.getMenuNm());
			menuRepository.save(menuSmenuOpt);
		}
		resourcessForStore.add(menuSmenuOpt);
		
		Action actionSmenuOptAdd = actionRepository.findOneByActionId("SmenuOptForStoreAdd");
		if (null == actionSmenuOptAdd) {
			actionSmenuOptAdd = new Action();
			actionSmenuOptAdd.setActionId("SmenuOptForStoreAdd");
			actionSmenuOptAdd.setActionNm("버튼 - 옵션 등록(매장 관리자)");
			actionSmenuOptAdd.setActionTp(codeBtn);
			actionSmenuOptAdd.setResTp(codeResAction);
			actionSmenuOptAdd.setUrl("/smenuOpt/store");
			actionSmenuOptAdd.setMethod("POST");
			actionSmenuOptAdd.setResDesc(actionSmenuOptAdd.getActionNm());
			actionRepository.save(actionSmenuOptAdd);
		}
		resourcessForStore.add(actionSmenuOptAdd);
		
		Action actionSmenuOptGet = actionRepository.findOneByActionId("SmenuOptForStoreGet");
		if (null == actionSmenuOptGet) {
			actionSmenuOptGet = new Action();
			actionSmenuOptGet.setActionId("SmenuOptForStoreGet");
			actionSmenuOptGet.setActionNm("버튼 - 옵션 정보 조회(매장 관리자)");
			actionSmenuOptGet.setActionTp(codeLink);
			actionSmenuOptGet.setResTp(codeResAction);
			actionSmenuOptGet.setUrl("/smenuOpt/store/*");
			actionSmenuOptGet.setMethod("GET");
			actionSmenuOptGet.setResDesc(actionSmenuOptGet.getActionNm());
			actionRepository.save(actionSmenuOptGet);
		}
		resourcessForStore.add(actionSmenuOptGet);
		
		Action actionSmenuOptUp = actionRepository.findOneByActionId("SmenuOptForStoreUp");
		if (null == actionSmenuOptUp) {
			actionSmenuOptUp = new Action();
			actionSmenuOptUp.setActionId("SmenuOptForStoreUp");
			actionSmenuOptUp.setActionNm("버튼 - 옵션 수정(매장 관리자)");
			actionSmenuOptUp.setActionTp(codeBtn);
			actionSmenuOptUp.setResTp(codeResAction);
			actionSmenuOptUp.setUrl("/smenuOpt/store/*");
			actionSmenuOptUp.setMethod("PUT");
			actionSmenuOptUp.setResDesc(actionSmenuOptUp.getActionNm());
			actionRepository.save(actionSmenuOptUp);
		}
		resourcessForStore.add(actionSmenuOptUp);
		
		Action actionSmenuOptDel = actionRepository.findOneByActionId("SmenuOptForStoreDel");
		if (null == actionSmenuOptDel) {
			actionSmenuOptDel = new Action();
			actionSmenuOptDel.setActionId("SmenuOptForStoreDel");
			actionSmenuOptDel.setActionNm("버튼 - 옵션 삭제(매장 관리자)");
			actionSmenuOptDel.setActionTp(codeBtn);
			actionSmenuOptDel.setResTp(codeResAction);
			actionSmenuOptDel.setUrl("/smenuOpt/store/*");
			actionSmenuOptDel.setMethod("DELETE");
			actionSmenuOptDel.setResDesc(actionSmenuOptDel.getActionNm());
			actionRepository.save(actionSmenuOptDel);
		}
		resourcessForStore.add(actionSmenuOptDel);
		
		Action actionSmenuOptSearch = actionRepository.findOneByActionId("SmenuOptForStoreSearch");
		if (null == actionSmenuOptSearch) {
			actionSmenuOptSearch = new Action();
			actionSmenuOptSearch.setActionId("SmenuOptForStoreSearch");
			actionSmenuOptSearch.setActionNm("버튼 - 옵션 리스트 조회(매장 관리자)");
			actionSmenuOptSearch.setActionTp(codeBtn);
			actionSmenuOptSearch.setResTp(codeResAction);
			actionSmenuOptSearch.setUrl("/smenuOpt/store");
			actionSmenuOptSearch.setMethod("GET");
			actionSmenuOptSearch.setResDesc(actionSmenuOptSearch.getActionNm());
			actionRepository.save(actionSmenuOptSearch);
		}
		resourcessForStore.add(actionSmenuOptSearch);
	}
	
	
	/**
	 * 할인 관리 메뉴 등록
	 */
	private void initDiscountMenu() {
		Menu menuDiscount = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "할인 관리", 1);
		if (null == menuDiscount) {
			menuDiscount = new Menu();
			menuDiscount.setMenuNm("할인 관리");
			menuDiscount.setMenuIcon("icon-discount");
			menuDiscount.setMlevel(1);
			menuDiscount.setOrd(12);
			menuDiscount.setResTp(codeResMenu);
			menuDiscount.setUrl("/v/business/discount/list_for_store");
			menuDiscount.setMethod("GET");
			menuDiscount.setResDesc("매장 관리자 " + menuDiscount.getMenuNm());
			menuRepository.save(menuDiscount);
		}
		resourcessForStore.add(menuDiscount);
		
		Action actionDiscountAdd = actionRepository.findOneByActionId("DiscountForStoreAdd");
		if (null == actionDiscountAdd) {
			actionDiscountAdd = new Action();
			actionDiscountAdd.setActionId("DiscountForStoreAdd");
			actionDiscountAdd.setActionNm("버튼 - 할인 등록(매장 관리자)");
			actionDiscountAdd.setActionTp(codeBtn);
			actionDiscountAdd.setResTp(codeResAction);
			actionDiscountAdd.setUrl("/discount/store");
			actionDiscountAdd.setMethod("POST");
			actionDiscountAdd.setResDesc(actionDiscountAdd.getActionNm());
			actionRepository.save(actionDiscountAdd);
		}
		resourcessForStore.add(actionDiscountAdd);
		
		Action actionDiscountGet = actionRepository.findOneByActionId("DiscountForStoreGet");
		if (null == actionDiscountGet) {
			actionDiscountGet = new Action();
			actionDiscountGet.setActionId("DiscountForStoreGet");
			actionDiscountGet.setActionNm("버튼 - 할인 정보 조회(매장 관리자)");
			actionDiscountGet.setActionTp(codeLink);
			actionDiscountGet.setResTp(codeResAction);
			actionDiscountGet.setUrl("/discount/store/*");
			actionDiscountGet.setMethod("GET");
			actionDiscountGet.setResDesc(actionDiscountGet.getActionNm());
			actionRepository.save(actionDiscountGet);
		}
		resourcessForStore.add(actionDiscountGet);
		
		Action actionDiscountUp = actionRepository.findOneByActionId("DiscountForStoreUp");
		if (null == actionDiscountUp) {
			actionDiscountUp = new Action();
			actionDiscountUp.setActionId("DiscountForStoreUp");
			actionDiscountUp.setActionNm("버튼 - 할인 수정(매장 관리자)");
			actionDiscountUp.setActionTp(codeBtn);
			actionDiscountUp.setResTp(codeResAction);
			actionDiscountUp.setUrl("/discount/store/*");
			actionDiscountUp.setMethod("PUT");
			actionDiscountUp.setResDesc(actionDiscountUp.getActionNm());
			actionRepository.save(actionDiscountUp);
		}
		resourcessForStore.add(actionDiscountUp);
		
		Action actionDiscountDel = actionRepository.findOneByActionId("DiscountForStoreDel");
		if (null == actionDiscountDel) {
			actionDiscountDel = new Action();
			actionDiscountDel.setActionId("DiscountForStoreDel");
			actionDiscountDel.setActionNm("버튼 - 할인 삭제(매장 관리자)");
			actionDiscountDel.setActionTp(codeBtn);
			actionDiscountDel.setResTp(codeResAction);
			actionDiscountDel.setUrl("/discount/store/*");
			actionDiscountDel.setMethod("DELETE");
			actionDiscountDel.setResDesc(actionDiscountDel.getActionNm());
			actionRepository.save(actionDiscountDel);
		}
		resourcessForStore.add(actionDiscountDel);
		
		Action actionDiscountSearch = actionRepository.findOneByActionId("DiscountForStoreSearch");
		if (null == actionDiscountSearch) {
			actionDiscountSearch = new Action();
			actionDiscountSearch.setActionId("DiscountForStoreSearch");
			actionDiscountSearch.setActionNm("버튼 - 할인 리스트 조회(매장 관리자)");
			actionDiscountSearch.setActionTp(codeBtn);
			actionDiscountSearch.setResTp(codeResAction);
			actionDiscountSearch.setUrl("/discount/store");
			actionDiscountSearch.setMethod("GET");
			actionDiscountSearch.setResDesc(actionDiscountSearch.getActionNm());
			actionRepository.save(actionDiscountSearch);
		}
		resourcessForStore.add(actionDiscountSearch);
	}
	
	
	/**
	 * 메뉴 관리 메뉴 등록
	 */
	private void initSmenuMenu() {
		Menu menuSmenu = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "메뉴 관리", 1);
		if (null == menuSmenu) {
			menuSmenu = new Menu();
			menuSmenu.setMenuNm("메뉴 관리");
			menuSmenu.setMenuIcon("icon-menu");
			menuSmenu.setMlevel(1);
			menuSmenu.setOrd(13);
			menuSmenu.setResTp(codeResMenu);
			menuSmenu.setUrl("/v/business/smenu/list_for_store");
			menuSmenu.setMethod("GET");
			menuSmenu.setResDesc("매장 관리자 " + menuSmenu.getMenuNm());
			menuRepository.save(menuSmenu);
		}
		resourcessForStore.add(menuSmenu);
		
		Action actionSmenuAdd = actionRepository.findOneByActionId("SmenuForStoreAdd");
		if (null == actionSmenuAdd) {
			actionSmenuAdd = new Action();
			actionSmenuAdd.setActionId("SmenuForStoreAdd");
			actionSmenuAdd.setActionNm("버튼 - 매장 메뉴 등록(매장 관리자)");
			actionSmenuAdd.setActionTp(codeBtn);
			actionSmenuAdd.setResTp(codeResAction);
			actionSmenuAdd.setUrl("/smenu/store");
			actionSmenuAdd.setMethod("POST");
			actionSmenuAdd.setResDesc(actionSmenuAdd.getActionNm());
			actionRepository.save(actionSmenuAdd);
		}
		resourcessForStore.add(actionSmenuAdd);
		
		Action actionSmenuGet = actionRepository.findOneByActionId("SmenuForStoreGet");
		if (null == actionSmenuGet) {
			actionSmenuGet = new Action();
			actionSmenuGet.setActionId("SmenuForStoreGet");
			actionSmenuGet.setActionNm("버튼 - 매장 메뉴 정보 조회(매장 관리자)");
			actionSmenuGet.setActionTp(codeLink);
			actionSmenuGet.setResTp(codeResAction);
			actionSmenuGet.setUrl("/smenu/store/*");
			actionSmenuGet.setMethod("GET");
			actionSmenuGet.setResDesc(actionSmenuGet.getActionNm());
			actionRepository.save(actionSmenuGet);
		}
		resourcessForStore.add(actionSmenuGet);
		
		Action actionSmenuUp = actionRepository.findOneByActionId("SmenuForStoreUp");
		if (null == actionSmenuUp) {
			actionSmenuUp = new Action();
			actionSmenuUp.setActionId("SmenuForStoreUp");
			actionSmenuUp.setActionNm("버튼 - 매장 메뉴 수정(매장 관리자)");
			actionSmenuUp.setActionTp(codeBtn);
			actionSmenuUp.setResTp(codeResAction);
			actionSmenuUp.setUrl("/smenu/store/*");
			actionSmenuUp.setMethod("PUT");
			actionSmenuUp.setResDesc(actionSmenuUp.getActionNm());
			actionRepository.save(actionSmenuUp);
		}
		resourcessForStore.add(actionSmenuUp);
		
		Action actionSmenuDel = actionRepository.findOneByActionId("SmenuForStoreDel");
		if (null == actionSmenuDel) {
			actionSmenuDel = new Action();
			actionSmenuDel.setActionId("SmenuForStoreDel");
			actionSmenuDel.setActionNm("버튼 - 매장 메뉴 삭제(매장 관리자)");
			actionSmenuDel.setActionTp(codeBtn);
			actionSmenuDel.setResTp(codeResAction);
			actionSmenuDel.setUrl("/smenu/store/*");
			actionSmenuDel.setMethod("DELETE");
			actionSmenuDel.setResDesc(actionSmenuDel.getActionNm());
			actionRepository.save(actionSmenuDel);
		}
		resourcessForStore.add(actionSmenuDel);
		
		Action actionSmenuUse = actionRepository.findOneByActionId("SmenuForStoreUse");
		if (null == actionSmenuUse) {
			actionSmenuUse = new Action();
			actionSmenuUse.setActionId("SmenuForStoreUse");
			actionSmenuUse.setActionNm("버튼 - 매장 메뉴 반영(매장 관리자)");
			actionSmenuUse.setActionTp(codeBtn);
			actionSmenuUse.setResTp(codeResAction);
			actionSmenuUse.setUrl("/smenu/store/use/*");
			actionSmenuUse.setMethod("PUT");
			actionSmenuUse.setResDesc(actionSmenuUse.getActionNm());
			actionRepository.save(actionSmenuUse);
		}
		resourcessForStore.add(actionSmenuUse);
		
		Action actionSmenuSearch = actionRepository.findOneByActionId("SmenuForStoreSearch");
		if (null == actionSmenuSearch) {
			actionSmenuSearch = new Action();
			actionSmenuSearch.setActionId("SmenuForStoreSearch");
			actionSmenuSearch.setActionNm("버튼 - 매장 메뉴 리스트 조회(매장 관리자)");
			actionSmenuSearch.setActionTp(codeBtn);
			actionSmenuSearch.setResTp(codeResAction);
			actionSmenuSearch.setUrl("/smenu/store");
			actionSmenuSearch.setMethod("GET");
			actionSmenuSearch.setResDesc(actionSmenuSearch.getActionNm());
			actionRepository.save(actionSmenuSearch);
		}
		resourcessForStore.add(actionSmenuSearch);
		
		Action actionSmenuDown = actionRepository.findOneByActionId("SmenuForStoreDown");
		if (null == actionSmenuDown) {
			actionSmenuDown = new Action();
			actionSmenuDown.setActionId("SmenuForStoreDown");
			actionSmenuDown.setActionNm("버튼 - 매장 메뉴 리스트 양식 다운로드(매장 관리자)");
			actionSmenuDown.setActionTp(codeBtn);
			actionSmenuDown.setResTp(codeResAction);
			actionSmenuDown.setUrl("/smenu/store/down");
			actionSmenuDown.setMethod("GET");
			actionSmenuDown.setResDesc(actionSmenuDown.getActionNm());
			actionRepository.save(actionSmenuDown);
		}
		resourcessForStore.add(actionSmenuDown);
		
		Action actionSmenuUpData = actionRepository.findOneByActionId("SmenuForStoreUpData");
		if (null == actionSmenuUpData) {
			actionSmenuUpData = new Action();
			actionSmenuUpData.setActionId("SmenuForStoreUpData");
			actionSmenuUpData.setActionNm("버튼 - 매장 메뉴 데이타 업로드(매장 관리자)");
			actionSmenuUpData.setActionTp(codeBtn);
			actionSmenuUpData.setResTp(codeResAction);
			actionSmenuUpData.setUrl("/smenu/store/up");
			actionSmenuUpData.setMethod("POST");
			actionSmenuUpData.setResDesc(actionSmenuUpData.getActionNm());
			actionRepository.save(actionSmenuUpData);
		}
		resourcessForStore.add(actionSmenuUpData);
		
		Action actionSmenuUpImg = actionRepository.findOneByActionId("SmenuForStoreUpImg");
		if (null == actionSmenuUpImg) {
			actionSmenuUpImg = new Action();
			actionSmenuUpImg.setActionId("SmenuForStoreUpImg");
			actionSmenuUpImg.setActionNm("버튼 - 매장 메뉴 이미지 업로드(매장 관리자)");
			actionSmenuUpImg.setActionTp(codeBtn);
			actionSmenuUpImg.setResTp(codeResAction);
			actionSmenuUpImg.setUrl("/smenu/store/upImg");
			actionSmenuUpImg.setMethod("POST");
			actionSmenuUpImg.setResDesc(actionSmenuUpImg.getActionNm());
			actionRepository.save(actionSmenuUpImg);
		}
		resourcessForStore.add(actionSmenuUpImg);
		
		Action actionSmenuUpOrd = actionRepository.findOneByActionId("SmenuForStoreUpOrd");
		if (null == actionSmenuUpOrd) {
			actionSmenuUpOrd = new Action();
			actionSmenuUpOrd.setActionId("SmenuForStoreUpOrd");
			actionSmenuUpOrd.setActionNm("버튼 - 매장 메뉴 순번 수정(매장 관리자)");
			actionSmenuUpOrd.setActionTp(codeBtn);
			actionSmenuUpOrd.setResTp(codeResAction);
			actionSmenuUpOrd.setUrl("/smenu/store/*/*");
			actionSmenuUpOrd.setMethod("PATCH");
			actionSmenuUpOrd.setResDesc(actionSmenuUpOrd.getActionNm());
			actionRepository.save(actionSmenuUpOrd);
		}
		resourcessForStore.add(actionSmenuUpOrd);
		
		
	}
	
	
	/**
	 * 주문 관리 메뉴 등록
	 */
	private void initOrderMenu() {
		Menu menuOrder = menuRepository.findOneByTopMenuAndMenuNmAndMlevel(null, "주문 관리", 1);
		if (null == menuOrder) {
			menuOrder = new Menu();
			menuOrder.setMenuNm("주문 관리");
			menuOrder.setMenuIcon("icon-order");
			menuOrder.setMlevel(1);
			menuOrder.setOrd(14);
			menuOrder.setResTp(codeResMenu);
			menuOrder.setUrl("/v/business/order/list");
			menuOrder.setMethod("GET");
			menuOrder.setResDesc("매장 관리자 " + menuOrder.getMenuNm());
			menuRepository.save(menuOrder);
		}
		resourcessForStore.add(menuOrder);
		
		Action actionOrderAdd = actionRepository.findOneByActionId("OrderForStoreAdd");
		if (null == actionOrderAdd) {
			actionOrderAdd = new Action();
			actionOrderAdd.setActionId("OrderForStoreAdd");
			actionOrderAdd.setActionNm("버튼 - 주문 등록(매장 관리자)");
			actionOrderAdd.setActionTp(codeBtn);
			actionOrderAdd.setResTp(codeResAction);
			actionOrderAdd.setUrl("/order");
			actionOrderAdd.setMethod("POST");
			actionOrderAdd.setResDesc(actionOrderAdd.getActionNm());
			actionRepository.save(actionOrderAdd);
		}
		resourcessForStore.add(actionOrderAdd);
		
		Action actionOrderGet = actionRepository.findOneByActionId("OrderForStoreGet");
		if (null == actionOrderGet) {
			actionOrderGet = new Action();
			actionOrderGet.setActionId("OrderForStoreGet");
			actionOrderGet.setActionNm("버튼 - 주문 정보 조회(매장 관리자)");
			actionOrderGet.setActionTp(codeLink);
			actionOrderGet.setResTp(codeResAction);
			actionOrderGet.setUrl("/order/*");
			actionOrderGet.setMethod("GET");
			actionOrderGet.setResDesc(actionOrderGet.getActionNm());
			actionRepository.save(actionOrderGet);
		}
		resourcessForStore.add(actionOrderGet);
		
		Action actionOrderUp = actionRepository.findOneByActionId("OrderForStoreUp");
		if (null == actionOrderUp) {
			actionOrderUp = new Action();
			actionOrderUp.setActionId("OrderForStoreUp");
			actionOrderUp.setActionNm("버튼 - 주문 수정(매장 관리자)");
			actionOrderUp.setActionTp(codeBtn);
			actionOrderUp.setResTp(codeResAction);
			actionOrderUp.setUrl("/order/*");
			actionOrderUp.setMethod("PUT");
			actionOrderUp.setResDesc(actionOrderUp.getActionNm());
			actionRepository.save(actionOrderUp);
		}
		resourcessForStore.add(actionOrderUp);
		
		Action actionOrderDel = actionRepository.findOneByActionId("OrderForStoreDel");
		if (null == actionOrderDel) {
			actionOrderDel = new Action();
			actionOrderDel.setActionId("OrderForStoreDel");
			actionOrderDel.setActionNm("버튼 - 주문 삭제(매장 관리자)");
			actionOrderDel.setActionTp(codeBtn);
			actionOrderDel.setResTp(codeResAction);
			actionOrderDel.setUrl("/order/*");
			actionOrderDel.setMethod("DELETE");
			actionOrderDel.setResDesc(actionOrderDel.getActionNm());
			actionRepository.save(actionOrderDel);
		}
		resourcessForStore.add(actionOrderDel);
		
		Action actionOrderSearchTotal = actionRepository.findOneByActionId("OrderForStoreSearchTotal");
		if (null == actionOrderSearchTotal) {
			actionOrderSearchTotal = new Action();
			actionOrderSearchTotal.setActionId("OrderForStoreSearchTotal");
			actionOrderSearchTotal.setActionNm("버튼 - 주문 리스트 카운터(매장 관리자)");
			actionOrderSearchTotal.setActionTp(codeBtn);
			actionOrderSearchTotal.setResTp(codeResAction);
			actionOrderSearchTotal.setUrl("/order/total");
			actionOrderSearchTotal.setMethod("GET");
			actionOrderSearchTotal.setResDesc(actionOrderSearchTotal.getActionNm());
			actionRepository.save(actionOrderSearchTotal);
		}
		resourcessForStore.add(actionOrderSearchTotal);
		
		Action actionOrderSearch = actionRepository.findOneByActionId("OrderForStoreSearch");
		if (null == actionOrderSearch) {
			actionOrderSearch = new Action();
			actionOrderSearch.setActionId("OrderForStoreSearch");
			actionOrderSearch.setActionNm("버튼 - 주문 리스트 조회(매장 관리자)");
			actionOrderSearch.setActionTp(codeBtn);
			actionOrderSearch.setResTp(codeResAction);
			actionOrderSearch.setUrl("/order");
			actionOrderSearch.setMethod("GET");
			actionOrderSearch.setResDesc(actionOrderSearch.getActionNm());
			actionRepository.save(actionOrderSearch);
		}
		resourcessForStore.add(actionOrderSearch);
		
		Action actionOrderStatus = actionRepository.findOneByActionId("OrderForStoreStatus");
		if (null == actionOrderStatus) {
			actionOrderStatus = new Action();
			actionOrderStatus.setActionId("OrderForStoreStatus");
			actionOrderStatus.setActionNm("버튼 - 주문 상태 변경(매장 관리자)");
			actionOrderStatus.setActionTp(codeBtn);
			actionOrderStatus.setResTp(codeResAction);
			actionOrderStatus.setUrl("/order/status/*/*");
			actionOrderStatus.setMethod("PATCH");
			actionOrderStatus.setResDesc(actionOrderStatus.getActionNm());
			actionRepository.save(actionOrderStatus);
		}
		resourcessForStore.add(actionOrderStatus);
		
		Action actionOrderCancelPay = actionRepository.findOneByActionId("OrderForStoreCancelPay");
		if (null == actionOrderCancelPay) {
			actionOrderCancelPay = new Action();
			actionOrderCancelPay.setActionId("OrderForStoreCancelPay");
			actionOrderCancelPay.setActionNm("버튼 - 결제 취소(매장 관리자)");
			actionOrderCancelPay.setActionTp(codeBtn);
			actionOrderCancelPay.setResTp(codeResAction);
			actionOrderCancelPay.setUrl("/order/cancelPay/*");
			actionOrderCancelPay.setMethod("PATCH");
			actionOrderCancelPay.setResDesc(actionOrderCancelPay.getActionNm());
			actionRepository.save(actionOrderCancelPay);
		}
		resourcessForStore.add(actionOrderCancelPay);
		
	}
	
	/**
	 * 매장 관리자 권한
	 */
	private void initOrderMngApi() {
		Action actionOrderSearchSync = actionRepository.findOneByActionId("MngAppOrderForStoreSync");
		if (null == actionOrderSearchSync) {
			actionOrderSearchSync = new Action();
			actionOrderSearchSync.setActionId("MngAppOrderForStoreSync");
			actionOrderSearchSync.setActionNm("API - 매장 관리앱 주문 리스트 동기화");
			actionOrderSearchSync.setActionTp(codeApi);
			actionOrderSearchSync.setResTp(codeResAction);
			actionOrderSearchSync.setUrl("/api/app/mng/orderSync/*");
			actionOrderSearchSync.setMethod("GET");
			actionOrderSearchSync.setResDesc(actionOrderSearchSync.getActionNm());
			actionRepository.save(actionOrderSearchSync);
		}
		resourcessForStore.add(actionOrderSearchSync);
		
		Action actionOrderUpSync = actionRepository.findOneByActionId("MngAppOrderForStoreUpSync");
		if (null == actionOrderUpSync) {
			actionOrderUpSync = new Action();
			actionOrderUpSync.setActionId("MngAppOrderForStoreUpSync");
			actionOrderUpSync.setActionNm("API - 매장 관리앱 주문 동기화 상태 업데이트");
			actionOrderUpSync.setActionTp(codeApi);
			actionOrderUpSync.setResTp(codeResAction);
			actionOrderUpSync.setUrl("/api/app/mng/orderUpSync");
			actionOrderUpSync.setMethod("POST");
			actionOrderUpSync.setResDesc(actionOrderUpSync.getActionNm());
			actionRepository.save(actionOrderUpSync);
		}
		resourcessForStore.add(actionOrderUpSync);
		
		Action actionOrderSearch = actionRepository.findOneByActionId("MngAppOrderForStoreSearch");
		if (null == actionOrderSearch) {
			actionOrderSearch = new Action();
			actionOrderSearch.setActionId("MngAppOrderForStoreSearch");
			actionOrderSearch.setActionNm("API - 매장 관리앱 주문 리스트 조회");
			actionOrderSearch.setActionTp(codeApi);
			actionOrderSearch.setResTp(codeResAction);
			actionOrderSearch.setUrl("/api/app/mng/order/*");
			actionOrderSearch.setMethod("GET");
			actionOrderSearch.setResDesc(actionOrderSearch.getActionNm());
			actionRepository.save(actionOrderSearch);
		}
		resourcessForStore.add(actionOrderSearch);
		
		Action actionOrderGet = actionRepository.findOneByActionId("MngAppOrderForStoreGet");
		if (null == actionOrderGet) {
			actionOrderGet = new Action();
			actionOrderGet.setActionId("MngAppOrderForStoreGet");
			actionOrderGet.setActionNm("API - 매장 관리앱 주문 상세 정보 조회");
			actionOrderGet.setActionTp(codeApi);
			actionOrderGet.setResTp(codeResAction);
			actionOrderGet.setUrl("/api/app/mng/order/*/*");
			actionOrderGet.setMethod("GET");
			actionOrderGet.setResDesc(actionOrderGet.getActionNm());
			actionRepository.save(actionOrderGet);
		}
		resourcessForStore.add(actionOrderGet);
		
		
		Action actionGetOrders = actionRepository.findOneByActionId("MngAppOrderForStoreGetOrders");
		if (null == actionGetOrders) {
			actionGetOrders = new Action();
			actionGetOrders.setActionId("MngAppOrderForStoreGetOrders");
			actionGetOrders.setActionNm("API - 매장 관리앱 주문 상세 정보 조회(신규)");
			actionGetOrders.setActionTp(codeApi);
			actionGetOrders.setResTp(codeResAction);
			actionGetOrders.setUrl("/api/app/mng/getOrders/*");
			actionGetOrders.setMethod("GET");
			actionGetOrders.setResDesc(actionGetOrders.getActionNm());
			actionRepository.save(actionGetOrders);
		}
		resourcessForStore.add(actionGetOrders);
		
		Action actionOrderStatus = actionRepository.findOneByActionId("MngAppOrderForStoreStatus");
		if (null == actionOrderStatus) {
			actionOrderStatus = new Action();
			actionOrderStatus.setActionId("MngAppOrderForStoreStatus");
			actionOrderStatus.setActionNm("API - 매장 관리앱 주문 상태 변경");
			actionOrderStatus.setActionTp(codeApi);
			actionOrderStatus.setResTp(codeResAction);
			actionOrderStatus.setUrl("/api/app/mng/order/status/*/*");
			actionOrderStatus.setMethod("POST");
			actionOrderStatus.setResDesc(actionOrderStatus.getActionNm());
			actionRepository.save(actionOrderStatus);
		}
		resourcessForStore.add(actionOrderStatus);
		
		Action actionOrderCancel = actionRepository.findOneByActionId("MngAppOrderForStoreCancel");
		if (null == actionOrderCancel) {
			actionOrderCancel = new Action();
			actionOrderCancel.setActionId("MngAppOrderForStoreCancel");
			actionOrderCancel.setActionNm("API - 매장 관리앱 주문 취소");
			actionOrderCancel.setActionTp(codeApi);
			actionOrderCancel.setResTp(codeResAction);
			actionOrderCancel.setUrl("/api/app/mng/orderDel/*");
			actionOrderCancel.setMethod("GET");
			actionOrderCancel.setResDesc(actionOrderCancel.getActionNm());
			actionRepository.save(actionOrderCancel);
		}
		resourcessForStore.add(actionOrderCancel);
		
		Action actionPosStoreSync = actionRepository.findOneByActionId("PosAppStoreSync");
		if (null == actionPosStoreSync) {
			actionPosStoreSync = new Action();
			actionPosStoreSync.setActionId("PosAppStoreSync");
			actionPosStoreSync.setActionNm("API - POS에서 매장 정보 동기화");
			actionPosStoreSync.setActionTp(codeApi);
			actionPosStoreSync.setResTp(codeResAction);
			actionPosStoreSync.setUrl("/api/app/mng/pos/store");
			actionPosStoreSync.setMethod("GET");
			actionPosStoreSync.setResDesc(actionPosStoreSync.getActionNm());
			actionRepository.save(actionPosStoreSync);
		}
		resourcessForStore.add(actionPosStoreSync);
		
		Action actionPosOrderSync = actionRepository.findOneByActionId("PosAppOrderSync");
		if (null == actionPosOrderSync) {
			actionPosOrderSync = new Action();
			actionPosOrderSync.setActionId("PosAppOrderSync");
			actionPosOrderSync.setActionNm("API - POS에서 일일 매출 동기화");
			actionPosOrderSync.setActionTp(codeApi);
			actionPosOrderSync.setResTp(codeResAction);
			actionPosOrderSync.setUrl("/api/app/mng/pos/order/*");
			actionPosOrderSync.setMethod("GET");
			actionPosOrderSync.setResDesc(actionPosOrderSync.getActionNm());
			actionRepository.save(actionPosOrderSync);
		}
		resourcessForStore.add(actionPosOrderSync);
	}
	
	
	/** 단말에서 사용 하는 API등록 */
	/**
	 * API 리소스 등록
	 */
	private void initApi() {
		// 다국어 데이타 조회
		Action actionGetLanData = actionRepository.findOneByActionId("GetLanData");
		if (null == actionGetLanData) {
			actionGetLanData = new Action();
			actionGetLanData.setActionId("GetLanData");
			actionGetLanData.setActionNm("다국어 정보 조회");
			actionGetLanData.setActionTp(codeApi);
			actionGetLanData.setResTp(codeResAction);
			actionGetLanData.setUrl("/api/getLanData/*");
			actionGetLanData.setMethod("GET");
			actionGetLanData.setResDesc(actionGetLanData.getActionNm());
			actionRepository.save(actionGetLanData);
		}
		resourcessForAppUser.add(actionGetLanData);
		
		
		// 약관정보 조회
		Action actionGetTerms = actionRepository.findOneByActionId("GetTerms");
		if (null == actionGetTerms) {
			actionGetTerms = new Action();
			actionGetTerms.setActionId("GetTerms");
			actionGetTerms.setActionNm("약관 정보 조회");
			actionGetTerms.setActionTp(codeApi);
			actionGetTerms.setResTp(codeResAction);
			actionGetTerms.setUrl("/api/getTerms/*/*");
			actionGetTerms.setMethod("GET");
			actionGetTerms.setResDesc(actionGetTerms.getActionNm());
			actionRepository.save(actionGetTerms);
		}
		resourcessForAppUser.add(actionGetTerms);
		
		// 약관동의 여부 조회
		Action actionGetTermsAccept = actionRepository.findOneByActionId("GetTermsAccept");
		if (null == actionGetTermsAccept) {
			actionGetTermsAccept = new Action();
			actionGetTermsAccept.setActionId("GetTermsAccept");
			actionGetTermsAccept.setActionNm("약관동의 여부 조회");
			actionGetTermsAccept.setActionTp(codeApi);
			actionGetTermsAccept.setResTp(codeResAction);
			actionGetTermsAccept.setUrl("/api/app/getTermsAccept/*/*");
			actionGetTermsAccept.setMethod("GET");
			actionGetTermsAccept.setResDesc(actionGetTermsAccept.getActionNm());
			actionRepository.save(actionGetTermsAccept);
		}
		resourcessForAppUser.add(actionGetTermsAccept);
		
		// 약관동의
		Action actionTermsAccept = actionRepository.findOneByActionId("TermsAccept");
		if (null == actionTermsAccept) {
			actionTermsAccept = new Action();
			actionTermsAccept.setActionId("TermsAccept");
			actionTermsAccept.setActionNm("약관동의");
			actionTermsAccept.setActionTp(codeApi);
			actionTermsAccept.setResTp(codeResAction);
			actionTermsAccept.setUrl("/api/app/termsAccept/*/*");
			actionTermsAccept.setMethod("POST");
			actionTermsAccept.setResDesc(actionTermsAccept.getActionNm());
			actionRepository.save(actionTermsAccept);
		}
		resourcessForAppUser.add(actionTermsAccept);
		
		// 공지사항 조회
		Action actionGetNoti = actionRepository.findOneByActionId("GetNoti");
		if (null == actionGetNoti) {
			actionGetNoti = new Action();
			actionGetNoti.setActionId("GetNoti");
			actionGetNoti.setActionNm("공지사항 조회");
			actionGetNoti.setActionTp(codeApi);
			actionGetNoti.setResTp(codeResAction);
			actionGetNoti.setUrl("/api/getNoti/*/*");
			actionGetNoti.setMethod("GET");
			actionGetNoti.setResDesc(actionGetNoti.getActionNm());
			actionRepository.save(actionGetNoti);
		}
		resourcessForAppUser.add(actionGetNoti);
				
		// 회원 정보 조회
		Action actionGetUser = actionRepository.findOneByActionId("GetUser");
		if (null == actionGetUser) {
			actionGetUser = new Action();
			actionGetUser.setActionId("GetUser");
			actionGetUser.setActionNm("앱 회원 조회");
			actionGetUser.setActionTp(codeApi);
			actionGetUser.setResTp(codeResAction);
			actionGetUser.setUrl("/api/app/user");
			actionGetUser.setMethod("GET");
			actionGetUser.setResDesc(actionGetUser.getActionNm());
			actionRepository.save(actionGetUser);
		}
		resourcessForAppUser.add(actionGetUser);
		
		// 회원 정보 수정
		Action actionUser = actionRepository.findOneByActionId("UpUser");
		if (null == actionUser) {
			actionUser = new Action();
			actionUser.setActionId("UpUser");
			actionUser.setActionNm("앱 회원 수정");
			actionUser.setActionTp(codeApi);
			actionUser.setResTp(codeResAction);
			actionUser.setUrl("/api/app/user");
			actionUser.setMethod("POST");
			actionUser.setResDesc(actionUser.getActionNm());
			actionRepository.save(actionUser);
		}
		resourcessForAppUser.add(actionUser);
		
		// 매장 유형 리스트 조회
		Action actionGetStoreTypeList = actionRepository.findOneByActionId("GetStoreTypeList");
		if (null == actionGetStoreTypeList) {
			actionGetStoreTypeList = new Action();
			actionGetStoreTypeList.setActionId("GetStoreTypeList");
			actionGetStoreTypeList.setActionNm("상점 유형 리스토 조회");
			actionGetStoreTypeList.setActionTp(codeApi);
			actionGetStoreTypeList.setResTp(codeResAction);
			actionGetStoreTypeList.setUrl("/api/app/getStoreTypeList/*");
			actionGetStoreTypeList.setMethod("GET");
			actionGetStoreTypeList.setResDesc(actionGetStoreTypeList.getActionNm());
			actionRepository.save(actionGetStoreTypeList);
		}
		resourcessForAppUser.add(actionGetStoreTypeList);
		
		// 매장 유형별 리스트 조회
		Action actionGetStoreListByTp = actionRepository.findOneByActionId("GetStoreListByTp");
		if (null == actionGetStoreListByTp) {
			actionGetStoreListByTp = new Action();
			actionGetStoreListByTp.setActionId("GetStoreListByTp");
			actionGetStoreListByTp.setActionNm("상점 유형별 상점 리스토 조회");
			actionGetStoreListByTp.setActionTp(codeApi);
			actionGetStoreListByTp.setResTp(codeResAction);
			actionGetStoreListByTp.setUrl("/api/app/getStoreListByTp/*/*/*/*");
			actionGetStoreListByTp.setMethod("GET");
			actionGetStoreListByTp.setResDesc(actionGetStoreListByTp.getActionNm());
			actionRepository.save(actionGetStoreListByTp);
		}
		resourcessForAppUser.add(actionGetStoreListByTp);
		
		// 매장 리스트 조회
		Action actionGetStoreList = actionRepository.findOneByActionId("GetStoreList");
		if (null == actionGetStoreList) {
			actionGetStoreList = new Action();
			actionGetStoreList.setActionId("GetStoreList");
			actionGetStoreList.setActionNm("상점 리스토 조회");
			actionGetStoreList.setActionTp(codeApi);
			actionGetStoreList.setResTp(codeResAction);
			actionGetStoreList.setUrl("/api/app/getStoreList/*/*/*/*/*");
			actionGetStoreList.setMethod("GET");
			actionGetStoreList.setResDesc(actionGetStoreList.getActionNm());
			actionRepository.save(actionGetStoreList);
		}
		resourcessForAppUser.add(actionGetStoreList);
		
		// 매장 정보 조회
		Action actionGetStore = actionRepository.findOneByActionId("GetStore");
		if (null == actionGetStore) {
			actionGetStore = new Action();
			actionGetStore.setActionId("GetStore");
			actionGetStore.setActionNm("상점 정보 조회");
			actionGetStore.setActionTp(codeApi);
			actionGetStore.setResTp(codeResAction);
			actionGetStore.setUrl("/api/app/getStore/*/*");
			actionGetStore.setMethod("GET");
			actionGetStore.setResDesc(actionGetStore.getActionNm());
			actionRepository.save(actionGetStore);
		}
		resourcessForAppUser.add(actionGetStore);
		
		// 주문 등록
		Action actionSaveOrder = actionRepository.findOneByActionId("SaveOrder");
		if (null == actionSaveOrder) {
			actionSaveOrder = new Action();
			actionSaveOrder.setActionId("SaveOrder");
			actionSaveOrder.setActionNm("앱 주문 등록");
			actionSaveOrder.setActionTp(codeApi);
			actionSaveOrder.setResTp(codeResAction);
			actionSaveOrder.setUrl("/api/app/order");
			actionSaveOrder.setMethod("POST");
			actionSaveOrder.setResDesc(actionSaveOrder.getActionNm());
			actionRepository.save(actionSaveOrder);
		}
		resourcessForAppUser.add(actionSaveOrder);
		
		// 주문 리스트 조회
		Action actionGetOrderList = actionRepository.findOneByActionId("GetOrderList");
		if (null == actionGetOrderList) {
			actionGetOrderList = new Action();
			actionGetOrderList.setActionId("GetOrderList");
			actionGetOrderList.setActionNm("주문 리스토 조회");
			actionGetOrderList.setActionTp(codeApi);
			actionGetOrderList.setResTp(codeResAction);
			actionGetOrderList.setUrl("/api/app/getOrderList/*/*");
			actionGetOrderList.setMethod("GET");
			actionGetOrderList.setResDesc(actionGetOrderList.getActionNm());
			actionRepository.save(actionGetOrderList);
		}
		resourcessForAppUser.add(actionGetOrderList);
		
		// 완료된 주문 리스트 조회
		Action actionGetOrderOverList = actionRepository.findOneByActionId("GetOrderOverList");
		if (null == actionGetOrderOverList) {
			actionGetOrderOverList = new Action();
			actionGetOrderOverList.setActionId("GetOrderOverList");
			actionGetOrderOverList.setActionNm("완료된 주문 리스토 조회");
			actionGetOrderOverList.setActionTp(codeApi);
			actionGetOrderOverList.setResTp(codeResAction);
			actionGetOrderOverList.setUrl("/api/app/getOrderOverList/*/*");
			actionGetOrderOverList.setMethod("GET");
			actionGetOrderOverList.setResDesc(actionGetOrderOverList.getActionNm());
			actionRepository.save(actionGetOrderOverList);
		}
		resourcessForAppUser.add(actionGetOrderOverList);
		
		// 주문 정보 조회
		Action actionGetOrder = actionRepository.findOneByActionId("GetOrder");
		if (null == actionGetOrder) {
			actionGetOrder = new Action();
			actionGetOrder.setActionId("GetOrder");
			actionGetOrder.setActionNm("주문 정보 조회");
			actionGetOrder.setActionTp(codeApi);
			actionGetOrder.setResTp(codeResAction);
			actionGetOrder.setUrl("/api/app/getOrder/*/*");
			actionGetOrder.setMethod("GET");
			actionGetOrder.setResDesc(actionGetOrder.getActionNm());
			actionRepository.save(actionGetOrder);
		}
		resourcessForAppUser.add(actionGetOrder);
		
		// 주문 취소
		Action actionCancelOrder = actionRepository.findOneByActionId("CancelOrder");
		if (null == actionCancelOrder) {
			actionCancelOrder = new Action();
			actionCancelOrder.setActionId("CancelOrder");
			actionCancelOrder.setActionNm("주문 취소");
			actionCancelOrder.setActionTp(codeApi);
			actionCancelOrder.setResTp(codeResAction);
			actionCancelOrder.setUrl("/api/app/cancelOrder/*/*");
			actionCancelOrder.setMethod("GET");
			actionCancelOrder.setResDesc(actionCancelOrder.getActionNm());
			actionRepository.save(actionCancelOrder);
		}
		resourcessForAppUser.add(actionCancelOrder);
				
		// 배너 주소 정보 조회
		Action actionGetBanner = actionRepository.findOneByActionId("GetBanner");
		if (null == actionGetBanner) {
			actionGetBanner = new Action();
			actionGetBanner.setActionId("GetBanner");
			actionGetBanner.setActionNm("배너 주소 정보 조회");
			actionGetBanner.setActionTp(codeApi);
			actionGetBanner.setResTp(codeResAction);
			actionGetBanner.setUrl("/api/app/getBanner");
			actionGetBanner.setMethod("GET");
			actionGetBanner.setResDesc(actionGetBanner.getActionNm());
			actionRepository.save(actionGetBanner);
		}
		resourcessForAppUser.add(actionGetBanner);
		
		
		// 찜 하기
		Action actionPostFavourite = actionRepository.findOneByActionId("PostFavourite");
		if (null == actionPostFavourite) {
			actionPostFavourite = new Action();
			actionPostFavourite.setActionId("PostFavourite");
			actionPostFavourite.setActionNm("찜 등록");
			actionPostFavourite.setActionTp(codeApi);
			actionPostFavourite.setResTp(codeResAction);
			actionPostFavourite.setUrl("/api/app/favourite/*");
			actionPostFavourite.setMethod("POST");
			actionPostFavourite.setResDesc(actionPostFavourite.getActionNm());
			actionRepository.save(actionPostFavourite);
		}
		resourcessForAppUser.add(actionPostFavourite);
		
		// 찜 리스트 조회
		Action actionGetFavourite = actionRepository.findOneByActionId("GetFavourite");
		if (null == actionGetFavourite) {
			actionGetFavourite = new Action();
			actionGetFavourite.setActionId("GetFavourite");
			actionGetFavourite.setActionNm("찜 리스트 조회");
			actionGetFavourite.setActionTp(codeApi);
			actionGetFavourite.setResTp(codeResAction);
			actionGetFavourite.setUrl("/api/app/favourite");
			actionGetFavourite.setMethod("GET");
			actionGetFavourite.setResDesc(actionGetFavourite.getActionNm());
			actionRepository.save(actionGetFavourite);
		}
		resourcessForAppUser.add(actionGetFavourite);
		
		// 찜 삭제
		Action actionDelFavourite = actionRepository.findOneByActionId("DelFavourite");
		if (null == actionDelFavourite) {
			actionDelFavourite = new Action();
			actionDelFavourite.setActionId("DelFavourite");
			actionDelFavourite.setActionNm("찜 삭제");
			actionDelFavourite.setActionTp(codeApi);
			actionDelFavourite.setResTp(codeResAction);
			actionDelFavourite.setUrl("/api/app/delFavourite/*");
			actionDelFavourite.setMethod("POST");
			actionDelFavourite.setResDesc(actionDelFavourite.getActionNm());
			actionRepository.save(actionDelFavourite);
		}
		resourcessForAppUser.add(actionDelFavourite);
				
		// 푸시 토큰 저장
		Action actionPushToken = actionRepository.findOneByActionId("PushToken");
		if (null == actionPushToken) {
			actionPushToken = new Action();
			actionPushToken.setActionId("PushToken");
			actionPushToken.setActionNm("푸시 토큰 저장");
			actionPushToken.setActionTp(codeApi);
			actionPushToken.setResTp(codeResAction);
			actionPushToken.setUrl("/api/app/push/*");
			actionPushToken.setMethod("POST");
			actionPushToken.setResDesc(actionPushToken.getActionNm());
			actionRepository.save(actionPushToken);
		}
		resourcessForAppUser.add(actionPushToken);
		
		Action actionInquiryAdd = actionRepository.findOneByActionId("InquiryAdd");
		if (null == actionInquiryAdd) {
			actionInquiryAdd = new Action();
			actionInquiryAdd.setActionId("InquiryAdd");
			actionInquiryAdd.setActionNm("1:1 문의 등록");
			actionInquiryAdd.setActionTp(codeLink);
			actionInquiryAdd.setResTp(codeResAction);
			actionInquiryAdd.setUrl("/api/app/inquiry");
			actionInquiryAdd.setMethod("POST");
			actionInquiryAdd.setResDesc(actionInquiryAdd.getActionNm());
			actionRepository.save(actionInquiryAdd);
		}
		resourcessForAppUser.add(actionInquiryAdd);
		
		Action actionInquirySearch = actionRepository.findOneByActionId("AppInquirySearch");
		if (null == actionInquirySearch) {
			actionInquirySearch = new Action();
			actionInquirySearch.setActionId("AppInquirySearch");
			actionInquirySearch.setActionNm("1:1 문의 조회");
			actionInquirySearch.setActionTp(codeLink);
			actionInquirySearch.setResTp(codeResAction);
			actionInquirySearch.setUrl("/api/app/inquiry/*/*");
			actionInquirySearch.setMethod("GET");
			actionInquirySearch.setResDesc(actionInquirySearch.getActionNm());
			actionRepository.save(actionInquirySearch);
		}
		resourcessForAppUser.add(actionInquirySearch);
		
		Action actionEventSearch = actionRepository.findOneByActionId("AppEventSearch");
		if (null == actionEventSearch) {
			actionEventSearch = new Action();
			actionEventSearch.setActionId("AppEventSearch");
			actionEventSearch.setActionNm("이벤트 문의 조회");
			actionEventSearch.setActionTp(codeLink);
			actionEventSearch.setResTp(codeResAction);
			actionEventSearch.setUrl("/api/getEvent/*/*");
			actionEventSearch.setMethod("GET");
			actionEventSearch.setResDesc(actionEventSearch.getActionNm());
			actionRepository.save(actionEventSearch);
		}
		resourcessForAppUser.add(actionEventSearch);
		
	}
	
	
}
