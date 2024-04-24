package org.lf.app.models.system.dictionary;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 언어 데이타
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class DictionaryService extends BaseService<Dictionary> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private DictionaryRepository repository;
	
	
	
	/**
	 * 언어ID, 언어를 통해 언어 데이타 정보 조회
	 * 
	 * @param id
	 * @param lan
	 * @return 언어 데이타 정보
	 */
	public Dictionary findOneByIdAndLan(String id, Lan lan) {
		return repository.findOneByIdAndLan(id, lan);
	}
	
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param dictionary
	 * @return 언어 정보 리스트
	 */
	public List<Dictionary> findDictionaryList(Dictionary dictionary, Integer dictionaryTpCd) {
		return repository.findDictionaryList(dictionary.getNm(), dictionaryTpCd);
	}
	
	
}
