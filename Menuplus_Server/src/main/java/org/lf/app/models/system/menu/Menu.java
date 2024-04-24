package org.lf.app.models.system.menu;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.lf.app.models.system.menu.MenuController.MenuControllerJsonView;
import org.lf.app.models.system.menu.MenuController.MenuControllerSubJsonView;
import org.lf.app.models.system.menu.MenuController.MenuControllerTopJsonView;
import org.lf.app.models.system.resources.Resources;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 메뉴
 * 
 * @author LF
 * 
 */
@Entity
@Table(name = "SYS_MENU", uniqueConstraints = { @UniqueConstraint(columnNames = { "top_menu_resources_cd", "menuNm", "mlevel" }) })
@PrimaryKeyJoinColumn(name = "resourcesCd")
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "topMenu", "subMenu" })
@ToString(callSuper = false, exclude = { "topMenu", "subMenu" })
public class Menu extends Resources {

	/** serialVersionUID. */
	private static final long serialVersionUID = -495331749665145669L;

	public interface MenuValid {}
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "메뉴명,{max}", groups = { MenuValid.class })
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + SPLIT + "메뉴명", groups = { MenuValid.class })
	@JsonView({ MenuControllerJsonView.class })
	private String menuNm;
	
	@Transient
	@JsonView({ MenuControllerJsonView.class })
	private String menuNmLan;

	/** 아이콘 */
	@Column(length = 50)
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + SPLIT + "아이콘,{max}", groups = { MenuValid.class })
	@JsonView({ MenuControllerJsonView.class })
	private String menuIcon;

	/** 레벨 */
	@Column(length = 1, nullable = false)
	@Min(value = 1, message = "{0}은 {1}보다 커야 합니다." + SPLIT + "레벨,{value}", groups = { MenuValid.class })
	@Max(value = 2, message = "{0}차 메뉴까지 등록 할 수 있습니다." + SPLIT + "{value}", groups = { MenuValid.class })
	@NotNull(message = "{0}은 필수 입력 항목입니다." + SPLIT + "레벨", groups = { MenuValid.class })
	@JsonView({ MenuControllerJsonView.class })
	private Integer mlevel = 1;

	/** 순서 */
	@Column(length = 2, nullable = false)
	@Min(value = 1, message = "{0}는 {1}보다 커야 합니다." + SPLIT + "순서,{value}", groups = { MenuValid.class })
	@Max(value = 99, message = "{0}는 {1}보다 작아야 합니다." + SPLIT + "순서,{value}", groups = { MenuValid.class })
	@NotNull(message = "{0}는 필수 입력 항목입니다." + SPLIT + "순서", groups = { MenuValid.class })
	@JsonView({ MenuControllerJsonView.class })
	private Integer ord = 1;

	/** 상위 메뉴 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@JsonView({ MenuControllerTopJsonView.class })
	private Menu topMenu;
	
	/** 하위 메뉴 */
	@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "topMenu")
	@JsonView({ MenuControllerSubJsonView.class })
	@OrderBy("ord")
	private List<Menu> subMenu;
	
	
}
