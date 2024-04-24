package org.lf.app.models.system.action;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.system.action.ActionController.ActionControllerCommonJsonView;
import org.lf.app.models.system.action.ActionController.ActionControllerJsonView;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.resources.Resources;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 이벤트
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_ACTION")
@PrimaryKeyJoinColumn(name="resourcesCd")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Action extends Resources {

	/** serialVersionUID. */
	private static final long serialVersionUID = 83786529525586226L;

	public interface ActionValid {}
	
	/** 코드 */
	@Column(length = 50, unique = true, nullable = false)
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "이벤트코드", groups = { ActionValid.class })
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "이벤트코드,{max}", groups = { ActionValid.class })
	@JsonView({ ActionControllerJsonView.class, ActionControllerCommonJsonView.class })
	private String actionId;

	/** 명 */
	@Column(length = 50, unique = true, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "이벤트명", groups = { ActionValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "이벤트명,{max}", groups = { ActionValid.class })
	@JsonView({ ActionControllerJsonView.class, ActionControllerCommonJsonView.class })
	private String actionNm;

	/** 타입 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ ActionControllerJsonView.class })
	private Code actionTp;
	
	
}
