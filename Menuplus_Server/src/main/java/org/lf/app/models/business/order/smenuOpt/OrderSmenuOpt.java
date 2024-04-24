package org.lf.app.models.business.order.smenuOpt;

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
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.Order.OrderValid;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 옵션 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORDER_SMENU_OPT")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "smenuOptInfos" })
@ToString(callSuper = false, exclude = { "smenuOptInfos" })
public class OrderSmenuOpt extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -699600789245002836L;

	
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class })
	private Integer smenuOptCd;

	/** 메뉴 옵션 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_SMENU_OPT_X_ORDER_SMENU_OPT_INFO",
			joinColumns=@JoinColumn(name="smenuOptCd"),
			inverseJoinColumns=@JoinColumn(name="smenuOptInfoCd"))
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "메뉴 옵션 코드", groups = { OrderValid.class })
	private List<OrderSmenuOptInfo> smenuOptInfos;
	
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String smenuOptNmLan;
	
	public String getSmenuOptNmLan() {
		if (!ObjectUtils.isEmpty(smenuOptInfos)) {
			for (int i = 0, len = smenuOptInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(smenuOptInfos.get(i).getLanTp().getId())) {
					smenuOptNmLan = smenuOptInfos.get(i).getSmenuOptInfoNm();
					break;
				}
				
				if (i == 0) {
					smenuOptNmLan = smenuOptInfos.get(i).getSmenuOptInfoNm();
				}
			}
		}
		
		return smenuOptNmLan;
	}
	
	/** 원가 */
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer cost;
	
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private Integer price;
	
	@Column(nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private int cnt = 0;
	
	
}
