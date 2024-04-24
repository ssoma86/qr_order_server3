package org.lf.app.models.system.menu;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.stereotype.Repository;

/**
 * 메뉴
 * 
 * @author LF
 *
 */
@Repository
public interface MenuRepository extends BaseRepository<Menu, Serializable> {

	/**
	 * 상위 메뉴, 메뉴명, 메뉴 레벨을 통해 메뉴 정보 조회
	 * 
	 * @param menuCd
	 * @param menuNm
	 * @param mlevel
	 * @return 메뉴 정보
	 */
	public Menu findOneByTopMenuAndMenuNmAndMlevel(Menu topMenu, String menuNm, int mlevel);

	/**
	 * 메뉴명, 사용여부를 통해 메뉴 정보 리스트 조회
	 * 
	 * @param mlevel
	 * @return 메뉴 정보 리스트
	 */
	public List<Menu> findByMlevelOrderByOrd(int mlevel);
	
	
}
