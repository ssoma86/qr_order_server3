package org.lf.app.models.business.smenu;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.utils.system.LogUtil;


/**
 * 메뉴 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class SmenuService extends BaseService<Smenu> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private SmenuRepository repository;
	
	
	
	/**
	 * 메뉴 리스트 조회
	 * 
	 * @param smenuNm
	 * @param useYn
	 * @return 메뉴 리스트
	 */
	public List<Smenu> findSmenuList(Integer storeCd, Integer categoryCd, String smenuNm, String useYn) {
		return repository.findSmenuList(smenuNm, storeCd, categoryCd, useYn);
	}
	
	/**
	 * 메뉴 리스트 조회
	 * 
	 * @param smenuNm
	 * @return 메뉴 리스트
	 */
	public List<Smenu> findSmenuList(String smenuNm) {
		return repository.findSmenuList(smenuNm);
	}
	
	/**
	 * 메뉴리스트 조회
	 * @param storeCd
	 * @param categoryCd
	 * @param useYn
	 * @return
	 */
	public List<Smenu> findSmenuList(Integer storeCd, Integer categoryCd,String useYn) {
		return repository.findSmenuList(storeCd, categoryCd, useYn);
	}
	
	
	public void updateCategoryInSmenu(@Param("categoryCd") Category category, @Param("smenuCd") Integer smenuCd) {
		repository.updateCategoryInSmenu(category, smenuCd);
	}
	
	public void deleteSmenuXCategory(@Param("smenuCd") Integer smenuCd) {
		repository.deleteSmenuXCategory(smenuCd);
	}
	
}
