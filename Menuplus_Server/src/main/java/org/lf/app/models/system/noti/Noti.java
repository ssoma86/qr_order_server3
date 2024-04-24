package org.lf.app.models.system.noti;

import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
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
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.noti.NotiController.NotiControllerCommonJsonView;
import org.lf.app.models.system.noti.NotiController.NotiControllerJsonView;
import org.lf.app.service.ClientController.ClientControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 공지사항
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SYS_NOTI")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "lan", "notiTarget" })
@ToString(callSuper = false, exclude={ "lan", "notiTarget" })
public class Noti extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -8647738175765137045L;

	public interface NotiValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ NotiControllerJsonView.class, NotiControllerCommonJsonView.class })
	private Integer notiCd;

	/** 언어 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(nullable = false)
	@Valid
	@JsonView({ NotiControllerJsonView.class })
	private Lan lan;
	
	/** 타이틀 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "제목,{max}", groups = { NotiValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "제목", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class, ClientControllerJsonView.class })
	private String notiTitle;
	
	/** 내용 */
	@Column(columnDefinition="TEXT", nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "내용", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class, ClientControllerJsonView.class })
	private String content;
	
	/** 시작 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "시작 일시", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class, ClientControllerJsonView.class })
	private Date startDtm;
	
	/** 종료 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "종료 일자", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class })
	private Date endDtm;
	
	/** 팝업 표시 여부 */
	@Column(length = 1, nullable = false)
	@Size(max = 1, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "팝업 표시 여부,{max}", groups = { NotiValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "팝업 표시 여부", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class, ClientControllerJsonView.class })
	private String popupYn = "N";
	
	/** 공지 타겟 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="SYS_NOTI_X_CODE",
			joinColumns=@JoinColumn(name="notiCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@Size(min = 1, message = "{0}을 선택 하여 주십시오." + SPLIT + "공지 대상,{max}", groups = { NotiValid.class })
	@JsonView({ NotiControllerJsonView.class })
	@OrderBy("ord")
	private List<Code> notiTarget;
	
	@Transient
	@JsonView({ NotiControllerJsonView.class })
	private String notiTargets;
	
}
