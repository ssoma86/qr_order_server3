package org.lf.app.service.mng;

import java.io.IOException;
import java.security.Principal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.models.business.order.OrderService;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;


/**
 * 
 * 매장 관리자 API
 * 
 * @author lby
 *
 */
@RestController
@RequestMapping("/api/app/mng/pos")
public class PosController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CodeService codeService;
	
	
	// JsonView
	public interface PosControllerJsonView {}
		
	
	/**
	 * 매장정보 동기화
	 */
	@GetMapping("/store")
	@JsonView(PosControllerJsonView.class)
	public Store store(Principal principal) throws IOException, ParseException {
		// 로그인 사용자 정보 조회
		User user = userService.findOne(principal.getName());
		
		return user.getStore();
	}
	
	
	/**
	 * 일일 매출 정보 동기화
	 */
	@GetMapping("/order/{dt}")
	@JsonView(PosControllerJsonView.class)
	public Map<String, Object> order(Principal principal, @PathVariable String dt) throws IOException, ParseException {
		Map<String, Object> result = new HashMap<>();
		result.put("date", dt);
		
		int amt = 0;
		
		// 로그인 사용자 정보 조회
		User user = userService.findOne(principal.getName());
		
		// 구분 코드 조회
		List<Code> salesList = codeService.findByTopCode("SALES_TP");
		
		// 매출 데이타 조회
		List<Map<String, Object>> amtSalesList = orderService.getAmtSales(user.getStore().getStoreCd(), dt);
		
		// 값 설정
		for (Code sales : salesList) {
			String key = sales.getVal();
			int orderAmt = 0;
			
			for (Map<String, Object> tmp : amtSalesList) {
				if (key.equals(MapUtils.getString(tmp, "val", "noKey"))) {
					orderAmt = MapUtils.getInteger(tmp, "order_amt", 0);
					break;
				}
			}
			
			amt += orderAmt;
			
			result.put(key, orderAmt);
		}
		
		result.put("Sum", amt);
		
		return result;
	}
	
	
	
}
