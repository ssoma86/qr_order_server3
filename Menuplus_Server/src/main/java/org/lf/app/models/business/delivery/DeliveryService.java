package org.lf.app.models.business.delivery;

import org.lf.app.models.BaseService;
import org.lf.app.models.business.store.Store;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class DeliveryService extends BaseService<Delivery> {

 @SuppressWarnings("unused")
 private LogUtil log = new LogUtil(getClass());

 @Autowired
 private DeliveryRepository repository;

 /**
  * 지역 주소 저장
  * 
  * @param deliveryList
  */
 public void saveDeliveryList(List<Delivery> deliveryList){
  repository.saveAll(deliveryList);
 }

 /**
  * 배달설정 주소 리스트 조회
  *
  * @param storeCd
  * @return
  */
 public List<Object[]> getDeliveryAddressList(Integer storeCd){
  return repository.getDeliveryAddressList(storeCd);
 }
 
 public void deleteDelivery(Integer storeCd){
  repository.deleteDelivery(storeCd);
 }
  
}
