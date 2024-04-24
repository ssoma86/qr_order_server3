package org.lf.app.models.system.terms;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;

/**
 * 약관
 * 
 * @author LF
 * 
 */
public interface TermsRepository extends BaseRepository<Terms, Serializable> {
	
	/**
	 * 언어를 통해서 약관정보 가져오기
	 * 
	 * @param lan 언어
	 * @param useYn 사용여부
	 * @return 약관 정보
	 */
	public List<Terms> findByLanAndUseYn(Lan lan, String useYn);
	
	/**
	 * 언어, 약관 타입을 통해서 약관정보 가져오기
	 * 
	 * @param lan 언어
	 * @param termsTp 약관 타입
	 * @return 약관 정보
	 */
	public Terms findOneByLanAndTermsTp(Lan lan, Code termsTp);
	
	/**
	 * 약관정보 정보 조회
	 * 
	 * @param useYn 사용여부
	 * @param lanId 언어 코드
	 * @param termsTpCd 약관 타입
	 * @return 약관정보 정보 리스트
	 */
	@Query("SELECT t "
			+ "FROM Terms t "
			+ "    LEFT JOIN t.lan tl "
			+ "    LEFT JOIN t.termsTp ttp "
			+ "WHERE t.useYn LIKE :useYn% "
			+ "  AND tl.id LIKE :lanId% "
			+ "  AND (:termsTpCd = 0 OR ttp.cd = :termsTpCd) "
			+ "ORDER BY tl.id, ttp.ord "
	)
	public List<Terms> findTermsList(@Param("useYn") String useYn,
			@Param("lanId") String lanId, @Param("termsTpCd") Integer termsTpCd);
	
	
}
