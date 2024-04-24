package org.lf.app.models.system.noti;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;


/**
 * 공지사항 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class NotiService extends BaseService<Noti> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private NotiRepository repository;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
    private CodeService codeService;
	
	
	
	/**
	 * 언어, 단말 타입, 날자를 통해서 공지사항 가져오기
	 * 
	 * @param lanId 언어 코드
	 * @param notiTargetVal 단말 타입
	 * @return 공지사항 정보 리스트
	 */
	public List<Noti> findNoti(String lanId) {
		// 언어 체크
		Lan lan = lanService.findOne(lanId);
		if(null == lan) {
			lan = lanService.findOne("en");
		}
		
		// 단말 타입
		Code notiTarget = codeService.findOneByTopCodeValAndVal("NotiTarget", "NotiTargetApp");
				
		return repository.findOneByLanAndNotiTarget(lanId, notiTarget.getCd(), new Date());
	}
	
	
	/**
	 * 웹 공지사항 정보 조회
	 * 
	 * @param notiTargetCd 단말 타입 코드
	 * @return 공지사항 정보 리스트
	 */
	public List<Noti> findOneByLanAndNotiTargetRef1(String notiTargetRef1, String popupYn) {
		return repository.findOneByLanAndNotiTargetRef1(
				LocaleContextHolder.getLocale().getLanguage(), notiTargetRef1, popupYn, new Date());
	}
	
	
	
	/**
	 * 공지사항 정보 조회
	 * 
	 * @param useYn 사용여부
	 * @param lanId 언어 코드
	 * @param notiTargetCd 단말 타입 코드
	 * @return 공지사항 정보 리스트
	 */
	public List<Noti> findNotiList(Noti noti, String lanId, Integer notiTargetCd) {
		return repository.findNotiList(noti.getUseYn(), lanId, notiTargetCd);
	}
	
	
}
