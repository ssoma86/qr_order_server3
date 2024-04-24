package org.lf.app.models.system.banner;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 배너 관리
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class BannerService extends BaseService<Banner> {

	@Autowired
	private BannerRepository repository;
	
	
	/**
	 * 
	 * @param useYn
	 * @return List<Banner>
	 */
	public List<Banner> findBannerList(String useYn) {
		return repository.findBannerList(useYn);
	}
	
	
}
