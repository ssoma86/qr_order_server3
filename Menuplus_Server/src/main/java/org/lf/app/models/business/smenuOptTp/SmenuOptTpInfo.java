package org.lf.app.models.business.smenuOptTp;

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
import org.lf.app.models.business.smenuOptTp.SmenuOptTpController.SmenuOptTpControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 옵션 그룹 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_SMENU_OPT_TP_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class SmenuOptTpInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1997943873239571580L;

	
	public interface SmenuOptTpInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private Integer smenuOptTpInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "옵션 그룹명", groups = { SmenuOptTpInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "옵션 그룹명,{max}", groups = { SmenuOptTpInfoValid.class })
	@JsonView({ SmenuOptTpControllerJsonView.class })
	private String smenuOptTpInfoNm;
	
	
}
