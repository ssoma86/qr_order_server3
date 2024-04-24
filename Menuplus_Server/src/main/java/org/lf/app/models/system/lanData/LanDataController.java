package org.lf.app.models.system.lanData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.system.lanData.LanData.LanDataValid;
import org.lf.app.utils.system.ExcelUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.i18n.LocaleContextHolder;
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
 * 키워드 데이타 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/lanData")
public class LanDataController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private LanDataService service;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private CodeService codeService;
	
	
	// JsonView
	public interface LanDataControllerJsonView extends BaseJsonView,
		LanControllerCommonJsonView, CodeControllerCommonJsonView {}
	public interface LanDataControllerCommonJsonView {}
	
		
	/**
	 * 추가
	 * 
	 * @param lanData 키워드 정보
	 */
	@PostMapping
	@CacheEvict(value = "lan", allEntries = true)
	public void add(@Validated(LanDataValid.class) @RequestBody LanData lanData) {
		service.save(lanData);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param cd 언어코드
	 * @return 키워드 정보
	 */
	@GetMapping("/{cd}")
	@JsonView(LanDataControllerJsonView.class)
	public LanData get(@PathVariable Integer cd) {
		return service.findOne(cd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param lan 키워드 정보
	 */
	@PutMapping("/{cd}")
	@CacheEvict(value = "lan", allEntries = true)
	public void up(@Validated(LanDataValid.class) @RequestBody LanData lanData, @PathVariable Integer cd) {
		lanData.setCd(cd);
		service.save(lanData);
	}
	
	
	/**
	 * 키워드 정보 리스트 조회
	 * 
	 * @param lanData 조회 파라메타
	 * @return 키워드 데이타 정보
	 */
	@GetMapping
	@JsonView(LanDataControllerJsonView.class)
	public List<LanData> search(LanData lanData, Integer clientTpCd) {
		List<LanData> lanDataList = service.findLanDataList(lanData, clientTpCd);
		
		lanDataList.forEach(lanDataTmp -> {
			lanDataTmp.getClientTp().setNmLan(service.getLanData(lanDataTmp.getClientTp().getNm(), LocaleContextHolder.getLocale()));
		});
		
		return lanDataList;
	}

	
	/**
	 * 키워드ID 사용여부 확인
	 * 
	 * @param id 키워드ID
	 * @return 키워드 데이타 정보
	 */
	@PatchMapping("/chk/{id}")
	@JsonView(LanDataControllerCommonJsonView.class)
	public LanData chkId(@PathVariable String id) {
		return service.findOneByIdAndLan(id, lanService.findOne("ko"));
	}
	
	
	/**
	 * 엑셀 출력
	 * 
	 * @param lanData 조회 파라메타
	 * @throws Exception 
	 */
	@GetMapping("/down")
	public void down(LanData lanData, HttpServletResponse res) throws Exception {
		// 해더 설정
		Lan lan = new Lan();
		lan.setNm("");
		List<Lan> lanList = lanService.findLanList(lan);
		String[] headers = new String[lanList.size() + 2];
		headers[0] = "코드" + ExcelUtil.SPLIT + "id" + ExcelUtil.SPLIT + "l";
		headers[1] = "단말 타입" + ExcelUtil.SPLIT + "clientTp" + ExcelUtil.SPLIT + "l";
		headers[2] = "한국어" + ExcelUtil.SPLIT + "ko" + ExcelUtil.SPLIT + "l";
		// 기타 언어 해더 설정
		lanList.forEach(data -> {
			if (!lanService.getDefaultLan().getId().equals(data.getId())) {
				headers[data.getOrd()+1] = data.getNm() + ExcelUtil.SPLIT + data.getId() + ExcelUtil.SPLIT + "l";
			}
		});
		
		// 데이타 설정
		List<Map<String, Object>> dataList = new ArrayList<>();
		List<LanData> lanDataList = service.findLanDataList(lanData, 0);
		
		lanDataList.forEach(data -> {
			Map<String, Object> tmp = new HashMap<>();
			tmp.put("id", data.getId());
			tmp.put("clientTp", data.getClientTp().getVal());
			tmp.put("ko", data.getNm());
			
			data.getLanDatas().forEach(tmpData -> {
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
			String[] headers = new String[lanList.size() + 2];
			
			int i = 0;
			
			headers[i++] = "코드" + ExcelUtil.SPLIT + "id";
			headers[i++] = "단말 타입" + ExcelUtil.SPLIT + "clientTp";
			headers[i++] = "한국어" + ExcelUtil.SPLIT + "ko";
			
			// 기타 언어 해더 설정
			for (Lan lanTmp : lanList) {
				if (!lanService.getDefaultLan().getId().equals(lanTmp.getId())) {
					headers[i++] = lanTmp.getNm() + ExcelUtil.SPLIT + lanTmp.getId();
				}
			}
            
			// 파일 데이타 읽어 오기
			List<Map<String, Object>> datas = new ExcelUtil().readExcel(workbook, headers);
			
            workbook.close();
            
            
            for (Map<String, Object> data : datas) {
            	Lan lanKo = lanService.findOne("ko");
            	String id = MapUtils.getString(data, "id");
            	Code clientTp = codeService.findOneByTopCodeValAndVal("CLIENT_TP", MapUtils.getString(data, "clientTp"));
            	
            	// 단말 타입을 입력 잘못했거나 안했을 시 전부 포함으로 설정
            	if (null == clientTp) {
            		clientTp = codeService.findOneByTopCodeValAndVal("CLIENT_TP", "ALL");
            	}
            	
            	// 이미 동일한 아이디로 등록 된게 있는지 확인 있으면 수정
            	LanData tmp = service.findOneByIdAndLan(id, lanKo);
            	
            	// 없으면 새로 생성
            	if (null == tmp) {
            		tmp = new LanData();
            		tmp.setLan(lanKo);
            		tmp.setId(id);
            	}
            	
            	tmp.setClientTp(clientTp);
            	tmp.setNm(MapUtils.getString(data, "ko"));
            	
            	List<LanData> subLanData = new ArrayList<>();
            	
            	// 타 언어 설정
            	for (Lan lanTmp : lanList) {
            		if (!lanService.getDefaultLan().getId().equals(lanTmp.getId())) {
    					String subNm = MapUtils.getString(data, lanTmp.getId());
    					
    					if (!StringUtils.isEmpty(subNm)) {
    						LanData tmpSub = service.findOneByIdAndLan(id, lanTmp);
        					if (null == tmpSub) {
        						tmpSub = new LanData();
        						tmpSub.setLan(lanTmp);
        						tmpSub.setId(id);
        	            	}
        					
        					tmpSub.setClientTp(clientTp);
        					tmpSub.setNm(MapUtils.getString(data, lanTmp.getId()));
        					
        					subLanData.add(tmpSub);
    					}
    				}
            	}
            	
            	tmp.setLanDatas(subLanData);
            	
            	service.save(tmp);
            }
		}
	}
	
	
}
