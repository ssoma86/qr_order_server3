package org.lf.app.models.system.banner;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 배너 관리
 * 
 * @author LF
 * 
 */
public interface BannerRepository extends BaseRepository<Banner, Serializable> {
	
	
	/**
	 * 
	 * @param useYn
	 * @return List<Banner>
	 */
	@Query("SELECT b "
			+ "FROM Banner b "
			+ "WHERE b.useYn LIKE :useYn% "
			+ "ORDER BY b.useYn DESC, b.bannerCd DESC "
	)
	public List<Banner> findBannerList(@Param("useYn") String useYn);
	
}
