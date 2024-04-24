package org.lf.app.config.socket;

import java.io.IOException;

import org.lf.app.models.system.lanData.LanDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAsync {

	@Autowired
	private LanDataService lanDataService;
	
	@Async
	public void sendMsg(String storeId) {
		try {
			WebSocketServer.sendMsg(storeId, lanDataService.getLanData("N건의 주문이 대기중입니다. ", LocaleContextHolder.getLocale()));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
