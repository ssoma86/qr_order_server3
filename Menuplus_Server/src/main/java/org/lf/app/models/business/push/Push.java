package org.lf.app.models.business.push;

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
import org.lf.app.models.business.push.PushController.PushControllerJsonView;
import org.lf.app.models.system.code.Code;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 푸시
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_PUSH")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Push extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -9006433019238398222L;

	public interface PushValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ PushControllerJsonView.class })
	private Integer pushCd;

	/** 타이틀 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "제목,{max}", groups = { PushValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "제목", groups = { PushValid.class })
	@JsonView({ PushControllerJsonView.class })
	private String title;
	
	/** 내용 */
	@Column(columnDefinition="TEXT", nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "내용", groups = { PushValid.class })
	@JsonView({ PushControllerJsonView.class })
	private String content;
	
	/** 발송 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@JsonView({ PushControllerJsonView.class })
	private Date sendDtm;
	
	/** 구분 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ PushControllerJsonView.class })
	private Code pushTp;
	
	
}
