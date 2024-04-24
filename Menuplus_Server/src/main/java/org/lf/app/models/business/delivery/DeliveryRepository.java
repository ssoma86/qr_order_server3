package org.lf.app.models.business.delivery;

import org.lf.app.models.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.io.Serializable;
import java.util.List;

public interface DeliveryRepository extends BaseRepository<Delivery, Serializable> {

 /**
  * 배달정보 삭제
  *
  * @param storeCd 
  */
 
 @Modifying
 @Query("DELETE "
         + "FROM Delivery "
         + "WHERE storeCd = :storeCd "
 )
 void deleteDelivery(@Param("storeCd") Integer storeCd);

 /**
  * 배달설정 주소 리스트 조회
  *
  * @param storeCd
  * @return 배달설정 주소 리스트
  */
 @Query("SELECT a , b " +
         "FROM Delivery a, Address b " +
         "WHERE a.addressCd = b.addressCd " +
         "AND a.storeCd = :storeCd " +
         "ORDER BY a.deliveryCd ASC"
 )
 List<Object[]> getDeliveryAddressList(@Param("storeCd") Integer storeCd);
 
}
