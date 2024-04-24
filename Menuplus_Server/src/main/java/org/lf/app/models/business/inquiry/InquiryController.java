package org.lf.app.models.business.inquiry;

import java.text.ParseException;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.inquiry.Inquiry.InquiryAnswerdValid;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 1:1 문의 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/inquiry")
public class InquiryController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private InquiryService service;
	
	
	// JsonView
	public interface InquiryControllerJsonView extends CodeControllerCommonJsonView {};
		
		
	
	/**
	 * 정보 조회
	 * 
	 * @param inquiryCd 1:1 문의 코드
	 * @return 1:1 문의 정보
	 */
	@GetMapping("/{inquiryCd}")
	@JsonView(InquiryControllerJsonView.class)
	public Inquiry get(@PathVariable Integer inquiryCd) {
		return service.findOne(inquiryCd);
	}
	
	
	/**
	 * 답변
	 * 
	 * @param user
	 * @param accountId
	 * @return 회원 정보
	 */
	@PutMapping("/{inquiryCd}")
	public void up(@Validated(InquiryAnswerdValid.class) @RequestBody Inquiry inquiry, @PathVariable Integer inquiryCd) {
		Inquiry tmp = service.findOne(inquiryCd);
		tmp.setAnswered(true);
		tmp.setAnswer(inquiry.getAnswer());
		
		service.save(tmp);
	}
	
	
	/**
	 * 리스트 조회
	 * @param inquiry
	 * @return 1:1 문의 정보 리스트
	 * @throws ParseException 
	 */
	@GetMapping
	@JsonView(InquiryControllerJsonView.class)
	public List<Inquiry> search(String startDtm, String endDtm, Boolean answered) throws ParseException {
		
		log.print(answered);
		
		return service.findInquiryList(startDtm, endDtm, answered);
	}
	
	
}
