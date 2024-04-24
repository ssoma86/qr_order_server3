package org.lf.app.models.system.auth;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.models.system.resources.ResourcesController.ResourcesControllerCommonJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 권한 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/auth")
public class AuthController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private AuthService service;
	
	// JsonView
	public interface AuthControllerJsonView extends BaseJsonView, ResourcesControllerCommonJsonView {}
	public interface AuthControllerCommonJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param auth 권한 정보
	 */
	@PostMapping
	public void add(@Valid @RequestBody Auth auth) {
		service.save(auth);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param authId 권한ID
	 * @return 권한 정보
	 */
	@GetMapping("/{authId}")
	@JsonView(AuthControllerJsonView.class)
	public Auth get(@PathVariable String authId) {
		return service.findOne(authId);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param auth 권한 정보
	 */
	@PutMapping("/{authId}")
	public void up(@Valid @RequestBody Auth auth, @PathVariable String authId) {
		auth.setAuthId(authId);
		service.save(auth);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param authIds
	 */
	@DeleteMapping("/{authIds}")
	public void del(@PathVariable String[] authIds) {
		service.useYn(authIds, "N");
	}
	
	
	/**
	 * 권한 정보 리스트 조회
	 * 
	 * @param auth
	 * @return 권한 정보 리스트
	 */
	@GetMapping
	@JsonView(AuthControllerJsonView.class)
	public List<Auth> search(Auth auth) {
		return service.findAuthList(auth);
	}
	
	
	/**
	 * 권한ID 사용여부 확인
	 * 
	 * @param authId 권한ID
	 * @return 권한 정보
	 */
	@PatchMapping("/chk/{authId}")
	@JsonView(AuthControllerCommonJsonView.class)
	public Auth chkAuthId(@PathVariable String authId) {
		return service.findOne(authId);
	}
	
	
	/**
	 * 권한명 사용여부 확인
	 * 
	 * @param authNm 권한명
	 * @return 권한 정보
	 */
	@PatchMapping("/chkNm/{authNm}")
	@JsonView(AuthControllerCommonJsonView.class)
	public Auth chkAuthNm(@PathVariable String authNm) {
		return service.findOneByAuthNm(authNm);
	}
	
	
	
}
