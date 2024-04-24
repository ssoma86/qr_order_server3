package org.lf.app.models.business.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lf.app.models.business.order.OrderRepository;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * 통계 관리
 * 
 * @author LF
 *
 */
@RestController
@SessionAttributes("sa_account")
@RequestMapping("/statistics")
public class StatisticsController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	
	/**
	 * 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/pay")
	public List<Map<String, Object>> searchPay(String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsPay(user.getStore().getStoreCd(),
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 상품별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/menu")
	public List<Map<String, Object>> searchMenu(String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsMenu(user.getStore().getStoreCd(),
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 상품별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/menu/cnt")
	public List<Map<String, Object>> searchMenuCnt(String smenuNm, String categoryNm, Integer smenuPrice, String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsMenuCnt(user.getStore().getStoreCd(), smenuNm, categoryNm, smenuPrice,
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 카테고리별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/category")
	public List<Map<String, Object>> searchCategory(String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsCategory(user.getStore().getStoreCd(),
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 카테고리별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/category/cnt")
	public List<Map<String, Object>> searchCategoryCnt(String categoryNm, String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsCategoryCnt(user.getStore().getStoreCd(), categoryNm,
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 옵션별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/opt")
	public List<Map<String, Object>> searchOpt(String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsOpt(user.getStore().getStoreCd(),
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 옵션별 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/opt/cnt")
	public List<Map<String, Object>> searchOptCnt(String optNm, Integer optPrice, String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsOptCnt(user.getStore().getStoreCd(), optNm, optPrice,
				LocaleContextHolder.getLocale().getLanguage(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 판매 방식 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/sales")
	public List<Map<String, Object>> searchSales(String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsSales(user.getStore().getStoreCd(), dateTp, startDt, endDt);
		
		return result;
	}
	
	
	/**
	 * 판매 방식 매출 현황
	 * 
	 * @param startDt
	 * @param endDt
	 * @return List<Map<String, Object>>
	 */
	@GetMapping("/sales/cnt")
	public List<Map<String, Object>> searchSalesCnt(Integer cd, String dateTp, String startDt, String endDt,
			@SessionAttribute(value = "sa_account") Account account) {
		List<Map<String, Object>> result = new ArrayList<>();
		
		User user = userService.findOne(account.getAccountId());
		
		// 분류별 조회 조건 설정
		if ("t".equals(dateTp)) {
			dateTp = "%Y-%m-%d %H:%i";
		} else if ("d".equals(dateTp)) {
			dateTp = "%Y-%m-%d";
		} else if ("m".equals(dateTp)) {
			dateTp = "%Y-%m";
		} else if ("y".equals(dateTp)) {
			dateTp = "%Y";
		}
		
		result = orderRepository.findStatisticsSalesCnt(user.getStore().getStoreCd(), cd,
				dateTp, startDt, endDt);
		
		return result;
	}
	
	
}
