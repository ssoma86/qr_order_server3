package org.lf.app.models.business.push;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.HttpUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.minidev.json.JSONObject;


/**
 * 푸시 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class PushService extends BaseService<Push> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private PushRepository repository;
	
	
	
	/**
	 * 푸시 정보 조회
	 * 
	 * @param startDtm 시작일시
	 * @param endDtm 종료일시
	 * @return 푸시 정보 리스트
	 * @throws ParseException 
	 */
	public List<Push> findPushList(String startDtm, String endDtm, Integer pushTpCd) throws ParseException {
		return repository.findPushList(DateUtil.strToDateTime(startDtm), DateUtil.strToDateTime(endDtm), pushTpCd);
	}
	
	
	/**
	 * 푸시 발송 및 저장
	 * @param push
	 * @throws IOException 
	 */
	public void sendAndSave(Push push) throws IOException {
		
		JSONObject param = new JSONObject();
		param.appendField("condition", "'" + push.getPushTp().getVal() + "' in topics");
		JSONObject data = new JSONObject();
		data.appendField("pushfication_cd", 1);
		data.appendField("title", push.getTitle());
		data.appendField("msg", push.getContent());
		param.appendField("data", data);
		
		this.sendPush(param.toJSONString());
		
		this.save(push);
	}
	
	
	/**
	 * 푸시 발송
	 * @param push
	 * @throws IOException 
	 */
	public void sendPush(String param) throws IOException {
		Map<String, Object> headers = new HashMap<>();
		headers.put("Authorization", "key=pushkey");
		headers.put("Content-Type", "application/json");
	        
		try {
			HttpUtil.doPost("https://fcm.googleapis.com/fcm/send", param, headers);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
