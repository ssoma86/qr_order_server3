package org.lf.app.models.business.event;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.event.EventController.EventControllerCommonJsonView;
import org.lf.app.models.business.event.EventController.EventControllerJsonView;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.ClientController.ClientControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 이벤트
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_EVENT")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "lan" })
@ToString(callSuper = false, exclude={ "lan" })
public class Event extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -8647738175765137045L;

	public interface EventValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ EventControllerJsonView.class, EventControllerCommonJsonView.class })
	private Integer eventCd;

	/** 언어 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(nullable = false)
	@Valid
	@JsonView({ EventControllerJsonView.class })
	private Lan lan;
	
	/** 타이틀 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "제목,{max}", groups = { EventValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "제목", groups = { EventValid.class })
	@JsonView({ EventControllerJsonView.class, ClientControllerJsonView.class })
	private String eventTitle;
	
	/** 내용 */
	@Column(columnDefinition="TEXT", nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "내용", groups = { EventValid.class })
	@JsonView({ EventControllerJsonView.class, ClientControllerJsonView.class })
	private String content;
	
	/** 시작 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "시작 일시", groups = { EventValid.class })
	@JsonView({ EventControllerJsonView.class, ClientControllerJsonView.class })
	private Date startDtm;
	
	/** 종료 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "종료 일자", groups = { EventValid.class })
	@JsonView({ EventControllerJsonView.class })
	private Date endDtm;
	
	/** 링크 */
	@Column(length = 100)
	@JsonView({ EventControllerJsonView.class })
	private String link = "";
	
	/** 메뉴정보 이미지 */
	@Column(length = 100)
	@JsonView({ EventControllerJsonView.class })
	private String img = "";
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class })
	private String imgFile;
	
}
