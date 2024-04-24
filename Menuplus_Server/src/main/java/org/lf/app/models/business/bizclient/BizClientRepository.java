package org.lf.app.models.business.bizclient;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 알림톡
 * 
 * @author LF
 * 
 */
public interface BizClientRepository extends JpaRepository<BizClient, Serializable> {
	
	
//	INSERT INTO biz_msg (MSG_TYPE, CMID, REQUEST_TIME, SEND_TIME, DEST_PHONE, SEND_PHONE,  MSG_BODY, TEMPLATE_CODE, SENDER_KEY, NATION_CODE) VALUES (
//			6, '201XXXXXXXXX', NOW(), NOW(), '01012341234', '0212341234',  '홍길동 고객님 다우기술 비즈메시지 프로모션에 당첨 되었습니다.', {템플릿코드}, {발신프로필키}, '82') 
	
	/**
	 * 
	 * 주문상태변경 후 알림메시지 발송 
	 */
	@Modifying
	@Query(nativeQuery = true, value = ""
			+ "INSERT INTO sjsms.msg_queue(msg_type, callback, dstaddr, subject, text, request_time) "
			+ "VALUES(:msg_type, :callback, :dstaddr, :subject, :text, now())")
	public void send(@Param("msg_type") String msg_type,@Param("callback") String callback,@Param("dstaddr") String dstaddr,
					@Param("subject") String subject,@Param("text") String text);
	
	
	@Query(nativeQuery = true, value = "SELECT * FROM BIZ_MSG WHERE DEST_PHONE LIKE %:dest_phone% ORDER BY SEND_TIME DESC")
	public List<Map<String, Object>> search(@Param("dest_phone") String dest_phone);
	
	
	
}
