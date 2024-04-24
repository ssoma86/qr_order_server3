package org.lf.app.models.system.option;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.lf.app.models.Base.BaseJsonView;
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
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 시스템 옵션 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/option")
public class OptionController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private OptionService service;
	
	
	// JsonView
	public interface OptionControllerJsonView extends BaseJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param option
	 */
	@PostMapping
	public void add(@Valid @RequestBody Option option) {
		service.save(option);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param optionId
	 * @return 옵션 정보
	 */
	@GetMapping("/{optionId}")
	@JsonView(OptionControllerJsonView.class)
	public Option get(@PathVariable String optionId) {
		return service.findOne(optionId);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param option
	 * @return 옵션 정보
	 */
	@PutMapping("/{optionId}")
	public void up(@Valid @RequestBody Option option, @PathVariable String optionId) {
		option.setOptionId(optionId);
		service.save(option);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param optionIds
	 */
	@DeleteMapping("/{optionIds}")
	public void del(@PathVariable String[] optionIds) {
		service.useYn(optionIds, "N");
	}
	
	
	/**
	 * 조회
	 * 
	 * @param option
	 * @return 옵션 정보 리스트
	 */
	@GetMapping
	@JsonView(OptionControllerJsonView.class)
	public List<Option> search(Option option) {
		return service.findOptionList(option);
	}
	
	
	/**
	 * 옵션ID 사용여부 확인
	 * 
	 * @param optionId
	 * @return 옵션 정보
	 */
	@PatchMapping("/chk/{optionId}")
	public Option chkOptionId(@PathVariable String optionId) {
		return service.findOneByOptionId(optionId);
	}
	
	
}
