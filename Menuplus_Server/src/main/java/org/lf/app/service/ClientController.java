package org.lf.app.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.lf.app.models.business.event.Event;
import org.lf.app.models.business.event.EventService;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanData;
import org.lf.app.models.system.lanData.LanDataController.LanDataControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.noti.Noti;
import org.lf.app.models.system.noti.NotiService;
import org.lf.app.models.system.terms.Terms;
import org.lf.app.models.system.terms.TermsController.TermsControllerCommonJsonView;
import org.lf.app.models.system.terms.TermsService;
import org.lf.app.service.app.AppService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

@RestController
@Transactional
@RequestMapping("/api")
public class ClientController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private LanDataService service;
	
	@Autowired
    private CodeService codeService;
	
	@Autowired
    private AppService appService;
	
	@Autowired
    private TermsService termsService;
	
	@Autowired
    private NotiService notiService;
	
	@Autowired
    private EventService eventService;
	
	
	public interface ClientControllerJsonView {}
	
	
	
	/**
	 * 단말에서 언어 데이타 조회
	 * @param clientTpVal
	 * @return 언어 데이타 리스트
	 */
	@GetMapping("/getLanData/{clientTpVal}")
	@JsonView(LanDataControllerCommonJsonView.class)
	public List<LanData> getLanData(@PathVariable String clientTpVal) {
		if (StringUtils.isEmpty(clientTpVal)) {
			clientTpVal = "WEB_APP";
		}
		
		return service.findByClientTp(codeService.findOneByTopCodeValAndVal("CLIENT_TP", clientTpVal));
	}
	
	
	/**
	 * 약관정보 조회
	 * @param lanId
	 * @return 이용 약관정보
	 */
	@GetMapping("/getTerms/{lanId}/{termsTp}")
	@JsonView(TermsControllerCommonJsonView.class)
	public Terms getTerms(@PathVariable String lanId, @PathVariable String termsTp) {
		appService.chkAndSettingLan(lanId);
		
		return termsService.findOneByLanAndTermsTp(lanId, termsTp);
	}
	
	
	/**
	 * 공지사항 조회
	 * @param lanId
	 * @return 공지사항 리스트
	 */
	@GetMapping("/getNoti/{notiCd}/{lanId}")
	@JsonView(ClientControllerJsonView.class)
	public Map<String, Object> getNoti(@PathVariable String lanId, @PathVariable Integer notiCd) {
		appService.chkAndSettingLan(lanId);
		
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> notis = new ArrayList<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat(service.getLanData("yyyy년 MM월 dd일 HH:mm", LocaleContextHolder.getLocale()));
		
		List<Noti> notiList = notiService.findOneByLanAndNotiTargetRef1("appUser", "");
		
		result.put("total", notiList.size());
		
		int jumpCnt = 0;
		boolean begin = 0 == notiCd ? true : false;
		
		for (Noti notiTmp : notiList) {
			// 시작점 찾기
			if (!begin) {
				jumpCnt++;
				
				if (notiTmp.getNotiCd().equals(notiCd)) {
					begin = true;
				}
				continue;
			}
			
			// 한번에 30개 씩 내려줌
			if (notis.size() >= 30) {
				break;
			}
			
			jumpCnt++;
			
			Map<String, Object> tmp = new HashMap<>();
			
			tmp.put("notiCd", notiTmp.getNotiCd());
			tmp.put("title", notiTmp.getNotiTitle());
			tmp.put("content", notiTmp.getContent());
			tmp.put("startDtm", sdf.format(notiTmp.getStartDtm()));
			
			notis.add(tmp);
		}
		
		result.put("list", notis);
		result.put("remain", notiList.size() - jumpCnt);
		
		return result;
	}
	
	
	/**
	 * 이벤트 조회
	 * @param lanId
	 * @return 공지사항 리스트
	 */
	@GetMapping("/getEvent/{evnetCd}/{lanId}")
	@JsonView(ClientControllerJsonView.class)
	public Map<String, Object> getEvent(@PathVariable String lanId, @PathVariable Integer evnetCd) {
		appService.chkAndSettingLan(lanId);
		
		Map<String, Object> result = new HashMap<>();
		List<Map<String, Object>> events = new ArrayList<>();
		
		SimpleDateFormat sdf = new SimpleDateFormat(service.getLanData("yyyy년 MM월 dd일 HH:mm", LocaleContextHolder.getLocale()));
		
		List<Event> eventList = eventService.findOneByLan();
		
		result.put("total", eventList.size());
		
		int jumpCnt = 0;
		boolean begin = 0 == evnetCd ? true : false;
		
		for (Event eventTmp : eventList) {
			// 시작점 찾기
			if (!begin) {
				jumpCnt++;
				
				if (eventTmp.getEventCd().equals(evnetCd)) {
					begin = true;
				}
				continue;
			}
			
			// 한번에 30개 씩 내려줌
			if (events.size() >= 30) {
				break;
			}
			
			jumpCnt++;
			
			Map<String, Object> tmp = new HashMap<>();
			
			tmp.put("eventCd", eventTmp.getEventCd());
			tmp.put("title", eventTmp.getEventTitle());
			tmp.put("content", eventTmp.getContent());
			tmp.put("link", eventTmp.getLink());
			tmp.put("img", eventTmp.getImg());
			tmp.put("startDtm", sdf.format(eventTmp.getStartDtm()));
			tmp.put("endDtm", sdf.format(eventTmp.getEndDtm()));
			
			events.add(tmp);
		}
		
		result.put("list", events);
		result.put("remain", eventList.size() - jumpCnt);
		
		return result;
	}
	
	
}
