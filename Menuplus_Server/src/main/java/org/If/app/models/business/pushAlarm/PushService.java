package org.If.app.models.business.pushAlarm;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 푸시알림 서비스
 * 
 * @author 박영근
 *
 */
@Service
@Transactional
public class PushService extends BaseService<PushLog> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private PushLogRepository repository;
	
	
}
