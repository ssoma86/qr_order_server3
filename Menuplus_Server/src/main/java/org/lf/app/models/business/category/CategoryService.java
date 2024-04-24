package org.lf.app.models.business.category;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.category.Category;
import org.lf.app.utils.system.LogUtil;


/**
 * 카테고리 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class CategoryService extends BaseService<Category> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private CategoryRepository repository;
	
	
	/**
	 * 카테고리 리스트 조회
	 * 
	 * @param storeCd
	 * @param categoryNm
	 * @param useYn
	 * @return 카테고리 리스트
	 */
	public List<Category> findCategoryList(Integer storeCd, String categoryNm, String useYn) {
		return repository.findCategoryList(storeCd, categoryNm, useYn);
	}
	
	
	/**
	 * 카테고리 리스트 조회
	 * 
	 * @param categoryNm
	 * @return 카테고리 리스트
	 */
	public List<Category> findCategoryList(String categoryNm) {
		return repository.findCategoryList(categoryNm);
	}
	
	
	/**
	 * 카테고리에 해당하는 메뉴리스트 순번 가져오기
	 * @param categoryCd
	 * @return
	 */
	public List<OrdSmenuInCategory> findSmenuOrdList4Category(@Param("categoryCd") Integer categoryCd){
		return repository.findSmenuOrdList4Category(categoryCd);
	}
	
	/**
	 * 카테고리와 연관된 메뉴리스트의 순번정보 저장
	 * @param categoryCd
	 * @param smenuCd
	 * @param ord
	 */
	public void insertOrdSmenuInCategory(@Param("categoryCd") Integer categoryCd
											, @Param("smenuCd") Integer smenuCd
											, @Param("smenuNmLan") String smenuNmLan
											, @Param("useYn") String useYn
											, @Param("ord") Integer ord) {
		
		repository.insertOrdSmenuInCategory(categoryCd, smenuCd, smenuNmLan, useYn, ord);
		
	}
	
	/**
	 * 메뉴정보 기준 삭제
	 * @param smenuCd
	 */
	public void deleteOrdSmenuInCategory4Smenu(@Param("smenuCd") Integer smenuCd) {
		repository.deleteOrdSmenuInCategory4Smenu(smenuCd);
	}
	
	/**
	 * category정보 기준 삭제
	 * @param categoryCd
	 */
	public void deleteOrdSmenuInCategory4Category(@Param("categoryCd") Integer categoryCd) {
		repository.deleteOrdSmenuInCategory4Category(categoryCd);
	}
	
	
	/**
	 * 카테고리 ord 업데이트
	 * @param categoryCd
	 * @param ord
	 */
	public void updateOrdInCategory(@Param("categoryCd") Integer categoryCd, @Param("ord") Integer ord) {

		repository.updateOrdInCategory(categoryCd, ord);

	}
	
	/**
	 * 카테고리 ord 가져오기
	 * @param categoryCd
	 * @return
	 */
	public Integer selectCategoryOrd(@Param("categoryCd") Integer categoryCd) {
		return repository.selectCategoryOrd(categoryCd);
	}
	
}
