package org.lf.app.models.business.order.category;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.system.lan.Lan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 카테고리 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORDER_CATEGORY_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class OrderCategoryInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -1507282696363584755L;

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer categoryInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	private String categoryInfoNm;
	
	
}
