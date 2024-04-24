package org.lf.app.models.business.member;

import java.text.ParseException;   
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.collections4.MapUtils;
import org.lf.app.models.BaseService;
import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.cust.Cust;
import org.lf.app.models.business.delivery.Delivery;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.discount.DiscountService;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


/**
 * 매장 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class MemberService extends BaseService<Member> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private MemberRepository repository;
	
	@Autowired
	private DiscountService discountService;
	
	@Autowired
	private LanDataService lanDataService;
	
	
	public List<Map<String, Object>> selectMemberPost(String hp) {
		return repository.selectMemberPost(hp);
	}
	
	public void delMemberPost(Integer cd) {
		repository.delMemberPost(cd);
	}
	public void insertMemberPost(String addr,String addrDtl, String tel) {
		repository.insertMemberPost(addr, addrDtl, tel);
	}
	

	public List<Map<String,Object>> selectDelivery(Integer cd) {
		return repository.selectDelivery(cd);
	}
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param custCd
	 * @param MemberNm
	 * @param useYn
	 * @return 매장 리스트 정보
	 */
	
	

}
