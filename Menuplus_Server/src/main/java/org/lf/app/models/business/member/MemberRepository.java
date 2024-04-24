package org.lf.app.models.business.member;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lf.app.models.BaseRepository;
import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.delivery.Delivery;
import org.lf.app.models.business.order.Order;
import org.lf.app.models.business.member.Member;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * 매장
 * 
 * @author LF
 * 
 */
public interface MemberRepository extends BaseRepository<Member, Serializable> {
	
	
	/**
	 * 휴대폰 번호로 주소 조회
	 * @param storeCd
	 * @param startDate
	 * @param endDate
	 * @return 주문 리스트
	 */
	@Query(nativeQuery = true,value=""
			+ "SELECT number, addr, addr_dtl "
			+ "FROM tbl_member_address "
			+ "WHERE tel = :hp "
			+ "order by number desc "
			+ "limit 5"
	)
	public List<Map<String, Object>> selectMemberPost(@Param("hp") String hp);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "INSERT INTO tbl_member_address (addr, addr_dtl, tel, use_yn) "
			+ "values(:addr, :addrDtl, :tel, 'Y') "

	)
	public void insertMemberPost(@Param("addr") String addr, @Param("addrDtl") String addrDtl, @Param("tel") String tel);
	
	@Modifying
	@Transactional
	@Query(nativeQuery = true,value=""
			+ "DELETE FROM tbl_member_address "
			+ "WHERE number = :cd "
	)
	
	
	public void delMemberPost(@Param("cd") Integer cd);
	
	@Query(nativeQuery = true,value=""
			+ "SELECT delivery_cost , a.address_cd,address_bub, address_heng, city "
			+ "FROM tbl_delivery a JOIN tbl_address b "
			+ "WHERE a.address_cd = b.address_cd "
			+ "AND store_cd = :cd "
	)
	public List<Map<String,Object>> selectDelivery(@Param("cd") Integer cd);
	
	
}
