package org.lf.app.models.business.store;

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
import org.lf.app.models.business.delivery.DeliveryRepository;
import org.lf.app.models.business.discount.Discount;
import org.lf.app.models.business.discount.DiscountService;
import org.lf.app.models.business.smenu.Smenu;
import org.lf.app.models.business.smenuOpt.SmenuOpt;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.lanData.LanDataService;
import org.lf.app.service.web.WebAppController;
import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.query.Param;
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
public class StoreService extends BaseService<Store> {
	
	private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StoreRepository repository;
	
	@Autowired
	private DiscountService discountService;
	
	@Autowired
	private LanDataService lanDataService;

	public void setDelivery(List<Object[]> list, Store store) {
		
		List<Address> delivery0 = new ArrayList<>();
		List<Address> delivery1 = new ArrayList<>();
		List<Address> delivery2 = new ArrayList<>();
		List<Address> delivery3 = new ArrayList<>();
		List<Address> delivery4 = new ArrayList<>();
		List<Address> delivery5 = new ArrayList<>();

		for (Object[] o : list){
			switch(((Delivery) o[0]).getDeliveryCost().toString()){
				case "0" : delivery0.add(((Address) o[1])); break;
				case "1000" : delivery1.add(((Address) o[1])); break;
				case "2000" : delivery2.add(((Address) o[1])); break;
				case "3000" : delivery3.add(((Address) o[1])); break;
				case "4000" : delivery4.add(((Address) o[1])); break;
				case "5000" : delivery5.add(((Address) o[1])); break;
			}
		}

		store.setDelivery0(delivery0);
		store.setDelivery1(delivery1);
		store.setDelivery2(delivery2);
		store.setDelivery3(delivery3);
		store.setDelivery4(delivery4);
		store.setDelivery5(delivery5);
	}

	/**
	 * 매장 리스트 조회
	 * 
	 * @param custCd
	 * @param storeNm
	 * @param useYn
	 * @return 매장 리스트 정보
	 */
	public List<Store> findStoreList(Integer custCd, String storeNm, String useYn) {
		return repository.findStoreList(custCd, storeNm, useYn);
	}
	
	
	/**
	 * 매장 유형별 매장 리스트 조회
	 * 
	 * @param useYn
	 * @return 매장 리스트 정보
	 */
	public List<Store> findStoreList(String useYn, String storeTp) {
		return repository.findByUseYnAndStoreTpStrContainingOrderByStoreId(useYn, storeTp);
	}
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param useYn
	 * @return 매장 리스트 정보
	 */
	public List<Store> findStoreList(String useYn) {
		return repository.findStoreList(useYn);
	}
	
	
	/**
	 * 매장 리스트 조회
	 * 
	 * @param storeId
	 * @return 매장 정보
	 */
	public Store findOneByStoreId(String storeId) {
		return repository.findOneByStoreIdAndUseYn(storeId, "Y");
	}
	
	
	/**
	 * 상점 정보 설정
	 * 다국어
	 * 코드 등
	 */
	public void settingStoreData(Store store) {
		setTimeData(store);	// 시간 표시 설정
		setSniffling(store);// 휴무 설정
		setHoliday(store);	// 휴무 설정
		setSalesNm(store);	// 판매방식 설정
		setLanNms(store);	// 언어 설정
	}
	
	
	/**
	 * 상점 리스트 정보 설정
	 * 다국어
	 * 코드 등
	 */
	public void settingStoreListDataForApp(Store store) {
		setPayTps(store);		// 결제 구분 다국어 설정
		setStoreImgList(store);	// 매장 이미지 리스트 설정
		setTimeData(store);		// 영업 시간 설정
		setSniffling(store);	// 휴무 설정
		setHoliday(store);		// 휴무 설정
		setOpen(store);			// 영업 상태 설정
		setSalesNm(store);		// 판매 방식 설정
		setDiscounts(store, "app");	// 주문별 할인 설정
	}
	
	
	/**
	 * 상점 정보 설정
	 * 다국어
	 * 코드 등
	 */
	public void settingStoreDataForApp(Store store) {
		setPayTps(store);		// 결제 구분 다국어 설정
		setStoreImgList(store);	// 매장 이미지 리스트 설정
		setTimeData(store);		// 영업 시간 설정
		setSniffling(store);	// 휴무 설정
		setHoliday(store);		// 휴무 설정
		setOpen(store);			// 영업 상태 설정
		setSmenuOptTps(store);	// 옵션 타입 설정
		setSalesNm(store);		// 판매 방식 설정
		setStopSelling(store);	// 판매 정지 구분
		setDiscounts(store, "app");	// 주문별 할인 설정
	}
	
	
	/**
	 * 상점 정보 설정
	 * 다국어
	 * 코드 등
	 */
	public void settingStoreDataForWeb(Store store) {
		setPayTps(store);		// 결제 구분 다국어 설정
		setStoreImgList(store);	// 매장 이미지 리스트 설정
		setTimeData(store);		// 영업 시간 설정
		setSniffling(store);	// 휴무 설정
		setHoliday(store);		// 휴무 설정
		setVactionDays(store);  // 기타휴무 설정
		setOpen(store);			// 영업 상태 설정
		setSmenuOptTpsWeb(store);	// 옵션 타입 설정
		setSalesNm(store);		// 판매 방식 설정
		setStopSelling(store);	// 판매 정지 구분
		setDiscounts(store, "qr");	// 주문별 할인 설정
	}
	
	
	/**
	 * 결제 구분 다국어 설정
	 * @param store
	 */
	public void setPayTps(Store store) {
		for (Code payTp : store.getPayTps()) {
			payTp.setNmLan(lanDataService.getLanData(payTp.getNm(), LocaleContextHolder.getLocale()));
		}
	}
	
	
	/**
	 * 매장 이미지 설정
	 * @param store
	 */
	public void setStoreImgList(Store store) {
		List<String> imgList = new ArrayList<>();
		
		if (!StringUtils.isEmpty(store.getStoreImg())) {
			imgList.add(store.getStoreImg());
		}
		
		if (!StringUtils.isEmpty(store.getStoreImg1())) {
			imgList.add(store.getStoreImg1());
		}
		
		if (!StringUtils.isEmpty(store.getStoreImg2())) {
			imgList.add(store.getStoreImg2());
		}
		
		if (!StringUtils.isEmpty(store.getStoreImg3())) {
			imgList.add(store.getStoreImg3());
		}
		
		if (!StringUtils.isEmpty(store.getStoreImg4())) {
			imgList.add(store.getStoreImg4());
		}
		
		if (!StringUtils.isEmpty(store.getStoreImg5())) {
			imgList.add(store.getStoreImg5());
		}
		
		String[] imgs = new String[imgList.size()];
		
		for (int i = 0, len = imgList.size(); i < len; i++) {
			imgs[i] = imgList.get(i);
		}
		
		store.setStoreImgList(imgs);
	}
	
	
	
