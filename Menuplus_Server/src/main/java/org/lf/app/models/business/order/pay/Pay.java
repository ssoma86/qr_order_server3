package org.lf.app.models.business.order.pay;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.lf.app.models.Base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 사업장
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_PAY")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Pay extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4575983544383818060L;

	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer payCd;

	/** 결과코드 */
	@Column(length = 10, nullable = false)
	private String replyCd;
	
	/** 결과메세지 */
	@Column(length = 200, nullable = false)
	private String replyMsg;
	
	/** 주문번호 */
	@Column(length = 10, nullable = false)
	private String orderNo;
	
	/** 카드번호 */
	@Column(length = 50)
	private String cardId;
	
	/** 카드명 */
	@Column(length = 50)
	private String cardNm;
	
	/** 승인금액 */
	@Column(length = 10, nullable = false)
	private String amt;
	
	/** 지불수단 */
	@Column(length = 20, nullable = false)
	private String payType;
	
	/** 승인일시 */
	@Column(length = 50, nullable = false)
	private String approvalYmdHms;
	
	/** 거래일련번호 */
	@Column(length = 10, nullable = false)
	private String seqNo;
	
	/** 승인번호 */
	@Column(length = 30, nullable = false)
	private String approvalNo;
	
	/** 결제 사 AllAt, TPay */
	@Column(length = 30, nullable = false)
	private String payComp;
	
	/** 취소여부 */
	@Column(length = 1, nullable = false)
	private String cancelYn = "N";
	
	/** 취소일시 */
	@Column(length = 50)
	private String cancelYmdHms;
	
	
}
