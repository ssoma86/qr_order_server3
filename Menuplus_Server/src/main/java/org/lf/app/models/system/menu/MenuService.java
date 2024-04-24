package org.lf.app.models.system.menu;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 메뉴
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class MenuService extends BaseService<Menu> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private MenuRepository repository;
	
	
	
	/**
	 * 메뉴명, 사용여부를 통해 메뉴 정보 리스트 조회
	 * 
	 * @return 메뉴 정보 리스트
	 */
	public List<Menu> findMenuList() {
		return repository.findByMlevelOrderByOrd(1);
	}

	
}
