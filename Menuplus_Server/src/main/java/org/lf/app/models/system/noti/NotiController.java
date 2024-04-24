package org.lf.app.models.system.noti;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.noti.Noti.NotiValid;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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

/**
 * 공지사항 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/noti")
public class NotiController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private NotiService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface NotiControllerJsonView extends BaseJsonView,
		LanControllerCommonJsonView, CodeControllerCommonJsonView {}
	public interface NotiControllerCommonJsonView {};
		
		
	/**
	 * 추가
	 * 
	 * @param noti 공지사항 정보
	 * @return 공지사항 정보
	 */
	@PostMapping
	public void add(@Validated(NotiValid.class) @RequestBody Noti noti) {
		service.save(noti);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param notiCd 공지사항 코드
	 * @return 공지사항 정보
	 */
	@GetMapping("/{notiCd}")
	@JsonView(NotiControllerJsonView.class)
	public Noti get(@PathVariable Integer notiCd) {
		return service.findOne(notiCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param noti 공지사항 정보
	 * @param notiCd 공지사항 코드
	 * @return 공지사항 정보
	 */
	@PutMapping("/{notiCd}")
	public void up(@Validated(NotiValid.class) @RequestBody Noti noti, @PathVariable Integer notiCd) {
		noti.setNotiCd(notiCd);
		service.save(noti);
	}
	
	/**
	 * 삭제
	 * 
	 * @param notiCd 공지사항 코드
	 */
	@DeleteMapping("/{notiCds}")
	public void del(@PathVariable Integer[] notiCds) {
		service.useYn(notiCds, "N");
	}
	
	/**
	 * 리스트 조회
	 * @param noti
	 * @param lanId 언어 값
	 * @param clientTpCd 단말 코드
	 * @return 공지사항 정보 리스트
	 */
	@GetMapping
	@JsonView(NotiControllerJsonView.class)
	public List<Noti> search(Noti noti, String lanId, Integer notiTargetCd) {
		List<Noti> notiList = service.findNotiList(noti, lanId, notiTargetCd);
		
		notiList.forEach(notiTmp -> {
			String notiTargets = "";
			
			for (int i = 0, len = notiTmp.getNotiTarget().size(); i < len; i++) {
				notiTmp.getNotiTarget().get(i).setNmLan(
						lanDataService.getLanData(notiTmp.getNotiTarget().get(i).getNm(), LocaleContextHolder.getLocale()));
				
				notiTargets += notiTmp.getNotiTarget().get(i).getNmLan() + ", ";
			}
			notiTmp.setNotiTargets(notiTargets.substring(0, notiTargets.length() - 2));
		});
		
		return notiList;
	}
	
	/**
	 * 리스트 조회
	 * @param noti
	 * @param lanId 언어 값
	 * @param clientTpCd 단말 코드
	 * @return 공지사항 정보 리스트
	 */
	@GetMapping("/showNotiList")
	@JsonView(NotiControllerJsonView.class)
	public List<Noti> getShowNotiList(@SessionAttribute(value = "sa_account") Account account) {
		List<Noti> notiList = new ArrayList<>();
		
		Iterator<Auth> auths = account.getAuths().iterator();
		
		while (auths.hasNext()) {
			Auth auth = auths.next();
			
			notiList.addAll(service.findOneByLanAndNotiTargetRef1(auth.getAuthId(), ""));
		}
		
		return notiList;
	}
	
	/**
	 * 리스트 조회
	 * @param noti
	 * @param lanId 언어 값
	 * @param clientTpCd 단말 코드
	 * @return 공지사항 정보 리스트
	 */
	@GetMapping("/showNotiData")
	@JsonView(NotiControllerJsonView.class)
	public List<Noti> getShowNotiData(@SessionAttribute(value = "sa_account") Account account) {
		List<Noti> notiList = new ArrayList<>();
		
		Iterator<Auth> auths = account.getAuths().iterator();
		
		while (auths.hasNext()) {
			Auth auth = auths.next();
			notiList.addAll(service.findOneByLanAndNotiTargetRef1(auth.getAuthId(), "Y"));
		}
		
		return notiList;
	}
	
}