	/**
	 * 매장 지원 언어 텍스트 표시
	 * @param store
	 */
	public void setLanNms(Store store) {
		String lanNms = "";
		
		if (!StringUtils.isEmpty(store.getDefaultLan())) {
			lanNms = store.getDefaultLan().getNm();
		}
		
		if (!StringUtils.isEmpty(store.getLans())) {
			for (int i = 0, len = store.getLans().size(); i < len; i++) {
				lanNms += "," + store.getLans().get(i).getNm();
			}
		}
		
		store.setLanNms(lanNms);
	}
	
	
	
	/**
	 * 시간 설정
	 * @param store
	 */
	public void setTimeData(Store store) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		
		List<String> breakTmList = new ArrayList<>();	// Break 시간 리스트
		
		if (!ObjectUtils.isEmpty(store.getStoreTimes())) {
			// 설정된 시간 리스트
			List<StoreTime> storeTimes = store.getStoreTimes();
			
			String openTmTmp = "";	// 오픈 시간
			String breakTmTmp = "";	// Break 시간
			
			List<Map<String, Object>> openTmTmps = new ArrayList<>();	// 오픈 시간 리스트
			List<Map<String, Object>> breakTmTmps = new ArrayList<>();	// Break 시간 리스트
			
			// 상세 설정 일때, 일주일  간 설정으로 7개 리스트
			if (store.isDetailYn()) {
				for (int i = 0, len = storeTimes.size(); i < len; i++) {
					
					StoreTime storeTime = storeTimes.get(i);
					
					// 첫 행 데이타 설정
					if (openTmTmps.size() == 0) {
						Map<String, Object> tmp = new HashMap<>();
						tmp.put("week", "[" + storeTime.getWeek() + "]");
						tmp.put("startTm", ObjectUtils.isEmpty(storeTime.getStartTm()) ? "" : sdf.format(storeTime.getStartTm()));
						tmp.put("endTm", ObjectUtils.isEmpty(storeTime.getEndTm()) ? "" : sdf.format(storeTime.getEndTm()));
						tmp.put("dayYn", storeTime.isDayYn());
						
						openTmTmps.add(tmp);
					} else {
						boolean isExist = false;
						
						// 같은 시간 설정인지 저장된 값리스트로 체크해서 중복이면 주정보만 추가 해줌
						for (int j = 0, jlen = openTmTmps.size(); j < jlen; j++) {
							// 24시간 설정
							if (storeTime.isDayYn()) {
								// 24시간 설정값 비교
								if (storeTime.isDayYn() == MapUtils.getBooleanValue(openTmTmps.get(j), "dayYn")) {
									isExist = true;
									openTmTmps.get(j).put("week", MapUtils.getString(openTmTmps.get(j), "week") + "[" + storeTime.getWeek() + "]");
								}
							} else {
								// 시간 설정 일때 비교
								if (sdf.format(storeTime.getStartTm()).equals(MapUtils.getString(openTmTmps.get(j), "startTm", null)) &&
										sdf.format(storeTime.getEndTm()).equals(MapUtils.getString(openTmTmps.get(j), "endTm", null))) {
									isExist = true;
									openTmTmps.get(j).put("week", MapUtils.getString(openTmTmps.get(j), "week") + "[" + storeTime.getWeek() + "]");
								}
							}
						}
						
						// 같은 시간 설정이 없을 시 새로 한줄 추가
						if (!isExist) {
							Map<String, Object> tmp = new HashMap<>();
							tmp.put("week", "[" + storeTime.getWeek() + "]");
							tmp.put("startTm", ObjectUtils.isEmpty(storeTime.getStartTm()) ? "" : sdf.format(storeTime.getStartTm()));
							tmp.put("endTm", ObjectUtils.isEmpty(storeTime.getEndTm()) ? "" : sdf.format(storeTime.getEndTm()));
							tmp.put("dayYn", storeTime.isDayYn());
							
							openTmTmps.add(tmp);
						}
					}
					
					breakTmTmp = "";
					// BreakTm1
					if (!ObjectUtils.isEmpty(storeTime.getBreakStartTm1()) && !ObjectUtils.isEmpty(storeTime.getBreakEndTm1())) {
						breakTmTmp = (ObjectUtils.isEmpty(storeTime.getBreakStartTm1()) ? "" : sdf.format(storeTime.getBreakStartTm1())) + " ~ " +
								(ObjectUtils.isEmpty(storeTime.getBreakEndTm1()) ? "" : sdf.format(storeTime.getBreakEndTm1()));
					}
					
					// BreakTm2
					if (!ObjectUtils.isEmpty(storeTime.getBreakStartTm2()) && !ObjectUtils.isEmpty(storeTime.getBreakEndTm2())) {
						// BreakTm1 이 있을 시
						if (!StringUtils.isEmpty(breakTmTmp)) {
							breakTmTmp += ", ";
						}
						breakTmTmp += (ObjectUtils.isEmpty(storeTime.getBreakStartTm2()) ? "" : sdf.format(storeTime.getBreakStartTm2())) + " ~ " +
								(ObjectUtils.isEmpty(storeTime.getBreakEndTm2()) ? "" : sdf.format(storeTime.getBreakEndTm2()));
					}
					
					// BreakTm 설정이 있을 시
					if (!StringUtils.isEmpty(breakTmTmp)) {
						// 첫행 일 시
						if (breakTmTmps.size() == 0) {
							Map<String, Object> tmp = new HashMap<>();
							tmp.put("week", "[" + storeTime.getWeek() + "]");
							tmp.put("breakTm", breakTmTmp);
							
							breakTmTmps.add(tmp);
						} else {
							boolean isExist = false;
							// 이미 등록된 같은 시간 타임이 있는지 체크
							for (int j = 0, jlen = breakTmTmps.size(); j < jlen; j++) {
								if (breakTmTmp.equals(MapUtils.getString(breakTmTmps.get(j), "breakTm", null))) {
									isExist = true;
									breakTmTmps.get(j).put("week", MapUtils.getString(breakTmTmps.get(j), "week") + "[" + storeTime.getWeek() + "]");
								}
							}
							
							// 같은 시간 설정이 없을 시 새로 한줄 추가
							if (!isExist) {
								Map<String, Object> tmp = new HashMap<>();
								tmp.put("week", "[" + storeTime.getWeek() + "]");
								tmp.put("breakTm", breakTmTmp);
								
								breakTmTmps.add(tmp);
							}
						}
					}
				}
			// 간편 설정 일때
			} else {
				Map<String, Object> openTmMapTmp = new HashMap<>();
				Map<String, Object> breakTmMapTmp = new HashMap<>();
				
				String breakTmTmp1 = "";
				String breakTmTmp2 = "";
				
				// 주중 시간 데이타 설정
				// 오픈 타임 설정(주중)
				openTmMapTmp.put("week", "[1][2][3][4][5]");
				openTmMapTmp.put("startTm", ObjectUtils.isEmpty(storeTimes.get(0).getStartTm()) ? "" : sdf.format(storeTimes.get(0).getStartTm()));
				openTmMapTmp.put("endTm", ObjectUtils.isEmpty(storeTimes.get(0).getEndTm()) ? "" : sdf.format(storeTimes.get(0).getEndTm()));
				openTmMapTmp.put("dayYn", storeTimes.get(0).isDayYn());
				
				openTmTmps.add(openTmMapTmp);
				
				// BreakTm 설정
				// BreakTm1
				if (!ObjectUtils.isEmpty(storeTimes.get(0).getBreakStartTm1()) && !ObjectUtils.isEmpty(storeTimes.get(0).getBreakEndTm1())) {
					breakTmTmp1 = (ObjectUtils.isEmpty(storeTimes.get(0).getBreakStartTm1()) ? "" : sdf.format(storeTimes.get(0).getBreakStartTm1())) + " ~ " +
							(ObjectUtils.isEmpty(storeTimes.get(0).getBreakEndTm1()) ? "" : sdf.format(storeTimes.get(0).getBreakEndTm1()));
				}
				
				// BreakTm2
				if (!ObjectUtils.isEmpty(storeTimes.get(0).getBreakStartTm2()) && !ObjectUtils.isEmpty(storeTimes.get(0).getBreakEndTm2())) {
					
					if (!StringUtils.isEmpty(breakTmTmp1)) {
						breakTmTmp1 += ", ";
					}
					breakTmTmp1 += (ObjectUtils.isEmpty(storeTimes.get(0).getBreakStartTm2()) ? "" : sdf.format(storeTimes.get(0).getBreakStartTm2())) + " ~ " +
							(ObjectUtils.isEmpty(storeTimes.get(0).getBreakEndTm2()) ? "" : sdf.format(storeTimes.get(0).getBreakEndTm2()));
				}
				
				if (!StringUtils.isEmpty(breakTmTmp1)) {
					breakTmMapTmp.put("week", "[1][2][3][4][5]");
					breakTmMapTmp.put("breakTm", breakTmTmp1);
					
					breakTmTmps.add(breakTmMapTmp);
				}
				
				// 주말 설정
				// 24 시간 설정인지 체크
				if (storeTimes.get(1).isDayYn()) {
					// 24 시간 이면 24 컬럼 비교
					if (storeTimes.get(1).isDayYn() == storeTimes.get(0).isDayYn()) {
						openTmMapTmp.put("week", MapUtils.getString(openTmMapTmp, "week") + "[6][7]");
					}
				} else {
					// 주중이 24시간으로 설정 되 있는지 체크, 시간 설정이 같은지 체크
					if (!storeTimes.get(0).isDayYn() &&
							sdf.format(storeTimes.get(1).getStartTm()).equals(sdf.format(storeTimes.get(0).getStartTm())) &&
							sdf.format(storeTimes.get(1).getEndTm()).equals(sdf.format(storeTimes.get(0).getEndTm()))) {
						openTmMapTmp.put("week", MapUtils.getString(openTmMapTmp, "week") + "[6][7]");
					} else {
						Map<String, Object> tmp1 = new HashMap<>();
						tmp1.put("week", "[6][7]");
						tmp1.put("startTm", ObjectUtils.isEmpty(storeTimes.get(1).getStartTm()) ? "" : sdf.format(storeTimes.get(1).getStartTm()));
						tmp1.put("endTm", ObjectUtils.isEmpty(storeTimes.get(1).getEndTm()) ? "" : sdf.format(storeTimes.get(1).getEndTm()));
						tmp1.put("dayYn", storeTimes.get(1).isDayYn());
						
						openTmTmps.add(tmp1);
					}
				}
				
				// BreakTm 설정
				// BreakTm1
				if (!ObjectUtils.isEmpty(storeTimes.get(1).getBreakStartTm1()) && !ObjectUtils.isEmpty(storeTimes.get(1).getBreakEndTm1())) {
					breakTmTmp2 = (ObjectUtils.isEmpty(storeTimes.get(1).getBreakStartTm1()) ? "" : sdf.format(storeTimes.get(1).getBreakStartTm1())) + " ~ " +
							(ObjectUtils.isEmpty(storeTimes.get(1).getBreakEndTm1()) ? "" : sdf.format(storeTimes.get(1).getBreakEndTm1()));
				}
				
				// BreakTm2
				if (!ObjectUtils.isEmpty(storeTimes.get(1).getBreakStartTm2()) && !ObjectUtils.isEmpty(storeTimes.get(1).getBreakEndTm2())) {
					
					if (!StringUtils.isEmpty(breakTmTmp2)) {
						breakTmTmp2 += ", ";
					}
					breakTmTmp2 += (ObjectUtils.isEmpty(storeTimes.get(1).getBreakStartTm2()) ? "" : sdf.format(storeTimes.get(1).getBreakStartTm2())) + " ~ " +
							(ObjectUtils.isEmpty(storeTimes.get(1).getBreakEndTm2()) ? "" : sdf.format(storeTimes.get(1).getBreakEndTm2()));
				}
				
				// 두번째 BreakTm2가 있을 시
				if (!StringUtils.isEmpty(breakTmTmp2)) {
					// 첫 BreakTm1이 없을 시
					if (StringUtils.isEmpty(breakTmTmp1)) {
						Map<String, Object> tmp1 = new HashMap<>();
						tmp1.put("week", "[6][7]");
						tmp1.put("breakTm", breakTmTmp2);
						
						breakTmTmps.add(tmp1);
					} else {
						if (breakTmTmp1.equals(breakTmTmp2)) {
							breakTmTmps.get(0).put("week", MapUtils.getString(breakTmTmps.get(0), "week") + "[6][7]");
						} else {
							Map<String, Object> tmp1 = new HashMap<>();
							tmp1.put("week", "[6][7]");
							tmp1.put("breakTm", breakTmTmp2);
							
							breakTmTmps.add(tmp1);
						}
					}
				}
				
			}
			
			// OpenTm 문구 설정
			for (int i = 0, len = openTmTmps.size(); i < len; i++) {
				openTmTmp += MapUtils.getString(openTmTmps.get(i), "week") + 
						(MapUtils.getBooleanValue(openTmTmps.get(i), "dayYn") ? (i > 0 ? " " : "") + "dayYn" : " " + 
							MapUtils.getString(openTmTmps.get(i), "startTm") + " ~ " + MapUtils.getString(openTmTmps.get(i), "endTm")) + ", ";
			}
			
			store.setOpenTm(openTmTmp.length() > 2 ? openTmTmp.substring(0, openTmTmp.length() - 2).trim() : "");
			
			breakTmTmp = "";
			// BreakTm 문구 설정
			for (int i = 0, len = breakTmTmps.size(); i < len; i++) {
				breakTmTmp += MapUtils.getString(breakTmTmps.get(i), "week") + " " + MapUtils.getString(breakTmTmps.get(i), "breakTm") + ", ";
				
				// Break tm 앱에서 별도 리스트로 설정
				String breakTm = MapUtils.getString(breakTmTmps.get(i), "week");
				// 전체 시간 통일 할떄
				breakTm = breakTm.replace("[1][2][3][4][5][6][7]", "").trim();
				breakTm = breakTm.replace("[1][2][3][4][5][6]", lanDataService.getLanData("주중.토", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[1][2][3][4][5][7]", lanDataService.getLanData("주중.일", LocaleContextHolder.getLocale()));
				
				// 주말 포함 아닐 시
				if (!breakTm.contains("[1][2][3][4][5][")) {
					breakTm = breakTm.replace("[1][2][3][4][5]", lanDataService.getLanData("주중", LocaleContextHolder.getLocale()));
				}
				
				// 주중 포함 아닐 시
				if (!breakTm.contains("][6][7]")) {
					breakTm = breakTm.replace("[6][7]", lanDataService.getLanData("주말", LocaleContextHolder.getLocale()));
				}
				
				breakTm = breakTm.replace("[1]", lanDataService.getLanData("월", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[2]", lanDataService.getLanData("화", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[3]", lanDataService.getLanData("수", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[4]", lanDataService.getLanData("목", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[5]", lanDataService.getLanData("금", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[6]", lanDataService.getLanData("토", LocaleContextHolder.getLocale()));
				breakTm = breakTm.replace("[7]", lanDataService.getLanData("일", LocaleContextHolder.getLocale()));
				
				breakTmList.add((breakTm + " " + MapUtils.getString(breakTmTmps.get(i), "breakTm")).trim());
			}
			
			store.setBreakTm(breakTmTmp.length() > 2 ? breakTmTmp.substring(0, breakTmTmp.length() - 2).trim() : "");
		}
		
		// 다국어 변환
		String openTm = store.getOpenTm();
		// 전체 시간 통일 할떄
		openTm = openTm.replace("[1][2][3][4][5][6][7]", "").trim();
		openTm = openTm.replace("[1][2][3][4][5][6]", lanDataService.getLanData("주중.토", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[1][2][3][4][5][7]", lanDataService.getLanData("주중.일", LocaleContextHolder.getLocale()));
		
		// 주말 포함 아닐 시
		if (!openTm.contains("[1][2][3][4][5][")) {
			openTm = openTm.replace("[1][2][3][4][5]", lanDataService.getLanData("주중", LocaleContextHolder.getLocale()));
		}
		
		// 주중 포함 아닐 시
		if (!openTm.contains("][6][7]")) {
			openTm = openTm.replace("[6][7]", lanDataService.getLanData("주말", LocaleContextHolder.getLocale()));
		}
		
		openTm = openTm.replace("[1]", lanDataService.getLanData("월", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[2]", lanDataService.getLanData("화", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[3]", lanDataService.getLanData("수", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[4]", lanDataService.getLanData("목", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[5]", lanDataService.getLanData("금", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[6]", lanDataService.getLanData("토", LocaleContextHolder.getLocale()));
		openTm = openTm.replace("[7]", lanDataService.getLanData("일", LocaleContextHolder.getLocale()));
		
		openTm = openTm.replaceAll("dayYn", lanDataService.getLanData("24시간", LocaleContextHolder.getLocale()));
		
		store.setOpenTm(openTm);
		
		// BreakTm 설정
		String breakTm = store.getBreakTm();
		
		// 전체 시간 통일 할떄
		breakTm = breakTm.replace("[1][2][3][4][5][6][7]", "").trim();
		breakTm = breakTm.replace("[1][2][3][4][5][6]", lanDataService.getLanData("주중.토", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[1][2][3][4][5][7]", lanDataService.getLanData("주중.일", LocaleContextHolder.getLocale()));
		
		// 주말 포함 아닐 시
		if (!breakTm.contains("[1][2][3][4][5][")) {
			breakTm = breakTm.replace("[1][2][3][4][5]", lanDataService.getLanData("주중", LocaleContextHolder.getLocale()));
		}
		
		// 주중 포함 아닐 시
		if (!breakTm.contains("][6][7]")) {
			breakTm = breakTm.replace("[6][7]", lanDataService.getLanData("주말", LocaleContextHolder.getLocale()));
		}
		
		breakTm = breakTm.replace("[1]", lanDataService.getLanData("월", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[2]", lanDataService.getLanData("화", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[3]", lanDataService.getLanData("수", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[4]", lanDataService.getLanData("목", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[5]", lanDataService.getLanData("금", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[6]", lanDataService.getLanData("토", LocaleContextHolder.getLocale()));
		breakTm = breakTm.replace("[7]", lanDataService.getLanData("일", LocaleContextHolder.getLocale()));
		
		store.setBreakTm(breakTm);
		store.setBreakTmList(breakTmList);
		// 월화수목금토일 기준
	}
	
	
	/**
	 * 휴무 컬럼 설정 (23.08.11) New
	 * @param store
	 */
	public void setHoliday(Store store) {
		String holiday = "";
		
		List<StoreTime> holidays = store.getStoreTimes();
		
		for(StoreTime st : holidays) {
			
			logger.info("===============st.isHoliday():" + st.isHoliday());
			
			if(st.isHoliday()) {
				switch (st.getWeek()) {
					case 1: holiday += "," + lanDataService.getLanData("월", LocaleContextHolder.getLocale()); break;
					case 2: holiday += "," + lanDataService.getLanData("화", LocaleContextHolder.getLocale()); break;
					case 3: holiday += "," + lanDataService.getLanData("수", LocaleContextHolder.getLocale()); break;
					case 4: holiday += "," + lanDataService.getLanData("목", LocaleContextHolder.getLocale()); break;
					case 5: holiday += "," + lanDataService.getLanData("금", LocaleContextHolder.getLocale()); break;
					case 6: holiday += "," + lanDataService.getLanData("토", LocaleContextHolder.getLocale()); break;
					case 7: holiday += "," + lanDataService.getLanData("일", LocaleContextHolder.getLocale()); break;	
					default: break;
				}
			}
		}
		
		if(holiday!="") {
			holiday += lanDataService.getLanData("요일 휴무", LocaleContextHolder.getLocale());
		}else {
			holiday += "없음";
		}
		
		logger.info("===========holiday:" + holiday);
		
		store.setHoliday(holiday);
		
	}
	
	/**
	 * 휴무 컬럼 설정(old 예전버전)
	 * @param store
	 */
	public void setHoliday2(Store store) {
		String holiday = "";
		
		if (!StringUtils.isEmpty(store.getTp())) {
			// 요일별
			if ("week".equals(store.getTp())) {
				if (!ObjectUtils.isEmpty(store.getWeeks())) {
					// 매주
					if ("ew".equals(store.getWeekTp())) {
						holiday = lanDataService.getLanData("매주", LocaleContextHolder.getLocale()) + " ";
					// 격주
					} else {
						holiday = lanDataService.getLanData("격주", LocaleContextHolder.getLocale()) + " ";
					}
				
					for (Integer week : store.getWeeks()) {
						switch (week) {
						case 1: holiday += lanDataService.getLanData("월", LocaleContextHolder.getLocale()); break;
						case 2: holiday += lanDataService.getLanData("화", LocaleContextHolder.getLocale()); break;
						case 3: holiday += lanDataService.getLanData("수", LocaleContextHolder.getLocale()); break;
						case 4: holiday += lanDataService.getLanData("목", LocaleContextHolder.getLocale()); break;
						case 5: holiday += lanDataService.getLanData("금", LocaleContextHolder.getLocale()); break;
						case 6: holiday += lanDataService.getLanData("토", LocaleContextHolder.getLocale()); break;
						case 7: holiday += lanDataService.getLanData("일", LocaleContextHolder.getLocale()); break;
						default: break;
						}
					}
					
					if ("bw".equals(store.getWeekTp())) {
						holiday += "(" + (!store.getSniffling() ? lanDataService.getLanData("이번주", LocaleContextHolder.getLocale()) : lanDataService.getLanData("다음주", LocaleContextHolder.getLocale())) + ") ";
					}
				}
			// 일자별
			} else {
				if (!ObjectUtils.isEmpty(store.getHolidayDays())) {
					for (Integer day : store.getHolidayDays()) {
						if ("".equals(holiday)) {
							holiday += "" + day;
						} else {
							holiday += "," + day;
						}
					}
					
					holiday += lanDataService.getLanData("일 휴무", LocaleContextHolder.getLocale());
				}
			}
			
			store.setHoliday(holiday);
		}
	}
	
	/**
	 * 기타휴무컬럼 설정
	 * @param store
	 */
	public void setVactionDays(Store store) {
		String vactionDays = "";
		
		
		if (!ObjectUtils.isEmpty(store.getVactionDays())) {
			for (String day : store.getVactionDays()) {
				if ("".equals(vactionDays)) {
					vactionDays += "" + day;
				} else {
					vactionDays += "," + day;
				}
			}
			
			vactionDays += lanDataService.getLanData("일 휴무", LocaleContextHolder.getLocale());
		}
			
			store.setVactionDay(vactionDays);
	}
	
	/**
	 * 오픈 상태 컬럼 설정
	 * @param store
	 */
	public void setOpen(Store store) {
		boolean isOpen = true;
		
		Calendar calendar = Calendar.getInstance();
		
		// 휴무일 체크
		if (!StringUtils.isEmpty(store.getTp())) {
			// 요일별
			if ("week".equals(store.getTp())) {
				if (!ObjectUtils.isEmpty(store.getWeeks())) {
					int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
	
					if (0 == day_of_week) {
						day_of_week = 7;
					}
					
					// 매주
					if ("ew".equals(store.getWeekTp())) {
						for (Integer week : store.getWeeks()) {
							if (week == day_of_week) {
								isOpen = false;
								break;
							}
						}
					// 격주
					} else {
						if ("bw".equals(store.getWeekTp())) {
							if (!store.getSniffling()) {
								for (Integer week : store.getWeeks()) {
									if (week == day_of_week) {
										isOpen = false;
										break;
									}
								}
							}
						}
					}
				}
			// 일자별
			} else {
				if (!ObjectUtils.isEmpty(store.getHolidayDays())) {
					for (Integer day : store.getHolidayDays()) {
						if (day == calendar.get(Calendar.DAY_OF_MONTH)) {
							isOpen = false;
							break;
						}
					}
				}
			}
		}
		
		// 기타 휴무 체크
		if (!ObjectUtils.isEmpty(store.getVactionDays())) {
			for (String day : store.getVactionDays()) {
				if (day.equals(DateUtil.dateToStr(calendar.getTime()))) {
					isOpen = false;
					break;
				}
			}
		}
		
		// 시간 체크
		if (isOpen && !ObjectUtils.isEmpty(store.getStoreTimes())) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			
			Calendar now = Calendar.getInstance();
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			
//			log.print("now = " + sdf.format(now.getTime()));
			
			int week = now.get(Calendar.DAY_OF_WEEK) - 1;

			if (0 == week) {
				week = 7;
			}
			
			if (!store.isDetailYn()) {
				if (week < 6) {
					week = 1;
				} else {
					week = 2;
				}
			}
			
			for (int i = 0, len = store.getStoreTimes().size(); i < len; i++) {
				StoreTime storeTime = store.getStoreTimes().get(i);
				
				if (storeTime.getWeek() == week) {
					
					if (storeTime.isDayYn()) {
						isOpen = true;
					} else {
						if (!ObjectUtils.isEmpty(storeTime.getStartTm()) && !ObjectUtils.isEmpty(storeTime.getEndTm())) {
							start.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getStartTm()).substring(0, 2)));
							start.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getStartTm()).substring(3, 5)));
							end.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getEndTm()).substring(0, 2)));
							end.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getEndTm()).substring(3, 5)));
							
//							log.print("now = " + sdf.format(now.getTime()));
//							log.print("start = " + sdf.format(storeTime.getStartTm()).substring(0, 2));
//							log.print("start = " + sdf.format(storeTime.getStartTm()).substring(3, 5));
//							log.print("start = " + sdf.format(start.getTime()));
//							log.print("end = " + sdf.format(storeTime.getEndTm()).substring(0, 2));
//							log.print("end = " + sdf.format(storeTime.getEndTm()).substring(3, 5));
//							log.print("end = " + sdf.format(end.getTime()));
							
							if (now.after(start) && now.before(end)) {
								isOpen = true;
							} else {
								isOpen = false;
							}
						}
					}
					
					if (!ObjectUtils.isEmpty(storeTime.getBreakStartTm1()) && !ObjectUtils.isEmpty(storeTime.getBreakEndTm1())) {
						start.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getBreakStartTm1()).substring(0, 2)));
						start.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getBreakStartTm1()).substring(3, 5)));
						end.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getBreakEndTm1()).substring(0, 2)));
						end.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getBreakEndTm1()).substring(3, 5)));
						
