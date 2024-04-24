package org.lf.app.models.business.smenuOptTp;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.smenuOptTp.SmenuOptTp.SmenuOptTpValid;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpInfo.SmenuOptTpInfoValid;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.system.lanData.LanDataService;
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

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 메뉴 옵션 그룹 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/smenuOptTp")
public class SmenuOptTpController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private SmenuOptTpService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface SmenuOptTpControllerJsonView extends StoreControllerJsonView {}
	public interface SmenuOptTpControllerCommonJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param smenuOptTp
	 */
	@PostMapping
	public void add(@Validated({ SmenuOptTpValid.class, SmenuOptTpInfoValid.class }) @RequestBody SmenuOptTp smenuOptTp) {
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
	@PutMapping("/modi/{smenuOptTpCd:\\d+}")
	public void up(@Validated({ SmenuOptTpValid.class, SmenuOptTpInfoValid.class }) @RequestBody SmenuOptTp smenuOptTp, @PathVariable Integer smenuOptTpCd) throws Exception {
		smenuOptTp.setSmenuOptTpCd(smenuOptTpCd);
		
		service.save(smenuOptTp);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param smenuOptTpCd
	 */
	@DeleteMapping("/del/{smenuOptTpCds}")
	public void del(@PathVariable Integer[] smenuOptTpCds) {
		service.useYn(smenuOptTpCds, "N");
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
	public List<SmenuOptTp> search(Integer storeCd, String smenuOptTpNm, String useYn) {
		
		List<SmenuOptTp> smenuOptTpList = service.findSmenuOptTpList(storeCd, smenuOptTpNm, useYn);
		
		smenuOptTpList.forEach(smenuOptTp -> {
			if (!ObjectUtils.isEmpty(smenuOptTp.getOptTp())) {
				smenuOptTp.getOptTp().setNmLan(lanDataService.getLanData(smenuOptTp.getOptTp().getNm(), LocaleContextHolder.getLocale()));
			}
		});
		
		return smenuOptTpList;
	}
	
	
}
