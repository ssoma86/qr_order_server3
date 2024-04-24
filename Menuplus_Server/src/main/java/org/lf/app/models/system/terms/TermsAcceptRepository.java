package org.lf.app.models.system.terms;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.lf.app.models.BaseRepository;

/**
 * 약관 동의 상세 정보
 * 
 * @author LF
 * 
 */
public interface TermsAcceptRepository extends BaseRepository<TermsAccept, Serializable> {
	
	/**
	 * 약관 동의 정보 정보 조회
	 * 
	 * @param uuid 폰 고유 키
	 * @return 약관 동의 정보 정보 리스트
	 */
	public List<TermsAccept> findByUuid(String uuid);
	
	/**
	 * 약관 동의 정보 정보 조회
	 * 
	 * @param uuid 폰 고유 키
	 * @param termsTpCd 약관 구분
	 * @return 약관 동의 정보 정보 리스트
	 */
	@Query("SELECT ta "
			+ "FROM TermsAccept ta "
			+ "    LEFT JOIN ta.termsTp tat "
			+ "WHERE ta.uuid LIKE :uuid% "
			+ "  AND (:termsTpCd IS NULL OR tat.cd = :termsTpCd) "
	)
	public List<TermsAccept> findTermsAcceptList(
			@Param("uuid") String uuid, @Param("termsTpCd") Integer termsTpCd);
	
	
}