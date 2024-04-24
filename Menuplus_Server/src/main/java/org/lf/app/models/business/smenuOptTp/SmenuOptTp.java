package org.lf.app.models.business.smenuOptTp;

import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.smenuOpt.SmenuOpt.SmenuOptValid;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpController.SmenuOptTpControllerCommonJsonView;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpController.SmenuOptTpControllerJsonView;
import org.lf.app.models.business.smenuOptTp.SmenuOptTpInfo.SmenuOptTpInfoValid;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 옵션 그룹 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_SMENU_OPT_TP")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "smenuOptTpInfos", "store", "smenuOpts" })
@ToString(callSuper = false, exclude = { "defaultLan", "smenuOptTpInfos", "store", "smenuOpts" })
public class SmenuOptTp extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -5067361713845454944L;
	
	
	public interface SmenuOptTpValid {}
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "옵션 그룹", groups = { SmenuOptValid.class })
	@JsonView({
		SmenuOptTpControllerJsonView.class, SmenuOptTpControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer smenuOptTpCd;

	/** 디폴트 언어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptTpControllerJsonView.class, SmenuOptTpControllerCommonJsonView.class })
	private Lan defaultLan;
	
	/** 메뉴 옵션 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_SMENU_OPT_TP_X_SMENU_OPT_TP_INFO",
			joinColumns=@JoinColumn(name="smenuOptTpCd"),
			inverseJoinColumns=@JoinColumn(name="smenuOptTpInfoCd"))
	@Valid
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private List<SmenuOptTpInfo> smenuOptTpInfos;
	
	/** 표시 순서 */
	@Column(length=2, nullable=false)
	@Min(value = 1, message="{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}", groups = { SmenuOptTpValid.class })
	@Max(value = 99, message="{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}", groups = { SmenuOptTpValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { SmenuOptTpValid.class })
	@JsonView({ SmenuOptTpControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer ord = 1;
	
	/** 최대 옵션 선택 개수 (2023.07.03 추가)*/
	@Column(length=2, nullable=false)
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { SmenuOptTpValid.class })
	@JsonView({ SmenuOptTpControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer smenuOptTpMaxCnt=1;
	
	/** 메뉴 옵션 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "메뉴 옵션 설명,{max}", groups = { SmenuOptTpValid.class })
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private String smenuOptTpDesc;
	
	/** 소속 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private Store store;
	
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "옵션 그룹명", groups = { SmenuOptTpInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "옵션 그룹명,{max}", groups = { SmenuOptTpInfoValid.class })
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private String smenuOptTpNm;
	
	@Transient
	@JsonView({
		SmenuOptTpControllerJsonView.class, SmenuOptTpControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String smenuOptTpNmLan;
	
	public String getSmenuOptTpNmLan() {
		smenuOptTpNmLan = smenuOptTpNm;
		
		if (!ObjectUtils.isEmpty(smenuOptTpInfos)) {
			for (int i = 0, len = smenuOptTpInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(smenuOptTpInfos.get(i).getLanTp().getId())) {
					smenuOptTpNmLan = smenuOptTpInfos.get(i).getSmenuOptTpInfoNm();
					break;
				}
			}
		}
		
		return smenuOptTpNmLan;
	}
	
	/** 옵션 선택 구분 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptTpControllerJsonView.class, WebAppControllerJsonView.class })
	private Code optTp;
	
	@Transient
	@JsonView(AppOrderControllerJsonView.class)
	private Integer optTpCd;
	
	public Integer getOptTpCd() {
		return optTp.getCd();
	}
	
	@Transient
	@JsonView(AppOrderControllerJsonView.class)
	private String optTpVal;
	
	public String getOptTpVal() {
		return optTp.getVal();
	}
	
	
	/** 옵션 리스트 */
//	@Transient
//	@JsonView({ WebAppControllerJsonView.class, AppControllerJsonView.class })
//	private List<SmenuOpt> smenuOpts;
	
	/** 옵션 리스트 */
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Map<String, Object>> smenuOpts;
	
}
