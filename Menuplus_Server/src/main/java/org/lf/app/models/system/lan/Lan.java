package org.lf.app.models.system.lan;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.category.Category.CategoryValid;
import org.lf.app.models.business.store.Store.StoreValid;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lan.LanController.LanControllerJsonView;
import org.lf.app.models.system.lanData.LanData.LanDataValid;
import org.lf.app.models.system.lanData.LanDataController.LanDataControllerCommonJsonView;
import org.lf.app.models.system.noti.Noti.NotiValid;
import org.lf.app.models.system.terms.Terms.TermsValid;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 언어 설정
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "SYS_LAN")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Lan extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -2081851548499100714L;

	/** 값 */
	@Id
	@Column(length = 10, nullable = false)
	@Size(max = 10, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "언어 코드,{max}")
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "언어 코드")
	@NotBlank(message = "{0}를 선택 하여 주십시오." + SPLIT + "언어", groups = { LanDataValid.class, NotiValid.class, TermsValid.class })
	@NotBlank(message = "{0}를 선택 하여 주십시오." + SPLIT + "디폴트 언어", groups = { StoreValid.class, CategoryValid.class })
	@JsonView({
		LanControllerJsonView.class, LanControllerCommonJsonView.class,
		WebAppControllerJsonView.class, LanDataControllerCommonJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String id;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "언어명,{max}")
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "언어명")
	@JsonView({
		LanControllerJsonView.class, LanControllerCommonJsonView.class,
		WebAppControllerJsonView.class, LanDataControllerCommonJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String nm;
	
	/** 정열 순서 */
	@Column(length = 2, nullable = false)
	@Min(value = 1, message = "{0}는 {1}부터 시작 합니다." + SPLIT + "순서,{value}")
	@Max(value = 99, message = "{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "순서")
	@JsonView({
		LanControllerJsonView.class, LanControllerCommonJsonView.class,
		WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private Integer ord = 1;
	
	/** 참조값1 */
	@Column(length = 50)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "참조값1,{max}")
	@JsonView({
		LanControllerJsonView.class, LanControllerCommonJsonView.class
	})
	private String ref1;
	
	/** 참조값2 */
	@Column(length = 50)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "참조값2,{max}")
	@JsonView({
		LanControllerJsonView.class, LanControllerCommonJsonView.class
	})
	private String ref2;
	
	/** 시스템 디폴트 언어 */
	@Column(nullable = false)
	@JsonView({ LanControllerJsonView.class, LanControllerCommonJsonView.class })
	private boolean default_ = false;
	
	
}
