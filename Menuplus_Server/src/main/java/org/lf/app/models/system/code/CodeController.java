package org.lf.app.models.system.code;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 코드 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/code")
public class CodeController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private CodeService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	public interface CodeControllerJsonView extends BaseJsonView {}
	public interface CodeControllerCommonJsonView {}
	public interface CodeControllerTopJsonView extends CodeControllerJsonView {}
	public interface CodeControllerSubJsonView extends CodeControllerJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param code
	 * @return 코드 정보
	 */
	@PostMapping
	@CacheEvict(value = "code", allEntries = true)
	public void add(@Valid @RequestBody Code code) {
		service.save(code);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param roleId 역할ID
	 * @return 역할 정보
	 */
	@GetMapping("/{cd}")
	@JsonView(CodeControllerTopJsonView.class)
	public Code get(@PathVariable Integer cd) {
		return service.findOne(cd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param code
	 * @return 코드 정보
	 */
	@PutMapping("/{cd:\\d+}")
	@CacheEvict(value = "code", allEntries = true)
	public void up(@Valid @RequestBody Code code, @PathVariable Integer cd) {
		code.setCd(cd);
		service.save(code);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param cd
	 */
	@DeleteMapping("/{cds}")
	@CacheEvict(value = "code", allEntries = true)
	public void del(@PathVariable Integer[] cds) {
		service.useYn(cds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param code
	 * @return 코드 정보 리스트
	 */
	@GetMapping
	@JsonView(CodeControllerSubJsonView.class)
	public List<Code> search() {
		List<Code> codeList = service.findCodeList();
		
		codeList.forEach(code -> {
			code.setNmLan(lanDataService.getLanData(code.getNm(), LocaleContextHolder.getLocale()));
			
			code.getSubCode().forEach(subCode -> subCode.setNmLan(
					lanDataService.getLanData(subCode.getNm(), LocaleContextHolder.getLocale())));
		});
		
		return codeList;
	}
	
	/**
	 * 코드 값 사용여부 확인
	 * 
	 * @param topCodeVal
	 * @return boolean
	 */
	@PatchMapping("/chk/{topCodeCd}/{codeVal}")
	@JsonView(CodeControllerCommonJsonView.class)
	public Code chkTopCodeVal(@PathVariable Integer topCodeCd, @PathVariable String codeVal) {
		return service.findOneByTopCodeAndVal(service.findOne(topCodeCd), codeVal);
	}
	
	/**
	 * 상위 코드 값을 통해서 코드 조회
	 * 
	 * @param codeVal
	 * @return 코드 정보 리스트
	 */
	@Cacheable("code")
	@GetMapping("/getCode/{codeVal}")
	@JsonView(CodeControllerCommonJsonView.class)
	public List<Code> getCode(@PathVariable String codeVal) {
		List<Code> codeList = service.findByTopCode(codeVal);
		
		if (!ObjectUtils.isEmpty(codeList) && codeList.size() > 0) {
			codeList.forEach(code -> {
				code.setNmLan(lanDataService.getLanData(code.getNm(), LocaleContextHolder.getLocale()));
			});
		}
		
		return codeList;
	}
	
	
}
