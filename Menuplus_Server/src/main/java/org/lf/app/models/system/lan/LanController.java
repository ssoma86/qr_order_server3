package org.lf.app.models.system.lan;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 언어 설정
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/lan")
public class LanController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private LanService service;
	
	
	
	// JsonView
	public interface LanControllerJsonView extends BaseJsonView {}
	public interface LanControllerCommonJsonView {}
		
		
	/**
	 * 추가
	 * 
	 * @param lan
	 */
	@PostMapping
	public void add(@Valid @RequestBody Lan lan) {
		service.save(lan);
		
		if (lan.isDefault_()) {
			service.setDefaultLan(lan);
		}
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param id 언어코드
	 * @return 언어 정보
	 */
	@GetMapping("/{id}")
	@JsonView(LanControllerJsonView.class)
	public Lan get(@PathVariable String id) {
		return service.findOne(id);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param id
	 * @return 언어 정보
	 */
	@PutMapping("/{id}")
	public void up(@Valid @RequestBody Lan lan, @PathVariable String id) {
		lan.setId(id);
		service.save(lan);
		service.setDefaultLan(lan);
	}
	
	
	/**
	 * 언어 정보 리스트 조회
	 * 
	 * @param lan
	 * @return 언어 정보
	 */
	@GetMapping
	@JsonView(LanControllerJsonView.class)
	public List<Lan> search(Lan lan) {
		return service.findLanList(lan);
	}

	
	/**
	 * 언어 코드 사용여부 확인
	 * 
	 * @param id
	 * @return 언어 정보
	 */
	@PatchMapping("/chk/{id}")
	@JsonView(LanControllerCommonJsonView.class)
	public Lan chkLanId(@PathVariable String id) {
		return service.findOne(id);
	}
	
	
}
