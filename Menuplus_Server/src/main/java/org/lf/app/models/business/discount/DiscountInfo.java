package org.lf.app.models.business.discount;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.discount.DiscountController.DiscountControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 옵션 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_DISCOUNT_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class DiscountInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6244126773346441610L;

	
	public interface DiscountInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ DiscountControllerJsonView.class })
	private Integer discountInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@Valid
	@JsonView({ DiscountControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "할인명", groups = { DiscountInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "할인명,{max}", groups = { DiscountInfoValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private String discountInfoNm;
	
	/** 할인 금액 */
	@Column
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "할인 금액,{value}", groups = { DiscountInfoValid.class })
	@Max(value = 90000000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "할인 금액,{value}", groups = { DiscountInfoValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private Integer price;
	
	/** 할인율 */
	@Column
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "할인율,{value}", groups = { DiscountInfoValid.class })
	@Max(value = 100, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "할인율,{value}", groups = { DiscountInfoValid.class })
	@JsonView({ DiscountControllerJsonView.class })
	private Integer percente;
	
	
}
