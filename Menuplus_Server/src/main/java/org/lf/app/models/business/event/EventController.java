package org.lf.app.models.business.event;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.business.event.Event.EventValid;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 이벤트 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/event")
public class EventController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private EventService service;
	
	@Autowired
	private FileUtil fileUtil;
	
	// JsonView
	public interface EventControllerJsonView extends BaseJsonView,
		LanControllerCommonJsonView {}
	public interface EventControllerCommonJsonView {};
		
		
	/**
	 * 추가
	 * 
	 * @param event 이벤트 정보
	 * @return 이벤트 정보
	 * @throws Exception 
	 */
	@PostMapping
	public void add(@Validated(EventValid.class) @RequestBody Event event) throws Exception {
		if (!StringUtils.isEmpty(event.getImgFile())) {
			event.setImg(fileUtil.saveFile("event", "EVENT", event.getImgFile()));
		}
		
		service.save(event);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param eventCd 이벤트 코드
	 * @return 이벤트 정보
	 */
	@GetMapping("/{eventCd}")
	@JsonView(EventControllerJsonView.class)
	public Event get(@PathVariable Integer eventCd) {
		return service.findOne(eventCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param event 이벤트 정보
	 * @param eventCd 이벤트 코드
	 * @return 이벤트 정보
	 * @throws Exception 
	 */
	@PutMapping("/{eventCd}")
	public void up(@Validated(EventValid.class) @RequestBody Event event, @PathVariable Integer eventCd) throws Exception {
		event.setEventCd(eventCd);
		
		if (!StringUtils.isEmpty(event.getImgFile()) && event.getImgFile().contains("base64")) {
			event.setImg(fileUtil.delAndSaveFile(event.getImg(), "event", "EVENT", event.getImgFile()));
		}
		
		service.save(event);
	}
	
	/**
	 * 삭제
	 * 
	 * @param eventCd 이벤트 코드
	 */
	@DeleteMapping("/{eventCds}")
	public void del(@PathVariable Integer[] eventCds) {
		service.useYn(eventCds, "N");
	}
	
	/**
	 * 리스트 조회
	 * @param event
	 * @param lanId 언어 값
	 * @return 이벤트 정보 리스트
	 */
	@GetMapping
	@JsonView(EventControllerJsonView.class)
	public List<Event> search(Event event, String lanId) {
		return service.findEventList(event, lanId);
	}
	
	
}
