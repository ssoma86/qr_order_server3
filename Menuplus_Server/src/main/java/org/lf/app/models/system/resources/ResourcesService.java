package org.lf.app.models.system.resources;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 리소스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class ResourcesService extends BaseService<Resources> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private ResourcesRepository repository;

	
	
	/**
	 * 주소, 타입을 통해서 리소스 정보 조회
	 * 
	 * @param url
	 * @param method
	 * @return 리소스 정보
	 */
	public Resources findOneByUrlAndMethod(String url, String method) {
		return repository.findOneByUrlAndMethod(url, method);
	}

	/**
	 * 주소, 타입을 통해서 리소스 정보 조회
	 * 
	 * @param resDesc
	 * @return 리소스 정보 리스트
	 */
	public List<Resources> findByResDesc(String resDesc) {
		return repository.findByResDescContaining(resDesc);
	}
	
	
}
