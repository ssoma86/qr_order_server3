package org.lf.app.models.system.auth;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.lf.app.models.BaseRepository;

/**
 * 권한
 * 
 * @author LF
 * 
 */
public interface AuthRepository extends BaseRepository<Auth, Serializable> {
	
	/**
	 * 권한명을 통해 권한 정보 조회
	 * 
	 * @param authNm
	 * @return 권한 정보
	 */
	public Auth findOneByAuthNm(String authNm);

	/**
	 * 권한명, 사용여부를 통해 권한 정보 리스트 조회
	 * 
	 * @param authId
	 * @param authNm
	 * @param useYn
	 * @return 권한 정보 리스트
	 */
	@Query("SELECT a "
			+ "FROM Auth a "
			+ "WHERE a.authId LIKE %:authId% "
			+ "  AND a.authNm LIKE %:authNm% "
			+ "  AND a.useYn LIKE :useYn% "
			+ "ORDER BY a.authId "
	)
	public List<Auth> findAuthList(@Param("authId") String authId,
			@Param("authNm") String authNm, @Param("useYn") String useYn);
	
}
