package org.lf.app.models.business.stuff;

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
import org.lf.app.models.business.stuff.StuffController.StuffControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 재료 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STUFF_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class StuffInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -1883461104659791551L;

	
	public interface StuffInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ StuffControllerJsonView.class })
	private Integer stuffInfoCd;

	/** 언어 구분 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ StuffControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "재료명", groups = { StuffInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "재료명,{max}", groups = { StuffInfoValid.class })
	@JsonView({ StuffControllerJsonView.class })
	private String stuffInfoNm;
	
	/** 원산지 */
	@Column(length = 50)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "원산지,{max}", groups = { StuffInfoValid.class })
	@JsonView({ StuffControllerJsonView.class })
	private String stuffInfoNation;
	
}
