package org.lf.app.models;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 기본 서비스
 * 
 * @author LF
 *
 */
@Transactional
public abstract class BaseService<T extends Base> {

	@Autowired
	public BaseRepository<T, Serializable> repository;
	
	
	
	/**
	 * 키 값 통해서 값 조회
	 * 
	 * @param Serializable id
	 * @return T
	 */
	public T findOne(Serializable id) {
		Optional<T> optional = repository.findById(id);
		return optional.orElse(null);
	}
	
	/**
	 * 객체 리스트 저장
	 * 
	 * @param List<T> baseList
	 * @return List<T>
	 */
	public List<T> save(List<T> baseList) {
		return repository.saveAll(baseList);
	}
	
	/**
	 * 객체 저장
	 * 
	 * @param T base
	 * @return T
	 */
	public T save(T base) {
		return repository.save(base);
	}
	
	/**
	 * 여러개 객체 enable상태 체인지
	 * 
	 * @param List<T> baseList
	 */
	public void useYn(Serializable[] ids, String useYn) {
		try {
			for (int i = 0; i < ids.length; i++) {
				Optional<T> optional = repository.findById(ids[i]);
				
				if (optional.isPresent()) {
					optional.get().setUseYn(useYn);
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
