package org.lf.app.models.business.inquiry;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.business.inquiry.InquiryController.InquiryControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 1:1 문의
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_INQUIRY")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Inquiry extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 1114112528103821613L;

	public interface InquiryValid {}
	public interface InquiryAnswerdValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ InquiryControllerJsonView.class })
	private Integer inquiryCd;

	/** 제목 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "제목,{max}", groups = { InquiryValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "제목", groups = { InquiryValid.class })
	@JsonView({ InquiryControllerJsonView.class })
	private String title;
	
	/** 내용 */
	@Column(columnDefinition="TEXT", nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "내용", groups = { InquiryValid.class })
	@JsonView({ InquiryControllerJsonView.class })
	private String content;
	
	/** 질문 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@JsonView({ InquiryControllerJsonView.class })
	private Date sendDtm;
	
	/** 답변 내용 */
	@Column(columnDefinition="TEXT")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "답변 내용", groups = { InquiryAnswerdValid.class })
	@JsonView({ InquiryControllerJsonView.class })
	private String answer;
	
	/** 답변 여부 */
	@Column(nullable = false)
	@JsonView({ InquiryControllerJsonView.class })
	private Boolean answered = false;
	
	
}
