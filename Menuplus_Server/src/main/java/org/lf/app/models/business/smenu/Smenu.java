package org.lf.app.models.business.smenu;

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
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.Category.CategoryValid;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.business.smenu.SmenuInfo.SmenuInfoValid;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.stuff.Stuff;
import org.lf.app.models.business.stuff.StuffController.StuffControllerCommonJsonView;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.mng.PosController.PosControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * 메뉴정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_SMENU")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "smenuInfos", "category", "discounts", "discountPrice", "smenuOptTps" })
@ToString(callSuper = false, exclude = { "defaultLan", "smenuInfos", "category", "discounts", "discountPrice", "smenuOptTps" })
public class Smenu extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -7417073520094852475L;

	
	
	public interface SmenuValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class,CategoryControllerJsonView.class })
	private Integer smenuCd;

	/** 디폴트 언어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class })
	private Lan defaultLan;
	
	/** 메뉴 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_SMENU_X_SMENU_INFO",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="smenuInfoCd"))
	@Valid
	@JsonView({ SmenuControllerJsonView.class })
	private List<SmenuInfo> smenuInfos;
	
	/** 표시 순서 */
	@Column(length=2, nullable=false)
	@Min(value = 1, message="{0}는 {1}보다 크거나 같아야 합니다." + SPLIT + "순서,{value}", groups = { SmenuValid.class })
	@Max(value = 99, message="{0}는 {1}보다 작거나 같아야 합니다." + SPLIT + "순서,{value}", groups = { SmenuValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { SmenuValid.class })
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private Integer ord = 1;
	
	/** 메뉴정보 이미지 */
	@Column(length = 100)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImg = "";
	
	@Transient
	@NotBlank(message="{0}는 필수 선택 항목입니다." + Base.SPLIT + "메뉴 이미지", groups = { SmenuValid.class })
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImgFile;
	
	
	/** 메뉴정보 이미지1 */
	@Column(length = 100)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImg1 = "";
	
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImgFile1;
	
	
	/** 메뉴정보 이미지2 */
	@Column(length = 100)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImg2 = "";
	
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String smenuImgFile2;
	
	
	/** 메뉴정보 이미지 리스트 */
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	private String[] smenuImgList;
	

	/** 메뉴 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "메뉴 설명,{max}", groups = { SmenuValid.class })
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class })
	private String smenuDesc;
	
	/** 소속 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_STORE",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="storeCd"))
	@JsonView({ SmenuControllerJsonView.class })
	private Store store;
	
	/** 카테고리 정보 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_CATEGORY",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="categoryCd"))
//	@NotNull(message = "{0}는 필수 입력 항목입니다." + Base.SPLIT + "카테고리", groups = { SmenuValid.class })
//	@Size(min = 1, message = "{0}는 하나 이상 선택 하여 주십시오." + SPLIT + "카테고리", groups = { SmenuValid.class })
//	@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	@JsonView({ SmenuControllerJsonView.class })
	private Category category;
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class })
	private String categoryNm;
	
	public String getCategoryNm() {
		
		String tempCategoryNm = "";
		
		if(category!=null && category.getCategoryNmLan()!=null && category.getCategoryNmLan().length() > 0) {
			tempCategoryNm = category.getCategoryNmLan();
		}
		
		return tempCategoryNm;
	}
	
	@Transient
//	@NotNull(message = "{0}는 필수 입력 항목입니다." + Base.SPLIT + "카테고리", groups = { SmenuValid.class })
	@Min(value = 0, message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "카테고리", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class })
	private int categoryCd;
	
	/** 재료 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_STUFF",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="stuffCd"))
	@OrderBy("stuffNm")
	@Where(clause = "use_yn = 'Y'")
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Stuff> stuffs;
	
	/** 메뉴 옵션 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_SMENU_OPT",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="smenuOptCd"))
	@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	@JsonView({ SmenuControllerJsonView.class, PosControllerJsonView.class, WebAppControllerJsonView.class })
	private List<SmenuOpt> smenuOpts;
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class })
	private int selectSmenuOptCd;
	
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "메뉴명", groups = { SmenuInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "메뉴명,{max}", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, PosControllerJsonView.class })
	private String smenuNm;
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class})
	private String smenuNmLan;
	
	public String getSmenuNmLan() {
		smenuNmLan = smenuNm;
		
		for (int i = 0, len = smenuInfos.size(); i < len; i++) {
			if (LocaleContextHolder.getLocale().getLanguage().equals(smenuInfos.get(i).getLanTp().getId())) {
				smenuNmLan = smenuInfos.get(i).getSmenuInfoNm();
				break;
			}
		}
		
		return smenuNmLan;
	}
	
	
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 커야 합니다." + Base.SPLIT + "원가,{value}", groups = { SmenuInfoValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작아야 합니다." + Base.SPLIT + "원가,{value}", groups = { SmenuInfoValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "원가", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class })
	private Integer cost;
	
	@Column(nullable = false)
	@Min(value = 0, message="{0}는 {1}보다 커야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuInfoValid.class })
	@Max(value = 90000000, message="{0}는 {1}보다 작아야 합니다." + Base.SPLIT + "단가,{value}", groups = { SmenuInfoValid.class })
	@NotNull(message="{0}는 필수 입력 항목입니다." + Base.SPLIT + "단가", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class, PosControllerJsonView.class })
	private Integer price = 0;
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer priceLan;
	
	public Integer getPriceLan() {
		priceLan = price;
		
		// 국제 화페 사용 시 해당 금액 설정
		if (store.isInternationalPayYn()) {
			for (int i = 0, len = smenuInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(smenuInfos.get(i).getLanTp().getId())) {
					priceLan = smenuInfos.get(i).getPrice();
					break;
				}
			}
		}
		
		return priceLan;
	}
	
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class })
	private int cnt;
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String unit;
	
	public String getUnit() {
		return store.getUnit();
	}
	
//	@Transient
//	@JsonView({ SmenuControllerJsonView.class })
//	private String categorysNm;
//	
//	public String getCategorysNm() {
//		for (int i = 0, len = categorys.size(); i < len; i++) {
//			if (i == 0) {
//				categorysNm = categorys.get(i).getCategoryNmLan();
//			} else {
//				categorysNm += ", " + categorys.get(i).getCategoryNmLan();
//			}
//		}
//		
//		return categorysNm;
//	}
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class })
	private String stuffsNm;
	
	public String getStuffsNm() {
		for (int i = 0, len = stuffs.size(); i < len; i++) {
			if (i == 0) {
				stuffsNm = stuffs.get(i).getStuffNmLan();
			} else {
				stuffsNm += ", " + stuffs.get(i).getStuffNmLan();
			}
		}
		
		return stuffsNm;
	}
	
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class })
	private String smenuOptsNm;
	
	public String getSmenuOptsNm() {
		for (int i = 0, len = smenuOpts.size(); i < len; i++) {
			if (i == 0) {
				smenuOptsNm = smenuOpts.get(i).getSmenuOptNmLan();
			} else {
				smenuOptsNm += ", " + smenuOpts.get(i).getSmenuOptNmLan();
			}
		}
		
		return smenuOptsNm;
	}
	
	
	/** 메뉴 옵션 그룹 */
//	@Transient
//	@JsonView({ WebAppControllerJsonView.class, AppControllerJsonView.class })
//	private List<SmenuOptTp> smenuOptTps;
	
	/** 메뉴 옵션 그룹 */
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Map<String, Object>> smenuOptTps;
	
	
	@Column(nullable = false)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer calorie = 0;
	
	/** 판매 정지 */
	@Column(nullable = false)
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private boolean stopSelling = false;
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String stopReasonNm;
	
	
	/** 판매 정지 사유 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ SmenuControllerJsonView.class })
	private Code stopReason;
	
	/** 기타 판매 정지 사유 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "판매 정지 사유,{max}", groups = { SmenuInfoValid.class })
	@JsonView({ SmenuControllerJsonView.class })
	private String otherReason = "";
	
	
	/** 할인 설정 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_SMENU_X_DISCOUNT",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="discountCd"))
	@JsonView({ SmenuControllerJsonView.class, WebAppControllerJsonView.class })
	@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	private List<Discount> discounts;
	
	@Transient
	@JsonView({ SmenuControllerJsonView.class })
	private String discountsNm;
	
	public String getDiscountsNm() {
		for (int i = 0, len = discounts.size(); i < len; i++) {
			if (i == 0) {
				discountsNm = discounts.get(i).getDiscountNmLan();
			} else {
				discountsNm += ", " + discounts.get(i).getDiscountNmLan();
			}
		}
		
		return discountsNm;
	}
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Map<String, Object>> discountPrice;
	
}
