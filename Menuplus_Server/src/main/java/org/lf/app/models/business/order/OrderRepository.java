package org.lf.app.models.business.order;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lf.app.models.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 주문관리
 * 
 * @author LF
 * 
 */
public interface OrderRepository extends BaseRepository<Order, Serializable> {
	
	
	/**
	 * 주문 리스트 조회(결제된 주문만 리스트 조회한다. payYn='Y')
	 * 24-02-14 수정( 결제여부(payYn) 가 'N' 이어도 amt값이 0이면 리스트에 나온다.)
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	@Query("SELECT o "
			+ " FROM Order o "
			+ "    LEFT JOIN o.store os "
			+ "    LEFT JOIN o.storeRoom osm "
			+ "    LEFT JOIN o.orderStatus oos "
			+ " WHERE o.useYn = 'Y' "
			+ "  AND os.storeCd = :storeCd "
			+ "  AND (:startDate IS NULL OR :startDate = '' OR (:startDate != '' AND o.orderDate >= :startDate) ) "
			+ "  AND (:endDate IS NULL OR :endDate = '' OR (:endDate != '' AND o.orderDate <= :endDate) ) "
			+ "  AND oos.cd IN (:orderStatusCd) "
			+ "  AND (:cancelYn IS NULL OR :cancelYn = '' OR o.cancelYn = :cancelYn ) "
			+ "  AND (:cancelPay IS NULL OR :cancelPay = '' OR o.cancelPay = :cancelPay ) "
			+ "  AND o.payYn = 'Y' "
			//+ "  AND (o.payYn = 'Y' OR (o.payYn = 'N' AND o.amt = 0) ) "
			+ " ORDER BY CASE WHEN 'OVER' = oos.val OR 'Y' = o.cancelYn THEN 1 ELSE 0 END, o.orderDate DESC "
	)
	public Page<Order> findOrderList(@Param("storeCd") Integer storeCd,
			@Param("startDate") String startDate, @Param("endDate") String endDate,
			@Param("orderStatusCd") List<Integer> orderStatusCd,
			@Param("cancelYn") String cancelYn, @Param("cancelPay") String cancelPay,
			Pageable pageable);
	
	
	
	
	/**
	 * 주문 리스트 조회
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	@Query("SELECT o "
			+ "FROM Order o "
			+ "    LEFT JOIN o.store os "
			+ "    LEFT JOIN o.orderStatus oos "
			+ "WHERE o.useYn = 'Y' "
			+ "  AND os.storeCd = :storeCd "
			+ "  AND (:startDate IS NULL OR :startDate = '' OR (:startDate != '' AND o.orderDate >= :startDate) ) "
			+ "  AND (:endDate IS NULL OR :endDate = '' OR (:endDate != '' AND o.orderDate < :endDate) ) "
			+ "  AND (:cancelYn IS NULL OR :cancelYn = '' OR o.cancelYn = :cancelYn ) "
			+ "  AND (:cancelPay IS NULL OR :cancelPay = '' OR o.cancelPay = :cancelPay ) "
			+ "  AND oos.cd IN (:orderStatusCd) "
			+ "  AND o.payYn = 'Y' "
			+ "ORDER BY CASE WHEN 'OVER' = oos.val OR 'Y' = o.cancelYn THEN 1 ELSE 0 END, o.orderDate DESC "
	)
	public List<Order> findOrderListForMngUser(@Param("storeCd") Integer storeCd,
			@Param("startDate") String startDate, @Param("endDate") String endDate,
			@Param("cancelYn") String cancelYn, @Param("cancelPay") String cancelPay,
			@Param("orderStatusCd") List<Integer> orderStatusCd);
	
	/**
	 * 주문 리스트 결과 업데이트
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	@Transactional
	@Modifying	
	@Query("update Order o set o.payYn = 'Y' "
			+ "WHERE o.orderId = :Id")
	public void updateOrderList(@Param("Id") String Id);
	
	
	/**
	 * 결제결과 업데이트(2023.07.28 추가)
	 * @param Id
	 * @param tid
	 * @param svcCd
	 */
	@Transactional
	@Modifying	
	@Query("update Order o set o.payYn = 'Y' "
			+ ", o.tid = :tid "
			+ ", o.svcCd = :svcCd "
			+ ", o.mid = :mid "
			+ ", o.payResultDiv = :payResultDiv "
			+ " WHERE o.orderId = :Id")
	public void updateOrderData(@Param("Id") String Id, @Param("tid") String tid , @Param("svcCd") String svcCd, @Param("mid") String mid, @Param("payResultDiv") String payResultDiv);
	
	
	
