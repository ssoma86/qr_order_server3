package org.lf.app.models.system.dictionary;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.system.lan.Lan;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 언어 데이타
 * 
 * @author LF
 * 
 */
public interface DictionaryRepository extends BaseRepository<Dictionary, Serializable> {
	
	/**
	 * 언어ID, 언어코드를 통해 언어 데이타 정보 조회
	 * 
	 * @param id
	 * @param lan
	 * @return 언어 데이타 정보
	 */
	@Cacheable("lan")
	public Dictionary findOneByIdAndLan(String id, Lan lan);
	
	/**
	 * 다국어 정보 리스트 조회
	 * 
	 * @param lanNm
	 * @return 다국어 정보 리스트
	 */
	@Query("SELECT DISTINCT d "
			+ "FROM Dictionary d "
			+ "    LEFT JOIN d.dictionarys ds "
			+ "    LEFT JOIN d.dictionaryTp dd "
			+ "WHERE d.lan.id = 'ko' "
			+ "  AND (d.id LIKE %:nm% OR d.nm LIKE %:nm% "
			+ "       OR (ds.nm IS NOT NULL AND ds.nm LIKE %:nm%)) "
			+ "  AND (:dictionaryTpCd = 0 OR dd.cd = :dictionaryTpCd) "
			+ "ORDER BY d.id "
	)
	public List<Dictionary> findDictionaryList(@Param("nm") String nm,
			@Param("dictionaryTpCd") Integer dictionaryTpCd);
	
	
}
