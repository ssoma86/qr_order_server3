package org.If.app.models.business.pushAlarm;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * push알림log
 * 23.11.20
 * @author ykpark
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_PUSH_LOG")
@Data
public class PushLog extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -3017478045768565790L;

	public interface PushValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "사업장", groups = { PushValid.class })
	@JsonView({ WebAppControllerJsonView.class})
	private Integer pushCd;

	/** 서비스 아이디 */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class})
	private String serviceId;
	
	/** 서비스 명 */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class})
	private String serviceNm;
	
	/** 프로젝트키  */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String projectKey;
	
	/** tid */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String tid;
	
	/** 제목 */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String title;
	
	/** 본문 */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String msg;
	
	/** token */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String token;
	
	/** osType */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String osType;
	
	/** resultCode */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String resultCode;
	
	/** resultMsg */
	@Column(length = 255)
	@JsonView({ WebAppControllerJsonView.class })
	private String resultMsg;
	
	/** 등록 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@JsonView({ WebAppControllerJsonView.class })
	private Date registDate;
	
}
