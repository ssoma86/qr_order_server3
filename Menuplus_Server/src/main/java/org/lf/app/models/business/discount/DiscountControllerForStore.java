package org.lf.app.models.business.discount;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.business.discount.Discount.DiscountValid;
import org.lf.app.models.business.discount.DiscountController.DiscountControllerJsonView;
import org.lf.app.models.business.discount.DiscountInfo.DiscountInfoValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 할인 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/discount/store")
public class DiscountControllerForStore {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DiscountService service;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	/**
	 * 추가
	 * 
	 * @param discount
	 */
	@PostMapping
	public void add(@Validated({ DiscountValid.class, DiscountInfoValid.class }) @RequestBody Discount discount,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		discount.setStore(user.getStore());
		discount.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(discount);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param discountCd 할인 코드
	 * @return 할인 정보
	 */
	@GetMapping("/{discountCd:\\d+}")
	@JsonView(DiscountControllerJsonView.class)
	public Discount get(@PathVariable Integer discountCd) {
		return service.findOne(discountCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param discount
	 * @param discountCd
	 */
	@PutMapping("/{discountCd:\\d+}")
	public void up(@Validated({ DiscountValid.class, DiscountInfoValid.class }) @RequestBody Discount discount, @PathVariable Integer discountCd,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		discount.setDiscountCd(discountCd);
		
		User user = userService.findOne(account.getAccountId());
		
		discount.setStore(user.getStore());
		discount.setDefaultLan(user.getStore().getDefaultLan());
		
		service.save(discount);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param discountCd
	 */
	@DeleteMapping("/{discountCds}")
	public void del(@PathVariable Integer[] discountCds) {
		service.useYn(discountCds, "N");
	}
	
	
	/**
	 * 리스트 조회
	 * 
	 * @param discount
	 * @param nationCd
	 * @return 메뉴 할인정보 리스트
	 */
	@GetMapping
	@JsonView(DiscountControllerJsonView.class)
	public List<Discount> search(String discountNm, String useYn,
			@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		List<Discount> discountList = service.findDiscountList(user.getStore().getStoreCd(), discountNm, useYn);
		
		discountList.forEach(discount -> {
			discount.getDiscountTarget().setNmLan(lanDataService.getLanData(discount.getDiscountTarget().getNm(), LocaleContextHolder.getLocale()));
			
			discount.getDiscountTp().setNmLan(lanDataService.getLanData(discount.getDiscountTp().getNm(), LocaleContextHolder.getLocale()));
			
			// 판매 방식
			discount.getSalesTps().forEach(salesTp -> {
				salesTp.setNmLan(lanDataService.getLanData(salesTp.getNm(), LocaleContextHolder.getLocale()));
				
				if (StringUtils.isEmpty(discount.getSalesTpNms())) {
					discount.setSalesTpNms(salesTp.getNmLan());
				} else {
					discount.setSalesTpNms(discount.getSalesTpNms() + ", " + salesTp.getNmLan());
				}
			});
		});
		
		return discountList;
	}
	
	
	/**
	 * 메뉴용 할인 리스트 조회
	 * 
	 * @param discount
	 * @param nationCd
	 * @return 메뉴 옵션정보 리스트
	 */
	@GetMapping("/forMenu")
	@JsonView(DiscountControllerJsonView.class)
	public List<Discount> getForMenu(@SessionAttribute(value = "sa_account", required = false) Account account) {
		
		User user = userService.findOne(account.getAccountId());
		
		Code target = codeService.findOneByTopCodeValAndVal("DISCOUNT_TARGET", "menu");
		
		return service.findByTarget(user.getStore().getStoreCd(), target.getCd());
	}
	
	
}
