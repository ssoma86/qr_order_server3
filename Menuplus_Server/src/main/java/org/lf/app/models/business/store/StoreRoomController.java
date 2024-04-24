package org.lf.app.models.business.store;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.lf.app.models.business.store.StoreController.StoreControllerJsonView;
import org.lf.app.models.business.store.StoreRoom.StoreRoomValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 객실정보 관리
 * 
 * @author 박영근
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/room")
public class StoreRoomController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	private static final Logger logger = LoggerFactory.getLogger(StoreRoomController.class);
	
	@Autowired
	private StoreRoomService service;
	
	@Autowired
	private UserService userService;
	
	// JsonView
	public interface StoreRoomControllerJsonView extends StoreControllerJsonView {}
	public interface StoreRoomControllerCommonJsonView {}
	
	/**
	 * 객실정보 추가(1건)
	 * 
	 * @param storeRoom
	 */
	@PostMapping
	public void add(@Validated({ StoreRoomValid.class }) @RequestBody StoreRoom storeRoom, @SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		storeRoom.setStore(user.getStore());
		storeRoom.setUseYn("Y");
		
		service.save(storeRoom);
	}
	
	/**
	 * 객실정보 여러건 등록
	 * @param storeRoomNmStart
	 * @param storeRoomNmEnd
	 * @param account
	 */
	@GetMapping("/rooms/{storeRoomNmStart}/{storeRoomNmEnd}")
	public int addAll( @PathVariable Integer[] storeRoomNmStart, @PathVariable Integer[] storeRoomNmEnd , @SessionAttribute(value = "sa_account") Account account) {
		
		logger.info("============================ 객실 정보 일괄 등록 start ===========================");
		
		User user = userService.findOne(account.getAccountId());
		
		StoreRoom storeRoom = null;
		List<StoreRoom> storeRoomList = null;
		
		int result = 0;
		
		
		if(null != storeRoomNmStart && null != storeRoomNmEnd ) {
			
			storeRoomList = new ArrayList();
			
			for(int i=0 ; i<storeRoomNmStart.length ; i++ ) {		
				
				logger.info("=========storeRoomNmStart[" + i + "]:" + storeRoomNmStart[i]);
				logger.info("=========storeRoomNmEnd[" + i + "]:" + storeRoomNmEnd[i]);
				
				for(int j=storeRoomNmStart[i] ; j <= storeRoomNmEnd[i] ; j++) {
					
					storeRoom = new StoreRoom();
					
					logger.info("=========j:" + j);
					
					storeRoom.setStore(user.getStore());
					storeRoom.setUseYn("Y");
					storeRoom.setStoreRoomNm(String.valueOf(j)); //객실이름 세팅
					
					storeRoomList.add(storeRoom);
				}
			}			
			
			if(null != storeRoomList && storeRoomList.size() > 0 ) {
				for(StoreRoom sRoom : storeRoomList  ) {
					
					StoreRoom tempStoreRoom = service.findStoreRoom(sRoom.getStore().getStoreCd(), sRoom.getStoreRoomNm(), "Y");
					//같은 상점에 같은 이름 그리고 사용중인 객실정보가 없을 경우에만 저장한다.
					if(tempStoreRoom == null) {
						service.save(sRoom); //객실정보 저장
						result = result + 1;
					}
					
				}
			}
			//result = service.save(storeRoomList).size();
			
		}
		
		return result;
		
	}
		
	/**
	 * 삭제
	 * 
	 * @param storeRoomCds
	 */
	@DeleteMapping("/{storeRoomCds}")
	public void del(@PathVariable Integer[] storeRoomCds) {
		service.useYn(storeRoomCds, "N");
	}
	
	/**
	 * 객실정보 리스트 조회
	 * @param req
	 * @param account
	 * @return
	 */
	@GetMapping
	@JsonView(StoreControllerJsonView.class)
	public List<StoreRoom> searchRoomsForStore(String storeRoomNm, String useYn, @SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());		
		List<StoreRoom> storeRoomList = service.findStoreRoomList(user.getStore().getStoreCd(), storeRoomNm, useYn);
		
		return storeRoomList;
	}
	
	/**
	 * 객실명  사용여부 확인
	 * 
	 * @param authId 권한ID
	 * @return 권한 정보
	 */
	@PatchMapping("/chk/{storeRoomNm}")
	@JsonView(StoreControllerJsonView.class)
	public StoreRoom chkStoreRoom(@PathVariable String storeRoomNm, @SessionAttribute(value = "sa_account") Account account) {
		
		User user = userService.findOne(account.getAccountId());	
		return service.findStoreRoom(user.getStore().getStoreCd(), storeRoomNm, "Y");
		
	}
	
}
