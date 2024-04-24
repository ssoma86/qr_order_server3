package org.lf.app.models.business.smenu;

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
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_SMENU_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class SmenuInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 8445993360596944402L;

	
	public interface SmenuInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ SmenuControllerJsonView.class })
	private Integer smenuInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "메뉴명", groups = { SmenuInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "메뉴명,{max}", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class })
	private String smenuInfoNm;
	
	/** 단가 */
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 커야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuInfoValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuInfoValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "단가", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class })
	private Integer price = 0;
	
	
}
