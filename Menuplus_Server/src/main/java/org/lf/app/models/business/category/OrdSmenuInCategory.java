package org.lf.app.models.business.category;

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
import org.lf.app.models.business.category.CategoryController.CategoryControllerCommonJsonView;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.business.category.CategoryInfo.CategoryInfoValid;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.Smenu.SmenuValid;
import org.lf.app.models.business.smenu.SmenuController.SmenuControllerJsonView;
import org.lf.app.models.business.smenu.SmenuInfo.SmenuInfoValid;
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
 * category 내에서 메뉴순서
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_ORD_SMENU_IN_CATEGORY")
@Data
public class OrdSmenuInCategory extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 3425864856648935048L;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({
		CategoryControllerJsonView.class, CategoryControllerCommonJsonView.class,
		WebAppControllerJsonView.class
	})
	private Integer ordCd;
	
	@Column
	@JsonView({ CategoryControllerJsonView.class ,WebAppControllerJsonView.class })
	private Integer categoryCd;

	@Column
	@JsonView({ CategoryControllerJsonView.class ,WebAppControllerJsonView.class })
	private Integer smenuCd;
	
	@Column
	@JsonView({ CategoryControllerJsonView.class ,WebAppControllerJsonView.class})
	private String smenuNmLan;
	
	@Column
	@JsonView({ CategoryControllerJsonView.class ,WebAppControllerJsonView.class })
	private Integer ord;
	
	@Transient
	@JsonView({ CategoryControllerJsonView.class, WebAppControllerJsonView.class, AppOrderControllerJsonView.class, SmenuControllerJsonView.class })
	private Smenu smenu;
	
}
