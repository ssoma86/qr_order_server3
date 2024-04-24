package org.lf.app.models.business.order;

import java.util.Date; 
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
import org.lf.app.models.Base;
import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.order.discount.OrderDiscount;
import org.lf.app.models.business.order.pay.Pay;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.Store.StoreValid;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.store.StoreRoom;
import org.lf.app.models.system.code.Code;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.mng.MngController.MngControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import kr.co.infinisoft.menuplus.util.CryptoConverter;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 주문정보
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_ORDER", indexes = { @Index(name = "INDEX_order_status_order_date", columnList = "order_status_cd,orderDate") } )
@Data
@EqualsAndHashCode(callSuper = false, exclude = {"storeRoom", "store", "smenus", "discounts", "salesTp", "orderStatus" })
@ToString(callSuper = false, exclude = { "storeRoom", "store", "smenus", "discounts", "salesTp", "orderStatus" })
public class Order extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 5528996842160413690L;

	
	
	public interface OrderValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer orderCd;
	
	/** 주문번호 */
	@Column
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String orderId;

	/** 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class })
	private Store store;
	
	/** 주소 */
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주소,{max}", groups = { StoreValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "주소", groups = { OrderValid.class })
	@JsonView({
		OrderControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private String addr;
	
	/** 상세 주소 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "상세 주소,{max}", groups = { StoreValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "상세 주소", groups = { OrderValid.class })
	@JsonView({
		OrderControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private String addrDtl;
	
	
	@Transient
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "매장 ID", groups = { OrderValid.class })
	private String storeId;
	
	/** 주문 시간 */
	@Column(nullable = false)
	@JsonView({
		OrderControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private Date orderDate;
	
	
	@Transient
	private String orderDt;
	
	@JsonView({ MngControllerJsonView.class })
	public String getOrderDt() {
		return DateUtil.dateToStr(getOrderDate());
	}
	
	@Transient
	private String orderTm;
	
	@JsonView({ MngControllerJsonView.class })
	public String getOrderTm() {
		return DateUtil.tmToStr(getOrderDate());
	}
	
	/** 테이블 ID */
	@Column
	@JsonView({
		OrderControllerJsonView.class, WebAppControllerJsonView.class,
		AppOrderControllerJsonView.class
	})
	private Integer tableId;
	
	/** 테이블 ID */
	@Transient
	private String tableIdStr;
	
	
	/** 주문자 전화번호, 포장일 때만 받음 */
	@Column(length = 100)
	@Pattern(regexp=ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "전화번호", groups = { OrderValid.class })
	@Convert(converter = CryptoConverter.class)
	@JsonView({ OrderControllerJsonView.class })
	private String tel;
	
	/** 주문 별도 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주문 별도 설명,{max}", groups = { OrderValid.class })
	@JsonView({ OrderControllerJsonView.class })
	private String orderDesc;
	
	/** 주문 주소 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주문 주소,{max}", groups = { OrderValid.class })
	@JsonView({ OrderControllerJsonView.class })
	private String orderAddr;
	
	/** 주문별 할인 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_X_ORDER_DISCOUNT",
			joinColumns=@JoinColumn(name="orderCd"),
			inverseJoinColumns=@JoinColumn(name="discountCd"))
	@JsonView({ OrderControllerJsonView.class })
	private List<OrderDiscount> discounts;
	
	@Transient
	private List<Integer> discount;
	
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String discountNms;	
	
	public String getDiscountNms() {
		if (!ObjectUtils.isEmpty(discounts) && discounts.size() > 0) {
			for (int i = 0, len = discounts.size(); i < len; i++) {
				if (0 == i) {
					discountNms = discounts.get(i).getDiscountNmLan() + " " +
							("price".equals(discounts.get(i).getDiscountTp().getVal()) ?
									discounts.get(i).getPrice() : discounts.get(i).getPercente() + "%");
				} else {
					discountNms += "," + discounts.get(i).getDiscountNmLan() + " " +
							("price".equals(discounts.get(i).getDiscountTp().getVal()) ?
									discounts.get(i).getPrice() : discounts.get(i).getPercente() + "%");
				}
			}
		}
		
		return discountNms;
	}
	
	/** 판매 방식 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class })
	private Code salesTp;
	
	@Transient
	@NotNull(message = "{0}은 필수 입력 항목입니다." + SPLIT + "판매 방식", groups = { OrderValid.class })
	private Integer salesTpCd;
	
	/** 주문 상태 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class  })
	private Code orderStatus;
	
	/** 결제 금액 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer amt;
	
	/** 화페 단위 */
	@Column(length = 5, nullable = false)
	@NotBlank(message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "화페 단위,{max}", groups = { OrderValid.class })
	@Size(max = 5, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "화페 단위,{max}", groups = { OrderValid.class })
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String unit;
	
	/** 배달비용 */
	@Column
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { OrderValid.class })
	@Max(value = 100000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { OrderValid.class })
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer deliveryCost = 0;
	
	
	/** 주문 포함한 메뉴 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_X_ORDER_SMENU",
			joinColumns=@JoinColumn(name="orderCd"),
			inverseJoinColumns=@JoinColumn(name="smenuCd"))
	@Valid
	@JsonView({ OrderControllerJsonView.class })
	private List<OrderSmenu> smenus;
	
	
	
	/** 주문 회원 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class, MngControllerJsonView.class })
	private AppUser appUser;
	
	
	@Column
	@JsonView(MngControllerJsonView.class)
	private Boolean appSyncYn = false;
	
	@Column(length = 1, nullable = false)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, MngControllerJsonView.class })
	private String cancelYn = "N";
	
	/** 결제 상태 */
	@Column(length = 1, nullable = false)
	@JsonView({ OrderControllerJsonView.class, MngControllerJsonView.class })
	private String payYn = "N";
	
	@Column(length = 1, nullable = false)
	@JsonView({ OrderControllerJsonView.class, MngControllerJsonView.class })
	private String cancelPay = "N";
	
	@OneToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class })
	private Pay pay;
	
	/** 배달 예상 시간 */
	@Column
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer deliveryTime = 0;
	
	// 조회 여부
	@Column
	@JsonIgnore
	private Boolean searchYn = true;
	
	// 선/후결제 구분
	@Column
//	@JsonIgnore
	private String payTp;
	
	/** 픽업예정시간 */
	@Column
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private Integer pickUpTime = 0;
	
	@Transient
	private String minFlag;
	
	@Transient
	private int minCost;
	
	@Column
	private String mid;
	
	@Column
	private String tid;

	/** 주문자 이름 */
	@Column(length = 50)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private String guestName;
	
	/** 객실 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class })
	private StoreRoom storeRoom;
	
	
	@Transient
	private int StoreRoomCd;
	
	@Column
	@JsonView({ OrderControllerJsonView.class })
	/** 결제수단 */
	private String svcCd;
	
	/** 취소 일짜 */
	@Column
	@JsonView({	OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class	})
	private Date cancelDate;
	
	/** 취소 사유 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class  })
	private Code cancelReason;
	
	/** push 발송 여부 */
	@Column(length = 1, nullable = false)
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class  })
	private String pushYn = "N";
	
	/** 결제정보  구분 ( from innopay or from noti) */
	@Column
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class  })
	private String payResultDiv;
	
	/** 대기시간  */
	@Column
	@JsonView({ OrderControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class  })
	private String waitTime;
	
	
}
