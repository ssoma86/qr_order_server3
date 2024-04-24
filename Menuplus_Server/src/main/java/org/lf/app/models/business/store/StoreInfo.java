package org.lf.app.models.business.store;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 매장 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STORE_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class StoreInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 6893885507832373428L;

	
	
	public interface StoreInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ StoreControllerJsonView.class })
	private Integer storeInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ StoreControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "매장명", groups = { StoreInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장명,{max}", groups = { StoreInfoValid.class })
	@JsonView({ StoreControllerJsonView.class })
	private String storeInfoNm;
	
	/** 주소 */
	@Column(length = 250, nullable = false)
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + Base.SPLIT + "매장 주소", groups = { StoreInfoValid.class })
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장 주소,{max}", groups = { StoreInfoValid.class })
	@JsonView({ StoreControllerJsonView.class })
	private String storeInfoAddr;
	
	/** 매장 설명 */
	@Column(length = 250)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "매장 설명", groups = { StoreInfoValid.class })
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장 설명,{max}", groups = { StoreInfoValid.class })
	@JsonView({ StoreControllerJsonView.class })
	private String storeInfoDesc;
	
}
