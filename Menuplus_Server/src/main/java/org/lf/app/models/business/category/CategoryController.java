package org.lf.app.models.business.category;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.category.Category.CategoryValid;
import org.lf.app.models.business.category.CategoryInfo.CategoryInfoValid;
import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.utils.system.LogUtil;
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
@RequestMapping("/category")
public class CategoryController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private CategoryService service;
	
	
	// JsonView
	public interface CategoryControllerJsonView extends StoreControllerJsonView {}
	public interface CategoryControllerCommonJsonView {}
		
		
	/**
	 * 추가
	 * 
	 * @param category
	 */
	@PostMapping
	public void add(@Validated({ CategoryValid.class, CategoryInfoValid.class }) @RequestBody Category category) {
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
		return service.findOne(categoryCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param category
	 * @param categoryCd
	 */
	@PostMapping("/modi/{categoryCd:\\d+}")
	public void up(@Validated({ CategoryValid.class, CategoryInfoValid.class }) @RequestBody Category category, @PathVariable Integer categoryCd) {
		category.setCategoryCd(categoryCd);
		
		service.save(category);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param categoryCd
	 */
	@GetMapping("/del/{categoryCds}")
	public void del(@PathVariable Integer[] categoryCds) {
		service.useYn(categoryCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param category
	 * @param nationCd
	 * @return 매장정보 리스트
	 */
	@GetMapping
	@JsonView(CategoryControllerJsonView.class)
	public List<Category> search(Integer storeCd, String categoryNm, String useYn) {
		return service.findCategoryList(storeCd, categoryNm, useYn);
	}
	
	
}
