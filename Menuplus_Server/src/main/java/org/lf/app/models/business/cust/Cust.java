package org.lf.app.models.business.cust;

import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.lf.app.models.Base;
import org.lf.app.models.business.cust.CustController.CustControllerCommonJsonView;
import org.lf.app.models.business.cust.CustController.CustControllerJsonView;
import org.lf.app.models.business.user.User.UserValid;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.tools.fileMan.FileMan;
import org.lf.app.utils.validation.ValidUtil;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 사업장
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_CUST")
@Data
@EqualsAndHashCode(callSuper = false, exclude={ "topCust" })
@ToString(callSuper = false, exclude={ "topCust" })
public class Cust extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -3017478045768565790L;

	
	
	public interface CustValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@NotNull(message = "{0}은 필수 선택 항목입니다." + SPLIT + "사업장", groups = { UserValid.class })
	@JsonView({ CustControllerJsonView.class, CustControllerCommonJsonView.class })
	private Integer custCd;

	/** ID */
	@Column(length = 50, nullable = false)
	@JsonView({ CustControllerJsonView.class, CustControllerCommonJsonView.class })
	private String custId;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "사업장명,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "사업장명", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class, CustControllerCommonJsonView.class })
	private String custNm;
	
	/** 사업자 등록 번호 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "사업자 등록 번호,{max}", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String busiNum;
	
	/** 대표자명 */
	@Column(length = 20, nullable = false)
	@Size(max = 20, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "대표자명,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "대표자명", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String ceoNm;
	
	/** 전화번호 */
	@Column(length = 20, nullable = false)
	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "전화번호", groups = { CustValid.class })
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "전화번호,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "전화번호", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String tel;
	
	/** 팩스번호 */
	@Column(length = 20)
	@Pattern(regexp = ValidUtil.REGEXP_TEL, message = "{0}를 규격에 맞게 입력 하여 주십시오." + SPLIT + "팩스 번호", groups = { CustValid.class })
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "팩스 번호,{max}", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String fax;
	
	/** 우편 번호 */
	@Column(length = 10, nullable = false)
	@Size(max = 10, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "우편 번호,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "우편 번호", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String zipCd;
	
	/** 주소 */
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "주소,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "주소", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String addr;
	
	/** 상세 주소 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "상세 주소,{max}", groups = { CustValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "상세 주소", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String addrDtl;
	
	/** 설명 */
	@Column(length = 250)
	@Size(max = 250, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "설명,{max}", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String custDesc;
	
	/** 상위 사업장 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_CUST_X_TOP_CUST",
			joinColumns=@JoinColumn(name="custCd"),
			inverseJoinColumns=@JoinColumn(name="topCustCd"))
	@JsonView({ CustControllerJsonView.class })
	private Cust topCust;
	
	
	/** 펌부 파일 */
	@OneToMany(cascade = CascadeType.REFRESH)
	@JoinTable(
			name="TBL_CUST_X_FILE_MAN",
			joinColumns=@JoinColumn(name="custCd"),
			inverseJoinColumns=@JoinColumn(name="seq"))
	@JsonView({ CustControllerJsonView.class })
	@Where(clause = "use_yn = 'Y'")
	private List<FileMan> files;
	
	/** 가입일 일자 */
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "가입일", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private Date startDt;
	
	/** 탈퇴일 일자 */
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+9")
	@JsonView({ CustControllerJsonView.class })
	private Date endDt;
	
	/** 정산계좌번호 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "정산계좌번호,{max}", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String accountNumber;
	
	/** 정산은행구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JsonView({ CustControllerJsonView.class })
	private Code bankTp;
	
	/** 예금주명 */
	@Column(length = 20)
	@Size(max = 20, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "예금주명,{max}", groups = { CustValid.class })
	@JsonView({ CustControllerJsonView.class })
	private String accountName;
	
	
}
