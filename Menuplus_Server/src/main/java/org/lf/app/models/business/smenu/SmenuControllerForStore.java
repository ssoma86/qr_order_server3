package org.lf.app.models.business.smenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.CategoryService;
import org.lf.app.models.business.smenu.Smenu.SmenuValid;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.business.smenu.SmenuInfo.SmenuInfoValid;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.business.smenuOpt.SmenuOptService;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.ExcelUtil;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 메뉴 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/smenu/store")
public class SmenuControllerForStore {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(SmenuControllerForStore.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SmenuService service;
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private SmenuOptService smenuOptService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private FileUtil fileUtil;
	
	
	/**
	 * 추가
	 * 
	 * @param smenu
	 * @throws Exception
	 */
	@PostMapping
	public void add(@Validated({ SmenuValid.class, SmenuInfoValid.class }) @RequestBody Smenu smenu,
			@SessionAttribute(value = "sa_account") Account account) throws Exception {
		
		User user = userService.findOne(account.getAccountId());
		
		smenu.setStore(user.getStore());
		smenu.setDefaultLan(user.getStore().getDefaultLan());
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile())) {
			smenu.setSmenuImg(fileUtil.saveFile("smenu", "SMENU", smenu.getSmenuImgFile()));
		}		
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile1())) { //23.10.24 메뉴이미지 추가
			smenu.setSmenuImg1(fileUtil.saveFile("smenu", "SMENU1", smenu.getSmenuImgFile1()));
		}
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile2())) { //23.10.24 메뉴이미지 추가
			smenu.setSmenuImg2(fileUtil.saveFile("smenu", "SMENU2", smenu.getSmenuImgFile2()));
		}
		
		Smenu temp = service.save(smenu);
		
		logger.info("===========temp.getSmenuCd():" + temp.getSmenuCd() );		
		
		categoryService.insertOrdSmenuInCategory(smenu.getCategoryCd(), temp.getSmenuCd(), smenu.getSmenuNm(), smenu.getUseYn(), 1);
		
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param smenuCd 메뉴 코드
	 * @return 메뉴 정보
	 */
	@GetMapping("/{smenuCd:\\d+}")
	@JsonView(SmenuControllerJsonView.class)
	public Smenu get(@PathVariable Integer smenuCd) {
		return service.findOne(smenuCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param smenu
	 * @param smenuCd
	 * @throws Exception
	 */
	@PutMapping("/{smenuCd:\\d+}")
	public void up(@Validated({ SmenuValid.class, SmenuInfoValid.class }) @RequestBody Smenu smenu, @PathVariable Integer smenuCd,
			@SessionAttribute(value = "sa_account") Account account) throws Exception {
		smenu.setSmenuCd(smenuCd);
		
		User user = userService.findOne(account.getAccountId());
		
		smenu.setCategory(categoryService.findOne(smenu.getCategoryCd()));
		smenu.setStore(user.getStore());
		smenu.setDefaultLan(user.getStore().getDefaultLan());
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile()) && smenu.getSmenuImgFile().contains("base64")) {
			smenu.setSmenuImg(fileUtil.delAndSaveFile(smenu.getSmenuImg(), "smenu", "SMENU", smenu.getSmenuImgFile()));
		}
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile1()) && smenu.getSmenuImgFile1().contains("base64")) { //23.10.24 메뉴이미지 추가
			smenu.setSmenuImg1(fileUtil.delAndSaveFile(smenu.getSmenuImg1(), "smenu", "SMENU1", smenu.getSmenuImgFile1()));
		}
		
		if (!StringUtils.isEmpty(smenu.getSmenuImgFile2()) && smenu.getSmenuImgFile2().contains("base64")) { //23.10.24 메뉴이미지 추가
			smenu.setSmenuImg2(fileUtil.delAndSaveFile(smenu.getSmenuImg2(), "smenu", "SMENU2", smenu.getSmenuImgFile2()));
		}
		
		service.save(smenu);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param smenuCd
	 */
	@PutMapping("/del/{smenuCds}")
	public void del(@PathVariable Integer[] smenuCds) {
		service.useYn(smenuCds, "N");
	}
	
	
	/**
	 * 반영
	 * 
	 * @param smenuCd
	 */
	@PutMapping("/use/{smenuCds}")
	public void use(@PathVariable Integer[] smenuCds) {
		service.useYn(smenuCds, "Y");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param smenu
	 * @param nationCd
	 * @return 메뉴정보 리스트
	 */
	@GetMapping
	@JsonView(SmenuControllerJsonView.class)
	public List<Smenu> search(Integer categoryCd, String smenuNm, String useYn,
			@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		logger.info("=================================service.findSmenuLis start=================");
		
		return service.findSmenuList(user.getStore().getStoreCd(), categoryCd, smenuNm, useYn);
	}
	
	
	/**
	 * 엑셀 출력
	 * 
	 * @param lanData 조회 파라메타
	 * @throws Exception 
	 */
	@GetMapping("/down")
	public void down(@SessionAttribute(value = "sa_account") Account account, HttpServletResponse res) throws Exception {
		
		User user = userService.findOne(account.getAccountId());
		
		Store store = storeService.findOne(user.getStore().getStoreCd());
		
		if (!ObjectUtils.isEmpty(store)) {
			// 해더 설정
			List<Lan> lanList = store.getLans();
			
			String[] headers = new String[(store.isInternationalPayYn() ? 5 + (lanList.size() * 2) : 5 + lanList.size())];
			
			int index = 0;
			
			headers[index++] = lanDataService.getLanData("메뉴명", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "smenuNm" + ExcelUtil.SPLIT + "l";
			
			// 기타 언어 해더 설정
			for (Lan data : lanList) {
				if (!lanService.getDefaultLan().getId().equals(data.getId())) {
					headers[index++] = lanDataService.getLanData("메뉴명", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "smenuNm" + data.getId() + ExcelUtil.SPLIT + "l";
				}
			}
			
			headers[index++] = lanDataService.getLanData("원가", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "cost" + ExcelUtil.SPLIT + "r";
			headers[index++] = lanDataService.getLanData("단가", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "price" + ExcelUtil.SPLIT + "r";
			
			if (store.isInternationalPayYn()) {
				// 기타 언어 해더 설정
				for (Lan data : lanList) {
					if (!lanService.getDefaultLan().getId().equals(data.getId())) {
						headers[index++] = lanDataService.getLanData("단가", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "price" + data.getId() + ExcelUtil.SPLIT + "l";
					}
				}
			}
			
			headers[index++] = lanDataService.getLanData("카테고리", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "category" + ExcelUtil.SPLIT + "l";
			headers[index++] = lanDataService.getLanData("옵션", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "option" + ExcelUtil.SPLIT + "l";
			
			// 엑셀 생성 해서 출력
			new ExcelUtil().exportExcel(headers, new ArrayList<Map<String, Object>>(), res);
		}
	}
	
	
	/**
	 * 엑셀 업로드
	 * 
	 * @param file 엑셀 파일 
	 * @throws Exception 
	 */
	@PostMapping("/up")
	public void up(@SessionAttribute(value = "sa_account") Account account, MultipartFile file) throws Exception {
		User user = userService.findOne(account.getAccountId());
		
		Store store = storeService.findOne(user.getStore().getStoreCd());
		
		List<Category> categoryList = categoryService.findCategoryList(user.getStore().getStoreCd(), "", "Y");
		
		if (!ObjectUtils.isEmpty(store) && !ObjectUtils.isEmpty(categoryList) && categoryList.size() > 0) {
			// 파일 및 파일 형식 체크
			if (null == file) {
				throw new RuntimeException("File is not exist!");
			} else if (!"application/vnd.ms-excel".equals(file.getContentType()) || !file.getOriginalFilename().endsWith(".xls")) {
				throw new RuntimeException("File type is not excel!");
			} else {
				HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
				
				// 해더 설정
				List<Lan> lanList = store.getLans();
				
				String[] headers = new String[(store.isInternationalPayYn() ? 5 + (lanList.size() * 2) : 5 + lanList.size())];
				
				int index = 0;
				
				headers[index++] = lanDataService.getLanData("메뉴명", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "smenuNm" + ExcelUtil.SPLIT + "l";
				
				// 기타 언어 해더 설정
				for (Lan data : lanList) {
					if (!lanService.getDefaultLan().getId().equals(data.getId())) {
						headers[index++] = lanDataService.getLanData("메뉴명", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "smenuNm" + data.getId() + ExcelUtil.SPLIT + "l";
					}
				}
				
				headers[index++] = lanDataService.getLanData("원가", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "cost" + ExcelUtil.SPLIT + "r";
				headers[index++] = lanDataService.getLanData("단가", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "price" + ExcelUtil.SPLIT + "r";
				
				if (store.isInternationalPayYn()) {
					// 기타 언어 해더 설정
					for (Lan data : lanList) {
						if (!lanService.getDefaultLan().getId().equals(data.getId())) {
							headers[index++] = lanDataService.getLanData("단가", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "price" + data.getId() + ExcelUtil.SPLIT + "l";
						}
					}
				}
				
				headers[index++] = lanDataService.getLanData("카테고리", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "category" + ExcelUtil.SPLIT + "l";
				headers[index++] = lanDataService.getLanData("옵션", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "option" + ExcelUtil.SPLIT + "l";
				
				// 파일 데이타 읽어 오기
				List<Map<String, Object>> datas = new ExcelUtil().readExcel(workbook, headers);
				
	            workbook.close();
	            
	            // 처음 카테고리 가져오기
	            Category defaultCategory = categoryList.get(0);
	            
	            
	            List<Smenu> smenuList = new ArrayList<>();
	            
	            // 순번 1로 설정
	            int ord = 1;
	            
	            for (Map<String, Object> data : datas) {
	            	String smenuNm = MapUtils.getString(data, "smenuNm");
	            	
	            	// 메뉴 명 설정이 없으면 등록 안함
	            	if (!StringUtils.isEmpty(smenuNm)) {
	            		// 운가
		            	Integer cost = MapUtils.getInteger(data, "cost");
		            	// 비여 있으면 0
		            	if (ObjectUtils.isEmpty(cost)) {
		            		cost = 0;
		            	}
		            	// 단가
		            	Integer price = MapUtils.getInteger(data, "price");
		            	// 비여 있으면 0
		            	if (ObjectUtils.isEmpty(price)) {
		            		price = 0;
		            	}
		            	
		            	String category = MapUtils.getString(data, "category", "");
		            	String option = MapUtils.getString(data, "option", "");
		            	
		            	Smenu smenu = new Smenu();
		            	smenu.setStore(store);
		            	smenu.setDefaultLan(store.getDefaultLan());
		            	smenu.setSmenuNm(smenuNm);
		            	smenu.setCost(cost);
		            	smenu.setPrice(price);
		            	smenu.setOrd(ord++);
		            	
		            	// 카테고리 설정
						/*
						 * String[] categorys = category.split(",");
						 * 
						 * List<Category> categorysList = new ArrayList<>();
						 * 
						 * for (String c : categorys) { List<Category> ctmpList =
						 * categoryService.findCategoryList(user.getStore().getStoreCd(), c.trim(), "");
						 * if (!ObjectUtils.isEmpty(ctmpList) && ctmpList.size() > 0) {
						 * categorysList.add(ctmpList.get(0)); } }
						 * 
						 * if (categorysList.size() == 0) { categorysList.add(defaultCategory); }
						 * 
						 * smenu.setCategorys(categorysList);
						 */
		            	
		            	// 옵션 설정
		            	String[] options = option.split(",");
		            	
		            	List<SmenuOpt> optionsList = new ArrayList<>();
		            	
		            	for (String c : options) {
		            		List<SmenuOpt> ctmpList = smenuOptService.findSmenuOptList(user.getStore().getStoreCd(), c.trim(), "");
		            		if (!ObjectUtils.isEmpty(ctmpList) && ctmpList.size() > 0) {
		            			optionsList.add(ctmpList.get(0));
		            		}
		            	}
		            	
		            	if (optionsList.size() > 0) {
		            		smenu.setSmenuOpts(optionsList);
		            	}
		            	
		            	// 메뉴 다국어 설정
		            	List<SmenuInfo> smenuInfos = new ArrayList<>();
		            	
		            	for (Lan lanTmp : lanList) {
		            		SmenuInfo smenuInfo = new SmenuInfo();
		            		smenuInfo.setLanTp(lanTmp);
		            		
		            		String smenuInfoNm = MapUtils.getString(data, "smenuNm" + lanTmp.getId());
		            		
		            		if (StringUtils.isEmpty(smenuInfoNm)) {
		            			smenuInfo.setSmenuInfoNm(smenuNm);
		            		} else {
		            			smenuInfo.setSmenuInfoNm(smenuInfoNm);
		            		}
		            		
		            		Integer smenuInfoPrice = MapUtils.getInteger(data, "price" + lanTmp.getId());
		            		
		            		if (ObjectUtils.isEmpty(smenuInfoPrice)) {
		            			smenuInfoPrice = price;
			            	}
		            		
		            		if (store.isInternationalPayYn()) {
		            			smenuInfo.setPrice(smenuInfoPrice);
		            		}
		            		
		            		smenuInfos.add(smenuInfo);
						}
		            	
		            	smenu.setSmenuInfos(smenuInfos);
		            	
		            	smenu.setUseYn("N");
		            	smenuList.add(smenu);
	            	}
	            }
	            
	            service.save(smenuList);
			}
		}
	}
	
	
	
	/**
	 * 메뉴 이미지 업로드
	 * @param file
	 * @throws Exception
	 */
	@PostMapping("/upImg")
	public void upImg(@SessionAttribute(value = "sa_account") Account account, MultipartFile file) throws Exception {
		User user = userService.findOne(account.getAccountId());
		
		Store store = storeService.findOne(user.getStore().getStoreCd());
		
		if (!ObjectUtils.isEmpty(store)) {
			// 파일 체크
			if (ObjectUtils.isEmpty(file)) {
				throw new RuntimeException("파일을 읽을 수 없습니다.");
			} else if (!"application/x-zip-compressed".equals(file.getContentType()) || !file.getOriginalFilename().endsWith(".zip")) {
				throw new RuntimeException(".zip 형식의 엑셀 파일만 읽을 수 있습니다.");
			} else {
				Map<String, String> fileMap = fileUtil.saveZipFile("smenu", "SMENU", file); 
				
				fileMap.forEach((key, fileName) -> {
					String[] keys = key.split("\\.");
					
					List<Smenu> smenuList = service.findSmenuList(user.getStore().getStoreCd(), null, keys[0], "");
					
					smenuList.forEach(smenu -> {
						try {
							smenu.setSmenuImg(fileName);
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					
					service.save(smenuList);
				});
			}
		}
	}
	
	
	/**
	 * 순번 수정
	 * 
	 * @param smenuCd
	 * @param ord
	 * @throws Exception
	 */
	@PatchMapping("/{smenuCd:\\d+}/{ord:\\d+}")
	public void upOrd(@PathVariable Integer smenuCd, @PathVariable Integer ord) throws Exception {
		Smenu smenu = service.findOne(smenuCd);
		
		smenu.setOrd(ord);
		
		service.save(smenu);
	}
	
	
}
