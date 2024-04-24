package org.lf.app;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.account.AccountService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.models.system.menu.Menu;
import org.lf.app.models.system.menu.MenuController.MenuControllerTopJsonView;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;


/**
 * Web 메인 컨트롤
 * 
 * @author LF
 *
 */
@Controller
@SessionAttributes({ "sa_account", "sa_actions" })
public class WebApp {
	private static final Logger logger = LoggerFactory.getLogger(WebApp.class);
	
	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private LanService lanService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private StoreService storeService;
	
	/**
	 * 각종 페이지 일괄 호출 설정
	 * 
	 * @param folder
	 * @param model
	 * @param params
	 * @param mav
	 * @return ModelAndView(folder/model)
	 */
	@GetMapping("/v/{folder}/{model}")
	public ModelAndView view(@PathVariable("folder") String folder, @PathVariable("model") String model,
			@RequestParam Map<String, Object> params, ModelAndView mav) {
		logger.info("**** request /"+folder+"/"+model);
		logger.info("**** params "+params.toString());
		mav.addAllObjects(params);
		mav.setViewName(folder + "/" + model);
		
		return mav;
	}
	
	
	/**
	 * 각종 페이지 일괄 호출 설정
	 * 
	 * @param folder
	 * @param model
	 * @param type
	 * @param params
	 * @param mav
	 * @return ModelAndView(folder/model/model_type)
	 */
	@GetMapping("/v/{folder}/{model}/{type}")
	public ModelAndView view(@PathVariable("folder") String folder, @PathVariable("model") String model,
			@PathVariable("type") String type, @RequestParam Map<String, Object> params, ModelAndView mav) {
		logger.info("**** request /"+folder+"/"+model+"/"+type);
		logger.info("**** params "+params.toString());
		mav.addAllObjects(params);
		mav.setViewName(folder + "/" + model + "/" + model + "_" + type);
		
		return mav;
	}
	
	
	/**
	 * 언어 코드(권한 체크 안함)
	 * 
	 * @return List<Lan> 언어 리스트
	 */
	@GetMapping("/language")
	public @ResponseBody List<Lan> language(@RequestParam(required = false) String storeId) {
		if (ObjectUtils.isEmpty(storeId)) {
			Lan lan = new Lan();
			lan.setNm("");
			return lanService.findLanList(lan);
		} else {
			Store store = storeService.findOneByStoreId(storeId);
			
			List<Lan> lans = new ArrayList<>();
			lans.add(store.getDefaultLan());
			lans.addAll(store.getLans());
			
			return lans;
		}
	}
	
	
	/**
	 * 메뉴
	 * 
	 * @return List<Menu>
	 */
	@GetMapping("/mainMenu")
	@JsonView(MenuControllerTopJsonView.class)
	public @ResponseBody Set<Menu> mainMenu(@SessionAttribute("sa_account") Account account) {
		Set<Menu> menus = new LinkedHashSet<>();
		
		account = accountService.findOne(account.getAccountId());
		
		// 메뉴 추가	
		account.getAuths().forEach(auth -> {
			auth.getResources().forEach(res -> {
				if (res instanceof Menu) {
					Menu menu = (Menu) res;
					
					if (!menus.contains(menu) && menu.getUseYn().equals("Y")) {
						menus.add(menu);
					}
				}
			});
		});
		
		menus.forEach(menu -> menu.setMenuNmLan(lanDataService.getLanData(menu.getMenuNm(), LocaleContextHolder.getLocale())));
		
		return menus;
	}
	
	
	/**
	 * 대시보드 데이타 조회
	 * 
	 * @return List<Menu>
	 */
	@GetMapping("/dashboard")
	@JsonView(MenuControllerTopJsonView.class)
	public @ResponseBody Map<String, Object> dashboard(@SessionAttribute("sa_account") Account account) {
		
		return null;
	}
	
	
}
