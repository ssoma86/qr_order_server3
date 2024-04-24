package org.lf.app.models.business.discount;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
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
import org.lf.app.models.business.discount.DiscountController.DiscountControllerCommonJsonView;
import org.lf.app.models.business.discount.DiscountController.DiscountControllerJsonView;
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
 * 할인 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_DISCOUNT")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "discountInfos", "discountTarget", "store", "discountTp" })
@ToString(callSuper = false, exclude = { "defaultLan", "discountInfos", "discountTarget", "store", "discountTp" })
public class Discount extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -2749778867472117819L;

	
	
	public interface DiscountValid {}
	public interface DiscountTargetValid {}
	public interface DiscountTpValid {}
	
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		DiscountControllerJsonView.class, DiscountControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer discountCd;

	/** 디폴트 언어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ DiscountControllerJsonView.class })
	private Lan defaultLan;
	
	/** 할인 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_DISCOUNT_X_DISCOUNT_INFO",
			joinColumns=@JoinColumn(name="discountCd"),
			inverseJoinColumns=@JoinColumn(name="discountInfoCd"))
	@Valid
	@JsonView({ DiscountControllerJsonView.class })
	private List<DiscountInfo> discountInfos;
	
	/** 할인 대상 구분 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ DiscountControllerJsonView.class })
	private Code discountTarget;
	
	/** 판매 방식 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_DISCOUNT_X_SALES_CODE",
			joinColumns=@JoinColumn(name="discountCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@Size(min = 1, message = "{0}은 하나 이상 선택 하여 주십시오." + SPLIT + "판매 방식", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	@OrderBy("ord")
	private List<Code> salesTps;
	
	@Transient
	@JsonView({ DiscountControllerJsonView.class, WebAppControllerJsonView.class })
	private String salesTpNms = "";
	
	/** 표시 순서 */
	@Column(length=2, nullable=false)
	@Min(value = 1, message="{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}", groups = { DiscountValid.class })
	@Max(value = 99, message="{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}", groups = { DiscountValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer ord = 1;
	
	/** 할인 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "할인 설명,{max}", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private String discountDesc;
	
	/** 소속 매장 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ DiscountControllerJsonView.class })
	private Store store;
	
	/** 할인명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "할인명", groups = { DiscountValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "할인명,{max}", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private String discountNm;
	
	@Transient
	@JsonView({
		DiscountControllerJsonView.class, DiscountControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String discountNmLan;
	
	public String getDiscountNmLan() {
		discountNmLan = discountNm;
		
		if (!ObjectUtils.isEmpty(discountInfos)) {
			for (int i = 0, len = discountInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(discountInfos.get(i).getLanTp().getId())) {
					discountNmLan = discountInfos.get(i).getDiscountInfoNm();
					break;
				}
			}
		}
		
		return discountNmLan;
	}
	
	/** 할인 유형 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({
		DiscountControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private Code discountTp;
	
	/** 할인 금액 */
	@Column(nullable = false)
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "할인 금액,{value}", groups = { DiscountValid.class })
	@Max(value = 90000000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "할인 금액,{value}", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private Integer price;
	
	@Transient
	@JsonView({
		DiscountControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private Integer priceLan;
	
	public Integer getPriceLan() {
		priceLan = price;
		
		// 국제 화페 사용 시 해당 금액 설정
		if (store.isInternationalPayYn()) {
			if (!ObjectUtils.isEmpty(discountInfos)) {
				for (int i = 0, len = discountInfos.size(); i < len; i++) {
					if (LocaleContextHolder.getLocale().getLanguage().equals(discountInfos.get(i).getLanTp().getId())) {
						priceLan = discountInfos.get(i).getPrice();
						break;
					}
				}
			}
		}
		
		return priceLan;
	}
	
	/** 할인율 */
	@Column(nullable = false)
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "할인율,{value}", groups = { DiscountValid.class })
	@Max(value = 100, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "할인율,{value}", groups = { DiscountValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private Integer percente;
	
	@Transient
	@JsonView({
		DiscountControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private Integer percenteLan;
	
	public Integer getPercenteLan() {
		percenteLan = percente;
		
		// 국제 화페 사용 시 해당 금액 설정
		if (store.isInternationalPayYn()) {
			if (!ObjectUtils.isEmpty(discountInfos)) {
				for (int i = 0, len = discountInfos.size(); i < len; i++) {
					if (LocaleContextHolder.getLocale().getLanguage().equals(discountInfos.get(i).getLanTp().getId())) {
						percenteLan = discountInfos.get(i).getPercente();
						break;
					}
				}
			}
		}
		
		return percenteLan;
	}
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String unit;
	
	public String getUnit() {
		return store.getUnit();
	}
	
}
