package org.lf.app.models.business.inquiry;

import java.text.ParseException;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 1:1 문의 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class InquiryService extends BaseService<Inquiry> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private InquiryRepository repository;
	
	
	
	/**
	 * 1:1 문의 정보 조회
	 * 
	 * @param startDtm 시작일시
	 * @param endDtm 종료일시
	 * @return answered 답변여부
	 * @throws ParseException 
	 */
	public List<Inquiry> findInquiryList(String startDtm, String endDtm, Boolean answered) throws ParseException {
		return repository.findInquiryList(DateUtil.strToDateTime(startDtm), DateUtil.strToDateTime(endDtm), answered);
	}
	
	
}
