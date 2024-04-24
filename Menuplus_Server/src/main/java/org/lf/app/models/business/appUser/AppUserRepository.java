package org.lf.app.models.business.appUser;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 회원 
 * 
 * @author LF
 * 
 */
public interface AppUserRepository extends BaseRepository<AppUser, Serializable> {
	
	
	
	/**
	 * 회원 리스트 조회
	 * 
	 * @param accountId
	 * @param accountNm
	 * @param useYn
	 * @param nonExpired
	 * @param authId
	 * @return 회원 리스트
	 */
	@Query("SELECT u "
			+ "FROM AppUser u "
			+ "    LEFT JOIN u.auths ua "
			+ "WHERE u.accountId LIKE %:accountId% "
			+ "  AND u.accountNm LIKE %:accountNm% "
			+ "  AND u.useYn LIKE :useYn% "
			+ "  AND u.nonExpired LIKE :nonExpired% "
			+ "  AND ua.authId = :authId "
			+ "ORDER BY u.accountId DESC "
	)
	public List<AppUser> findUserList(
			@Param("accountId") String accountId, @Param("accountNm") String accountNm,
			@Param("useYn") String useYn, @Param("nonExpired") String nonExpired,
			@Param("authId") String authId);
	
	
	/**
	 * 
	 * @param socialId
	 * @return 앱 회원 정보
	 */
	public AppUser findOneBySocialId(String socialId);


	/**
	 * 이메일을 통해 계정 정보 조회
	 * 
	 * @param email
	 * @return 계정 정보
	 */
	public List<AppUser> findOneByEmail(String email);
	
	
	/**
	 * 사용중 및 토큰 있는 사용자
	 * 
	 * @return 계정 정보
	 */
	public List<AppUser> findByUseYnAndPushTokenIsNotNull(String useYn);
	
	
}
