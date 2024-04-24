package org.lf.app.models.system.account;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.business.appUser.AppUser.AppUserValid;
import org.lf.app.models.business.appUser.AppUserController.AppUserControllerJsonView;
import org.lf.app.models.business.order.OrderController.OrderControllerJsonView;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.system.account.AccountController.AccountControllerCommonJsonView;
import org.lf.app.models.system.account.AccountController.AccountControllerJsonView;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.app.AppStoreController.AppControllerJsonViewForStoreList;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.lf.app.utils.validation.ValidUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 계정
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="SYS_ACCOUNT")
/** 계정 객체 상속 받음 테이블은 두개 생성됨 */
@Inheritance(strategy=InheritanceType.JOINED)
@Data
@EqualsAndHashCode(callSuper = true, exclude={ "auths" })
@ToString(callSuper = true, exclude={ "auths" })
public class Account extends Base implements UserDetails {
    
	/** serialVersionUID. */
	private static final long serialVersionUID = 1619061598719496637L;
	
	// Valid
	public interface AccountPwValid {}
	public interface AccountValid {}

	/** 아이디 */
	@Id
	@Column(length = 50)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "아이디,{max}", groups = { AccountValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "아이디", groups = { AccountValid.class })
	@JsonView({ AccountControllerJsonView.class, AccountControllerCommonJsonView.class })
	private String accountId;

	/** 비밀번호 */
	@Column(length = 60, nullable = false)
	@Size(min = 8, max = 60, message = "{0}는 {1}~{2}자 이내로 입력 하여 주십시오." + SPLIT + "비밀번호,{min},{max}", groups = { AccountPwValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "비밀번호", groups = { AccountPwValid.class })
	@JsonView({ AccountControllerJsonView.class })
	private String accountPw;

	/** 사용자명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "사용자명,{max}", groups = { AccountValid.class, AppUserValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "사용자명", groups = { AccountValid.class })
	@JsonView({ AccountControllerJsonView.class, AccountControllerCommonJsonView.class, OrderControllerJsonView.class })
	private String accountNm;

	/** 잠금 여부 */
	@Column(length = 1, nullable = false)
	@JsonView({ AccountControllerJsonView.class })
	private String nonLocked = "Y";

	/** 만료 여부 */
	@Column(length = 1, nullable = false)
	@JsonView({ AccountControllerJsonView.class })
	private String nonExpired = "Y";

	/** 인증 기간 만료 여부 */
	@Column(length = 1, nullable = false)
	@JsonView({AccountControllerJsonView.class})
	private String certificateNonExpired = "Y";

	/** 폰번호 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "핸드폰 번호,{max}", groups = { AccountValid.class, AppUserValid.class })
	@Pattern(regexp=ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "핸드폰 번호", groups = { AccountValid.class, AppUserValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "핸드폰 번호", groups = { AccountValid.class })
	@JsonView({ AccountControllerJsonView.class })
	private String tel;
	
	/** 이메일 */
	@Column(length = 100)
	@Size(max = 100, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "이메일,{max}", groups = { AccountValid.class, AppUserValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "이메일", groups = { AccountValid.class, AppUserValid.class })
	@Pattern(regexp=ValidUtil.REGEX_EMAIL, message = "{0}을 규격에 맞게 입력 하여 주십시오." + SPLIT + "이메일", groups = { AccountValid.class, AppUserValid.class })
	@JsonView({ AccountControllerJsonView.class })
	private String email;

	/**
	 * 1>只有OneToOne，OneToMany，ManyToMany上才有mappedBy属性，ManyToOne不存在该属性；(主方拥有 mappedBy)
	 * 2>mappedBy标签一定是定义在被拥有方的，他指向拥有方；
	 * 3>mappedBy的含义，应该理解为，拥有方能够自动维护跟被拥有方的关系，当然，如果从被拥有方，通过手工强行来维护拥有方的关系也是可以做到的；
	 * 4>mappedBy跟joinColumn/JoinTable总是处于互斥的一方，可以理解为正是由于拥有方的关联被拥有方的字段存在，拥有方才拥有了被拥有方。mappedBy这方定义JoinColumn/JoinTable总是失效的，不会建立对应的字段或者表。
	 */
	
	/** 역할 집합 */
	@ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JoinTable(
			name="SYS_ACCOUNT_X_AUTH",
			joinColumns=@JoinColumn(name="accountId"),
			inverseJoinColumns=@JoinColumn(name="authId"))
	@JsonView({ AccountControllerJsonView.class })
	private Set<Auth> auths;
	
	
	/***
	 * osType (23.11.08)
	 */
	@Column(length = 20)
	@JsonView({ AccountControllerJsonView.class, AppOrderControllerJsonView.class })
	private String osType;
	
	
	/***
	 * packageName (23.11.08)
	 */
	@Column(length = 20)
	@JsonView({ AccountControllerJsonView.class, AppOrderControllerJsonView.class })
	private String packageName;

	
	/***
	 * token (23.11.08)
	 */
	@Column(length = 255)
	@JsonView({ AccountControllerJsonView.class, AppOrderControllerJsonView.class })
	private String token;
	
	
	/***
	 * deviceId (23.11.08)
	 */
	@Column(length = 255)
	@JsonView({ AccountControllerJsonView.class, AppOrderControllerJsonView.class })
	private String deviceId;
	
	
	/**
	 * Spring security UserDetails 전용 변수
	 */
	@Override
	@Transient
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> grantedAuthority = new HashSet<>();
		
		try {
			if (null != getAuths()) {
				getAuths().forEach(auth -> {
					grantedAuthority.addAll(auth.getResources());
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return grantedAuthority;
	}

	@Override
	@Transient
	@JsonIgnore
	public String getPassword() {
		return getAccountPw();
	}

	@Override
	@Transient
	@JsonIgnore
	public String getUsername() {
		return getAccountId();
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return "Y".equals(getNonExpired());
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return "Y".equals(getNonLocked());
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return "Y".equals(getCertificateNonExpired());
	}

	@Override
	@Transient
	@JsonIgnore
	public boolean isEnabled() {
		return "Y".equals(getUseYn());
	}
	
	/** 로그인 최종 일자 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@JsonView({ AccountControllerJsonView.class })
	private Date lastLoginDtm;
	
	/** 비밀번호 변경일 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+9")
	@JsonView({ AccountControllerJsonView.class })
	private Date passChangeDtm;
	
	/** 임시 비밀번호 */
	@Column(length = 60)
	private String tmpPassword;
	
	/**
	 * 앱에서 로그인시 자동로그인 체크여부 (23.11.30)
	 */
	@Transient
	@JsonView({AppOrderControllerJsonView.class})
	private String autoChkYn;
	
	/**
	 * 앱에서 로그인시 쿠기에서 보내는 정보인지 여부 (23.11.30)
	 */
	@Transient
	@JsonView({AppOrderControllerJsonView.class})
	private String cookieYn;
	
	
	
}
