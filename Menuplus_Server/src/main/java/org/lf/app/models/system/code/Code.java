package org.lf.app.models.system.code;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.push.Push.PushValid;
import org.lf.app.models.system.action.Action.ActionValid;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerSubJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerTopJsonView;
import org.lf.app.models.system.dictionary.Dictionary.DictionaryValid;
import org.lf.app.models.system.lanData.LanData.LanDataValid;
import org.lf.app.models.system.noti.Noti.NotiValid;
import org.lf.app.models.system.resources.Resources.ResourcesValid;
import org.lf.app.models.system.terms.Terms.TermsValid;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 공통 코드
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "SYS_CODE", indexes = { @Index(name = "INDEX_top_code_val", columnList = "top_code_cd,val", unique = true)} )
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "topCode", "subCode" })
@ToString(callSuper = false, exclude = { "topCode", "subCode" })
public class Code extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6952178468421356970L;

	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull(message = "{0}을 선택 하여 주십시오." + SPLIT + "타입", groups = { ResourcesValid.class, ActionValid.class, DictionaryValid.class })
	@NotNull(message = "{0}을 선택 하여 주십시오." + SPLIT + "단말 타입", groups = { LanDataValid.class, NotiValid.class })
	@NotNull(message = "{0}을 선택 하여 주십시오." + SPLIT + "약관 타입", groups = { TermsValid.class })
	@NotNull(message = "{0}을 선택 하여 주십시오." + SPLIT + "구분", groups = { PushValid.class })
	@JsonView({
		CodeControllerJsonView.class, CodeControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private Integer cd;

	/** 값 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "코드값,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "코드값")
	@JsonView({
		CodeControllerJsonView.class, CodeControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String val;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "코드명,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "코드명")
	@JsonView({ CodeControllerJsonView.class, CodeControllerCommonJsonView.class })
	private String nm;
	
	@Transient
	@JsonView({
		CodeControllerJsonView.class, CodeControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String nmLan;
	
	/** 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0} {1}은 {2}자 이내로 입력 하여 주십시오." + SPLIT + "코드,설명,{max}")
	@JsonView({ CodeControllerJsonView.class })
	private String codeDesc;
	
	/** 하위 코드 일때 정열 순서 */
	@Column(length = 2, nullable = false)
	@Min(value = 1, message = "{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}")
	@Max(value = 99, message = "{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "순서")
	@JsonView({
		CodeControllerJsonView.class, CodeControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private Integer ord = 1;
	
	/** 예외 메세지1 */
	@Column(length = 50)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "참조값1,{max}")
	@JsonView({ CodeControllerJsonView.class, CodeControllerCommonJsonView.class })
	private String ref1;
	
	/** 예외 메세지2*/
	@Column(length = 50)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "참조값2,{max}")
	@JsonView({ CodeControllerJsonView.class, CodeControllerCommonJsonView.class })
	private String ref2;
	
	/** 예외 메세지3*/
	@Column(length = 50)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "참조값3,{max}")
	@JsonView({ CodeControllerJsonView.class, CodeControllerCommonJsonView.class })
	private String ref3;
	
	/** 상위 코드 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView({ CodeControllerTopJsonView.class })
	private Code topCode;
	
	/** 하위 코드 */
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "topCode")
	@JsonView({ CodeControllerSubJsonView.class })
	@OrderBy("ord")
	private List<Code> subCode;
	
}
