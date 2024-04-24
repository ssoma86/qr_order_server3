package org.lf.app.models.business.delivery;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreController.StoreControllerCommonJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 배달
 *
 * @author LF
 *
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_DELIVERY")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Delivery extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4699434046191623020L;


	public interface DeliveryValid {}

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
		OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private Integer deliveryCd;

	/** 코드 */
	@Column
	@JsonView({
	        StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
	        WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
	        OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
	        AppControllerJsonViewForFavourite.class
	})
	private Integer storeCd;

	/** 배달비용 */
  	@Column(length = 10, columnDefinition = "int(6) default 0 comment '배달비용'")
	@Min(value = 0, message="{0}은 {1}보다 크거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { Store.StoreValid.class })
	@Max(value = 100000, message="{0}은 {1}보다 작거나 같아야 합니다." + Base.SPLIT + "배달비용,{value}", groups = { Store.StoreValid.class })
	@JsonView({
			StoreControllerJsonView.class, WebAppControllerJsonView.class,
			AppOrderControllerJsonView.class, AppControllerJsonViewForFavourite.class
	})
	private Integer deliveryCost = 0;

	@Column(length = 11, columnDefinition = "int(11) not null comment '배달주소'")
	@NotNull
  	@JsonView({
          StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
          WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
          OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
          AppControllerJsonViewForFavourite.class
  })
  private Integer addressCd;

}
