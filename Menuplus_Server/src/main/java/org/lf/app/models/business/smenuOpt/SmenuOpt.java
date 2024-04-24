package org.lf.app.models.business.smenuOpt;

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
import org.lf.app.models.business.smenuOpt.SmenuOptController.SmenuOptControllerCommonJsonView;
import org.lf.app.models.business.smenuOpt.SmenuOptController.SmenuOptControllerJsonView;
import org.lf.app.models.business.smenuOptTp.SmenuOptTp;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.mng.PosController.PosControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 옵션 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_SMENU_OPT")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "smenuOptInfos", "smenuOptTp", "store" })
@ToString(callSuper = false, exclude = { "defaultLan", "smenuOptInfos", "smenuOptTp", "store" })
public class SmenuOpt extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6873619818894036448L;
	
	public interface SmenuOptValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		SmenuOptControllerJsonView.class, SmenuOptControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer smenuOptCd;

	/** 디폴트 언어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptControllerJsonView.class, SmenuOptControllerCommonJsonView.class })
	private Lan defaultLan;
	
	/** 메뉴 옵션 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_SMENU_OPT_X_SMENU_OPT_INFO",
			joinColumns=@JoinColumn(name="smenuOptCd"),
			inverseJoinColumns=@JoinColumn(name="smenuOptInfoCd"))
	@Valid
	@JsonView({ SmenuOptControllerJsonView.class })
	private List<SmenuOptInfo> smenuOptInfos;
	
	/** 옵션 그룹 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptControllerJsonView.class })
	private SmenuOptTp smenuOptTp;
	
	/** 표시 순서 */
	@Column(length=2, nullable=false)
	@Min(value = 1, message="{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}", groups = { SmenuOptValid.class })
	@Max(value = 99, message="{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}", groups = { SmenuOptValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { SmenuOptValid.class })
	@JsonView({
		SmenuOptControllerJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer ord = 1;
	
	/** 메뉴 옵션 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "옵션 설명,{max}", groups = { SmenuOptValid.class })
	@JsonView({ SmenuOptControllerJsonView.class })
	private String smenuOptDesc;
	
	/** 소속 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ SmenuOptControllerJsonView.class })
	private Store store;
	
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "옵션명", groups = { SmenuOptValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "옵션명,{max}", groups = { SmenuOptValid.class })
	@JsonView({ SmenuOptControllerJsonView.class, PosControllerJsonView.class })
	private String smenuOptNm;
	
	@Transient
	@JsonView({
		SmenuOptControllerJsonView.class, SmenuOptControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String smenuOptNmLan;
	
	public String getSmenuOptNmLan() {
		smenuOptNmLan = smenuOptNm;
		
		if (!ObjectUtils.isEmpty(smenuOptInfos)) {
			for (int i = 0, len = smenuOptInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(smenuOptInfos.get(i).getLanTp().getId())) {
					smenuOptNmLan = smenuOptInfos.get(i).getSmenuOptInfoNm();
					break;
				}
			}
		}
		
		return smenuOptNmLan;
	}
	
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 커야 합니다." + Base.SPLIT + "원가,{value}", groups = { SmenuOptValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작아야 합니다." + Base.SPLIT + "원가,{value}", groups = { SmenuOptValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "원가", groups = { SmenuOptValid.class })
	@JsonView({ SmenuOptControllerJsonView.class })
	private Integer cost;
	
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuOptValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuOptValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "단가", groups = { SmenuOptValid.class })
	@JsonView({ SmenuOptControllerJsonView.class, PosControllerJsonView.class })
	private Integer price;
	
	@Transient
	@JsonView({ SmenuOptControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer priceLan;
	
	public Integer getPriceLan() {
		priceLan = price;
		
		// 국제 화페 사용 시 해당 금액 설정
		if (store.isInternationalPayYn()) {
			if (!ObjectUtils.isEmpty(smenuOptInfos)) {
				for (int i = 0, len = smenuOptInfos.size(); i < len; i++) {
					if (LocaleContextHolder.getLocale().getLanguage().equals(smenuOptInfos.get(i).getLanTp().getId())) {
						priceLan = smenuOptInfos.get(i).getPrice();
						break;
					}
				}
			}
		}
		
		return priceLan;
	}
	
	
}
