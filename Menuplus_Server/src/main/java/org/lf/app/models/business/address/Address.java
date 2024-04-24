package org.lf.app.models.business.address;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerCommonJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
//import org.lf.app.service.app.AppOrderController.AppControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForFavourite;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 주소
 *
 * @author LF
 *
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ADDRESS")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Address extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4699434046191623020L;


	public interface AddressValid {}

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class, 
		//AppControllerJsonView.class,
		OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private Integer addressCd;


	/** 시군구(광역시,특별시 제외) */
	@Column(length = 50, columnDefinition = "varchar(50) not null comment '시군구(광역시,특별시 제외)'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class
		//, AppControllerJsonView.class 
		})
	private String city;

	/** 법정 읍면동 */
	@Column(length = 20, columnDefinition = "varchar(20) not null comment '법정 읍면동'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class
		//, AppControllerJsonView.class
		})
	private String addressBub;

	/** 행정 동리명 */
	@Column(length = 20, columnDefinition = "varchar(20) not null comment '행정 동리명'")
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class
		//, AppControllerJsonView.class 
		})
	private String addressHeng;

}
