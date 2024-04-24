package org.lf.app.models.system.auth;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.system.auth.AuthController.AuthControllerCommonJsonView;
import org.lf.app.models.system.auth.AuthController.AuthControllerJsonView;
import org.lf.app.models.system.resources.Resources;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 권한
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_AUTH")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "resources" })
@ToString(callSuper = false, exclude={ "resources" })
public class Auth extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 5126245359317662010L;

	/** 코드 */
	@Id
	@Column(length = 50)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "권한ID,{max}")
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "권한ID")
	@JsonView({ AuthControllerJsonView.class, AuthControllerCommonJsonView.class })
	private String authId;

	/** 명 */
	@Column(length = 50, unique = true, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "권한명,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "권한명")
	@JsonView({ AuthControllerJsonView.class, AuthControllerCommonJsonView.class })
	private String authNm;

	/** 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0} {1}은 {2}자 이내로 입력 하여 주십시오." + SPLIT + "권한,설명,{max}")
	@JsonView({ AuthControllerJsonView.class })
	private String authDesc;
	
	/** 리소스 집합 */
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinTable(
			name = "SYS_AUTH_X_RESOURCES",
			joinColumns = @JoinColumn(name = "authId"),
			inverseJoinColumns = @JoinColumn(name = "resourcesCd"))
	@JsonView({ AuthControllerJsonView.class })
	@OrderBy("mlevel, ord")
	private Set<Resources> resources;

	
}
