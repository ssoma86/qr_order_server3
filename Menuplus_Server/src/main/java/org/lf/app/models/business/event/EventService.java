package org.lf.app.models.business.event;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;


/**
 * 이벤트 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class EventService extends BaseService<Event> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private EventRepository repository;
	
	
	/**
	 * 이벤트 정보 조회
	 * 
	 * @param useYn 사용여부
	 * @param lanId 언어 코드
	 * @return 이벤트 정보 리스트
	 */
	public List<Event> findEventList(Event event, String lanId) {
		return repository.findEventList(event.getUseYn(), lanId);
	}
	
	
	/**
	 * 앱에서 이벤트 정보 조회
	 * 
	 * @return 공지사항 정보 리스트
	 */
	public List<Event> findOneByLan() {
		return repository.findOneByLan(
				LocaleContextHolder.getLocale().getLanguage(),
				new Date());
	}
	
	
}
