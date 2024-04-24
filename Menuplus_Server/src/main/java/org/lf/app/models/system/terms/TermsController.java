package org.lf.app.models.system.terms;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.terms.Terms.TermsValid;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 약관 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/terms")
public class TermsController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private TermsService service;
	
	@Autowired
	private TermsAcceptService accetpService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	
	// JsonView
	public interface TermsControllerJsonView extends BaseJsonView,
		LanControllerCommonJsonView, CodeControllerCommonJsonView {}
	public interface TermsControllerCommonJsonView {};
		
	/**
	 * 추가
	 * 
	 * @param terms 약관 정보
	 */
	@PostMapping
	public void add(@Validated(TermsValid.class) @RequestBody Terms terms) {
		service.save(terms);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param termsCd 약관 코드
	 * @return 약관 정보
	 */
	@GetMapping("/{termsCd}")
	@JsonView(TermsControllerJsonView.class)
	public Terms get(@PathVariable Integer termsCd) {
		return service.findOne(termsCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param terms 약관 정보
	 * @param termsCd 약관 코드
	 */
	@PutMapping("/{termsCd}")
	public void up(@Validated(TermsValid.class) @RequestBody Terms terms, @PathVariable Integer termsCd) {
		terms.setTermsCd(termsCd);
		service.save(terms);
	}
	
	/**
	 * 삭제
	 * 
	 * @param termsCd 약관 코드
	 */
	@DeleteMapping("/{termsCds}")
	public void del(@PathVariable Integer[] termsCds) {
		service.useYn(termsCds, "N");
	}
	
	/**
	 * 리스트 조회
	 * 
	 * @param terms
	 * @param lanId 언어 값
	 * @return 약관 정보 리스트
	 */
	@GetMapping
	@JsonView(TermsControllerJsonView.class)
	public List<Terms> search(Terms terms, String lanId, Integer termsTpCd) {
		List<Terms> termsList = service.findTermsList(terms, lanId, termsTpCd);
		
		termsList.forEach(termsTmp -> {
			termsTmp.getTermsTp().setNmLan(
					lanDataService.getLanData(termsTmp.getTermsTp().getNm(), LocaleContextHolder.getLocale()));
		});
		
		return termsList;
	}
	
	/**
	 * 언어별 약관 사용여부 확인
	 * 
	 * @param lanId 언어 값
	 * @return 약관 정보
	 */
	@PatchMapping("/chk/{lanId}/{termsTpCd}")
	@JsonView(TermsControllerCommonJsonView.class)
	public Terms chkLanAndTermsTp(@PathVariable String lanId, @PathVariable Integer termsTpCd) {
		return service.findOneByLanIdAndTermsTp(lanId, termsTpCd);
	}
	
	/**
	 * 동의 리스트 조회
	 * 
	 * @param uuid 폰 고유 키
	 * @param termsTpCd 약관 구분
	 * @return 약관 동의 정보 리스트
	 */
	@GetMapping("/accept")
	public List<TermsAccept> searchAccept(String uuid, Integer termsTpCd) {
		return accetpService.findTermsAcceptList(uuid, termsTpCd);
	}
	
	
}
