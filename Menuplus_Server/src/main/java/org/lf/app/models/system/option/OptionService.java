package org.lf.app.models.system.option;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 시스템 옵션
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class OptionService extends BaseService<Option> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private OptionRepository repository;

	
	
	/**
	 * 옵션명을 통해 옵션 정보 조회
	 * 
	 * @param optionNm
	 * @return 옵션 정보
	 */
	public Option findOneByOptionId(String optionNm) {
		return repository.findOneByOptionId(optionNm);
	}
	
	/**
	 * 옵션명, 사용여부를 통해 옵션 정보 리스트 조회
	 * 
	 * @param option
	 * @return 옵션 정보 리스트
	 */
	public List<Option> findOptionList(Option option) {
		return repository.findOptionList(option.getOptionNm(), option.getUseYn());
	}

	
}