	/**
	 * 결제취소값을 Y로 변경(2023.09.20 추가)
	 * @param tid
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "update tbl_order "
			+ "set cancelYn = 'Y' "
			+ "   , order_status_cd = 32 "
			+ "WHERE tid = :tid "
	)
	public void updateCancelYn4Order(@Param("tid") String tid);
	
	
	/**
	 * 시스템 장애로 인한 결제취소시 업데이트(2023.12.05 추가)
	 * @param tid
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "update tbl_order"
			+ " set cancel_yn = 'Y' "
			+ "   , order_status_cd = 32 "
			+ "   , cancel_reason_cd = 93 "
			+ "   , cancel_date = sysdate() "
			+ " where tid = :tid "
	)
	public void updateCancelYn4SysErr(@Param("tid") String tid);
	
	/**
	 * push알림시 주문건의 pushYn을 'Y'로 변경(2023.12.15) 
	 * @param orderCd
	 */
	@Transactional
	@Modifying	
	@Query(nativeQuery = true,value=""
			+ "update tbl_order "
			+ " set push_yn = 'Y' "
			+ " where order_cd = :orderCd "
	)
	public void updatePushYn4Order(@Param("orderCd") Integer orderCd);
	
	/**
	 * 주문내역 key값 가져오기(23.09.22)
	 * @param hp
	 * @param storeCd
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT order_cd "
			+ "FROM tbl_order "
			+ "WHERE tel = :hp "
			+ " AND store_store_cd = :storeCd "
			+ " AND DATE_FORMAT(order_date, '%Y-%m-%d') between DATE_ADD(DATE_FORMAT(now(), '%Y-%m-%d'),interval -2 day) and DATE_FORMAT(now(), '%Y-%m-%d') "
			+ " AND pay_yn='Y'"
			+ "order by order_cd desc "
			//+ "limit 5"
	)
	public List<String> getOrderCd(@Param("hp") String hp, @Param("storeCd") Integer storeCd);
	
	
	
	@Query(nativeQuery = true,value=""
			+ "SELECT TID, GOODS_AMT "
			+ "FROM PG.tb_card_trans "
			+ "WHERE MOID = :orderId "
			+ "AND MID = :MID "
			+ "order by APP_DT desc "
			+ "limit 1"
	)
	public Map<String,String> getTid(@Param("orderId") String orderId,@Param("MID") String MID);
	
	
	/**
	 * 주문번호 유무확인
	 * @param orderId
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT COUNT(*) "
			+ "FROM tbl_order "
			+ "WHERE ORDER_ID = :orderId "
	)
	public int getCount4OrderId(@Param("orderId") String orderId);
	
	
	
	/**
	 * orderId에 해당하는 push알림이 발송 안된 주문 유무확인(23.12.15)
	 * @param orderId
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT COUNT(*) "
			+ "FROM tbl_order "
			+ "WHERE ORDER_ID = :orderId "
			+ "  and (push_yn is null or push_yn = 'N')  "
	)
	public int getPushOrderCount4OrderId(@Param("orderId") String orderId);
	
	
	
	/**
	 * order_cd 구하기(23.12.04)
	 * @param orderId
	 * @return
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT order_cd"
			+ "FROM tbl_order "
			+ "WHERE ORDER_ID = :orderId "
	)
	public int getOrderCd4OrderId(@Param("orderId") String orderId);
	
	
	/**
	 * 주문 리스트 조회
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	@Query("SELECT o "
			+ "FROM Order o "
			+ "    LEFT JOIN o.store os "
			+ "    LEFT JOIN o.orderStatus oos "
			+ "WHERE o.useYn = 'Y' "
			+ "  AND os.storeCd = :storeCd "
			+ "  AND (:orderStatusCd IS NULL OR oos.cd = :orderStatusCd) "
			+ "  AND (o.appSyncYn IS NULL OR o.appSyncYn = :appSyncYn) "
			+ "ORDER BY CASE WHEN 'OVER' = oos.val THEN 1 ELSE 0 END, o.orderDate DESC "
	)
	public List<Order> findOrderListForMngUser(@Param("storeCd") Integer storeCd,
			@Param("orderStatusCd") Integer orderStatusCd, @Param("appSyncYn") Boolean appSyncYn);
	
	
	
	
	
	/**
	 * 앱 회원 주문 리스트 조회, 30 개 한페이지, 페이지 카운터
	 * @param accountId
	 * @return 주문 리스트
	 */
	@Query("SELECT o "
			+ "FROM Order o "
			+ "    LEFT JOIN o.appUser oa "
			+ "    LEFT JOIN o.orderStatus oos "
			+ "WHERE o.useYn = 'Y' "
			+ "  AND oa.accountId = :accountId "
			+ "  AND ( ('Y' = :over AND ('OVER' = oos.val OR 'Y' = o.cancelYn)) OR ('N' = :over AND ('OVER' != oos.val AND 'N' = o.cancelYn)) ) "
			+ "ORDER BY CASE WHEN 'OVER' = oos.val OR 'Y' = o.cancelYn THEN 1 ELSE 0 END, o.orderDate DESC "
	)
	public List<Order> findOrderListForAppUser(@Param("accountId") String accountId,
			@Param("over") String over);
	
	
	
	
	
