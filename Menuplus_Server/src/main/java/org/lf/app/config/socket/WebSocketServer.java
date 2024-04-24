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
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@ServerEndpoint(value = "/websocket/{storeId}/{notiTime}")
@Component
public class WebSocketServer {
	
	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
	
    public static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    
    private Session session;

    private String storeId = "";	// 매장 아이디
    private Integer notiTime = 0;	// 공지 타임
    private long lastTime = 0;		// 마지막 공지 시간
    private int orderCnt = 0;		// 오더 수량
    private boolean wait = false;	// 대기 상태
    
    /**
     * 연결
     * @param session
     * @param storeId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("storeId") String storeId,
    		@PathParam("notiTime") Integer notiTime) {
		this.session = session;
        this.storeId = storeId;
        this.notiTime = notiTime;
        this.lastTime = Calendar.getInstance().getTime().getTime() - 1000 * 60 * notiTime;
        logger.info("Server connenct....");
        
        logger.info("==================session:" + session);
        logger.info("==================session.getMaxIdleTimeout():" + this.session.getMaxIdleTimeout());
        logger.info("==================this.storeId:" + this.storeId);
        logger.info("==================this.notiTime:" + this.notiTime);
        logger.info("==================this.lastTime:" + this.lastTime);
        
        webSocketSet.add(this);
       
        
        // 처음 소캣 접속 시 주문 있으면 한번 날려줌
        LanDataService lanDataService = (LanDataService)SpringUtil.getBean("lanDataService");
        
        try {
        	Thread.sleep(10000);
			this.sendMsg(this, lanDataService.getLanData("N건의 주문이 대기중입니다. ", LocaleContextHolder.getLocale()));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
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
        error.printStackTrace();
        logger.info("error: "+error);
    }
    

    /**
     * 메세지 발송
     * @param message
     * @throws IOException 
     * @throws InterruptedException 
     */
    public static void sendMsg(String storeId, String message) throws IOException, InterruptedException {
    	
    	logger.info("==================================websocket sendMsg storeId:" + storeId);   	
    	logger.info("==================================WebSocketServer.webSocketSet:" + WebSocketServer.webSocketSet);   
    	
    	for (WebSocketServer item : WebSocketServer.webSocketSet) {
    		
    		logger.info("==================================websocket sendMsg item.storeId:" + item.storeId);   
    		logger.info("==================================websocket sendMsg item.notiTime:" + item.notiTime); 
    		
    		if (item.storeId.equals(storeId)) {
    			// 실시간
    			if (0 == item.notiTime) {
    				logger.info("==================111111111111111111111111111111111111111111================"); 
    				item.session.getBasicRemote().sendText(message.replaceFirst("N", "" + 1));
    			} else {
    				logger.info("==================222222222222222222222222222222222222222222================"); 
    				item.sendMsg(item, message);
    			}
    			
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
    public void sendMsg(WebSocketServer item, String message) throws IOException, InterruptedException {
    	// 시간 계산
		long time = (Calendar.getInstance().getTime().getTime() - item.lastTime) / (1000 * 60) - 2000;
		
		time = Calendar.getInstance().getTime().getTime();
		
		logger.info("==================time:" + time); 
		logger.info("==================item.notiTime:" + item.notiTime); 
		
		// 발송 간격이 되었을 시 발송
		if (time >= item.notiTime) {
			// 라스트 타임 재설정
			item.lastTime = Calendar.getInstance().getTime().getTime();
			
			Thread.sleep(2000); // 주문 저장후 텀이 있음으로 저장 되기전 카운터 하게됨 2총 텀을 주고 카운터 함
			OrderService orderService = (OrderService)SpringUtil.getBean("orderService");
			
			// 이미 조회 된거와 취소 건은 제외 하기 위해서 다시 조회 함
			orderCnt = orderService.findSearchCnt(item.storeId);
			
			logger.info("==================orderCnt:" + orderCnt); 
			
			if (orderCnt > 0) {
				// 소캣 발송
				item.session.getBasicRemote().sendText(message.replaceFirst("N", "" + orderCnt));
				logger.info("==================message:" + message.replaceFirst("N", "" + orderCnt)); 
				
			}
			// 카운터 초기화
			item.orderCnt = 0;
			item.wait = false;
		} else {
			// 대기 상태이면 다시 호출 안함
			if (!item.wait) {
				item.wait = true;
				// 남은 시간 만큼 대기
				Thread.sleep((item.notiTime - time) * 60 * 1000);
				
				item.sendMsg(item, message);
			}
		}
    }
    
    
}
