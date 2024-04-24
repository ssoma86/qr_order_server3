package org.lf.app.models.business.cust;

import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;
import org.lf.app.models.Base;
import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.business.category.CategoryController.CategoryControllerCommonJsonView;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.business.category.CategoryInfo.CategoryInfoValid;
import org.lf.app.models.business.cust.Cust;
import org.lf.app.models.business.cust.CustController.CustControllerJsonView;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.Smenu.SmenuValid;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.service.app.AppOrderController.AppOrderControllerJsonView;
import org.lf.app.service.mng.PosController.PosControllerJsonView;
import org.lf.app.service.web.WebAppController.WebAppControllerJsonView;
import org.springframework.context.i18n.LocaleContextHolder;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 사업장별 결제방법
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_PAY_METHOD")
@Data
public class PayMethod extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 3425864856648935043L;
	
	public interface PayMethodValid {}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({CustControllerJsonView.class, WebAppControllerJsonView.class})
	private Integer pmCd;
	
	
	/** mid */
	@Column(length = 10)
	@Size(max = 10, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "mid,{max}", groups = { PayMethodValid.class })
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String mid;
	
	/** mkey */
	@Column(length = 255)
	@Size(max = 255, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "mkey,{max}", groups = { PayMethodValid.class })
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String mkey;
	
	/** overseas_mid(해외결제용 mid) */
	@Column(length = 10)
	@Size(max = 10, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "mid,{max}", groups = { PayMethodValid.class })
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String overseasMid;
	
	/** overseas_mkey(해외결제용 mkey) */
	@Column(length = 255)
	@Size(max = 255, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "mkey,{max}", groups = { PayMethodValid.class })
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String overseasMkey;
	
	/** method */
	@Column(length = 100)
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String methods;
	
	@Column
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private int custCd;
	
	/** 해외결제통화 */
	@Column
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private String currency;
	
	@Transient
	@JsonView({ CustControllerJsonView.class, WebAppControllerJsonView.class })
	private List<String> payMethodList;
	
}
