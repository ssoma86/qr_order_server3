package org.lf.app.models.system.history;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.system.account.AccountController.AccountControllerCommonJsonView;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerCommonJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 히스토리 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/history")
public class HistoryController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private HistoryService service;
	
	public interface HistoryControllerJsonView extends AccountControllerCommonJsonView, ResourcesControllerCommonJsonView {}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param History 히스토리 정보
	 * @return 히스토리 정보 리스트
	 */
	@GetMapping
	@JsonView(HistoryControllerJsonView.class)
	public List<History> search(String accountId, Integer resourcesCd, String sdtm, String edtm) {
		return service.findHistoryList(accountId, resourcesCd, sdtm, edtm);
	}
	
	
}
