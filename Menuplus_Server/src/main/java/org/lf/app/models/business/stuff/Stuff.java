package org.lf.app.models.business.stuff;

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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.stuff.StuffController.StuffControllerCommonJsonView;
import org.lf.app.models.business.stuff.StuffController.StuffControllerJsonView;
import org.lf.app.models.business.stuff.StuffInfo.StuffInfoValid;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 재료 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STUFF")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "stuffInfos", "store" })
@ToString(callSuper = false, exclude = { "defaultLan", "stuffInfos", "store" })
public class Stuff extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4024834719287340155L;

	
	public interface StuffValid {}
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		StuffControllerJsonView.class, StuffControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer stuffCd;
	
	/** 디폴트 언어 */
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ StuffControllerJsonView.class, StuffControllerCommonJsonView.class })
	private Lan defaultLan;
	
	/** 재료 정보 다국어 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_STUFF_X_STUFF_INFO",
			joinColumns=@JoinColumn(name="stuffCd"),
			inverseJoinColumns=@JoinColumn(name="stuffInfoCd"))
	@Valid
	@JsonView(StuffControllerJsonView.class)
	private List<StuffInfo> stuffInfos;
	
	@Column(length = 50)
	@JsonView(StuffControllerJsonView.class)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "원산지,{max}", groups = { StuffValid.class })
	private String stuffNation;
	
	@Transient
	@JsonView({
		StuffControllerJsonView.class, StuffControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String stuffNationCd;
	
	@Transient
	@JsonView({
		StuffControllerJsonView.class, StuffControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String stuffNationNm;
	
	public String getStuffNationNm() {
		stuffNationNm = stuffNation;
		
		for (int i = 0, len = stuffInfos.size(); i < len; i++) {
			if (LocaleContextHolder.getLocale().getLanguage().equals(stuffInfos.get(i).getLanTp().getId())) {
				stuffNationNm = stuffInfos.get(i).getStuffInfoNation();
				break;
			}
		}
		
		return stuffNationNm;
	}
	
	
	/** 재료 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "재료 설명,{max}", groups = { StuffValid.class })
	@JsonView({ StuffControllerJsonView.class })
	private String stuffDesc;
	
	/** 소속 매장 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView(StuffControllerJsonView.class)
	private Store store;
	
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "재료명", groups = { StuffInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "재료명,{max}", groups = { StuffInfoValid.class })
	@JsonView(StuffControllerJsonView.class)
	private String stuffNm;
	
	@Transient
	@JsonView({
		StuffControllerJsonView.class, StuffControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String stuffNmLan;
	
	public String getStuffNmLan() {
		stuffNmLan = stuffNm;
		
		for (int i = 0, len = stuffInfos.size(); i < len; i++) {
			if (LocaleContextHolder.getLocale().getLanguage().equals(stuffInfos.get(i).getLanTp().getId())) {
				stuffNmLan = stuffInfos.get(i).getStuffInfoNm();
				break;
			}
		}
		
		return stuffNmLan;
	}
	
	
	
}
