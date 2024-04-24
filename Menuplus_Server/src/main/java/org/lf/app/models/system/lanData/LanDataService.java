package org.lf.app.models.system.lanData;

import java.util.List;
import java.util.Locale;

import javax.transaction.Transactional;

import org.lf.app.config.i18n.IMessageService;
import org.lf.app.models.BaseService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


/**
 * 언어 데이타
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class LanDataService extends BaseService<LanData> implements IMessageService {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private LanDataRepository repository;
	
	@Autowired
	private LanService lanService;
	
	
	
	/**
	 * 언어ID, 언어를 통해 언어 데이타 정보 조회
	 * 
	 * @param id
	 * @param lan
	 * @return 언어 데이타 정보
	 */
	public LanData findOneByIdAndLan(String id, Lan lan) {
		return repository.findOneByIdAndLan(id, lan);
	}
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param lanData
	 * @return 언어 정보 리스트
	 */
	public List<LanData> findLanDataList(LanData lanData, Integer clientTpCd) {
		return repository.findLanDataList(lanData.getNm(), clientTpCd);
	}
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param lanData
	 * @return 언어 정보 리스트
	 */
	public List<LanData> findByClientTp(Code clientTp) {
		return repository.findByClientTp(clientTp);
	}
	
	
	/**
	 * 언어 문자열 조회
	 * 
	 * @param code
	 * @param locale
	 * @return 언어 문자열
	 */
	@Cacheable("lan")
	@Override
	public String getLanData(String code, Locale locale) {
		Lan lan = lanService.findOne(locale.getLanguage());
		
		if (ObjectUtils.isEmpty(lan)) {
			return code;
		} else {
			LanData lanData = repository.findOneByIdAndLan(code, lan);
			
			// 다국어 데이타 없으면 원래 데이타 리턴 함
			return !ObjectUtils.isEmpty(lanData) &&
				   !ObjectUtils.isEmpty(lanData.getNm()) ?
						   lanData.getNm() : code;
		}
	}
	
}
