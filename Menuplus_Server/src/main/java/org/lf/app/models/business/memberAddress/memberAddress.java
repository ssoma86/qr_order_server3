package org.lf.app.models.business.memberAddress;

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
import org.lf.app.models.business.member.Member;
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
@Table(name="TBL_MEMBER_ADDRESS")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)


public class memberAddress extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 4699434046191623020L;

	public interface MemberValid {}
		
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "고유번호", groups = { MemberValid.class })
	@JsonView({
		StoreControllerJsonView.class, StoreControllerCommonJsonView.class,
		WebAppControllerJsonView.class, AppOrderControllerJsonView.class,
		OrderControllerJsonView.class, AppControllerJsonViewForStoreList.class,
		AppControllerJsonViewForFavourite.class
	})
	/**전화번호*/
	private Integer number;
	
	 	
	 	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "전화번호", groups = { MemberValid.class })
	 	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "전화번호,{max}", groups = { MemberValid.class })
	 	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "전화번호", groups = { MemberValid.class })
	 	@JsonView({
	 		 WebAppControllerJsonView.class,
	 	
	 	})
	 private String tel;
		
	
	
	/** 주소 */
	
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주소,{max}", groups = { MemberValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "주소", groups = { MemberValid.class })
	@JsonView(WebAppControllerJsonView.class)
	private String addr;
	
	/** 상세 주소 */
	
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "상세 주소,{max}", groups = { MemberValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "상세 주소", groups = { MemberValid.class })
	@JsonView(WebAppControllerJsonView.class)
	private String addrDtl;


	
	
}
