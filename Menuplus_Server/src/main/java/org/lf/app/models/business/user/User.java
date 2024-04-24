package org.lf.app.models.business.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.Valid;

import org.lf.app.models.business.cust.Cust;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.user.UserController.UserControllerCustJsonView;
import org.lf.app.models.business.user.UserController.UserControllerStoreJsonView;
import org.lf.app.models.system.account.Account;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 사용자 정보
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TBL_USER")
/** 계정 객체 상속 받음 테이블은 두개 생성됨 */
@PrimaryKeyJoinColumn(name="accountId")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "cust", "store" })
@ToString(callSuper = false, exclude={ "cust", "store" })
public class User extends Account {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6253538521296363908L;

	public interface UserValid {}
	
	
	/** 사업장 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@Valid
	@JsonView({ UserControllerCustJsonView.class, UserControllerStoreJsonView.class })
	private Cust cust;
	
	/** 소속 매장 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@Valid
	@JsonView({ UserControllerStoreJsonView.class })
	private Store store;
	
	
}