	/**
	 * 주문 통지 카운터
	 * @param storeId
	 * @return 주문 통지 카운터
	 */
	@Query("SELECT DISTINCT o "
			+ "FROM Order o "
			+ "    LEFT JOIN o.store os "
			+ "    LEFT JOIN o.orderStatus oos "
			+ "WHERE o.useYn = 'Y' "
			+ "  AND os.storeId = :storeId "
			+ "  AND o.cancelYn = 'N' "
			+ "  AND o.payYn = 'Y' "
			+ "  AND 'ORDER' = oos.val "			
			//+ "  AND o.searchYn = false "
	)
	public List<Order> findSearchCnt(@Param("storeId") String storeId);
	
	
	
	/** ===============================  통계 부분 ================================== */
	
	
	/**
	 * 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " +
			// 판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			"FROM tbl_order torder " + 
			" " +
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_info tsmenu_x_info ON tsmenu.smenu_cd = tsmenu_x_info.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_info tsmenu_info ON tsmenu_x_info.smenu_info_cd = tsmenu_info.smenu_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_category tcategory ON tsmenu.category_category_cd = tcategory.category_cd " + 
			"LEFT JOIN tbl_order_category_x_order_category_info tcategory_x_info ON tcategory.category_cd = tcategory_x_info.category_cd " + 
			"LEFT JOIN tbl_order_category_info tcategory_info ON tcategory_x_info.category_info_cd = tcategory_info.category_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info tsmenu_discount_x_info ON tsmenu_discount.discount_cd = tsmenu_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info tsmenu_discount_info ON tsmenu_discount_x_info.discount_info_cd = tsmenu_discount_info.discount_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info torder_discount_x_info ON torder_discount.discount_cd = torder_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info torder_discount_info ON torder_discount_x_info.discount_info_cd = torder_discount_info.discount_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND tsmenu_info.lan_tp_id = :lanId " + 
			"  AND tcategory_info.lan_tp_id = :lanId " + 
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " +
			"  AND (tsmenu_discount_info.lan_tp_id IS NULL OR tsmenu_discount_info.lan_tp_id = :lanId) " +
			"  AND (torder_discount_info.lan_tp_id IS NULL OR torder_discount_info.lan_tp_id = :lanId) " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " +
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY date_format(torder.order_date, :dateTp) " +
			"ORDER BY date_format(torder.order_date, :dateTp) "
	)
	public List<Map<String, Object>> findStatisticsPay(@Param("storeCd") Integer storeCd,
														@Param("lanId") String lanId, @Param("dateTp") String dateTp,
														@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 상품별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	tsmenu_info.smenu_info_nm AS smenu_nm " + 
			"	, tcategory_info.category_info_nm AS category_nm " + 
			"	, tsmenu.cost AS cost " + 
			"	, tsmenu.price AS smenu_price " + 
			// 판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			"FROM tbl_order torder " + 
			" " +
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_info tsmenu_x_info ON tsmenu.smenu_cd = tsmenu_x_info.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_info tsmenu_info ON tsmenu_x_info.smenu_info_cd = tsmenu_info.smenu_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_category tcategory ON tsmenu.category_category_cd = tcategory.category_cd " + 
			"LEFT JOIN tbl_order_category_x_order_category_info tcategory_x_info ON tcategory.category_cd = tcategory_x_info.category_cd " + 
			"LEFT JOIN tbl_order_category_info tcategory_info ON tcategory_x_info.category_info_cd = tcategory_info.category_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info tsmenu_discount_x_info ON tsmenu_discount.discount_cd = tsmenu_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info tsmenu_discount_info ON tsmenu_discount_x_info.discount_info_cd = tsmenu_discount_info.discount_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info torder_discount_x_info ON torder_discount.discount_cd = torder_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info torder_discount_info ON torder_discount_x_info.discount_info_cd = torder_discount_info.discount_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND tsmenu_info.lan_tp_id = :lanId " + 
			"  AND tcategory_info.lan_tp_id = :lanId " + 
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " +
			"  AND (tsmenu_discount_info.lan_tp_id IS NULL OR tsmenu_discount_info.lan_tp_id = :lanId) " +
			"  AND (torder_discount_info.lan_tp_id IS NULL OR torder_discount_info.lan_tp_id = :lanId) " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY tsmenu_info.smenu_info_nm, tcategory_info.category_info_nm, tsmenu.price " +
			"ORDER BY SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) DESC, tsmenu_info.smenu_info_nm, tcategory_info.category_info_nm, tsmenu.price "
	)
	public List<Map<String, Object>> findStatisticsMenu(@Param("storeCd") Integer storeCd,
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 상품별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " + 
			"	, tsmenu_info.smenu_info_nm AS smenu_nm " + 
			"	, tcategory_info.category_info_nm AS category_nm " + 
			"	, tsmenu.price AS smenu_price " + 
			// 판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			"FROM tbl_order torder " + 
			" " +
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_info tsmenu_x_info ON tsmenu.smenu_cd = tsmenu_x_info.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_info tsmenu_info ON tsmenu_x_info.smenu_info_cd = tsmenu_info.smenu_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_category tcategory ON tsmenu.category_category_cd = tcategory.category_cd " + 
			"LEFT JOIN tbl_order_category_x_order_category_info tcategory_x_info ON tcategory.category_cd = tcategory_x_info.category_cd " + 
			"LEFT JOIN tbl_order_category_info tcategory_info ON tcategory_x_info.category_info_cd = tcategory_info.category_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info tsmenu_discount_x_info ON tsmenu_discount.discount_cd = tsmenu_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info tsmenu_discount_info ON tsmenu_discount_x_info.discount_info_cd = tsmenu_discount_info.discount_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info torder_discount_x_info ON torder_discount.discount_cd = torder_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info torder_discount_info ON torder_discount_x_info.discount_info_cd = torder_discount_info.discount_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND tsmenu_info.lan_tp_id = :lanId " + 
			"  AND tcategory_info.lan_tp_id = :lanId " + 
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " +
			"  AND (tsmenu_discount_info.lan_tp_id IS NULL OR tsmenu_discount_info.lan_tp_id = :lanId) " +
			"  AND (torder_discount_info.lan_tp_id IS NULL OR torder_discount_info.lan_tp_id = :lanId) " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND tsmenu_info.smenu_info_nm = :smenuNm " + 
			"  AND tcategory_info.category_info_nm = :categoryNm " + 
			"  AND tsmenu.price = :smenuPrice " + 
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY date_format(torder.order_date, :dateTp), tsmenu_info.smenu_info_nm, tcategory_info.category_info_nm, tsmenu.price " +
			"ORDER BY date_format(torder.order_date, :dateTp) "
	)
	public List<Map<String, Object>> findStatisticsMenuCnt(
			@Param("storeCd") Integer storeCd, @Param("smenuNm") String smenuNm,
			@Param("categoryNm") String categoryNm, @Param("smenuPrice") Integer smenuPrice, 
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 카테고리별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	tcategory_info.category_info_nm AS category_nm " + 
			// 판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			"FROM tbl_order torder " + 
			" " +
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_info tsmenu_x_info ON tsmenu.smenu_cd = tsmenu_x_info.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_info tsmenu_info ON tsmenu_x_info.smenu_info_cd = tsmenu_info.smenu_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_category tcategory ON tsmenu.category_category_cd = tcategory.category_cd " + 
			"LEFT JOIN tbl_order_category_x_order_category_info tcategory_x_info ON tcategory.category_cd = tcategory_x_info.category_cd " + 
			"LEFT JOIN tbl_order_category_info tcategory_info ON tcategory_x_info.category_info_cd = tcategory_info.category_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info tsmenu_discount_x_info ON tsmenu_discount.discount_cd = tsmenu_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info tsmenu_discount_info ON tsmenu_discount_x_info.discount_info_cd = tsmenu_discount_info.discount_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info torder_discount_x_info ON torder_discount.discount_cd = torder_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info torder_discount_info ON torder_discount_x_info.discount_info_cd = torder_discount_info.discount_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND tsmenu_info.lan_tp_id = :lanId " + 
			"  AND tcategory_info.lan_tp_id = :lanId " + 
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " +
			"  AND (tsmenu_discount_info.lan_tp_id IS NULL OR tsmenu_discount_info.lan_tp_id = :lanId) " +
			"  AND (torder_discount_info.lan_tp_id IS NULL OR torder_discount_info.lan_tp_id = :lanId) " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY tcategory_info.category_info_nm " +
			"ORDER BY SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) DESC, tcategory_info.category_info_nm "
	)
	public List<Map<String, Object>> findStatisticsCategory(@Param("storeCd") Integer storeCd,
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 카테고리별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " + 
			"	, tcategory_info.category_info_nm AS category_nm " + 
			// 판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			"FROM tbl_order torder " + 
			" " +
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_info tsmenu_x_info ON tsmenu.smenu_cd = tsmenu_x_info.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_info tsmenu_info ON tsmenu_x_info.smenu_info_cd = tsmenu_info.smenu_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_category tcategory ON tsmenu.category_category_cd = tcategory.category_cd " + 
			"LEFT JOIN tbl_order_category_x_order_category_info tcategory_x_info ON tcategory.category_cd = tcategory_x_info.category_cd " + 
			"LEFT JOIN tbl_order_category_info tcategory_info ON tcategory_x_info.category_info_cd = tcategory_info.category_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info tsmenu_discount_x_info ON tsmenu_discount.discount_cd = tsmenu_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info tsmenu_discount_info ON tsmenu_discount_x_info.discount_info_cd = tsmenu_discount_info.discount_info_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			"LEFT JOIN tbl_order_discount_x_order_discount_info torder_discount_x_info ON torder_discount.discount_cd = torder_discount_x_info.discount_cd " + 
			"LEFT JOIN tbl_order_discount_info torder_discount_info ON torder_discount_x_info.discount_info_cd = torder_discount_info.discount_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND tsmenu_info.lan_tp_id = :lanId " + 
			"  AND tcategory_info.lan_tp_id = :lanId " + 
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " +
			"  AND (tsmenu_discount_info.lan_tp_id IS NULL OR tsmenu_discount_info.lan_tp_id = :lanId) " +
			"  AND (torder_discount_info.lan_tp_id IS NULL OR torder_discount_info.lan_tp_id = :lanId) " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"  AND tcategory_info.category_info_nm = :categoryNm " + 
			"GROUP BY date_format(torder.order_date, :dateTp), tcategory_info.category_info_nm " +
			"ORDER BY date_format(torder.order_date, :dateTp) "
	)
	public List<Map<String, Object>> findStatisticsCategoryCnt(
			@Param("storeCd") Integer storeCd, @Param("categoryNm") String categoryNm,
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 옵션별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	tsmenu_opt_info.smenu_opt_info_nm AS opt_nm " + 
			"	, tsmenu_opt.cost AS smenu_opt_cost " +
			"	, tsmenu_opt.price AS smenu_opt_price " +
			
			//판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 옵션 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END) AS smenu_opt_cnt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			
			"FROM tbl_order torder " + 
			" " + 
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " + 
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"  AND tsmenu_opt.smenu_opt_cd IS NOT NULL " + 
			"GROUP BY tsmenu_opt_info.smenu_opt_info_nm, tsmenu_opt.price " + 
			"ORDER BY SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu_opt.cnt ELSE 0 END) DESC, tsmenu_opt_info.smenu_opt_info_nm, tsmenu_opt.price "
	)
	public List<Map<String, Object>> findStatisticsOpt(@Param("storeCd") Integer storeCd,
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 옵션별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " + 
			"	, tsmenu_opt_info.smenu_opt_info_nm AS opt_nm " + 
			"	, tsmenu_opt.price AS smenu_opt_price " +
			
			//판매구분별 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Store' = scode.val THEN tsmenu.cnt ELSE 0 END) AS store_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Packing' = scode.val THEN tsmenu.cnt ELSE 0 END) AS packing_cnt " + 
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 'Delivery' = scode.val THEN tsmenu.cnt ELSE 0 END) AS delivery_cnt" +
			// 옵션 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END) AS smenu_opt_cnt " +
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu_opt.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
			
			"FROM tbl_order torder " + 
			" " + 
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_x_order_smenu_opt_info tsmenu_opt_x_info ON tsmenu_opt.smenu_opt_cd = tsmenu_opt_x_info.smenu_opt_cd " + 
			"LEFT JOIN tbl_order_smenu_opt_info tsmenu_opt_info ON tsmenu_opt_x_info.smenu_opt_info_cd = tsmenu_opt_info.smenu_opt_info_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND (tsmenu_opt_info.lan_tp_id IS NULL OR tsmenu_opt_info.lan_tp_id = :lanId) " + 
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND tsmenu_opt_info.smenu_opt_info_nm = :optNm " +
			"  AND tsmenu_opt.price = :optPrice " +
			"  AND tsmenu_opt.smenu_opt_cd IS NOT NULL " +
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY date_format(torder.order_date, :dateTp), tsmenu_opt_info.smenu_opt_info_nm, tsmenu_opt.price " + 
			"ORDER BY date_format(torder.order_date, :dateTp) "
	)
	public List<Map<String, Object>> findStatisticsOptCnt(
			@Param("storeCd") Integer storeCd, @Param("optNm") String optNm, @Param("optPrice") Integer optPrice,
			@Param("lanId") String lanId, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 판매 방식별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	scode.cd, scode.nm " + 
			
			//메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " + 
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
						
			"FROM tbl_order torder " + 
			" " + 
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY scode.nm " + 
			"ORDER BY SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) DESC, scode.ord "
	)
	public List<Map<String, Object>> findStatisticsSales(
			@Param("storeCd") Integer storeCd, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	/**
	 * 판매 방식별 매출 현황
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " + 
			"	, scode.nm " + 
			
			//메뉴 수량
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS smenu_cnt " +
			// 메뉴 합계 금액
			"	, SUM(tsmenu.price * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) AS smenu_amt " +
			// 할인 금액
			"	, SUM(CASE WHEN 0 = tsmenu_discount.price THEN tsmenu.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) * tsmenu_discount.percente / 100 " +
			"              ELSE tsmenu_discount.price * (CASE WHEN 'N' = cancel_yn THEN tsmenu.cnt ELSE 0 END) END) AS discount_amt " + 
			// 옵션 금액
			"	, SUM(tsmenu_opt.price * (CASE WHEN 'N' = cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS smenu_opt_amt " +
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
			// 주문별 할인 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn AND 0 = torder_discount.price THEN torder.amt * torder_discount.percente / 100 ELSE torder_discount.price END) AS order_discount_amt " +
			// 취소 수량
			"	, SUM(CASE WHEN 'Y' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END) AS cancel_cnt " +
			// 이익 총금액 - 메뉴 원가 - 옵션 원가
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) - " +
			"     SUM(tsmenu.cost * (CASE WHEN 'N' = torder.cancel_yn THEN tsmenu.cnt ELSE 0 END)) - " +
			"     SUM(tsmenu_opt.cost * (CASE WHEN 'N' = torder.cancel_yn THEN (tsmenu.cnt * tsmenu_opt.cnt) ELSE 0 END)) AS profit_amt " + 
						
			"FROM tbl_order torder " + 
			" " + 
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_smenu torder_tsmenu ON torder.order_cd = torder_tsmenu.order_cd " + 
			"LEFT JOIN tbl_order_smenu tsmenu ON torder_tsmenu.smenu_cd = tsmenu.smenu_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_smenu_opt tsmenu_tsmenu_opt ON tsmenu.smenu_cd = tsmenu_tsmenu_opt.smenu_cd " + 
			"LEFT JOIN tbl_order_smenu_opt tsmenu_opt ON tsmenu_tsmenu_opt.smenu_opt_cd = tsmenu_opt.smenu_opt_cd " + 
			" " + 
			"LEFT JOIN tbl_order_smenu_x_order_discount tsmenu_tsmenu_discount ON tsmenu.smenu_cd = tsmenu_tsmenu_discount.smenu_cd " + 
			"LEFT JOIN tbl_order_discount tsmenu_discount ON tsmenu_tsmenu_discount.discount_cd = tsmenu_discount.discount_cd " + 
			" " + 
			"LEFT JOIN tbl_order_x_order_discount torder_torder_discount ON torder.order_cd = torder_torder_discount.order_cd " + 
			"LEFT JOIN tbl_order_discount torder_discount ON torder_torder_discount.discount_cd = torder_discount.discount_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"  AND scode.cd = :cd " + 
			"GROUP BY date_format(torder.order_date, :dateTp), scode.ord " + 
			"ORDER BY date_format(torder.order_date, :dateTp), scode.ord "
	)
	public List<Map<String, Object>> findStatisticsSalesCnt(
			@Param("storeCd") Integer storeCd, @Param("cd") Integer cd,
			@Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	
	/**
	 * 포스에서 일일 주문 금액 합계, 매출 구분별
	 * 
	 * @param dateTp
	 * @param startDt
	 * @param endDt
	 * @return 매출 현황
	 */
	@Query(nativeQuery = true, value = "" +
			"SELECT " + 
			"	date_format(torder.order_date, :dateTp) AS dt " + 
			"	, scode.val " +
			"	, scode.nm " + 
			
			// 주문 금액
			"	, SUM(CASE WHEN 'N' = torder.cancel_yn THEN torder.amt ELSE 0 END) AS order_amt " +
						
			"FROM tbl_order torder " + 
			" " + 
			"LEFT JOIN sys_code scode ON scode.cd = torder.sales_tp_cd " + 
			" " +
			"LEFT JOIN sys_code scode_statu ON scode_statu.cd = torder.order_status_cd " + 
			" " + 
			"WHERE torder.use_yn = 'Y' " +
			"  AND date_format(torder.order_date, :dateTp) >= :startDt " + 
			"  AND date_format(torder.order_date, :dateTp) <= :endDt " + 
			"  AND torder.store_store_cd = :storeCd " + 
			"  AND scode_statu.val = 'OVER' " + 
			"GROUP BY date_format(torder.order_date, :dateTp), scode.ord " + 
			"ORDER BY date_format(torder.order_date, :dateTp), scode.ord "
	)
	public List<Map<String, Object>> findStatisticsAmtSales(
			@Param("storeCd") Integer storeCd, @Param("dateTp") String dateTp,
			@Param("startDt") String startDt, @Param("endDt") String endDt);
	
	
	  Optional<Order> findByOrderId(String orderId);
	
}
