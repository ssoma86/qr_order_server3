package org.lf.app.models.system.lan;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;


/**
 * 언어 설정
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class LanService extends BaseService<Lan> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private LanRepository repository;
	
	
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param lan 언어 파라메타
	 * @return 언어 정보 리스트
	 */
	public List<Lan> findLanList(Lan lan) {
		return repository.findLanList(lan.getNm(), lan.getUseYn());
	}
	
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param lan 언어 파라메타
	 * @return 언어 정보 리스트
	 */
	public Lan getDefaultLan() {
		return repository.findOneByDefault(true);
	}
	
	
	/**
	 * 디폴트 언어 설정
	 * 
	 * @param lan 언어 파라메타
	 */
	public void setDefaultLan(Lan lan) {
		List<Lan> lanList = repository.findAll(Sort.by("ord"));
		
		boolean defaultIsExist = false;
		
		for (Lan lanTmp : lanList) {
			if (null != lan) {
				if (lan.equals(lanTmp) && lan.isDefault_() && "Y".equals(lan.getUseYn())) {
					lanTmp.setDefault_(true);
					defaultIsExist = true;
				} else {
					lanTmp.setDefault_(false);
				}
			} else {
				if ("Y".equals(lanTmp.getUseYn()) && lanTmp.isDefault_()) {
					defaultIsExist = true;
				}
			}
		}
		
		// 삭제 하거나 수정시 기본값이 없으면 다시 설정 해줌
		if (!defaultIsExist) {
			for (Lan lanTmp : lanList) {
				if ("Y".equals(lanTmp.getUseYn())) {
					lanTmp.setDefault_(true);
					break;
				}
			}
		}
		
		repository.saveAll(lanList);
	}
	
	
}
