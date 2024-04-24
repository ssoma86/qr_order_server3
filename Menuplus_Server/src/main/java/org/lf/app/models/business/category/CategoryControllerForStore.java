package org.lf.app.models.business.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.lf.app.models.business.category.Category.CategoryValid;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.business.category.CategoryInfo.CategoryInfoValid;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.SmenuService;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 카테고리 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/category/store")
public class CategoryControllerForStore {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(CategoryControllerForStore.class);
	
	@Autowired
	private CategoryService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private SmenuService smenuService;
	
	
	/**
	 * 추가
	 * 
	 * @param category
	 */
	@PostMapping
	public void add(@Validated({ CategoryValid.class, CategoryInfoValid.class }) @RequestBody Category category,
			@SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		category.setStore(user.getStore());
		category.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(category);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param categoryCd 카테고리 코드
	 * @return 카테고리 정보
	 */
	@GetMapping("/{categoryCd:\\d+}")
	@JsonView(CategoryControllerJsonView.class)
	public Category get(@PathVariable Integer categoryCd) {
		
		Category cate = service.findOne(categoryCd);
		
		logger.info("===========cate.getCategoryNm():" + cate.getCategoryNm());
		logger.info("===========cate.getOrd()():" + cate.getOrd());
		
		int categoryOrd = service.selectCategoryOrd(categoryCd);// 카테고리 ord 가져오기		
		cate.setOrd(categoryOrd);
		
		List<Smenu> smenus = cate.getSmenus();
		List<Map<String, Object>> tempSmenus = new ArrayList();
		Map<String, Object> tempSmenu = null;
		for(Smenu sm : smenus) {
			logger.info("===sm.getSmenuCd():" + sm.getSmenuCd());
			logger.info("===sm.getSmenuNmLan():" + sm.getSmenuNmLan());
			
			tempSmenu = new HashMap<>();			
			tempSmenu.put("smenuCd", sm.getSmenuCd());
			tempSmenu.put("smenuNmLan", sm.getSmenuNmLan());
			tempSmenus.add(tempSmenu);
		}
		
		cate.setSmenuList(tempSmenus);
		
		//카테고리에 해당하는 메뉴리스트 순번 세팅(23.08.21)
		List<OrdSmenuInCategory> ordSmenuInCategory = service.findSmenuOrdList4Category(categoryCd);
		
		logger.info("=============ordSmenuInCategory.size():" + ordSmenuInCategory.size());
		
		cate.setOrdSmenuInCategory(ordSmenuInCategory);
		
		return cate;
		
	}
	
	
	/**
	 * 수정
	 * 
	 * @param category
	 * @param categoryCd
	 */
	@PutMapping("/{categoryCd:\\d+}")
	public void up(@Validated({ CategoryValid.class, CategoryInfoValid.class }) @RequestBody Category category, @PathVariable Integer categoryCd,
			@SessionAttribute(value = "sa_account") Account account) {
		
		logger.info("========================= up start ========================");	
		logger.info("========================= categoryCd:" + categoryCd);	
		
		category.setCategoryCd(categoryCd);
		
		User user = userService.findOne(account.getAccountId());
		
		category.setStore(user.getStore());
		category.setDefaultLan(user.getStore().getDefaultLan());
		
		logger.info("===============category.getSmenus().size():" + category.getSmenus().size());	
		
		if(null != category.getSmenus() && category.getSmenus().size() > 0) {
			for(Smenu smenu : category.getSmenus()) {
				smenuService.deleteSmenuXCategory(smenu.getSmenuCd()); //메뉴삭제 in tbl_smenu_x_category
				service.deleteOrdSmenuInCategory4Smenu(smenu.getSmenuCd()); //메뉴삭제 in tbl_ord_smenu_in_category
			}
		}
		
		service.save(category);		
		
		service.deleteOrdSmenuInCategory4Category(categoryCd);//카테고리에 해당하는 기존 메뉴정보 삭제
		
		if(category.getOrdSmenuInCategory()!=null && category.getOrdSmenuInCategory().size() > 0) {
			
			for(OrdSmenuInCategory temp : category.getOrdSmenuInCategory()) {
				service.insertOrdSmenuInCategory(categoryCd, temp.getSmenuCd(), temp.getSmenuNmLan(), "Y", temp.getOrd()); //카테고리에 해당하는 메뉴 순번정보 저장
			}
		}
		
	}
	
	/**
	 * 카테고리에 해당하는 상품 순번 수정(23.08.21)
	 * @param category
	 * @param categoryCd
	 * @param account
	 */
	@PutMapping("/ord/{categoryCd:\\d+}")
	public void upOrd( @RequestBody Category category, @PathVariable Integer categoryCd, @SessionAttribute(value = "sa_account") Account account) {
		
		logger.info("=============upOrd start=============");
		logger.info("=============upOrd categoryCd============= : " + categoryCd);
		
		category.setCategoryCd(categoryCd);
		
		//메뉴순번정보 수정(23.08.21)		
		service.deleteOrdSmenuInCategory4Category(categoryCd); //카테고리에 해당하는 기존 메뉴 순번정보 삭제
		
		if(category.getOrdSmenuInCategory()!=null && category.getOrdSmenuInCategory().size() > 0) {
			
			for(OrdSmenuInCategory temp : category.getOrdSmenuInCategory()) {
				service.insertOrdSmenuInCategory(categoryCd, temp.getSmenuCd(), temp.getSmenuNmLan(), "Y",temp.getOrd()); //카테고리에 해당하는 메뉴 순번정보 저장
			}
		}
		
	}
	
	
	/**
	 * 카테고리 순번 수정(23.09.07)
	 * @param categoryList
	 * @param account
	 */
	@PostMapping("/categoryOrd")
	public void updateOrdInCategory( @RequestBody List<Category> categoryList, @SessionAttribute(value = "sa_account") Account account) {
		
		logger.info("=============updateOrdInCategory start=============");
		
		if(categoryList!=null && categoryList.size() > 0) {
			
			for(Category category : categoryList) {
				service.updateOrdInCategory(category.getCategoryCd(), category.getOrd()); //카테고리에  순번정보 수정
			}
		}
		
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param categoryCd
	 */
	@DeleteMapping("/{categoryCds}")
	public void del(@PathVariable Integer[] categoryCds) {
		service.useYn(categoryCds, "N");
	}
	
	
	/**
	 * 상품그룹에 해당하는 메뉴 개수 가져오기(23.10.10)
	 * @param account
	 * @param categoryCds
	 * @return
	 */
	@GetMapping("/menuCnt4Categorys/{categoryCds}")
	@JsonView(CategoryControllerJsonView.class)
	public Integer[] getMenuCnt4Category(@SessionAttribute(value = "sa_account") Account account, @PathVariable Integer[] categoryCds) {
		
		logger.info("=============getMenuCnt4Category start=============");
		
		User user = userService.findOne(account.getAccountId());		
		Integer[] iaMenuCnt = null;
		
		if(null != categoryCds && categoryCds.length > 0) {		
			
			iaMenuCnt = new Integer[categoryCds.length];
			int iTempOrd = 0;
			
			List<Smenu> tempSmenuList = null;
			
			for(Integer categoryCd : categoryCds) {
				tempSmenuList = smenuService.findSmenuList(user.getStore().getStoreCd(), categoryCd, "Y");
				
				logger.info("=================================categoryCd:" + categoryCd);
				logger.info("=================================user.getStore().getStoreCd():" + user.getStore().getStoreCd());
				logger.info("=================================tempSmenuList.size():" + tempSmenuList.size());
				
				iaMenuCnt[iTempOrd] = tempSmenuList.size();
				iTempOrd = iTempOrd + 1;
			}
			
		}
		
		return iaMenuCnt;
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param category
	 * @param nationCd
	 * @return 카테고리 정보 리스트
	 */
	@GetMapping
	@JsonView(CategoryControllerJsonView.class)
	public List<Category> search(String categoryNm, String useYn, @SessionAttribute(value = "sa_account") Account account) {
		User user = userService.findOne(account.getAccountId());		
		return service.findCategoryList(user.getStore().getStoreCd(), categoryNm, useYn);
	}
	
}
