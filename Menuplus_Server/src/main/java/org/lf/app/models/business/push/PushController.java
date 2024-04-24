package org.lf.app.models.business.push;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.push.Push.PushValid;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 푸시 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/push")
public class PushController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private PushService service;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	// JsonView
	public interface PushControllerJsonView extends CodeControllerCommonJsonView {};
		
		
	/**
	 * 추가
	 * 
	 * @param push 푸시 정보
	 * @return 푸시 정보
	 * @throws IOException 
	 */
	@PostMapping
	public void add(@Validated(PushValid.class) @RequestBody Push push) throws IOException {
		push.setSendDtm(new Date());
		service.sendAndSave(push);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param pushCd 푸시 코드
	 * @return 푸시 정보
	 */
	@GetMapping("/{pushCd}")
	@JsonView(PushControllerJsonView.class)
	public Push get(@PathVariable Integer pushCd) {
		return service.findOne(pushCd);
	}
	
	
	/**
	 * 리스트 조회
	 * @param push
	 * @return 푸시 정보 리스트
	 * @throws ParseException 
	 */
	@GetMapping
	@JsonView(PushControllerJsonView.class)
	public List<Push> search(String startDtm, String endDtm, Integer pushTpCd) throws ParseException {
		
		List<Push> pushList = service.findPushList(startDtm, endDtm, pushTpCd);
		
		pushList.forEach(push -> {
			push.getPushTp().setNmLan(
					lanDataService.getLanData(push.getPushTp().getNm(), LocaleContextHolder.getLocale()));
		});
		
		return pushList;
	}
	
	
}
