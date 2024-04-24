package org.lf.app.models.system.history;

import java.util.List;

import org.lf.app.models.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 작업 기록
 * @author LF
 *
 */
@Service
public class HistoryService extends BaseService<History> {

	@Autowired
	private HistoryRepository repository;
	
	/**
	 * 히스토리 리스트 조회
	 * 
	 * @param accountId
	 * @param resourcesCd
	 * @param sdtm
	 * @param edtm
	 * @return 히스토리 리스트
	 */
	public List<History> findHistoryList(String accountId, Integer resourcesCd, String sdtm, String edtm) {
		return repository.findHistoryList(accountId, resourcesCd, sdtm, edtm);
	}
	
}