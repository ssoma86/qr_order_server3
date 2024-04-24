package org.lf.app.models.business.smenuOpt;

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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.smenuOpt.SmenuOptController.SmenuOptControllerJsonView;
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
@Table(name="TBL_SMENU_OPT_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class SmenuOptInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 998812370828279928L;

	
	public interface SmenuOptInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ SmenuOptControllerJsonView.class })
	private Integer smenuOptInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "옵션명", groups = { SmenuOptInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "옵션명,{max}", groups = { SmenuOptInfoValid.class })
	@JsonView({ SmenuOptControllerJsonView.class })
	private String smenuOptInfoNm;
	
	/** 단가 */
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuOptInfoValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuOptInfoValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "단가", groups = { SmenuOptInfoValid.class })
	@JsonView({ SmenuOptControllerJsonView.class })
	private Integer price;
	
	
	
}
