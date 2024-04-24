package org.lf.app.models.business.category;

import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.lf.app.models.Base;
import org.lf.app.models.business.category.CategoryController.CategoryControllerCommonJsonView;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.business.category.CategoryInfo.CategoryInfoValid;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.Smenu.SmenuValid;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.mng.PosController.PosControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 카테고리
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_CATEGORY")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "categoryInfos", "store", "smenus" })
@ToString(callSuper = false, exclude = { "defaultLan", "categoryInfos", "store", "smenus" })
public class Category extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 3425864856648935043L;
	
	
	public interface CategoryValid {}
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		CategoryControllerJsonView.class, CategoryControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class
	})
	private Integer categoryCd;

	/** 디폴트 언어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({
		CategoryControllerJsonView.class, CategoryControllerCommonJsonView.class
	})
	private Lan defaultLan;
	
	/** 메뉴 카테고리 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_CATEGORY_X_CATEGORY_INFO",
			joinColumns=@JoinColumn(name="categoryCd"),
			inverseJoinColumns=@JoinColumn(name="categoryInfoCd"))
	@Valid
	@JsonView({ CategoryControllerJsonView.class })
	private List<CategoryInfo> categoryInfos;
	
	/** 표시 순서 */
	@Column(length=2, nullable=false)
	@Min(value = 1, message="{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}", groups = { CategoryValid.class })
	@Max(value = 99, message="{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}", groups = { CategoryValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { CategoryValid.class })
	@JsonView({
		CategoryControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, PosControllerJsonView.class
	})
	private Integer ord = 1;
	
	/** 메뉴 카테고리 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "카테고리 설명,{max}", groups = { CategoryValid.class })
	@JsonView({ CategoryControllerJsonView.class, WebAppControllerJsonView.class })
	private String categoryDesc;
	
	/** 소속 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ CategoryControllerJsonView.class })
	private Store store;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "카테고리명", groups = { CategoryInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "카테고리명,{max}", groups = { CategoryInfoValid.class })
	@JsonView({ CategoryControllerJsonView.class, PosControllerJsonView.class })
	private String categoryNm;
	
	@Transient
	@JsonView({
		CategoryControllerJsonView.class, CategoryControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class
	})
	private String categoryNmLan;
	
	public String getCategoryNmLan() {
		categoryNmLan = categoryNm;
		
		for (int i = 0, len = categoryInfos.size(); i < len; i++) {
			if (LocaleContextHolder.getLocale().getLanguage().equals(categoryInfos.get(i).getLanTp().getId())) {
				categoryNmLan = categoryInfos.get(i).getCategoryInfoNm();
				break;
			}
		}
		
		return categoryNmLan;
	}
	
//	@JsonView({ WebAppControllerJsonView.class, AppControllerJsonView.class, PosControllerJsonView.class })
//	@ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "categorys")
//	@OrderBy("ord")
//	@Where(clause = "use_yn = 'Y'")
//	public List<Smenu> smenus;
	
	
	/** 메뉴 정보 */
	@OneToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_CATEGORY",
			joinColumns=@JoinColumn(name="categoryCd"),
			inverseJoinColumns=@JoinColumn(name="smenuCd"))
	//@Size(min = 1, message = "{0}는 하나 이상 선택 하여 주십시오." + SPLIT + "카테고리", groups = { SmenuValid.class })
	//@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	@JsonView({ CategoryControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class})
	private List<Smenu> smenus;
	
	
	@Transient
	@JsonView({ CategoryControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Map<String, Object>> smenuList;
	
	
	@Transient
	@JsonView({ CategoryControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	@OrderBy("ord")
	private List<OrdSmenuInCategory> ordSmenuInCategory;
	
}
