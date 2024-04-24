package org.lf.app.models.business.store;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 영업 시간
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_STORE_TIME")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class StoreTime extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 827937807009446106L;
	
	
	public interface StoreTimeValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView(StoreControllerJsonView.class)
	private Integer storeTimeCd;
	
	/** 요일 1 ~ 7 */
	@Column(length = 1, nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private int week;
	
	/** 24 여부 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean dayYn = false;
	
	/** 영업 시작 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date startTm;
	
	/** 영업 종료 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date endTm;
	
	/** 브레크타임 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date breakStartTm1;
	
	/** 브레크타임 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date breakEndTm1;
	
	/** 브레크타임 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date breakStartTm2;
	
	/** 브레크타임 시간 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="HH:mm", timezone="GMT+9")
	@JsonView(StoreControllerJsonView.class)
	private Date breakEndTm2;
	
	/** 휴무 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean isHoliday = false;
	
	/** 일괄설정 */
	@Column(nullable = false)
	@JsonView(StoreControllerJsonView.class)
	private boolean isBatch = false;
	
}
