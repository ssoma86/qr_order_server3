package org.lf.app.models.business.bizclient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 카테고리 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/biz")
public class BizClientController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private BizClientService service;
	
	
	
	/**
	 * 정보 조회
	 * 
	 * @param categoryCd 카테고리 코드
	 * @return 카테고리 정보
	 */
	@PostMapping("/send")
	public void get(@RequestParam Map<String, String> params) {
		String destPhone = MapUtils.getString(params, "destPhone");
		String msgBody = MapUtils.getString(params, "msgBody");
		String templateCode = "";
		String senderKey = "";
		
		service.sendBiz(destPhone, msgBody, templateCode, senderKey);
	}
	
	
	/**
	 * 추가
	 * 
	 * @param push 푸시 정보
	 * @return 푸시 정보
	 * @throws IOException 
	 */
	@PostMapping
	public void add(@RequestBody Map<String, String> params) throws IOException {
		String destPhone = MapUtils.getString(params, "destPhone");
		String msgBody = MapUtils.getString(params, "msgBody");
		String templateCode = MapUtils.getString(params, "templateCode");
		String senderKey = MapUtils.getString(params, "senderKey");
		
		service.sendBiz(destPhone, msgBody, templateCode, senderKey);
	}
	
	
	
	/**
	 * 리스트 조회
	 */
	@GetMapping
	public List<Map<String, Object>> search(String dest_phone) {
		return service.search(dest_phone);
	}
	
	
	
}
