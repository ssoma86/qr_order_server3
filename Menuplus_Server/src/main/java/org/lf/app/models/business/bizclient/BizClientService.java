package org.lf.app.models.business.bizclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.category.Category;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;


/**
 * 카테고리 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class BizClientService extends BaseService<Category> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private BizClientRepository repository;
	
	
	
	/**
	 * 알림톡 발송
	 * 
	 * @param destPhone
	 * @param msgBody
	 * @param templateCode
	 * @param senderKey
	 */
	public void sendBiz(String callback, String text, String dstaddr, String subject) {
		
		repository.send("1", callback, dstaddr, subject, text);
	}
	
	
	
	public List<Map<String, Object>> search(String dest_phone) {
		return repository.search(dest_phone);
	}
	
	
	
	
}
