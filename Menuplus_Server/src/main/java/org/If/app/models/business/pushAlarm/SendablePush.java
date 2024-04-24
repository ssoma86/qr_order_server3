package org.If.app.models.business.pushAlarm;

import java.util.Map;

public interface SendablePush {
	
	public Map SendPush(Map messageData) throws Exception;
	
	public static enum pushType { MSG , RECEIPT, RESERVATION};
	
	
}
