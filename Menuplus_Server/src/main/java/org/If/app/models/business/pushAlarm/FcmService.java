package org.If.app.models.business.pushAlarm;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import org.lf.app.service.web.WebAppController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import net.minidev.json.JSONObject;

/**
 * push알림 서비스(2023.11.17)
 * 
 * @author ykpark 
 *
 */
@Service
public class FcmService extends HttpServlet implements SendablePush {

	private FirebaseApp firebaseApp;
		
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory.getLogger(FcmService.class);
	
	private static final String PROJECT_ID = "api-project-1032749216875";
	
	//private static final String SERVICE_ID = "AAAA8HSmdGs:APA91bGci7T3rYPW-6N5saEvlhdsel98Oy4DA_lv7G3zKOmWkRSuRvLSEzWUzVtOgWSitmhMzA6nyohswJqUSd_0LguWZ44BHK1oA2Ufu0MNg2GWrGdir2lHwATGEg7U2GNfoOy2AKIb";
	
	//private static final String SERVICE_NAME = "Innopay";
	
	private static final String RES_PATH = "api-project-1032749216875-firebase-adminsdk-8e2bc-d2e16773fc.json";
	
//	@Value("${fcm_key_path}")
//	private String RES_PATH;
	
//	@Autowired 
//	private ResourceLoader resLoader; 
	
//	@Autowired
//	private PushRepository pushRepository;
	
	public Map SendPush(Map messageData) throws Exception {
		
		logger.info("==== Request SendPush : "+messageData.toString());
		initializeFirebaseApp();
		
		// Send messages 
		if (sendMessage(messageData)) {
			logger.info("================== Push 알림 성공 ==========");
			messageData.put("resultCode", "0000");
			messageData.put("resultMsg", "Push 알림 성공");
			// DB INSERT
			//insertPushResult(messageData);
		} else {
			logger.info("================== Push 알림 실패 ==========");
			//messageData = failurePushResult(messageData, "");
			this.firebaseApp.delete();
			this.firebaseApp = null;
			messageData.put("resultCode", "0001");
	        messageData.put("resultMsg", "Push 알림 실패");
		}
		return messageData;
	}
	
	private void initializeFirebaseApp()  throws Exception  {
		
		logger.info("================RES_PATH:" + RES_PATH);
		
		if (FirebaseApp.DEFAULT_APP_NAME == null || FirebaseApp.getApps().size() == 0) {
			
			ClassPathResource resource = new ClassPathResource(RES_PATH);
			
			logger.info("====resource:" + resource);
			logger.info("====resource.getInputStream():" + resource.getInputStream());
			logger.info("====FirebaseApp.DEFAULT_APP_NAME:" +  FirebaseApp.DEFAULT_APP_NAME);
			
			InputStream serviceAccount = resource.getInputStream();
			
			logger.info("====serviceAccount:" + serviceAccount);
			
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setDatabaseUrl("https://" + PROJECT_ID + ".firebaseio.com")
					.build();
			this.firebaseApp = FirebaseApp.initializeApp(options);
			
		} else {
			this.firebaseApp = FirebaseApp.getInstance(FirebaseApp.DEFAULT_APP_NAME);
			
		}
		
	}
	
	private boolean sendMessage(Map data) {
	    try {
	        if (data != null && data.size() > 0) {
	            JSONObject jsonObj = new JSONObject(data);
	            
	            String title = "";
	            if(null != data.get("title")) {
	            	title = data.get("title").toString();
	            }
	            
	            String mesg = "";
	            if(null != data.get("mesg")) {
	            	mesg = data.get("mesg").toString();
	            }
	            
//	            String detailUrl = "";
//	            if(null != data.get("detailUrl")) {
//	            	detailUrl = data.get("detailUrl").toString();
//	            }
	            
	            
	            String osType = data.get("osType").toString();
	            
	            Message message = null;
	            if(osType.equals("android")) {
	            	
	            	// for Android  
//		            AndroidNotification androidNofi = AndroidNotification.builder()
//		                    .setSound("dingdong.mp3")
//		                    .build();
//		            AndroidConfig androidConfig = AndroidConfig.builder()
//		                    .setNotification(androidNofi)
//		                    .build(); 
	            	
	            	 message = Message.builder()
	 	                    .setNotification(Notification.builder()
	 	                            .setTitle(title)
	 	                            .setBody(mesg)
	 	                            .build())
	 	                    //.setAndroidConfig(androidConfig)   
	 	                    .putData("title", title)
	 	                    .putData("body", mesg)
	 	                    .setToken(data.get("token").toString())
	 	                    .build();
	            	 
	            } else {
	            	 // for iOS
		            Aps aps = Aps.builder()
		                .setSound("dingdong.mp3")
		                .setBadge(1)
		                .build();

		            ApnsConfig apnsConfig = ApnsConfig.builder()
		                        .setAps(aps)
		                        .build();
		            
		            message = Message.builder()
	 	                    .setNotification(Notification.builder()
	 	                            .setTitle(title)
	 	                            .setBody(mesg)
	 	                            .build())
	 	                    .setApnsConfig(apnsConfig)
	 	                    .putData("title", title)
	 	                    .putData("body", mesg)
	 	                    .setToken(data.get("token").toString())
	 	                    .build();
	            }
	            
	            logger.info("====message:" + message.toString());
	            
	            String results = FirebaseMessaging.getInstance(this.firebaseApp).send(message);
	            
	            if(null == results || results.length() == 0) {
	                return false;
	            }else {
	                logger.info("FCM Result message :" + results);
	                return true;
	            }
	            
	        }else {
	            return false;
	        }
	    }catch(FirebaseMessagingException ex) {
	        String errCode = ex.getErrorCode().toString();
	        String errMsg = ex.getMessage();
	        
	        logger.info("====== FirebaseMessagingException errCode  : " + errCode);
	        logger.info("====== FirebaseMessagingException errMsg  : " + errMsg);
	        
	        return false;
	    }catch(Exception e) {
	    	logger.info("====== FCM sendMessage exception  : " + e.getMessage());	
	        return false;
	    }
	}
	
	
	
}
