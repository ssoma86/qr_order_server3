package org.lf.app.models.system.dictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.CategoryInfo;
import org.lf.app.models.business.category.CategoryService;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.discount.DiscountInfo;
import org.lf.app.models.business.discount.DiscountService;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.SmenuInfo;
import org.lf.app.models.business.smenu.SmenuService;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.business.smenuOpt.SmenuOptInfo;
import org.lf.app.models.business.smenuOpt.SmenuOptService;
import org.lf.app.models.business.smenuOptTp.SmenuOptTp;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpInfo;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpService;
import org.lf.app.models.business.stuff.Stuff;
import org.lf.app.models.business.stuff.StuffInfo;
import org.lf.app.models.business.stuff.StuffService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.dictionary.Dictionary.DictionaryValid;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.ExcelUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 사전 데이타 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/dictionary")
public class DictionaryController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private DictionaryService service;
	
	@Autowired
	private LanDataService lanDataservice;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private SmenuService smenuService;
	
	@Autowired
	private SmenuOptTpService smenuOptTpService;
	
	@Autowired
	private SmenuOptService smenuOptService;
	
	@Autowired
	private DiscountService discountService;
	
	@Autowired
	private StuffService stuffService;
	
	
	// JsonView
	public interface DictionaryControllerJsonView extends BaseJsonView,
		LanControllerCommonJsonView, CodeControllerCommonJsonView {}
	public interface DictionaryControllerCommonJsonView {}
	
		
	/**
	 * 추가
	 * 
	 * @param dictionary 사전 정보
	 */
	@PostMapping
	@CacheEvict(value = "lan", allEntries = true)
	public void add(@Validated(DictionaryValid.class) @RequestBody Dictionary dictionary) {
		service.save(dictionary);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param cd 언어코드
	 * @return 사전 정보
	 */
	@GetMapping("/{cd}")
	@JsonView(DictionaryControllerJsonView.class)
	public Dictionary get(@PathVariable Integer cd) {
		return service.findOne(cd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param lan 사전 정보
	 */
	@PutMapping("/{cd}")
	@CacheEvict(value = "lan", allEntries = true)
	public void up(@Validated(DictionaryValid.class) @RequestBody Dictionary dictionary, @PathVariable Integer cd) {
		dictionary.setCd(cd);
		service.save(dictionary);
	}
	
	
	/**
	 * 사전 정보 리스트 조회
	 * 
	 * @param dictionary 조회 파라메타
	 * @return 사전 데이타 정보
	 */
	@GetMapping
	@JsonView(DictionaryControllerJsonView.class)
	public List<Dictionary> search(Dictionary dictionary, Integer dictionaryTpCd) {
		List<Dictionary> dictionaryList = service.findDictionaryList(dictionary, dictionaryTpCd);
		
		dictionaryList.forEach(dictionaryTmp -> {
			dictionaryTmp.getDictionaryTp().setNmLan(lanDataservice.getLanData(dictionaryTmp.getDictionaryTp().getNm(), LocaleContextHolder.getLocale()));
		});
		
		return dictionaryList;
	}

	
	/**
	 * 사전ID 사용여부 확인
	 * 
	 * @param id 사전ID
	 * @return 사전 데이타 정보
	 */
	@PatchMapping("/chk/{id}")
	@JsonView(DictionaryControllerCommonJsonView.class)
	public Dictionary chkId(@PathVariable String id) {
		return service.findOneByIdAndLan(id, lanService.findOne("ko"));
	}
	
	
	/**
	 * 엑셀 출력
	 * 
	 * @param dictionary 조회 파라메타
	 * @throws Exception 
	 */
	@GetMapping("/down")
	public void down(Dictionary dictionary, HttpServletResponse res) throws Exception {
		// 해더 설정
		Lan lan = new Lan();
		lan.setNm("");
		List<Lan> lanList = lanService.findLanList(lan);
		String[] headers = new String[lanList.size() + 1];
		
		int i = 0;
				
		headers[i++] = "코드" + ExcelUtil.SPLIT + "id" + ExcelUtil.SPLIT + "l";
		headers[i++] = "한국어" + ExcelUtil.SPLIT + "ko" + ExcelUtil.SPLIT + "l";
		
		// 기타 언어 해더 설정
		for (Lan data : lanList) {
			if (!"ko".equals(data.getId())) {
				headers[i++] = data.getNm() + ExcelUtil.SPLIT + data.getId() + ExcelUtil.SPLIT + "l";
			}
		}
		
		
		// 데이타 설정
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<Dictionary> dictionaryList = service.findDictionaryList(dictionary, 0);
		
		dictionaryList.forEach(data -> {
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("id", data.getId());
			tmp.put("ko", data.getNm());
			
			data.getDictionarys().forEach(tmpData -> {
				tmp.put(tmpData.getLan().getId(), tmpData.getNm());
			});
			
			dataList.add(tmp);
		});
		
		// 엑셀 생성 해서 출력
		new ExcelUtil().exportExcel(headers, dataList, res);
	}
	
	/**
	 * 엑셀 업로드
	 * 
	 * @param file 엑셀 파일 
	 * @throws Exception 
	 */
	@PostMapping("/up")
	@CacheEvict(value = "lan", allEntries = true)
	public void up(MultipartFile file) throws Exception {
		// 파일 및 파일 형식 체크
		if (null == file) {
			throw new RuntimeException("File is not exist!");
		} else if (!"application/vnd.ms-excel".equals(file.getContentType()) || !file.getOriginalFilename().endsWith(".xls")) {
			throw new RuntimeException("File type is not excel!");
		} else {
			HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
			
			// 해더 설정
			Lan lan = new Lan();
			lan.setNm("");
			List<Lan> lanList = lanService.findLanList(lan);
			String[] headers = new String[lanList.size() + 1];
			
			int i = 0;
			
			headers[i++] = "코드" + ExcelUtil.SPLIT + "id";
			headers[i++] = "한국어" + ExcelUtil.SPLIT + "ko";
			
			// 기타 언어 해더 설정
			for (Lan data : lanList) {
				if (!"ko".equals(data.getId())) {
					headers[i++] = data.getNm() + ExcelUtil.SPLIT + data.getId();
				}
			}
            
			// 파일 데이타 읽어 오기
			List<Map<String, Object>> datas = new ExcelUtil().readExcel(workbook, headers);
			
            workbook.close();
            
            // 데이타 저장
            List<Dictionary> dataList = new ArrayList<>();
            
            for (Map<String, Object> data : datas) {
            	Lan lanKo = lanService.findOne("ko");
            	String id = MapUtils.getString(data, "id");
            	
            	// 이미 동일한 아이디로 등록 된게 있는지 확인 있으면 수정
            	Dictionary tmp = service.findOneByIdAndLan(id, lanKo);
            	
            	// 없으면 새로 생성
            	if (null == tmp) {
            		tmp = new Dictionary();
            		tmp.setLan(lanKo);
            		tmp.setId(id);
            	}
            	
            	tmp.setNm(MapUtils.getString(data, "ko"));
            	
            	List<Dictionary> subDictionary = new ArrayList<>();
            	
            	// 타 언어 설정
            	for (Lan lanTmp : lanList) {
            		if (!"ko".equals(lanTmp.getId())) {
    					String subNm = MapUtils.getString(data, lanTmp.getId());
    					
    					if (!StringUtils.isEmpty(subNm)) {
    						Dictionary tmpSub = service.findOneByIdAndLan(id, lanTmp);
        					if (null == tmpSub) {
        						tmpSub = new Dictionary();
        						tmpSub.setLan(lanTmp);
        						tmpSub.setId(id);
        	            	}
        					
        					tmpSub.setNm(MapUtils.getString(data, lanTmp.getId()));
        					
        					subDictionary.add(tmpSub);
    					}
    				}
            	}
            	
            	tmp.setDictionarys(subDictionary);
            	
            	dataList.add(tmp);
            }
            
            service.save(dataList);
		}
	}
	
	
	/**
	 * 등록된 다국어 정보 리스트 조회
	 * 
	 * @param dictionary 조회 파라메타
	 * @return 사전 데이타 정보
	 */
	@GetMapping("/searchText")
	@JsonView(DictionaryControllerJsonView.class)
	public List<Dictionary> search(String searchText, Integer dictionaryTpCd) {
		List<Dictionary> dictionaryList = new ArrayList<>();
		
		Code dictionaryTp = codeService.findOne(dictionaryTpCd);
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "category".equals(dictionaryTp.getVal())) {
			List<Category> dataList = categoryService.findCategoryList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "category");
			
			for (Category data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getCategoryNm());
				tmp.setNm(data.getCategoryNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (CategoryInfo subData : data.getCategoryInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getCategoryInfoNm());
					subTmp.setNm(subData.getCategoryInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "menu".equals(dictionaryTp.getVal())) {
			List<Smenu> dataList = smenuService.findSmenuList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "menu");
			
			for (Smenu data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getSmenuNm());
				tmp.setNm(data.getSmenuNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (SmenuInfo subData : data.getSmenuInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getSmenuInfoNm());
					subTmp.setNm(subData.getSmenuInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "optTp".equals(dictionaryTp.getVal())) {
			List<SmenuOptTp> dataList = smenuOptTpService.findSmenuOptTpList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "optTp");
			
			for (SmenuOptTp data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getSmenuOptTpNm());
				tmp.setNm(data.getSmenuOptTpNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (SmenuOptTpInfo subData : data.getSmenuOptTpInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getSmenuOptTpInfoNm());
					subTmp.setNm(subData.getSmenuOptTpInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "opt".equals(dictionaryTp.getVal())) {
			List<SmenuOpt> dataList = smenuOptService.findSmenuOptList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "opt");
			
			for (SmenuOpt data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getSmenuOptNm());
				tmp.setNm(data.getSmenuOptNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (SmenuOptInfo subData : data.getSmenuOptInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getSmenuOptInfoNm());
					subTmp.setNm(subData.getSmenuOptInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "discount".equals(dictionaryTp.getVal())) {
			List<Discount> dataList = discountService.findDiscountList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "discount");
			
			for (Discount data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getDiscountNm());
				tmp.setNm(data.getDiscountNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (DiscountInfo subData : data.getDiscountInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getDiscountInfoNm());
					subTmp.setNm(subData.getDiscountInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		if (ObjectUtils.isEmpty(dictionaryTp) || "stuff".equals(dictionaryTp.getVal())) {
			List<Stuff> dataList = stuffService.findStuffList(searchText);
			
			Code dictionaryTpTmp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", "stuff");
			
			for (Stuff data : dataList) {
				Dictionary tmp = new Dictionary();
				tmp.setDictionaryTp(dictionaryTpTmp);
				tmp.setId(data.getStuffNm());
				tmp.setNm(data.getStuffNm());
				tmp.setLan(data.getDefaultLan());
				
				List<Dictionary> tmps = new ArrayList<>();
				
				for (StuffInfo subData : data.getStuffInfos()) {
					Dictionary subTmp = new Dictionary();
					subTmp.setDictionaryTp(dictionaryTpTmp);
					subTmp.setId(subData.getStuffInfoNm());
					subTmp.setNm(subData.getStuffInfoNm());
					subTmp.setLan(subData.getLanTp());
					
					tmps.add(subTmp);
				}
				
				tmp.setDictionarys(tmps);
				
				dictionaryList.add(tmp);
			}
		}
		
		return dictionaryList;
	}
	
	
	/**
	 * 사전 정보 리스트 조회
	 * 
	 * @param dictionary 조회 파라메타
	 * @return 사전 데이타 정보
	 */
	@GetMapping("/search")
	@JsonView(DictionaryControllerJsonView.class)
	public List<Dictionary> search(Dictionary dictionary, String dictionaryTpVal) {
		Code dictionaryTp = codeService.findOneByTopCodeValAndVal("DICTIONARY_TP", dictionaryTpVal);
		
		List<Dictionary> dictionaryList = service.findDictionaryList(dictionary, dictionaryTp.getCd());
		
		dictionaryList.forEach(dictionaryTmp -> {
			dictionaryTmp.getDictionaryTp().setNmLan(lanDataservice.getLanData(dictionaryTmp.getDictionaryTp().getNm(), LocaleContextHolder.getLocale()));
		});
		
		return dictionaryList;
	}
	
	
}
