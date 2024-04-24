package org.lf.app.models.system.action;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.system.action.Action.ActionValid;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.resources.Resources.ResourcesValid;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerCommonJsonView;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
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
 * 이벤트 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/action")
public class ActionController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private ActionService service;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface ActionControllerJsonView extends ResourcesControllerJsonView {}
	public interface ActionControllerCommonJsonView extends ResourcesControllerCommonJsonView {};
	
	
	/**
	 * 추가
	 * 
	 * @param action 이벤트 정보
	 */
	@PostMapping
	public void add(@Validated({ResourcesValid.class, ActionValid.class}) @RequestBody Action action) {
		action.setResTp(codeService.findOneByTopCodeValAndVal("RES_TP", "action"));
		service.save(action);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param resourcesCd 이벤트 코드
	 * @return 이벤트 정보
	 */
	@GetMapping("/{resourcesCd:\\d+}")
	@JsonView(ActionControllerJsonView.class)
	public Action get(@PathVariable Integer resourcesCd) {
		return service.findOne(resourcesCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param action
	 * @param resourcesCd
	 * @return 이벤트 정보
	 */
	@PutMapping("/{resourcesCd:\\d+}")
	public void up(@Validated({ResourcesValid.class, ActionValid.class}) @RequestBody Action action, @PathVariable Integer resourcesCd) {
		action.setResourcesCd(resourcesCd);
		action.setResTp(codeService.findOneByTopCodeValAndVal("RES_TP", "action"));
		service.save(action);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param resourcesCds
	 */
	@DeleteMapping("/{resourcesCds}")
	public void del(@PathVariable Integer[] resourcesCds) {
		service.useYn(resourcesCds, "N");
	}
	
	
	/**
	 * 이벤트 정보 리스트 조회
	 * 
	 * @param action
	 * @return 이벤트 정보
	 */
	@GetMapping
	@JsonView(ActionControllerJsonView.class)
	public List<Action> search(Action action, Integer actionTpCd) {
		List<Action> actionList = service.findActionList(action, actionTpCd);
		
		actionList.forEach(actionTmp -> {
			if (!ObjectUtils.isEmpty(actionTmp.getActionTp())) {
				actionTmp.getActionTp().setNmLan(
						lanDataService.getLanData(actionTmp.getActionTp().getNm(), LocaleContextHolder.getLocale()));
			}
		});
		
		return actionList;
	}

	
	/**
	 * 이벤트 아이디 중복여부 확인
	 * 
	 * @param actionId
	 * @return 이벤트 정보
	 */
	@PatchMapping("/chk/{actionId}")
	@JsonView(ActionControllerCommonJsonView.class)
	public Action chkActionId(@PathVariable String actionId) {
		return service.findOneByActionId(actionId);
	}
	
	
	/**
	 * 이벤트명 중복여부 확인
	 * 
	 * @param actionNm
	 * @return 이벤트 정보
	 */
	@PatchMapping("/chkNm/{actionNm}")
	@JsonView(ActionControllerCommonJsonView.class)
	public Action chkActionNm(@PathVariable String actionNm) {
		return service.findOneByActionNm(actionNm);
	}
	
	
}
