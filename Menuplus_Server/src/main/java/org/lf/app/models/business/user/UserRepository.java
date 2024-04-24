package org.lf.app.models.business.user;

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
public interface UserRepository extends BaseRepository<User, Serializable> {
	
	
	
	/**
	 * 회원 리스트 조회
	 * 
	 * @param accountId
	 * @param accountNm
	 * @param useYn
	 * @param nonExpired
	 * @param custCd
	 * @param authId
	 * @return 회원 리스트
	 */
	@Query("SELECT u "
			+ "FROM User u "
			+ "    LEFT JOIN u.cust uc "
			+ "    LEFT JOIN u.auths ua "
			+ "WHERE u.accountId LIKE %:accountId% "
			+ "  AND u.accountNm LIKE %:accountNm% "
			+ "  AND u.useYn LIKE :useYn% "
			+ "  AND u.nonExpired LIKE :nonExpired% "
			+ "  AND ( uc.custCd = :custCd OR :custCd IS NULL ) "
			+ "  AND ua.authId = :authId "
			+ "ORDER BY uc.custCd, u.accountId "
	)
	public List<User> findUserList(
			@Param("accountId") String accountId, @Param("accountNm") String accountNm,
			@Param("useYn") String useYn, @Param("nonExpired") String nonExpired,
			@Param("custCd") Integer custCd, @Param("authId") String authId);
	
	
}
