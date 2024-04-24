package org.lf.app.models.business.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.category.Category;
import org.lf.app.models.business.category.CategoryInfo;
import org.lf.app.models.business.category.CategoryService;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.discount.DiscountInfo;
import org.lf.app.models.business.discount.DiscountService;
import org.lf.app.models.business.order.category.OrderCategory;
import org.lf.app.models.business.order.category.OrderCategoryInfo;
import org.lf.app.models.business.order.discount.OrderDiscount;
import org.lf.app.models.business.order.discount.OrderDiscountInfo;
import org.lf.app.models.business.order.smenu.OrderSmenu;
import org.lf.app.models.business.order.smenu.OrderSmenuInfo;
import org.lf.app.models.business.order.smenuOpt.OrderSmenuOpt;
import org.lf.app.models.business.order.smenuOpt.OrderSmenuOptInfo;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenu.SmenuInfo;
import org.lf.app.models.business.smenu.SmenuService;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.business.smenuOpt.SmenuOptInfo;
import org.lf.app.models.business.smenuOpt.SmenuOptService;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.code.CodeService;
import org.lf.app.models.tools.seqBuilder.SeqBuilderService;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.lf.app.models.business.order.Order;

/**
 * 주문관리 서비스
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class OrderService extends BaseService<Order> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private OrderRepository repository;
	
	@Autowired
	private DiscountService discountService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private SmenuOptService smenuOptService;
	
	@Autowired
	private SmenuService smenuService;
	
	@Autowired
    private CodeService codeService;
	
	@Autowired
	private SeqBuilderService seqBuilderService;
	
	
	
	/**
	 * 주문 리스트 조회
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	public Page<Order> findOrderList(Integer storeCd, String startDate, String endDate,
			List<Integer> orderStatusCd, String cancelYn, String cancelPay, Pageable pageable) {
		return repository.findOrderList(storeCd, startDate, endDate, orderStatusCd, cancelYn, cancelPay, pageable);
	}
	
	
	/**
	 * 주문 리스트 조회(매장 관리자 앱)
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	public List<Order> findOrderListForMngUser(Integer storeCd, String startDate, String endDate,
			String cancelYn, String cancelPay, List<Integer> orderStatusCd) {
		return repository.findOrderListForMngUser(storeCd, startDate, endDate, cancelYn, cancelPay, orderStatusCd);
	}
	
	/**
	 * 주문 리스트 결과 업데이트(매장 관리자 앱)
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	
	public void updateOrderList(String Id) {
		repository.updateOrderList(Id);
	}
	
	/**
	 * 결제결과 업데이트(2023.07.28 추가)
	 * @param Id
	 * @param tid
	 * @param svcCd
	 */
	public void updateOrderData(String Id, String tid , String svcCd, String mid, String payResultDiv) {
		repository.updateOrderData(Id, tid, svcCd, mid, payResultDiv);
	}
	
	/**
	 * 결제취소값을 Y로 변경(2023.09.20 추가)
	 * @param tid
	 */
	public void updateCancelYn4Order(@Param("tid") String tid) {
		repository.updateCancelYn4Order(tid);
	}
	
	/**
	 * 시스템 장애로 인한 결제취소시 업데이트(2023.12.05 추가)
	 * @param tid
	 */
	public void updateCancelYn4SysErr(@Param("tid") String tid) {
		repository.updateCancelYn4SysErr(tid);
	}
	
	/**
	 * 주문건의 push알림 발송 여부 'Y'로 업데이트(2023.12.15)
	 * @param orderCd
	 */
	public void updatePushYn4Order(@Param("orderCd") Integer orderCd) {
		repository.updatePushYn4Order(orderCd);
	}
	
	/**
	 * tbl_order 테이블에서 주문번호 유무확인
	 * @param orderId
	 * @return
	 */
	public int getCount4OrderId(String orderId) {
		return repository.getCount4OrderId(orderId);
	}	
	
	/**
	 * orderId에 해당하는 push알림이 발송 안된 주문 유무확인(23.12.15)
	 * @param orderId
	 * @return
	 */
	public int getPushOrderCount4OrderId(@Param("orderId") String orderId) {
		return repository.getPushOrderCount4OrderId(orderId);
	}
	
	/**
	 * 주문 리스트 조회
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	public List<Order> findOrderListForMngUser(Integer storeRoomCd, Integer orderStatusCd, Boolean appSyncYn) {
		return repository.findOrderListForMngUser(storeRoomCd, orderStatusCd, appSyncYn);
	}
	
	
	/**
	 * 앱 회원 주문 리스트 조회, 30 개 한페이지, 페이지 카운터
	 * @param accountId
	 * @param orderCd
	 * @return 주문 리스트
	 */
	public List<Order> findOrderListForAppUser(String accountId, String over) {
		return repository.findOrderListForAppUser(accountId, over);
	}
	
	
	/**
	 * 주문 통지 카운터
	 * @param storeId
	 * @return 주문 리스트
	 */
	public Integer findSearchCnt(String storeId) {
		List<Order> orderList = repository.findSearchCnt(storeId);
		
		// 통지 카운터 한건은 조회 됨으로 변경 해줌
		orderList.forEach(order -> order.setSearchYn(true));
		
		return orderList.size();
	}
	
	
	/**
	 * 주문 데이타 설정
	 */
	public void setOrderData(Order order) {
		order.setOrderStatus(codeService.findOneByTopCodeValAndVal("ORDER_STATUS", "ORDER"));
		order.setOrderDate(new Date());
		order.setSearchYn(false);
		setSalesTp(order);
		setOrderDiscounts(order);
		setOrderSmenu(order);
		setOrderAmt(order);
	}
	
	/**
	 * 주문 데이터 설정(amt 값을 사용자 side에서 넘겨주는 경우 setOrderAmt는 필요가 없다.)
	 * @param order
	 */
	public void setOrderData4Room(Order order) {
		order.setOrderStatus(codeService.findOneByTopCodeValAndVal("ORDER_STATUS", "ORDER"));
		order.setOrderDate(new Date());
		order.setSearchYn(false);
		setSalesTp(order);
		setOrderDiscounts(order);
		setOrderSmenu(order);
		//setOrderAmt(order);
	}
	
	
	/**
	 * 주문 ID 설정
	 * @param order
	 */
	public void setOrderId(Order order) {
		// 주문 번호 설정, 판매방식에 따라 구분
		String prefix = StringUtils.isEmpty(order.getSalesTp().getRef3()) ? "" : order.getSalesTp().getRef3();
		String orderId = seqBuilderService.getOrderId(prefix);
		
		order.setOrderId(orderId);
	}
	
	public List<String> getOrderCd(String hp, Integer storeCd) {
		// 주문 번호 설정, 판매방식에 따라 구분
		
		return repository.getOrderCd(hp, storeCd);
	}
	
	
	/**
	 * 판매방식 설정
	 * @param order
	 */
	public void setSalesTp(Order order) {
		Code salesTp = codeService.findOne(order.getSalesTpCd());
		
		order.setSalesTp(salesTp);
	}
	
	
	/**
	 * 주문별 백업 할인율 설정
	 * @param order
	 */
	public void setOrderDiscounts(Order order) {
		if (!ObjectUtils.isEmpty(order.getDiscount()) && order.getDiscount().size() > 0) {
			
			List<OrderDiscount> discounts = new ArrayList<>();
			
			for (Integer discountCd : order.getDiscount()) {
				Discount tmp = discountService.findOne(discountCd);
				
				if (!ObjectUtils.isEmpty(tmp) && "Y".equals(tmp.getUseYn())) {
					OrderDiscount discount = new OrderDiscount();
					discount.setDiscountTp(tmp.getDiscountTp());
					discount.setPrice(tmp.getPrice());
					discount.setPercente(tmp.getPercente());
					
					// 다국어 설정
					List<OrderDiscountInfo> discountInfos = new ArrayList<>();
					
					OrderDiscountInfo discountInfo = new OrderDiscountInfo();
					discountInfo.setLanTp(tmp.getDefaultLan());
					discountInfo.setDiscountInfoNm(tmp.getDiscountNm());
					
					discountInfos.add(discountInfo);
					
					if (!ObjectUtils.isEmpty(tmp.getDiscountInfos()) && tmp.getDiscountInfos().size() > 0) {
						for (DiscountInfo tmpInfo : tmp.getDiscountInfos()) {
							OrderDiscountInfo discountInfoTmp = new OrderDiscountInfo();
							discountInfoTmp.setLanTp(tmpInfo.getLanTp());
							discountInfoTmp.setDiscountInfoNm(tmpInfo.getDiscountInfoNm());
							discountInfos.add(discountInfoTmp);
						}
					}
					
					discount.setDiscountInfos(discountInfos);
					
					discounts.add(discount);
				}
			}
			
			order.setDiscounts(discounts);
		}
	}
	
	
	/**
	 * 주문별 백업 메뉴 설정
	 * @param order
	 */
	public void setOrderSmenu(Order order) {
		if (!ObjectUtils.isEmpty(order.getSmenus()) && order.getSmenus().size() > 0) {
			for (OrderSmenu smenu : order.getSmenus()) {
				Smenu tmp = smenuService.findOne(smenu.getSmenuCd());
				
				if (!ObjectUtils.isEmpty(tmp) && "Y".equals(tmp.getUseYn())) {
					smenu.setSmenuCd(null);
					setOrderCatetory(smenu);
					setOrderSmenuOpt(smenu);
					setOrderSmenuDiscounts(smenu);
					
					
					smenu.setPrice(tmp.getPrice());
					smenu.setCost(tmp.getCost());
					
					// 다국어 설정
					List<OrderSmenuInfo> smenuInfos = new ArrayList<>();
					
					OrderSmenuInfo smenuInfo = new OrderSmenuInfo();
					smenuInfo.setLanTp(tmp.getDefaultLan());
					smenuInfo.setSmenuInfoNm(tmp.getSmenuNm());
					
					smenuInfos.add(smenuInfo);
					
					if (!ObjectUtils.isEmpty(tmp.getSmenuInfos()) && tmp.getSmenuInfos().size() > 0) {
						for (SmenuInfo tmpInfo : tmp.getSmenuInfos()) {
							OrderSmenuInfo smenuInfoTmp = new OrderSmenuInfo();
							smenuInfoTmp.setLanTp(tmpInfo.getLanTp());
							smenuInfoTmp.setSmenuInfoNm(tmpInfo.getSmenuInfoNm());
							smenuInfos.add(smenuInfoTmp);
						}
					}
					
					smenu.setSmenuInfos(smenuInfos);
				}
			}
		}
	}
	
	
	/**
	 * 카테고리 정보 설정
	 */
	public void setOrderCatetory(OrderSmenu smenu) { 
		if (!ObjectUtils.isEmpty(smenu.getCategoryCd())) {
			// 카테고리 설정
			Category tmp = categoryService.findOne(smenu.getCategoryCd());
			
			if (!ObjectUtils.isEmpty(tmp) && "Y".equals(tmp.getUseYn())) {
				OrderCategory category = new OrderCategory();
				
				// 다국어 설정
				List<OrderCategoryInfo> categoryInfos = new ArrayList<>();
				
				OrderCategoryInfo categoryInfo = new OrderCategoryInfo();
				categoryInfo.setLanTp(tmp.getDefaultLan());
				categoryInfo.setCategoryInfoNm(tmp.getCategoryNm());
				
				categoryInfos.add(categoryInfo);
				
				if (!ObjectUtils.isEmpty(tmp.getCategoryInfos()) && tmp.getCategoryInfos().size() > 0) {
					for (CategoryInfo tmpInfo : tmp.getCategoryInfos()) {
						OrderCategoryInfo categoryInfoTmp = new OrderCategoryInfo();
						categoryInfoTmp.setLanTp(tmpInfo.getLanTp());
						categoryInfoTmp.setCategoryInfoNm(tmpInfo.getCategoryInfoNm());
						categoryInfos.add(categoryInfoTmp);
					}
				}
				
				category.setCategoryInfos(categoryInfos);
				
				smenu.setCategory(category);
			}
		}
	}
	
	
	/**
	 * 옵션 정보 설정
	 */
	public void setOrderSmenuOpt(OrderSmenu smenu) {
		if (!ObjectUtils.isEmpty(smenu.getSmenuOpts()) && smenu.getSmenuOpts().size() > 0) {
			for (OrderSmenuOpt smenuOpt : smenu.getSmenuOpts()) {
				// 옵션 정보 설정
				SmenuOpt tmp = smenuOptService.findOne(smenuOpt.getSmenuOptCd());
				
				if (!ObjectUtils.isEmpty(tmp) && "Y".equals(tmp.getUseYn())) {
					smenuOpt.setSmenuOptCd(null);
					smenuOpt.setCost(tmp.getCost());
					smenuOpt.setPrice(tmp.getPrice());
					
					// 다국어 설정
					List<OrderSmenuOptInfo> smenuOptInfos = new ArrayList<>();
					
					OrderSmenuOptInfo smenuOptInfo = new OrderSmenuOptInfo();
					smenuOptInfo.setLanTp(tmp.getDefaultLan());
					smenuOptInfo.setSmenuOptInfoNm(tmp.getSmenuOptNm());
					
					smenuOptInfos.add(smenuOptInfo);
					
					if (!ObjectUtils.isEmpty(tmp.getSmenuOptInfos()) && tmp.getSmenuOptInfos().size() > 0) {
						for (SmenuOptInfo tmpInfo : tmp.getSmenuOptInfos()) {
							OrderSmenuOptInfo smenuOptInfoTmp = new OrderSmenuOptInfo();
							smenuOptInfoTmp.setLanTp(tmpInfo.getLanTp());
							smenuOptInfoTmp.setSmenuOptInfoNm(tmpInfo.getSmenuOptInfoNm());
							smenuOptInfos.add(smenuOptInfoTmp);
						}
					}
					
					smenuOpt.setSmenuOptInfos(smenuOptInfos);
				}
			}
		}
	}
	
	
	/**
	 * 메뉴 할인 설정
	 * @param order
	 */
	public void setOrderSmenuDiscounts(OrderSmenu smenu) {
		if (!ObjectUtils.isEmpty(smenu.getDiscount()) && smenu.getDiscount().size() > 0) {
			
			List<OrderDiscount> discounts = new ArrayList<>();
			
			for (Integer discountCd : smenu.getDiscount()) {
				Discount tmp = discountService.findOne(discountCd);
				
				if (!ObjectUtils.isEmpty(tmp) && "Y".equals(tmp.getUseYn())) {
					OrderDiscount discount = new OrderDiscount();
					discount.setDiscountTp(tmp.getDiscountTp());
					discount.setPrice(tmp.getPrice());
					discount.setPercente(tmp.getPercente());
					
					// 다국어 설정
					List<OrderDiscountInfo> discountInfos = new ArrayList<>();
					
					OrderDiscountInfo discountInfo = new OrderDiscountInfo();
					discountInfo.setLanTp(tmp.getDefaultLan());
					discountInfo.setDiscountInfoNm(tmp.getDiscountNm());
					
					discountInfos.add(discountInfo);
					
					if (!ObjectUtils.isEmpty(tmp.getDiscountInfos()) && tmp.getDiscountInfos().size() > 0) {
						for (DiscountInfo tmpInfo : tmp.getDiscountInfos()) {
							OrderDiscountInfo discountInfoTmp = new OrderDiscountInfo();
							discountInfoTmp.setLanTp(tmpInfo.getLanTp());
							discountInfoTmp.setDiscountInfoNm(tmpInfo.getDiscountInfoNm());
							discountInfos.add(discountInfoTmp);
						}
					}
					
					discount.setDiscountInfos(discountInfos);
					
					discounts.add(discount);
				}
			}
			
			smenu.setDiscounts(discounts);
		}
	}
	
	
	
	
	
	/**
	 * 주문 데이타 금액 설정
	 */
	public void setOrderAmt(Order order) {
//		log.print(order);
		
		int amt = 0;
		if(order.getMinFlag().contentEquals("TRUE")) {
			amt = order.getMinCost();
		}
		else{
		for (OrderSmenu smenu : order.getSmenus()) {
			
			int menuAmt = 0;
			
			// 메뉴 금액 계산
			menuAmt += smenu.getPrice() * smenu.getCnt();
			
//			log.print("menuAmt += smenu.getPrice() * smenu.getCnt() : " + menuAmt + " += " + smenu.getPrice() + " * " + smenu.getCnt());
			
			if (!ObjectUtils.isEmpty(smenu.getSmenuOpts()) && smenu.getSmenuOpts().size() > 0) {
				// 옵션 금액 계산
				for (OrderSmenuOpt smenuOpt : smenu.getSmenuOpts()) {
					menuAmt += smenuOpt.getPrice() * smenuOpt.getCnt();
					
//					log.print("menuAmt += smenuOpt.getPrice() + smenuOpt.getCnt() : " + menuAmt + " += " + smenuOpt.getPrice() + " * " + smenuOpt.getCnt());
				}
			}
			
			if (!ObjectUtils.isEmpty(smenu.getDiscounts()) && smenu.getDiscounts().size() > 0) {
				// 메뉴 할인 계산
				for (OrderDiscount discount : smenu.getDiscounts()) {
					
//					log.print("discount.getDiscountTp().getVal() : " + discount.getDiscountTp().getVal() + " += " + discount.getPrice() + " , " + discount.getPercente());
					
					if ("price".equals(discount.getDiscountTp().getVal())) {
						menuAmt -= discount.getPrice() * smenu.getCnt();
					} else {
						menuAmt -= (smenu.getPrice() * discount.getPercente() * 0.01) * smenu.getCnt();
					}
				}
			}
			
			amt += menuAmt;
		}
		
		if (!ObjectUtils.isEmpty(order.getDiscounts()) && order.getDiscounts().size() > 0) {
			// 주문 할인 계산
			for (OrderDiscount discount : order.getDiscounts()) {
				
//				log.print("2discount.getDiscountTp().getVal() : " + discount.getDiscountTp().getVal() + " += " + discount.getPrice() + " , " + discount.getPercente());
				
				if ("price".equals(discount.getDiscountTp().getVal())) {
					amt -= discount.getPrice();
				} else {
					amt -= (amt * discount.getPercente() * 0.01);
				}
			}
		}
		}
		// 배달비 추가
		//if(order.getSalesTp() != null)
		//{
			// 배달비 추가
		//if ("Delivery".equals(order.getSalesTp().getVal())) {
			amt += order.getDeliveryCost();
			// 주문에 그떄 배달비 설정
			order.setDeliveryCost(order.getDeliveryCost());
			
		//}
		//}	
		order.setAmt(amt);
	}
	
	
	
	/**
	 * 포스에서 일일 주문 동기화, 매출 구분별
	 * 
	 * @param storeCd
	 * @param cd
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return
	 */
	public List<Map<String, Object>> getAmtSales(Integer storeCd, String dt) {
		return repository.findStatisticsAmtSales(storeCd, "%Y-%m-%d", dt, dt);
	}
	//Moid로 거래고유번호 Select
	public Map<String,String> getTid(String orderId, String MID ) {
	
	return repository.getTid(orderId,MID);
	}
	
	public Order findByOid(String id) {
		Optional<Order> optional = repository.findByOrderId(id);
		return optional.orElse(null);
	}
	
	
	
	
	
}
