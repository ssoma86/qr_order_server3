package org.lf.app.config.socket;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@ServerEndpoint(value = "/websocket/{accountId}")
@Component
public class WebSocketServer4App {
	
	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer4App.class);
	
    public static CopyOnWriteArraySet<WebSocketServer4App> webSocketSet = new CopyOnWriteArraySet<WebSocketServer4App>();
    
    private Session session;

    private String accountId = "";	// 관리자 로그인 아이디
    private long lastTime = 0;		// 마지막 공지 시간
    private boolean wait = false;	// 대기 상태
    
    /**
     * 연결
     * @param session
     * @param storeId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("accountId") String accountId) {
		this.session = session;
        this.accountId = accountId;
        this.lastTime = Calendar.getInstance().getTime().getTime() - 1000 * 60;
        logger.info("Server connenct....");
        
        logger.info("==================session:" + session);
        logger.info("==================session.getMaxIdleTimeout():" + this.session.getMaxIdleTimeout());
        logger.info("==================this.accountId:" + this.accountId);
        logger.info("==================this.lastTime:" + this.lastTime);
        
        webSocketSet.add(this);
       
        
        // 처음 소캣 접속 시 주문 있으면 한번 날려줌
        //LanDataService lanDataService = (LanDataService)SpringUtil.getBean("lanDataService");
        
//        try {
//        	Thread.sleep(10000);
//			this.sendMsg(this, lanDataService.getLanData("N건의 주문이 대기중입니다. ", LocaleContextHolder.getLocale()));
//		} catch (IOException | InterruptedException e) {
//			e.printStackTrace();
//		}
    }

    
    /**
     * Close
     */
    @OnClose
    public void onClose() {
    	 logger.info("Server close....");
        webSocketSet.remove(this);
        
    }

    
    /**
     * 메세지 발송
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    	 logger.info("message....");
    }

	/**
	 * Error
	 * @param session
	 * @param error
	 */
    @OnError
    public void onError(Session session, Throwable error) {
        //error.printStackTrace();
        logger.info("error: "+error);
        
        if(error.equals("java.io.EOFException")) {
        	
        	try {
        		session.close();
        	}catch(IOException ex) {
        		ex.getMessage();
        	}
        }
       
    }
    

    /**
     * 메세지 발송
     * @param message
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void sendMsg(String accountId, String message) throws IOException, InterruptedException {
    	
    	logger.info("==================================websocket sendMsg accountId:" + accountId);   	
    	logger.info("==================================WebSocketServer.webSocketSet:" + WebSocketServer4App.webSocketSet);   
    	
    	for (WebSocketServer4App item : WebSocketServer4App.webSocketSet) {
    		
    		logger.info("==================================websocket sendMsg item.accountId:" + item.accountId);   
    		
    		if (item.accountId.equals(accountId)) {    			
				logger.info("================= item.sendMsg ================"); 
				item.sendMsg(item, message);
    			
    			break;
    		}
        }
    }

    
    /**
     * 메세지 전송
     * @param session
     * @param message
     * @throws IOException
     * @throws InterruptedException 
     */
    public void sendMsg(WebSocketServer4App item, String message) throws IOException, InterruptedException {
    	// 시간 계산
		long time = (Calendar.getInstance().getTime().getTime() - item.lastTime) / (1000 * 60) - 2000;
		
		time = Calendar.getInstance().getTime().getTime();
		
		logger.info("==================time:" + time); 
		logger.info("==================message:" + message); 
		
		// 발송 간격이 되었을 시 발송
		if (time >= 1) {
			// 라스트 타임 재설정
			item.lastTime = Calendar.getInstance().getTime().getTime();
			
			//Thread.sleep(2000); // 주문 저장후 텀이 있음으로 저장 되기전 카운터 하게됨 2총 텀을 주고 카운터 함			
			logger.info("===========1111111111111=======time:" + time); 
			
			// 소캣 발송
			item.session.getBasicRemote().sendText("새 주문이 들어 왔습니다.");
				
			// 카운터 초기화 
			item.wait = false;
		} else {
			
			logger.info("===========222222222222=======time:" + time); 
			
			// 대기 상태이면 다시 호출 안함
			if (!item.wait) {
				
				logger.info("===========wait======="); 
				
				item.wait = true;
				// 남은 시간 만큼 대기
				Thread.sleep((1 - time) * 60 * 1000);
				
				item.sendMsg(item, message);
			}
		}
    }
    
    
}
