package org.lf.app.models.business.member;

import java.util.Date;  
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.Valid;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import org.lf.app.models.Base;
import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerCommonJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.user.User.UserValid;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForFavourite;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.lf.app.utils.validation.ValidUtil;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
 
/**
 * 매장
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_MEMBER")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)


public class Member extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4699434046191623020L;

	public interface MemberValid {}
		
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "회원번호", groups = { MemberValid.class })
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
		OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	private Integer memberCd;
	
	/** 주문자명 */
	
	@Column(length = 20, nullable = false)
	@Size(max = 20, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주문자명,{max}", groups = { MemberValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "주문자명", groups = { MemberValid.class })
	@JsonView(WebAppControllerJsonView.class)
	private String orderNm;
	
	/** 전화번호 */
	
	@Column(length = 20, nullable = false)
	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "전화번호", groups = { MemberValid.class })
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "전화번호,{max}", groups = { MemberValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "전화번호", groups = { MemberValid.class })
	@JsonView({
		 WebAppControllerJsonView.class,
	
	})
	private String tel;
	
	/** 이용 횟수 */
	
	@Column(length = 20, nullable = false)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "이용 횟수,{max}", groups = { MemberValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "이용 횟수", groups = { MemberValid.class })
	@JsonView(WebAppControllerJsonView.class)
	private Integer useCount;
	



	/** 최근이용일 일자 */
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "최근 이용일", groups = { MemberValid.class })
	@JsonView(WebAppControllerJsonView.class)
	private Date latelyDt;
	
	
}
