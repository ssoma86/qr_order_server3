package org.lf.app.models.business.order.smenu;

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
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORDER_SMENU_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class OrderSmenuInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 3036779079694158917L;

	
	public interface SmenuInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ OrderControllerJsonView.class })
	private Integer smenuInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ OrderControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@JsonView({ OrderControllerJsonView.class })
	private String smenuInfoNm;
	
	
}
