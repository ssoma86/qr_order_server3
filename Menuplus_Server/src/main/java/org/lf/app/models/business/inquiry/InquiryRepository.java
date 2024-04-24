package org.lf.app.models.business.inquiry;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 1:1 문의
 * 
 * @author LF
 * 
 */
public interface InquiryRepository extends BaseRepository<Inquiry, Serializable> {
	
	
	/**
	 * 날자를 통해서 1:1 문의 가져오기
	 * 
	 * @param startDtm 시작 일시
	 * @param endDtm 종료 일시
	 * @param answered 답변여부
	 * @return 1:1 문의 정보 리스트
	 */
	@Query("SELECT i "
			+ "FROM Inquiry i "
			+ "WHERE i.sendDtm >= :startDtm "
			+ "  AND i.sendDtm <= :endDtm "
			+ "  AND (:answered IS NULL OR i.answered = :answered) "
			+ "ORDER BY i.sendDtm DESC"
	)
	public List<Inquiry> findInquiryList(
			@Param("startDtm") Date startDtm, @Param("endDtm") Date endDtm,
			@Param("answered") Boolean answered);
	
	
}
