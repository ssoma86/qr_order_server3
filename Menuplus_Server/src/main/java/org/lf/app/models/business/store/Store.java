package org.lf.app.models.business.store;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.lf.app.models.Base;
import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.cust.Cust;
import org.lf.app.models.business.delivery.Delivery;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerCommonJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.stuff.Stuff;
import org.lf.app.models.business.user.User.UserValid;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.tools.fileMan.FileMan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForFavourite;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.mng.PosController.PosControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 매장
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STORE")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "defaultLan", "storeInfos",
		"storeTimes", "payTps", "lans", "cust", "files", "categorys", "stuffs",
		"webSalesTps", "appSalesTps", "discounts", "discountPrice" })
@ToString(callSuper = false, exclude = { "defaultLan", "storeInfos",
		"storeTimes", "payTps", "lans", "cust", "files", "categorys", "stuffs",
		"webSalesTps", "appSalesTps", "discounts", "discountPrice" })
public class Store extends Base { 

	/** serialVersionUID. */
	private static final long serialVersionUID = 4699434046191623020L;

	
	
	public interface StoreValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "매장", groups = { UserValid.class })
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
		OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private Integer storeCd;

	/** ID */
	@Column(length = 50, nullable = false)
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class, PosControllerJsonView.class
	})
	private String storeId;
	
	/** 매장 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_STORE_X_STORE_INFO",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="storeInfoCd"))
	@Valid
	@JsonView(StoreControllerJsonView.class)
	private List<StoreInfo> storeInfos;
	
	/** 영업시간 설정 타입 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean detailYn = false;
	
	/** 영업시간 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_STORE_X_STORE_TIMES",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="storeTimeCd"))
	@Valid
	@JsonView(StoreControllerJsonView.class)
	private List<StoreTime> storeTimes;
	
	
	/** 결제 구분 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_STORE_X_PAY_TP",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@Size(min = 1, message = "{0}은 하나 이상 선택 하여 주십시오." + SPLIT + "결제 구분", groups = { StoreValid.class })
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	@Where(clause = "use_yn = 'Y'")
	private List<Code> payTps;
	
	/** 매장 메인 이미지 */
	@Column(length = 100)
	@JsonView({ StoreControllerJsonView.class, AppControllerJsonViewForFavourite.class })
	private String storeImg;
	
	@Transient
	@NotBlank(message="{0}는 필수 선택 항목입니다." + Base.SPLIT + "매장 메인 이미지", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile;
	
	/** 매장 이미지 */
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String[] storeImgList;
	
	/** 사업자 등록 번호 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "사업자 등록 번호,{max}", groups = { StoreValid.class })
	//@JsonView(StoreControllerJsonView.class)
	@JsonView({StoreControllerJsonView.class, WebAppControllerJsonView.class})
	private String busiNum;
	
	/** 대표자명 */
	@Column(length = 20, nullable = false)
	@Size(max = 20, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "대표자명,{max}", groups = { StoreValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "대표자명", groups = { StoreValid.class })
	@JsonView({StoreControllerJsonView.class, WebAppControllerJsonView.class})
	private String ceoNm;
	
	/** 전화번호 */
	@Column(length = 20, nullable = false)
	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "전화번호", groups = { StoreValid.class })
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "전화번호,{max}", groups = { StoreValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "전화번호", groups = { StoreValid.class })
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private String tel;
	
	/** 팩스번호 */
	@Column(length = 20)
	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "팩스 번호", groups = { StoreValid.class })
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "팩스 번호,{max}", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String fax;
	
	/** 우편 번호 */
	@Column(length = 10, nullable = false)
	@Size(max = 10, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "우편 번호,{max}", groups = { StoreValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "우편 번호", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String zipCd;
	
	/** 주소 */
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주소,{max}", groups = { StoreValid.class })
	//@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "주소", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String addr;
	
	/** 상세 주소 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "상세 주소,{max}", groups = { StoreValid.class })
	//@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "상세 주소", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String addrDtl;

	/** 최소주문금액 */
	@Column
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "최소주문금액,{value}", groups = { StoreValid.class })
	@Max(value = 100000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "최소주문금액,{value}", groups = { StoreValid.class })
	@JsonView({
			StoreControllerJsonView.class, WebAppControllerJsonView.class,
			AppOrderControllerJsonView.class, AppControllerJsonViewForFavourite.class
	})
	private Integer minCost = 0;
	
	/** 매장 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "매장 설명,{max}", groups = { StoreValid.class })
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private String storeDesc;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private String storeDescLan;
	
	public String getStoreDescLan() {
		storeDescLan = storeDesc;
		
		if (!ObjectUtils.isEmpty(storeInfos)) {
			for (int i = 0, len = storeInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(storeInfos.get(i).getLanTp().getId())) {
					storeDescLan = storeInfos.get(i).getStoreInfoDesc();
					break;
				}
			}
		}
		
		return storeDescLan;
	}
	
	/** 디폴트 언어 */
	@OneToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private Lan defaultLan;
	
	/** 추가 언어 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@OrderBy("ord")
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	@Where(clause = "use_yn = 'Y'")
	private List<Lan> lans;

	/** 소속 사업장 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_CUST_X_STORE",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="custCd"))
	@JsonView(StoreControllerJsonView.class)
	private Cust cust;
	
	/** 펌부 파일 */
	@OneToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_STORE_X_FILE_MAN",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="seq"))
	@JsonView(StoreControllerJsonView.class)
	@Where(clause = "use_yn = 'Y'")
	private List<FileMan> files;
	
	
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "매장명", groups = { StoreValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장명,{max}", groups = { StoreValid.class })
	@JsonView({ StoreControllerJsonView.class, PosControllerJsonView.class })
	private String storeNm;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private String storeNmLan;
	
	public String getStoreNmLan() {
		storeNmLan = storeNm;
		
		if (!ObjectUtils.isEmpty(storeInfos)) {
			for (int i = 0, len = storeInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(storeInfos.get(i).getLanTp().getId())) {
					storeNmLan = storeInfos.get(i).getStoreInfoNm();
					break;
				}
			}
		}
		
		return storeNmLan;
	}
	
	@Column(length = 250, nullable = false)
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + Base.SPLIT + "매장 주소", groups = { StoreValid.class })
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장 주소,{max}", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String storeAddr;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private String storeAddrLan;
	
	public String getStoreAddrLan() {
		storeAddrLan = storeAddr;
		
		if (!ObjectUtils.isEmpty(storeInfos)) {
			for (int i = 0, len = storeInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(storeInfos.get(i).getLanTp().getId())) {
					storeAddrLan = storeInfos.get(i).getStoreInfoAddr();
					break;
				}
			}
		}
		
		return storeAddrLan;
	}
	
	@Column(length = 250, nullable = false)
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + Base.SPLIT + "상세매장 주소", groups = { StoreValid.class })
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "상세매장 주소,{max}", groups = { StoreValid.class })
	@JsonView({StoreControllerJsonView.class, WebAppControllerJsonView.class})
	private String storeAddrDtl;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String lanNms;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private String openTm;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private String breakTm;
	
	@Transient
	@JsonView({
		WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private List<String> breakTmList;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String holiday;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class
	})
	private String vactionDay;
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private boolean open;
	
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class, PosControllerJsonView.class })
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "store")
	@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	public List<Category> categorys;
	
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	@OneToMany(cascade = CascadeType.REFRESH, mappedBy = "store")
	@Where(clause = "use_yn = 'Y'")
	public List<Stuff> stuffs;
	
	
	/** 가입일 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "가입일 일시", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private Date startDt;
	
	/** 탈퇴일 일자 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date endDt;
	
	/** 주문 통지 사용여부 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean orderNoti = false;
	
	/** 자동 주문 시간 타임, 분단위 */
	@Column
	@JsonView(StoreControllerJsonView.class)
	private Integer notiTime = 0;
	
	/** 웹에서 판매 방식 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_STORE_X_WEB_SALES_CODE",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@Size(min = 1, message = "{0}을 하나 이상 선택 하여 주십시오." + SPLIT + "웹 판매 방식", groups = { StoreValid.class })
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class })
	@Where(clause = "use_yn = 'Y'")
	private List<Code> webSalesTps;
	
	/** 앱에서 판매 방식 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_STORE_X_APP_SALES_CODE",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@Size(min = 1, message = "{0}을 하나 이상 선택 하여 주십시오." + SPLIT + "앱 판매 방식", groups = { StoreValid.class })
	@JsonView({ StoreControllerJsonView.class, AppOrderControllerJsonView.class })
	@Where(clause = "use_yn = 'Y'")
	private List<Code> appSalesTps;
	
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class })
	private String webSalesTpNms = "";
	
	@Transient
	@JsonView({
		StoreControllerJsonView.class, AppOrderControllerJsonView.class,
		AppControllerJsonViewForStoreList.class, AppControllerJsonViewForFavourite.class
	})
	private String appSalesTpNms = "";
	
	/** 필터 옵션 구분 */
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_STORE_X_FILTER_TP",
			joinColumns=@JoinColumn(name="storeCd"),
			inverseJoinColumns=@JoinColumn(name="cd"))
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class })
	@Where(clause = "use_yn = 'Y'")
	private List<Code> filterTps;
	
	/** 주방 프린트 사용여부 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean printYn = false;
	
	/** 할인 설정 */
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	@OrderBy("ord")
	@Where(clause = "use_yn = 'Y'")
	private List<Discount> discounts;

	/** 배달 정보 */
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	@Where(clause = "use_yn = 'Y'")
	private List<Delivery> delivery;

	/** 배달비용 */
	@Column
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { StoreValid.class })
	@Max(value = 100000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { StoreValid.class })
	@JsonView({
			StoreControllerJsonView.class, WebAppControllerJsonView.class,
			AppOrderControllerJsonView.class, AppControllerJsonViewForFavourite.class
	})
	private Integer deliveryCost = 0;

	/** 국제 결제 사용여부 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean internationalPayYn = false;
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String unit;
	
	public String getUnit() {
		unit = defaultLan.getRef1();
		
		if (!ObjectUtils.isEmpty(storeInfos)) {
			for (int i = 0, len = storeInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(storeInfos.get(i).getLanTp().getId())) {
					if (internationalPayYn) {
						unit = storeInfos.get(i).getLanTp().getRef1();
					} else {
						unit = storeInfos.get(i).getLanTp().getRef2();
					}
					
					break;
				}
			}
		}
		
		return unit;
	}
	
	
	/** 주문자 화면 가로, 세로 여부  23.08.09*/
	@Column
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class })
	private String design;
	
	
	/** GPS lng 경도 */
	@Column(nullable = false)
	@NotNull(message = "{0}은 필수 입력 항목입니다." + SPLIT + "GPS 경도 값", groups = { StoreValid.class })
	@JsonView({ StoreControllerJsonView.class, AppOrderControllerJsonView.class })
	private double gpsLng;
	
	/** GPS lat 위도 */
	@Column(nullable = false)
	@NotNull(message = "{0}은 필수 입력 항목입니다." + SPLIT + "GPS 위도 값", groups = { StoreValid.class })
	@JsonView({ StoreControllerJsonView.class, AppOrderControllerJsonView.class })
	private double gpsLat;
	
	
	/** 정산계좌번호 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "정산계좌번호,{max}", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String accountNumber;
	
	/** 정산은행구분 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView(StoreControllerJsonView.class)
	private Code bankTp;
	
	/** 예금주명 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "예금주명,{max}", groups = { StoreValid.class })
	@JsonView(StoreControllerJsonView.class)
	private String accountName;
	
	
	/** 현재 위치와싀 매장 거리, 앱표시 데이타에서 사용 */
	@Transient
	@JsonView({ AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class })
	private double distance = 0;
	
	
	/** 정기 휴무 */
	/* 고정값: week: 요일별 설정, day: 일자별 설정 */
	@Column(length = 5, nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private String tp;
	
	/* 고정값: ew: 매주, bw: 격주 */
	@Column(length = 5)
	@JsonView(StoreControllerJsonView.class)
	private String weekTp;
	
	@ElementCollection
	@Column
	@JsonView(StoreControllerJsonView.class)
	private List<Integer> weeks;
	
	/* 2019-11-04 일 기준으로 홀,짝 주 */
	@Column
	@JsonView(StoreControllerJsonView.class)
	private Boolean weekSniffling;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private Boolean sniffling;
	
	@ElementCollection
	@Column
	@JsonView(StoreControllerJsonView.class)
	private List<Integer> holidayDays;
	
	
	/** 휴가 */
	@ElementCollection
	@Column
	@JsonView(StoreControllerJsonView.class)
	private List<String> vactionDays;
	
	
	/** 매장 이미지 */
	@Column(length = 100)
	@JsonView(StoreControllerJsonView.class)
	private String storeImg1;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile1;
	
	/** 매장 이미지 */
	@Column(length = 100)
	@JsonView(StoreControllerJsonView.class)
	private String storeImg2;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile2;
	
	/** 매장 이미지 */
	@Column(length = 100)
	@JsonView(StoreControllerJsonView.class)
	private String storeImg3;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile3;
	
	/** 매장 이미지 */
	@Column(length = 100)
	@JsonView(StoreControllerJsonView.class)
	private String storeImg4;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile4;
	
	/** 매장 이미지 */
	@Column(length = 100)
	@JsonView(StoreControllerJsonView.class)
	private String storeImg5;
	
	@Transient
	@JsonView(StoreControllerJsonView.class)
	private String storeImgFile5;
	
	/** 매장 유형 */
	@ElementCollection
	@Column
	@JsonView({ StoreControllerJsonView.class })
	private List<String> storeTps;
	
	// 매장 유형별 LIKE 검색을 편히 하기 위해서 storeTpStr 컬럼추가해서 저장함
	public void setStoreTps(List<String> storeTps) {
		this.storeTps = storeTps;
		
		this.storeTpStr = ""; 
				
		for (String tmp : storeTps) {
			this.storeTpStr += tmp + ",";
		}
	}
	
	@Column(length = 100)
	private String storeTpStr;
	
	@Transient
	@JsonView({ WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Map<String, Object>> discountPrice;
	
	@Transient
	@JsonView({ AppOrderControllerJsonView.class, AppControllerJsonViewForStoreList.class })
	private Boolean favourite = false;
	
	/** 픽업 가능 시간 */
	@Column
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer pickUpTime = 0;
	
	/** 배달주소 목록  */
	@Transient
	private List<Address> address;

	/** 배달 무료 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery0;

	/** 배달 1000원 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery1;

	/** 배달 2000원 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery2;

	/** 배달 3000원 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery3;

	/** 배달 4000원 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery4;

	/** 배달 5000원 지역 */
	@Transient
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private List<Address> delivery5;

	/** 특이사항 입력0 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력0'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo0;

	/** 특이사항 입력1 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력1'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo1;

	/** 특이사항 입력2 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력2'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo2;

	/** 특이사항 입력3 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력3'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo3;

	/** 특이사항 입력4 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력4'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo4;

	/** 특이사항 입력5 */
	@Column(length = 1000, columnDefinition = "varchar(1000) default null comment '특이사항 입력5'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deliveryMemo5;
	
}
