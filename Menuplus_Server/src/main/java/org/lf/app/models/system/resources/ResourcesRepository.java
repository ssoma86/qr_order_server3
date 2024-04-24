package org.lf.app.models.system.resources;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.BaseRepository;

/**
 * 리소스
 * 
 * @author LF
 * 
 */
public interface ResourcesRepository extends BaseRepository<Resources, Serializable> {
	
	/**
	 * 주소, 타입을 통해서 리소스 정보 조회
	 * 
	 * @param url
	 * @param method
	 * @return 리소스 정보
	 */
	public Resources findOneByUrlAndMethod(String url, String method);
	
	/**
	 * 주설명을 통해서 리소스 정보 조회
	 * 
	 * @param resDesc
	 * @return 리소스 정보
	 */
	public List<Resources> findByResDescContaining(String resDesc);
	
}
