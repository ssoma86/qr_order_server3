package org.lf.app.models.business.store;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.store.StoreRoomController.StoreRoomControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 매장별 객실 정보
 * 
 * @author 박영근
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STORE_ROOM")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class StoreRoom extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 6893885507832373228L;

	public interface StoreRoomValid {}
	
	/** 객실키 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class,StoreRoomControllerJsonView.class,OrderControllerJsonView.class })
	private Integer storeRoomCd;

	
	/** 객실명 */
	@Column(length = 250, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "객실명", groups = { StoreRoomValid.class })
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "매장명,{max}", groups = { StoreRoomValid.class })
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class,StoreRoomControllerJsonView.class,OrderControllerJsonView.class  })
	private String storeRoomNm;
	
	
	/** 객실 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "객실 설명,{max}", groups = { StoreRoomValid.class })
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class,StoreRoomControllerJsonView.class,OrderControllerJsonView.class  })
	private String storeRoomDesc;
	
	
	/** 매장 */
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JsonView({ StoreControllerJsonView.class, WebAppControllerJsonView.class,StoreRoomControllerJsonView.class,OrderControllerJsonView.class  })
	private Store store;
	
}
