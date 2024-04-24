package org.lf.app.models.system.history;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.lf.app.models.Base;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.history.HistoryController.HistoryControllerJsonView;
import org.lf.app.models.system.resources.Resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 히스토리
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_HISTORY")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "account", "resources" })
@ToString(callSuper = false, exclude={ "account", "resources" })
public class History extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4384552192808715695L;

	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer seq;

	@Column(length = 50)
	@JsonView(HistoryControllerJsonView.class)
	private String sessionId;
	
	/** 계정 */
	@OneToOne(cascade = CascadeType.REFRESH, optional = true)
	@JoinColumn(name = "account_id", nullable = true)
	@JsonView(HistoryControllerJsonView.class)
	private Account account;
	
	/** 리소스 */
	@OneToOne(cascade = CascadeType.REFRESH, optional = true)
	@JoinColumn(name = "resources_cd", nullable = true)
	@JsonView(HistoryControllerJsonView.class)
	private Resources resources;
	
	/** 등록일시 */
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@Column
	@JsonView(HistoryControllerJsonView.class)
	protected Date inDtm;

	/** 등록IP */
	@Column(length = 24)
	@JsonView(HistoryControllerJsonView.class)
	protected String inIp;
	
	/** 기기 구분  (앱접속 : app)*/
	@Column(length = 10)
	@JsonView(HistoryControllerJsonView.class)
	private String deviceType;
	
}
