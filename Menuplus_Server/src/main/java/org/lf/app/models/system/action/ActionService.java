package org.lf.app.models.system.action;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 이벤트
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class ActionService extends BaseService<Action> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private ActionRepository repository;

	
	
	/**
	 * 이벤트ID을 통해서 이벤트 정보 조회
	 * 
	 * @param actionId
	 * @return 이벤트 정보
	 */
	public Action findOneByActionId(String actionId) {
		return repository.findOneByActionId(actionId);
	}
	
	
	/**
	 * 이벤트명을 통해서 이벤트 정보 조회
	 * 
	 * @param actionNm
	 * @return 이벤트 정보
	 */
	public Action findOneByActionNm(String actionNm) {
		return repository.findOneByActionNm(actionNm);
	}
	
	/**
	 * 이벤트명, 사용여부를 통해서 이벤트 정보 리스트 조회
	 * 
	 * @param resourceNm
	 * @param useYn
	 * @return 이벤트 정보 리스트
	 */
	public List<Action> findActionList(Action action, Integer actionTp) {
		return repository.findActionList(action.getActionNm(), action.getUseYn(), actionTp);
	}

	
}
