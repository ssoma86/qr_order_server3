package org.lf.app.models.business.stuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.stuff.Stuff.StuffValid;
import org.lf.app.models.business.stuff.StuffInfo.StuffInfoValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.ExcelUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 재료 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/stuff")
public class StuffController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StuffService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private StoreService storeService;
	
	
	
	// JsonView
	public interface StuffControllerJsonView extends StoreControllerJsonView,
		CodeControllerCommonJsonView {}
	public interface StuffControllerCommonJsonView {}
	
	
	
	/**
	 * 추가
	 * 
	 * @param stuff
	 */
	@PostMapping
	public void add(@Validated({ StuffValid.class, StuffInfoValid.class }) @RequestBody Stuff stuff) {
		service.save(stuff);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param stuffCd 재료 코드
	 * @return 재료 정보
	 */
	@GetMapping("/{stuffCd:\\d+}")
	@JsonView(StuffControllerJsonView.class)
	public Stuff get(@PathVariable Integer stuffCd) {
		return service.findOne(stuffCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param stuff
	 * @param stuffCd
	 */
	@PutMapping("/modi/{stuffCd:\\d+}")
	public void up(@Validated({ StuffValid.class, StuffInfoValid.class }) @RequestBody Stuff stuff, @PathVariable Integer stuffCd) {
		stuff.setStuffCd(stuffCd);
		
		service.save(stuff);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param stuffCd
	 */
	@DeleteMapping("/del/{stuffCds}")
	public void del(@PathVariable Integer[] stuffCds) {
		service.useYn(stuffCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param stuff
	 * @param nationCd
	 * @return 재료정보 리스트
	 */
	@GetMapping
	@JsonView(StuffControllerJsonView.class)
	public List<Stuff> search(Integer storeCd, String stuffNm, String useYn) {
		return service.findStuffList(storeCd, stuffNm, useYn);
	}
	
	
	/**
	 * 엑셀 출력
	 * 
	 * @param lanData 조회 파라메타
	 * @throws Exception 
	 */
	@GetMapping("/down/{storeCd}")
	public void down(@PathVariable Integer storeCd, HttpServletResponse res) throws Exception {
		
		Store store = storeService.findOne(storeCd);
		
		if (!ObjectUtils.isEmpty(store)) {
			// 해더 설정
			List<Lan> lanList = store.getLans();
			
			String[] headers = new String[2 + lanList.size() * 2];
			
			int index = 0;
			
			headers[index++] = lanDataService.getLanData("재료명", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "stuffNm" + ExcelUtil.SPLIT + "l";
			
			// 기타 언어 해더 설정
			for (Lan data : lanList) {
				if (!lanService.getDefaultLan().getId().equals(data.getId())) {
					headers[index++] = lanDataService.getLanData("재료명", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "stuffNm" + data.getId() + ExcelUtil.SPLIT + "l";
				}
			}
			
			headers[index++] = lanDataService.getLanData("원산지", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "stuffNation" + ExcelUtil.SPLIT + "l";
			
			// 기타 언어 해더 설정
			for (Lan data : lanList) {
				if (!lanService.getDefaultLan().getId().equals(data.getId())) {
					headers[index++] = lanDataService.getLanData("원산지", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "stuffNation" + data.getId() + ExcelUtil.SPLIT + "l";
				}
			}
			
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
	@PostMapping("/up/{storeCd}")
	public void up(@PathVariable Integer storeCd, MultipartFile file) throws Exception {
		Store store = storeService.findOne(storeCd);
		
		if (!ObjectUtils.isEmpty(store)) {
			// 파일 및 파일 형식 체크
			if (null == file) {
				throw new RuntimeException("File is not exist!");
			} else if (!"application/vnd.ms-excel".equals(file.getContentType()) || !file.getOriginalFilename().endsWith(".xls")) {
				throw new RuntimeException("File type is not excel!");
			} else {
				HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
				
				// 해더 설정
				List<Lan> lanList = store.getLans();
				
				String[] headers = new String[2 + lanList.size() * 2];
				
				int index = 0;
				
				headers[index++] = lanDataService.getLanData("재료명", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "stuffNm" + ExcelUtil.SPLIT + "l";
				
				// 기타 언어 해더 설정
				for (Lan data : lanList) {
					if (!lanService.getDefaultLan().getId().equals(data.getId())) {
						headers[index++] = lanDataService.getLanData("재료명", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "stuffNm" + data.getId() + ExcelUtil.SPLIT + "l";
					}
				}
				
				headers[index++] = lanDataService.getLanData("원산지", LocaleContextHolder.getLocale()) + ExcelUtil.SPLIT + "stuffNation" + ExcelUtil.SPLIT + "l";
				
				// 기타 언어 해더 설정
				for (Lan data : lanList) {
					if (!lanService.getDefaultLan().getId().equals(data.getId())) {
						headers[index++] = lanDataService.getLanData("원산지", LocaleContextHolder.getLocale()) + "(" + data.getNm() + ")" + ExcelUtil.SPLIT + "stuffNation" + data.getId() + ExcelUtil.SPLIT + "l";
					}
				}
				
				// 파일 데이타 읽어 오기
				List<Map<String, Object>> datas = new ExcelUtil().readExcel(workbook, headers);
				
	            workbook.close();
	            
	            
	            List<Stuff> stuffList = new ArrayList<>();
	            
	            for (Map<String, Object> data : datas) {
	            	String stuffNm = MapUtils.getString(data, "stuffNm");
	            	
	            	// 재료명 설정이 없으면 등록 안함
	            	if (!StringUtils.isEmpty(stuffNm)) {
		            	String stuffNation = MapUtils.getString(data, "stuffNation", "");
		            	
		            	Stuff stuff = new Stuff();
		            	stuff.setStore(store);
		            	stuff.setDefaultLan(store.getDefaultLan());
		            	stuff.setStuffNm(stuffNm);
		            	stuff.setStuffNation(stuffNation);
		            	
		            	// 재료명 다국어 설정
		            	List<StuffInfo> stuffInfos = new ArrayList<>();
		            	
		            	for (Lan lanTmp : lanList) {
		            		StuffInfo stuffInfo = new StuffInfo();
		            		stuffInfo.setLanTp(lanTmp);
		            		
		            		String stuffInfoNm = MapUtils.getString(data, "stuffNm" + lanTmp.getId());
		            		
		            		if (StringUtils.isEmpty(stuffInfoNm)) {
		            			stuffInfo.setStuffInfoNm(stuffNm);
		            		} else {
		            			stuffInfo.setStuffInfoNm(stuffInfoNm);
		            		}
		            		
		            		String stuffInfoNation = MapUtils.getString(data, "stuffNation" + lanTmp.getId());
		            		
		            		if (StringUtils.isEmpty(stuffInfoNation)) {
		            			stuffInfo.setStuffInfoNation(stuffNation);
		            		} else {
		            			stuffInfo.setStuffInfoNation(stuffInfoNation);
		            		}
		            		
		            		stuffInfos.add(stuffInfo);
						}
		            	
		            	stuff.setStuffInfos(stuffInfos);
		            	
		            	stuffList.add(stuff);
	            	}
	            }
	            
	            service.save(stuffList);
			}
		}
	}
	
	
}
