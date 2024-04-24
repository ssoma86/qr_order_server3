package org.lf.app.models.system.option;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.system.option.OptionController.OptionControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 시스템 옵션
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_OPTION")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Option extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -1191037168012178115L;

	/** 코드 */
	@Id
	@Column(length = 50, unique = true, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "옵션ID,{max}")
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "옵션ID")
	@JsonView(OptionControllerJsonView.class)
	private String optionId;

	/** 명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "옵션명,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "옵션명")
	@JsonView(OptionControllerJsonView.class)
	private String optionNm;
	
	/** 값 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "옵션값,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "옵션값")
	@JsonView(OptionControllerJsonView.class)
	private String optionVal;
	
	/** 참조 값1 */
	@Column(length = 50)
	@Size(max = 50, message = "{0} {1}은 {2}자 이내로 입력 하여 주십시오." + SPLIT + "옵션,참조값1,{max}")
	@JsonView(OptionControllerJsonView.class)
	private String optionRef1;
	
	/** 참조 값2 */
	@Column(length = 50)
	@Size(max = 50, message = "{0} {1}는 {2}자 이내로 입력 하여 주십시오." + SPLIT + "옵션,참조값2,{max}")
	@JsonView(OptionControllerJsonView.class)
	private String optionRef2;

	/** 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0} {1}은 {2}자 이내로 입력 하여 주십시오." + SPLIT + "옵션,설명,{max}")
	@JsonView(OptionControllerJsonView.class)
	private String optionDesc;

	
}
