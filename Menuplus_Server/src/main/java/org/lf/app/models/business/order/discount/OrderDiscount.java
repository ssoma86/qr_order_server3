package org.lf.app.models.business.order.discount;

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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.system.code.Code;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 할인 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORDER_DISCOUNT")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "discountInfos" })
@ToString(callSuper = false, exclude = { "discountInfos" })
public class OrderDiscount extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -8478154691158244894L;
	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class })
	private Integer discountCd;

	/** 할인 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_DISCOUNT_X_ORDER_DISCOUNT_INFO",
			joinColumns=@JoinColumn(name="discountCd"),
			inverseJoinColumns=@JoinColumn(name="discountInfoCd"))
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "할인 정보 코드", groups = { OrderValid.class })
	private List<OrderDiscountInfo> discountInfos;
	
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String discountNmLan;
	
	public String getDiscountNmLan() {
		if (!ObjectUtils.isEmpty(discountInfos)) {
			for (int i = 0, len = discountInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(discountInfos.get(i).getLanTp().getId())) {
					discountNmLan = discountInfos.get(i).getDiscountInfoNm();
					break;
				}
				
				if (i == 0) {
					discountNmLan = discountInfos.get(i).getDiscountInfoNm();
				}
			}
		}
		
		return discountNmLan;
	}
	
	/** 할인 유형 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class })
	private Code discountTp;
	
	/** 할인 금액 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer price;
	
	/** 할인율 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer percente;
	
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String discountTpNm;
	
}
