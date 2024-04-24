package org.lf.app.models.system.option;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.lf.app.models.BaseRepository;

/**
 * 시스템 옵션
 * 
 * @author LF
 * 
 */
public interface OptionRepository extends BaseRepository<Option, Serializable> {
	
	/**
	 * 옵션명을 통해 옵션 정보 조회
	 * 
	 * @param optionNm
	 * @return 옵션 정보
	 */
	public Option findOneByOptionId(String optionId);
	
	/**
	 * 옵션명, 사용여부를 통해 옵션 정보 리스트 조회
	 * 
	 * @param optionNm
	 * @param useYn
	 * @return 옵션 정보 리스트
	 */
	@Query("SELECT o "
			+ "FROM Option o "
			+ "WHERE (o.optionId LIKE %:optionNm% OR o.optionVal LIKE %:optionNm% OR o.optionNm LIKE %:optionNm%) "
			+ "  AND o.useYn LIKE :useYn% "
	)
	public List<Option> findOptionList(@Param("optionNm") String optionNm, @Param("useYn") String useYn);
	
	
}
