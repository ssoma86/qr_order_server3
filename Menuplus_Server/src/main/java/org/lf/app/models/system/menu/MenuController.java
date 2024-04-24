package org.lf.app.models.system.menu;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.menu.Menu.MenuValid;
import org.lf.app.models.system.resources.Resources.ResourcesValid;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 메뉴 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/menu")
public class MenuController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private MenuService service;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface MenuControllerJsonView extends ResourcesControllerJsonView {}
	public interface MenuControllerTopJsonView extends MenuControllerJsonView {}
	public interface MenuControllerSubJsonView extends MenuControllerJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param menu
	 */
	@PostMapping
	public void add(@Validated({ResourcesValid.class, MenuValid.class}) @RequestBody Menu menu) {
		menu.setResTp(codeService.findOneByTopCodeValAndVal("RES_TP", "menu"));
		service.save(menu);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param roleId 역할ID
	 * @return 역할 정보
	 */
	@GetMapping("/{resourcesCd}")
	@JsonView(MenuControllerTopJsonView.class)
	public Menu get(@PathVariable Integer resourcesCd) {
		return service.findOne(resourcesCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param menu
	 */
	@PutMapping("/{resourcesCd}")
	public void up(@Validated({ResourcesValid.class, MenuValid.class}) @RequestBody Menu menu, @PathVariable Integer resourcesCd) {
		menu.setResourcesCd(resourcesCd);
		menu.setResTp(codeService.findOneByTopCodeValAndVal("RES_TP", "menu"));
		service.save(menu);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param resourcesCds
	 */
	@DeleteMapping("/{resourcesCds}")
	public void del(@PathVariable Integer[] resourcesCds) {
		service.useYn(resourcesCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param menu
	 * @return 메뉴 정보
	 */
	@GetMapping
	@JsonView(MenuControllerSubJsonView.class)
	public List<Menu> search() {
		List<Menu> menuList = service.findMenuList();
		
		menuList.forEach(menu -> {
			menu.setMenuNmLan(
					lanDataService.getLanData(menu.getMenuNm(), LocaleContextHolder.getLocale()));
			
			menu.getSubMenu().forEach(subMenu -> subMenu.setMenuNmLan(
					lanDataService.getLanData(subMenu.getMenuNm(), LocaleContextHolder.getLocale())));
		});
		
		return menuList;
	}
	
	
}
