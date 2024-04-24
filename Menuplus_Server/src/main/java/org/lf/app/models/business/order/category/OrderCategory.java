package org.lf.app.models.business.order.category;

import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;

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
@Table(name="TBL_ORDER_CATEGORY")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "categoryInfos" })
@ToString(callSuper = false, exclude = { "categoryInfos" })
public class OrderCategory extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -428764884471073641L;

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class })
	private Integer categoryCd;

	/** 메뉴 카테고리 정보 다국어 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name="TBL_ORDER_CATEGORY_X_ORDER_CATEGORY_INFO",
			joinColumns=@JoinColumn(name="categoryCd"),
			inverseJoinColumns=@JoinColumn(name="categoryInfoCd"))
	private List<OrderCategoryInfo> categoryInfos;
	
	/** 카테고리 명 */
	@Transient
	@JsonView({ OrderControllerJsonView.class })
	private String categoryNmLan;
	
	public String getCategoryNmLan() {
		if (!ObjectUtils.isEmpty(categoryInfos)) {
			for (int i = 0, len = categoryInfos.size(); i < len; i++) {
				if (LocaleContextHolder.getLocale().getLanguage().equals(categoryInfos.get(i).getLanTp().getId())) {
					categoryNmLan = categoryInfos.get(i).getCategoryInfoNm();
					break;
				}
				
				if (i == 0) {
					categoryNmLan = categoryInfos.get(i).getCategoryInfoNm();
				}
			}
		}
		
		return categoryNmLan;
	}
	
	
}
