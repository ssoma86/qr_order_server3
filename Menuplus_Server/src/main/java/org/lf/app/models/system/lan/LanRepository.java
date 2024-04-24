package org.lf.app.models.system.lan;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.lf.app.models.BaseRepository;

/**
 * 언어 설정
 * 
 * @author LF
 * 
 */
public interface LanRepository extends BaseRepository<Lan, Serializable> {
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param nm 언어 코드 | 언어명
	 * @param useYn 사용여부
	 * @return 언어 정보 리스트
	 */
	@Query("SELECT l "
			+ "FROM Lan l "
			+ "WHERE (l.id LIKE %:nm% OR l.nm LIKE %:nm%) "
			+ "  AND l.useYn LIKE :useYn% "
			+ "ORDER BY l.default_ DESC, l.ord "
	)
	public List<Lan> findLanList(@Param("nm") String nm, @Param("useYn") String useYn);
	
	
	/**
	 * 시스템 디폴트 언어 가져오비
	 * @param default_
	 * @return
	 */
	@Query("SELECT l "
			+ "FROM Lan l "
			+ "WHERE l.default_ = :default_ "
	)
	public Lan findOneByDefault(@Param("default_") Boolean default_);
	
	
}
