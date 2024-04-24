package org.lf.app.models.business.order.smenu;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.order.category.OrderCategory;
import org.lf.app.models.business.order.discount.OrderDiscount;
import org.lf.app.models.business.order.smenuOpt.OrderSmenuOpt;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 주문시 메뉴 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORDER_SMENU")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "smenuInfos", "category", "smenuOpts", "discounts" })
@ToString(callSuper = false, exclude = { "smenuInfos", "category", "smenuOpts", "discounts" })
public class OrderSmenu extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 2400631248652136652L;

	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class })
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "메뉴 코드", groups = { OrderValid.class })
	private Integer smenuCd;

	/** 메뉴 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_SMENU_X_ORDER_SMENU_INFO",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="smenuInfoCd"))
	private List<OrderSmenuInfo> smenuInfos;
	
	/** 카테고리 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToOne(cascade = CascadeType.ALL)
	@JsonView({ OrderControllerJsonView.class })
	private OrderCategory category;
	
	@Transient
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "카테고리", groups = { OrderValid.class })
	private Integer categoryCd;
	
	/** 메뉴 옵션 정보 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_SMENU_X_ORDER_SMENU_OPT",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="smenuOptCd"))
	@JsonView({ OrderControllerJsonView.class })
	private List<OrderSmenuOpt> smenuOpts;
	
	/** 할인 정보 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_SMENU_X_ORDER_DISCOUNT",
			joinColumns=@JoinColumn(name="smenuCd"),
			inverseJoinColumns=@JoinColumn(name="discountCd"))
	@JsonView({ OrderControllerJsonView.class })
	private List<OrderDiscount> discounts;
	
	@Transient
	private List<Integer> discount;
	
	/** 메뉴명 */
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String smenuNmLan;
	
	public String getSmenuNmLan() {
		
		if (!ObjectUtils.isEmpty(smenuInfos)) {
			for (int i = 0, len = smenuInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(smenuInfos.get(i).getLanTp().getId())) {
					smenuNmLan = smenuInfos.get(i).getSmenuInfoNm();
					break;
				}
				
				if (i == 0) {
					smenuNmLan = smenuInfos.get(i).getSmenuInfoNm();
				}
			}
		}
		
		return smenuNmLan;
	}
	
	/** 원가 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer cost;
	
	/** 단가 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer price;
	
	/** 주문 수량 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private int cnt;
	
	
}

