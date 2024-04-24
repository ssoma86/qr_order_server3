package org.lf.app.models.system.terms;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.terms.TermsController.TermsControllerCommonJsonView;
import org.lf.app.models.system.terms.TermsController.TermsControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 약관
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SYS_TERMS", indexes = { @Index(name="INDEX_lan_terms_tp", columnList="lan_id,terms_tp_cd", unique = true) })
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "lan", "termsTp" })
@ToString(callSuper = false, exclude={ "lan", "termsTp" })
public class Terms extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4693481508635458525L;
	
	public interface TermsValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ TermsControllerJsonView.class, TermsControllerCommonJsonView.class })
	private Integer termsCd;

	/** 언어 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(nullable = false)
	@Valid
	@JsonView({ TermsControllerJsonView.class })
	private Lan lan;
	
	/** 약관 타입 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ TermsControllerJsonView.class })
	private Code termsTp;
	
	/** 타이틀 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "제목,{max}", groups = { TermsValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "제목", groups = { TermsValid.class })
	@JsonView({ TermsControllerJsonView.class, TermsControllerCommonJsonView.class })
	private String termsTitle;
	
	/** 내용 */
	@Column(columnDefinition="LONGTEXT", nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "내용", groups = { TermsValid.class })
	@JsonView({ TermsControllerJsonView.class, TermsControllerCommonJsonView.class })
	private String content;
	
	
}