//						log.print("now = " + sdf.format(now.getTime()));
//						log.print("start = " + sdf.format(storeTime.getBreakStartTm1()).substring(0, 2));
//						log.print("start = " + sdf.format(storeTime.getBreakStartTm1()).substring(3, 5));
//						log.print("start = " + sdf.format(start.getTime()));
//						log.print("end = " + sdf.format(storeTime.getBreakEndTm1()).substring(0, 2));
//						log.print("end = " + sdf.format(storeTime.getBreakEndTm1()).substring(3, 5));
//						log.print("end = " + sdf.format(end.getTime()));
						
						if (now.after(start) && now.before(end)) {
							isOpen = false;
						}
					}
					
					if (!ObjectUtils.isEmpty(storeTime.getBreakStartTm2()) && !ObjectUtils.isEmpty(storeTime.getBreakEndTm2())) {
						start.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getBreakStartTm2()).substring(0, 2)));
						start.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getBreakStartTm2()).substring(3, 5)));
						end.set(Calendar.HOUR, Integer.parseInt(sdf.format(storeTime.getBreakEndTm2()).substring(0, 2)));
						end.set(Calendar.MINUTE, Integer.parseInt(sdf.format(storeTime.getBreakEndTm2()).substring(3, 5)));
						
						if (now.after(start) && now.before(end)) {
							isOpen = false;
						}
					}
				}
			}
		}
		
		store.setOpen(isOpen);
	}
	
	
	
	/**
	 * 옵션 설정
	 * @param store
	 */
	public void setSmenuOptTps(Store store) {
		for (Category category : store.getCategorys()) {
			for (Smenu menu : category.getSmenus()) {
				// 옵션 그룹
				List<Map<String, Object>> smenuOptTps = new ArrayList<>();
				// 판단용 임시 변수
				List<Integer> optTpCds = new ArrayList<>();
				
				// 옵션 그룹 설정
				for (SmenuOpt smenuOpt : menu.getSmenuOpts()) {
					// 사용 가능한 데이타만 표시
					if ("Y".equals(smenuOpt.getUseYn()) && "Y".equals(smenuOpt.getSmenuOptTp().getUseYn())) {
						// 저장 체크
						if (!optTpCds.contains(smenuOpt.getSmenuOptTp().getSmenuOptTpCd())) {
							
							optTpCds.add(smenuOpt.getSmenuOptTp().getSmenuOptTpCd());
							
							Map<String, Object> smenuOptTp = new HashMap<>();
							smenuOptTp.put("smenuOptTpCd", smenuOpt.getSmenuOptTp().getSmenuOptTpCd());
							smenuOptTp.put("ord", smenuOpt.getSmenuOptTp().getOrd());
							smenuOptTp.put("smenuOptTpMaxCnt", smenuOpt.getSmenuOptTp().getSmenuOptTpMaxCnt()); //최대 옵션 선택 개수 추가(2023.07.03)
							smenuOptTp.put("smenuOptTpNmLan", smenuOpt.getSmenuOptTp().getSmenuOptTpNmLan());
							smenuOptTp.put("optTpCd", smenuOpt.getSmenuOptTp().getOptTpCd());
							smenuOptTp.put("optTpVal", smenuOpt.getSmenuOptTp().getOptTpVal());
							
							List<Map<String, Object>> smenuOpts = new ArrayList<>();
							List<Integer> optCds = new ArrayList<>();
							
							for (SmenuOpt smenuOpt2 : menu.getSmenuOpts()) {
								if ("Y".equals(smenuOpt2.getUseYn())) {
									if (smenuOpt.getSmenuOptTp().getSmenuOptTpCd() == smenuOpt2.getSmenuOptTp().getSmenuOptTpCd()) {
										if (!optCds.contains(smenuOpt2.getSmenuOptCd())) {
											optCds.add(smenuOpt2.getSmenuOptCd());
											
											Map<String, Object> smenuOptTmp = new HashMap<>();
											smenuOptTmp.put("smenuOptCd", smenuOpt2.getSmenuOptCd());
											smenuOptTmp.put("ord", smenuOpt2.getOrd());
											smenuOptTmp.put("smenuOptNmLan", smenuOpt2.getSmenuOptNmLan());
											smenuOptTmp.put("priceLan", smenuOpt2.getPriceLan());
											
											smenuOpts.add(smenuOptTmp);
										}
									}
								}
							}
							
							smenuOptTp.put("smenuOpts", smenuOpts);
							
							smenuOptTps.add(smenuOptTp);
						}
					}
				}
				
				menu.setSmenuOptTps(smenuOptTps);
			}
		}
	}
	
	
	
	/**
	 * 옵션 설정
	 * @param store
	 */
	public void setSmenuOptTpsWeb(Store store) {
		for (Category category : store.getCategorys()) {
			for (Smenu menu : category.getSmenus()) {
				// 옵션 그룹
				List<Map<String, Object>> smenuOptTps = new ArrayList<>();
				// 판단용 임시 변수
				List<Integer> optTpCds = new ArrayList<>();
				
				// 옵션 그룹 설정
				for (SmenuOpt smenuOpt : menu.getSmenuOpts()) {
					// 사용 가능한 데이타만 표시
					if ("Y".equals(smenuOpt.getUseYn()) && "Y".equals(smenuOpt.getSmenuOptTp().getUseYn())) {
						// 저장 체크
						if (!optTpCds.contains(smenuOpt.getSmenuOptTp().getSmenuOptTpCd())) {
							
							optTpCds.add(smenuOpt.getSmenuOptTp().getSmenuOptTpCd());
							
							Map<String, Object> smenuOptTp = new HashMap<>();
							smenuOptTp.put("smenuOptTpCd", smenuOpt.getSmenuOptTp().getSmenuOptTpCd());
							smenuOptTp.put("ord", smenuOpt.getSmenuOptTp().getOrd());
							smenuOptTp.put("smenuOptTpMaxCnt", smenuOpt.getSmenuOptTp().getSmenuOptTpMaxCnt()); //최대 옵션 선택 개수 추가(2023.07.03)
							smenuOptTp.put("smenuOptTpNmLan", smenuOpt.getSmenuOptTp().getSmenuOptTpNmLan());
							smenuOptTp.put("optTpCd", smenuOpt.getSmenuOptTp().getOptTpCd());
							smenuOptTp.put("optTpVal", smenuOpt.getSmenuOptTp().getOptTpVal());
							
							List<Map<String, Object>> smenuOpts = new ArrayList<>();
							List<Integer> optCds = new ArrayList<>();
							
							for (SmenuOpt smenuOpt2 : menu.getSmenuOpts()) {
								if ("Y".equals(smenuOpt2.getUseYn())) {
									if (smenuOpt.getSmenuOptTp().getSmenuOptTpCd() == smenuOpt2.getSmenuOptTp().getSmenuOptTpCd()) {
										if (!optCds.contains(smenuOpt2.getSmenuOptCd())) {
											optCds.add(smenuOpt2.getSmenuOptCd());
											
											Map<String, Object> smenuOptTmp = new HashMap<>();
											smenuOptTmp.put("smenuOptCd", smenuOpt2.getSmenuOptCd());
											smenuOptTmp.put("ord", smenuOpt2.getOrd());
											smenuOptTmp.put("smenuOptNmLan", smenuOpt2.getSmenuOptNmLan());
											smenuOptTmp.put("priceLan", smenuOpt2.getPriceLan());
											smenuOptTmp.put("checked", false);
											smenuOptTmp.put("cnt", 0);
											
											smenuOpts.add(smenuOptTmp);
										}
									}
								}
							}
							
							smenuOptTp.put("smenuOpts", smenuOpts);
							
							smenuOptTps.add(smenuOptTp);
						}
					}
				}
				
				menu.setSmenuOptTps(smenuOptTps);
			}
		}
	}
	
	
	/**
	 * 판매 방식 표시 컬럼 설정
	 * @param store
	 */
	public void setSalesNm(Store store) {
		
		// 웹에서 판매 방식
		for (Code webSalesTp : store.getWebSalesTps()) {
			webSalesTp.setNmLan(lanDataService.getLanData(webSalesTp.getNm(), LocaleContextHolder.getLocale()));
			store.setWebSalesTpNms(store.getWebSalesTpNms() +
					("".equals(store.getWebSalesTpNms()) ? "" : ", ") +
					lanDataService.getLanData(webSalesTp.getNm(), LocaleContextHolder.getLocale()));
		}
		
		// 앱에서 판매 방식
		for (Code appSalesTp : store.getAppSalesTps()) {
			appSalesTp.setNmLan(lanDataService.getLanData(appSalesTp.getNm(), LocaleContextHolder.getLocale()));
			store.setAppSalesTpNms(store.getAppSalesTpNms() +
					("".equals(store.getAppSalesTpNms()) ? "" : ", ") +
					lanDataService.getLanData(appSalesTp.getNm(), LocaleContextHolder.getLocale()));
		}
		
	}
	
	
	
	/**
	 * 판매정지 사유 다국어 설정
	 * @param store
	 */
	public void setStopSelling(Store store) {
		store.getCategorys().forEach(category -> {
			category.getSmenus().forEach(menu -> {
				// 판매정지 사유 다국어 설정
				if (menu.isStopSelling() && !ObjectUtils.isEmpty(menu.getStopReason())) {
					if ("other".equals(menu.getStopReason().getVal())) {
						menu.setStopReasonNm(lanDataService.getLanData(menu.getOtherReason(), LocaleContextHolder.getLocale()));
					} else {
						menu.setStopReasonNm(lanDataService.getLanData(menu.getStopReason().getNm(), LocaleContextHolder.getLocale()));
					}
				}
			});
		});
	}
	
	
	
	/**
	 * 주문별 할인 설정
	 * @param store
	 */
	public void setDiscounts(Store store, String clientVal) {
		
		List<Discount> discountList = discountService.findByTargetAndClient(store.getStoreCd(), clientVal);
		
		// 이후에 삭제
		discountList.forEach(discount -> {
			discount.getDiscountTp().setNmLan(lanDataService.getLanData(discount.getDiscountTp().getNm(), LocaleContextHolder.getLocale()));
			
			discount.getSalesTps().forEach(sales -> {
				sales.setNmLan(lanDataService.getLanData(sales.getNm(), LocaleContextHolder.getLocale()));
			});
		});
		
		store.setDiscounts(discountList);
		
		
		// 앱에서만 개별 데이타 구조 변경
		List<Map<String, Object>> storeDiscountPrice = new ArrayList<>();
		
		discountList.forEach(discount -> {
			discount.getSalesTps().forEach(sales -> {
				sales.setNmLan(lanDataService.getLanData(sales.getNm(), LocaleContextHolder.getLocale()));
				
				int num = -1;
				
				for (int i = 0, len = storeDiscountPrice.size(); i < len; i++) {
					if (storeDiscountPrice.get(i).get("salesTpCd") == sales.getCd()) {
						num = i;
						break;
					}
				}
				
				// 할인 금액 계산
				int price = 0;
				double percente = 0.0;
				
				if ("price".equals(discount.getDiscountTp().getVal())) {
					price = discount.getPriceLan();
				} else {
					percente = Double.valueOf(discount.getPercenteLan()) / 100;
				}
				
				if (num > -1) {
					int dPrice = ((Integer)storeDiscountPrice.get(num).get("discountPrice")) + price;
					double dPercente = ((Double)storeDiscountPrice.get(num).get("discountPercente")) + percente;
					
					storeDiscountPrice.get(num).put("discountPrice", dPrice);
					storeDiscountPrice.get(num).put("discountPercente", dPercente);
					((List<String>)storeDiscountPrice.get(num).get("discountTpVal")).add(discount.getDiscountTp().getVal());
					((List<String>)storeDiscountPrice.get(num).get("discountNm")).add(discount.getDiscountNmLan());
					((List<Integer>)storeDiscountPrice.get(num).get("discountCd")).add(discount.getDiscountCd());
				} else {
					Map<String, Object> tmp = new HashMap<>();
					tmp.put("salesTpCd", sales.getCd());
					tmp.put("salesTpVal", sales.getVal());
					
					if ("price".equals(discount.getDiscountTp().getVal())) {
						tmp.put("discountPrice", discount.getPriceLan());
						tmp.put("discountPercente", 0.0);
					} else {
						tmp.put("discountPrice", 0);
						tmp.put("discountPercente", Double.valueOf(discount.getPercenteLan()) / 100);
					}
					
					List<String> discountTpVals = new ArrayList<>();
					discountTpVals.add(discount.getDiscountTp().getVal());
					tmp.put("discountTpVal", discountTpVals);
					
					List<String> discountNms = new ArrayList<>();
					discountNms.add(discount.getDiscountNmLan());
					tmp.put("discountNm", discountNms);
					
					List<Integer> discountCds = new ArrayList<>();
					discountCds.add(discount.getDiscountCd());
					tmp.put("discountCd", discountCds);
					
					tmp.put("unit", store.getUnit());
					
					storeDiscountPrice.add(tmp);
				}
				
			});
		});
		
		store.setDiscountPrice(storeDiscountPrice);
		
		
		// 앱에 내려주는 할인 정보 특별 처리
		store.getCategorys().forEach(category -> {
			category.getSmenus().forEach(menu -> {
				
				// 앱에서만 개별 데이타 구조 변경
				List<Map<String, Object>> discountPrice = new ArrayList<>();
				
				menu.getDiscounts().forEach(discount -> {
					discount.getSalesTps().forEach(sales -> {
						sales.setNmLan(lanDataService.getLanData(sales.getNm(), LocaleContextHolder.getLocale()));
						
						int num = -1;
						
						for (int i = 0, len = discountPrice.size(); i < len; i++) {
							if (discountPrice.get(i).get("salesTpCd") == sales.getCd()) {
								num = i;
								break;
							}
						}
						
						// 할인 금액 계산
						int price = 0;
						
						if ("price".equals(discount.getDiscountTp().getVal())) {
							price = discount.getPriceLan();
						} else {
							price = menu.getPriceLan() * discount.getPercenteLan() / 100;
						}
						
						if (num > -1) {
							int dPrice = ((Integer)discountPrice.get(num).get("discountPrice")) - price;
							
							if (dPrice < 0) {
								dPrice = 0;
							}
							
							discountPrice.get(num).put("discountPrice", dPrice);
							((List<String>)discountPrice.get(num).get("discountNm")).add(discount.getDiscountNmLan());
							((List<Integer>)discountPrice.get(num).get("discountCd")).add(discount.getDiscountCd());
						} else {
							Map<String, Object> tmp = new HashMap<>();
							tmp.put("salesTpCd", sales.getCd());
							tmp.put("salesTpVal", sales.getVal());
							
							int dPrice = menu.getPriceLan() - price;
							
							if (dPrice < 0) {
								dPrice = 0;
							}
							tmp.put("discountPrice", dPrice);
							
							List<String> discountNms = new ArrayList<>();
							discountNms.add(discount.getDiscountNmLan());
							tmp.put("discountNm", discountNms);
							
							List<Integer> discountCds = new ArrayList<>();
							discountCds.add(discount.getDiscountCd());
							tmp.put("discountCd", discountCds);
							
							discountPrice.add(tmp);
						}
						
					});
				});
				
				menu.setDiscountPrice(discountPrice);
			});
		});
		
	}
	
	
	public void setWeekSniffling(Store store) {
		if (!ObjectUtils.isEmpty(store.getWeeks())) {
			Collections.sort(store.getWeeks(), new Comparator<Integer>() {
				@Override
				public int compare(Integer s1, Integer s2) {
					if (s1 > s2) {
						return 1;
					} else if (s1 < s2) {
						return -1;
					}
					
					return 0;
				}
			});
		}
		
		if (!ObjectUtils.isEmpty(store.getHolidayDays())) {
			Collections.sort(store.getHolidayDays(), new Comparator<Integer>() {
				@Override
				public int compare(Integer s1, Integer s2) {
					if (s1 > s2) {
						return 1;
					} else if (s1 < s2) {
						return -1;
					}
					
					return 0;
				}
			});
		}
		
		
//		if (!ObjectUtils.isEmpty(store.getVactionDays())) {
//			Collections.sort(store.getVactionDays(), new Comparator<String>() {
//				@Override
//				public int compare(String s1, String s2) {
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//					Date d1;
//					Date d2;
//					try {
//						d1 = sdf.parse(s1);
//						d2 = sdf.parse(s2);
//						
//						if (d1.before(d2)) {
//							return 1;
//						} else if (d1.after(d2)) {
//							return -1;
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					
//					return 0;
//				}
//			});
//			
//		}
		
		try {
			Date ref = new SimpleDateFormat("yyyy-MM-dd").parse("2019-10-28");
			int day = (int)((new Date().getTime() - ref.getTime()) / (3600 * 1000 * 24));
			
			if (((day / 7) % 2) == 0) {
				store.setWeekSniffling(store.getSniffling());
			} else {
				store.setWeekSniffling(!store.getSniffling());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	public void setSniffling(Store store) {
		try {
			Date ref = new SimpleDateFormat("yyyy-MM-dd").parse("2019-10-28");
			int day = (int)((new Date().getTime() - ref.getTime()) / (3600 * 1000 * 24));
			
			if (((day / 7) % 2) == 0) {
				store.setSniffling(ObjectUtils.isEmpty(store.getWeekSniffling()) ? true : store.getWeekSniffling());
			} else {
				store.setSniffling(ObjectUtils.isEmpty(store.getWeekSniffling()) ? true : !store.getWeekSniffling());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 지역주소 리스트 조회
	 *
	 * @param
	 * @return 지역주소 리스트
	 */
	public List<Address> findAddrList(String city) {
		return repository.findAddrList(city);
	}
	
	/**
	 * 스토어에 해당하는 design(주문자화면 가로,세로 보기)값 가져오기 (23.08.09)
	 * @param storeCd
	 * @return
	 */
	public String getDesign4Store(int storeCd) {
		return repository.getDesign4Store(storeCd);
	}
	
	/**
	 * 주문자화면 가로,세로 보기 update(23.08.09)
	 * @param storeCd
	 * @param design
	 */
	public void updateDesign(int storeCd, String design ){
		repository.updateDesign(storeCd, design);
	}
	
}