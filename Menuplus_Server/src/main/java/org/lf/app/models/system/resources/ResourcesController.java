package org.lf.app.models.system.resources;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 리소스 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/resources")
public class ResourcesController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private ResourcesService service;
	

	
	public interface ResourcesControllerJsonView extends BaseJsonView, CodeControllerCommonJsonView {}
	public interface ResourcesControllerCommonJsonView {}
	
	
	/**
	 * 리소스명 URL, METHOD 중복여부 확인
	 * 
	 * @param url
	 * @param method
	 * @return 리소스 정보
	 */
	@PatchMapping("/chk/{url}/{method}")
	@JsonView(ResourcesControllerCommonJsonView.class)
	public Resources chkUrlAndMethod(@PathVariable String url, @PathVariable String method) {
		return service.findOneByUrlAndMethod(url.replaceAll("-", "/"), method);
	}
	
	
	/**
	 * 리소스 정보 조회
	 * 
	 * @param resDesc
	 * @return 리소스 정보 리스트
	 */
	@GetMapping("/{resDesc}")
	@JsonView(ResourcesControllerCommonJsonView.class)
	public List<Resources> findByResDesc(@PathVariable String resDesc) {
		return service.findByResDesc(resDesc);
	}
	
	
}
