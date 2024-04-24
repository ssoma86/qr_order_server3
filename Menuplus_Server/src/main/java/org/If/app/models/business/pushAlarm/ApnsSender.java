package org.If.app.models.business.pushAlarm;

import javapns.Push;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationBigPayload;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.ResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApnsSender implements SendablePush{
	private Logger logger = LoggerFactory.getLogger(ApnsSender.class);

	public static final String PACKAGE_INNOPAY = "kr.co.infinisoft.innopay"; // 이노페이 (안드로이드와 iOS가 패키지명이 다름)
	// INNOPAY APNS 인증서
	public String APNS_INNOPAY_PRODUCT_KEY 			= "/WEB-INF/apns/apns_innopay_distribution.p12";
	public String APNS_INNOPAY_PRODUCT_KEY_PW		= "1q2w3e4r!";
	
	@Autowired
	ServletContext context;
	
	@Override
	public Map SendPush(Map reservationVO) {
		JSONObject data = new JSONObject(reservationVO);
		try {
			HashMap<String,Object> result = sendMessage(reservationVO.get("packageName").toString(),
														reservationVO.get("deviceId").toString(), 
														data.toJSONString());
			if (!result.isEmpty()) {
				logger.info("**** Push Sent message {}", data.toJSONString());
				reservationVO.put("resultCode", "0000");
				reservationVO.put("resultMsg", "Push 알림 성공");
				
			} else {
				reservationVO.put("resultCode", "0001");
				reservationVO.put("resultMsg", "Push 알림 실패");
			}
		} catch (Exception e) {
			reservationVO.put("resultCode", "0009");
			reservationVO.put("resultMsg", "APNS 전송 오류");
			logger.error("APNS 메시지 전송 중 Exception 발생", e.getMessage(), e);
		}
		return reservationVO;
	}
	
	private HashMap<String,Object> sendMessage(String packageName, String deviceId, String msg) throws Exception {
		logger.info("IOS PUSH - ApnsNotification.sendMessage");
		HashMap<String, Object> result = new HashMap<String, Object>();
		
		if (null == deviceId || deviceId.length() < 1) {
			return result;
		}

		PushNotificationBigPayload payload = PushNotificationBigPayload.complex();
		org.json.JSONObject json = new org.json.JSONObject(msg);
		payload.addAlert(json.getString("title"));
		payload.addCustomDictionary("d", msg);
		
		logger.info("[PUSH] PackageName -> " + packageName);
		PushedNotification notification;
		if (packageName.equals(PACKAGE_INNOPAY)) { // INNOPAY APNS
			if ((notification = isSuccess(payload, context.getRealPath(APNS_INNOPAY_PRODUCT_KEY), APNS_INNOPAY_PRODUCT_KEY_PW, true, deviceId)) != null) {
				result.put("result", notification);
				result.put("keyName", "innopayPubKey");
				result.put("key", "APNS_PRODUCTION_KEY");
			} else {
				logger.error("[PUSH] 발송 실패 -> " + payload.getPayload().toString());
			}
		}
		
		return result;
	}
	
	private PushedNotification isSuccess(PushNotificationBigPayload payload, Object keystore, String password, boolean isProduction, String deviceId) {
		try {
			PushedNotifications notifications = Push.payload(payload, keystore, password, isProduction, deviceId);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < notifications.size(); i++) {
				if (notifications.get(i).isSuccessful()) {
					logger.info("[PUSH] SUCCESSED REASON -> " + notifications.get(i).toString());
					return notifications.get(i);
				} else {
					builder.append(String.format("[%d] %s", i, notifications.get(i).getResponse())).append("\n");
				}
			}
			logger.warn("[PUSH] FAILED REASON -> " + builder.toString());
		} catch (Exception e) {
			logger.error("[PUSH] EXCEPTION -> " + e.getMessage());
		}
		return null;
	}
	
}
