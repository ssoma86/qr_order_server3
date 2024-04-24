package org.lf.app.models.system.terms;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.system.code.Code;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 약관 동의 상세 정보
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_TERMS_ACCEPT")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class TermsAccept extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1743286745694107626L;
	
	public interface TermsAcceptValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer termsAcceptCd;

	/** 폰 고유키 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "UUID,{max}", groups = { TermsAcceptValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "UUID", groups = { TermsAcceptValid.class })
	private String uuid;
	
	/** 약관 타입 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	private Code termsTp;
	
	/** 현지 시간 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	private Date acceptDtm;
	
	
}
