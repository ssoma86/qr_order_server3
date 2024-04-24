package org.lf.app.models.business.appUser;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.lf.app.models.business.appUser.AppUserController.AppUserControllerJsonView;
import org.lf.app.models.business.cust.Cust.CustValid;
import org.lf.app.models.business.inquiry.Inquiry;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.system.account.Account;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForFavourite;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 회원 정보
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_APP_USER")
/** 계정 객체 상속 받음 테이블은 두개 생성됨 */
@PrimaryKeyJoinColumn(name="accountId")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "store", "favourites" })
@ToString(callSuper = false, exclude={ "store", "favourites" })
public class AppUser extends Account {

	/** serialVersionUID. */
	private static final long serialVersionUID = -2495415184075409812L;
	
	
	public interface AppUserValid {}
	
	
	/** 소속 매장 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JsonView({ AppUserControllerJsonView.class })
	private Store store;
	
	/** 소셜 ID */
	@Column(length = 60)
	@JsonView({ AppUserControllerJsonView.class, WebAppControllerJsonView.class })
	private String socialId;
	
	/** 닉네임 */
	@Column(length = 60, nullable = false)
	@Size(min = 1, max = 60, message = "{0}은 {1}~{2}자 이내로 입력 하여 주십시오." + SPLIT + "닉네임,{min},{max}", groups = { AppUserValid.class })
	@JsonView({ AppUserControllerJsonView.class, WebAppControllerJsonView.class })
	private String nickName;
	
	/** 이미지 */
	@Column(length = 250)
	@JsonView({ AppUserControllerJsonView.class, WebAppControllerJsonView.class })
	private String profileImg;

	
	/** 소셜 타입 */
	@Column(length = 10)
	private String socialType;
	
	/** 비밀번호 */
	@Column(length = 60)
	private String socialPassword;
	
	/** 가입일 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "가입일 일시", groups = { CustValid.class })
	@JsonView({ AppUserControllerJsonView.class, WebAppControllerJsonView.class })
	private Date startDtm;
	
	/** 탈퇴일 일자 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@JsonView({ AppUserControllerJsonView.class, WebAppControllerJsonView.class })
	private Date endDtm;
	
	@ManyToMany(cascade = CascadeType.REFRESH)
	@JsonView({ AppControllerJsonViewForFavourite.class })
	private Set<Store> favourites;
	
	/** push token */
	@Column(length = 200)
	@JsonView({ AppUserControllerJsonView.class })
	private String pushToken;
	
	/** 1:1 문의 */
	@OneToMany(cascade = CascadeType.REFRESH)
	@JsonView({ AppControllerJsonViewForFavourite.class })
	@OrderBy("answered DESC, inquiryCd DESC")
	private List<Inquiry> inquirys;
	
	
}
