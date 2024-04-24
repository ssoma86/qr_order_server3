package org.lf.app.models.system.account;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.lf.app.models.BaseRepository;

/**
 * 계정
 * 
 * @author LF
 * 
 */
public interface AccountRepository extends BaseRepository<Account, Serializable> {


	/**
	 * 전화번호를 통해 계정 정보 조회
	 * 
	 * @param tel
	 * @return 계정 정보
	 */
	public Account findOneByTel(String tel);
	
	
	/**
	 * 임시 코드를 통해 계정 정보 조회
	 * 
	 * @param tmpPassword
	 * @return 계정 정보
	 */
	public Account findOneByTmpPassword(String tmpPassword);
	
	
	/**
	 * 이메일을 통해 계정 정보 조회
	 * 
	 * @param email
	 * @return 계정 정보
	 */
	public List<Account> findOneByEmail(String email);
	
	
	/**
	 * 계정 정보 조회(24.02.08)
	 * 
	 * @param accountId
	 * @return 계정 정보
	 */
	@Query("SELECT a "
			+ "FROM Account a "
			+ "WHERE binary(a.accountId) = :accountId "
	)
	public Account findAccountByAccountId(@Param("accountId") String accountId);
	
	
	/**
	 * 계정 리스트 조회
	 * 
	 * @param accountId
	 * @param accountNm
	 * @param useYn
	 * @param nonLocked
	 * @param nonExpired
	 * @param certificateNonExpired
	 * @return 계정 리스트
	 */
	@Query("SELECT a "
			+ "FROM Account a "
			+ "WHERE a.accountId LIKE %:accountId% "
			+ "  AND a.accountNm LIKE %:accountNm% "
			+ "  AND a.useYn LIKE :useYn% "
			+ "  AND a.nonLocked LIKE :nonLocked% "
			+ "  AND a.nonExpired LIKE :nonExpired% "
			+ "  AND a.certificateNonExpired LIKE :certificateNonExpired% "
			+ "ORDER BY a.accountId"
	)
	public List<Account> findAccountList(@Param("accountId") String accountId,
			@Param("accountNm") String accountNm, @Param("useYn") String useYn,
			@Param("nonLocked") String nonLocked, @Param("nonExpired") String nonExpired,
			@Param("certificateNonExpired") String certificateNonExpired);
	
	
	/**
	 * 계정 리스트 조회(자동검색기능에서 사용)
	 * 
	 * @param accountNm
	 * @return 계정 리스트
	 */
	@Query("SELECT a "
			+ "FROM Account a "
			+ "WHERE a.accountId LIKE %:accountNm% "
			+ "  OR a.accountNm LIKE %:accountNm% "
			+ "ORDER BY a.accountId, a.accountNm "
	)
	public List<Account> findAccountList(@Param("accountNm") String accountNm);

	/**
	 *
	 * 가맹점관리자 로그인정보와 일치하는지 비교(아이디,비밀번호)
	 *
	 * @param id
	 * @param pw
	 * @return 결과값 ( 1 : 성공, 0: 실패)
	 */
	@Query(	"SELECT (case when a.accountPw = :pw then 1 else 0 end)" +
					"FROM Account a " +
					"WHERE a.accountId = :id"
	)
	public int findOneByMerchantId(@Param("id") String id, @Param("pw") String pw);
	
	
	/**
	 * token 값 업데이트(23.11.20)
	 * @param token
	 * @param accountId
	 */
	@Transactional
	@Modifying	
	@Query("update Account a set a.token = :token "
			+ "WHERE a.accountId = :accountId")
	public void updateToken4AccountId(@Param("token") String token, @Param("accountId") String accountId );
	
	
	/**
	 * push관련 값들 업데이트(23.11.23)
	 * @param token
	 * @param osType
	 * @param accountId
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "update sys_account set token = :token "
			+ ", os_type = :osType "
			+ "WHERE account_id = :accountId")
	public void updatePushInfo4AccountId(@Param("token") String token, @Param("osType") String osType, @Param("accountId") String accountId );
	
	
	
	/**
	 * storeCd에 해당하는 accountId 리스트 얻어오기(23.11.28)
	 * @param storeCd
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT account_id "
			+ "FROM tbl_user "
			+ "WHERE store_store_cd = :storeCd "
	)
	public List<String> getAccountIdList(@Param("storeCd") Integer storeCd);
	
	/**
	 * push알림 log 저장 (23.12.12)
	 * @param tid
	 * @param title
	 * @param msg
	 * @param token
	 * @param osType
	 * @param result_code
	 * @param result_msg
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "insert into tbl_push_log "
			+ " (tid, title, msg, token, os_type, result_code, result_msg, regist_date) "
			+ " values ( :tid, :title, :msg, :token, :osType, :result_code, :result_msg, sysdate())")
	public void insertPushLog(@Param("tid") String tid
								, @Param("title") String title
								, @Param("msg") String msg
								, @Param("token") String token
								, @Param("osType") String osType
								, @Param("result_code") String result_code 
								, @Param("result_msg") String result_msg);
	
	
	
	
}
