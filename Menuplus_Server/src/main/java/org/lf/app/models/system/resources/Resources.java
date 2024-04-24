package org.lf.app.models.system.resources;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerCommonJsonView;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerJsonView;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 리소스
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_RESOURCES", indexes={ @Index(name="INDEX_Url_Method", columnList="url,method", unique = true) })
@Inheritance(strategy=InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Resources extends Base implements GrantedAuthority {

	/** serialVersionUID. */
	private static final long serialVersionUID = 6468492126751876113L;

	
	public interface ResourcesValid {}
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView({ ResourcesControllerJsonView.class, ResourcesControllerCommonJsonView.class })
	private Integer resourcesCd;

	/** URL */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주소,{max}", groups = { ResourcesValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "주소", groups = { ResourcesValid.class })
	@JsonView({ ResourcesControllerJsonView.class, ResourcesControllerCommonJsonView.class })
	private String url;
	
	/** URL 호출 타입 */
	@Column(length = 10, nullable = false)
	@Size(max = 10, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "호출 타입,{max}", groups = { ResourcesValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "호출 타입", groups = { ResourcesValid.class })
	@JsonView({ ResourcesControllerJsonView.class, ResourcesControllerCommonJsonView.class })
	private String method;

	/** 타입 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@Valid
	@JsonView({ ResourcesControllerJsonView.class })
	private Code resTp;
	
	/** 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "설명,{max}", groups = { ResourcesValid.class })
	@JsonView({ ResourcesControllerJsonView.class, ResourcesControllerCommonJsonView.class })
	private String resDesc;

	@Override
	@Transient
	public String getAuthority() {
		return this.getUrl() + ";" + this.getMethod();
	}
	
	
}
