package org.lf.app.service.app;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.lf.app.models.business.appUser.AppUser;
import org.lf.app.models.business.appUser.AppUserService;
import org.lf.app.models.business.store.Store;
import org.lf.app.models.business.store.StoreService;
import org.lf.app.models.system.banner.Banner;
import org.lf.app.models.system.banner.BannerService;
import org.lf.app.models.system.lanData.LanDataService;
//import org.lf.app.service.app.AppOrderController.AppControllerJsonView;
import org.lf.app.utils.system.GpsUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 앱 회원 상점 정보 관련 API
 * @author lby
 *
 */
@RestController
@RequestMapping("/api/app")
public class AppStoreController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private StoreService storeService;
	
	@Autowired
    private AppUserService appUserService;
	
	@Autowired
	private LanDataService lanDataService;
	
	@Autowired
	private AppService appService;
	
	@Autowired
	private BannerService bannerService;
	
	
	
	// JsonView
	public interface AppControllerJsonViewForStoreList {}
	public interface AppControllerJsonViewForFavourite {}
	
	
	
	/**
	 * 구분별 상점 리스트 조회
	 * 
	 * @param lanId
	 * @param gpsLat
	 * @param gpsLng
	 * @return 상점 리스트 조회
	 */
//	@GetMapping("/getStoreListByTp/{lanId}/{storeTp}/{gpsLat}/{gpsLng}")
//	@JsonView(AppControllerJsonViewForStoreList.class)
//	public Map<String, List<Store>> getStoreListByTp(@PathVariable String lanId, @PathVariable String storeTp,
//			@PathVariable double gpsLat, @PathVariable double gpsLng, Principal principal) {
//		appService.chkAndSettingLan(lanId);
//		
//		Set<Store> favourites = null;
//		
//		if (principal != null) {
//			AppUser appUser = appUserService.findOne(principal.getName());
//			favourites = appUser.getFavourites();
//		}
//		
//		List<Store> storeList = null;
//		
//		if ("00".equals(storeTp)) {
//			storeList = storeService.findStoreList("Y");
//		} else {
//			storeList = storeService.findStoreList("Y", storeTp);
//		}
//		
//		for (Store store : storeList) {
//			storeService.settingStoreListDataForApp(store);
//			
//			// gps를 통해 거리 계산
//			store.setDistance(GpsUtil.getDistance(store.getGpsLat(), store.getGpsLng(), gpsLat, gpsLng));
//			
//			for (Store favouriteStore : favourites) {
//				if (favouriteStore.getStoreCd() == store.getStoreCd()) {
//					store.setFavourite(true);
//				}
//			}
//		}
//		
//		// 가까운 거리로 소트
//		Collections.sort(storeList, new Comparator<Store>() {
//			@Override
//			public int compare(Store s1, Store s2) {
//				double distance = s1.getDistance() - s2.getDistance();
//				
//				if (distance > 0) {
//					return 1;
//				} else if (distance < 0) {
//					return -1;
//				}
//				
//				return 0;
//			}
//		});
//		
//		Map<String, List<Store>> result = new HashMap<>();
//		result.put("storeList", storeList);
//		
//		
//		return result;
//	}
	
	
	
	/**
	 * 구분별 상점 리스트 조회
	 * 
	 * @param lanId
	 * @param gpsLat
	 * @param gpsLng
	 * @return 상점 리스트 조회
	 */
	@GetMapping("/getStoreList/{lanId}/{storeTp}/{gpsLat}/{gpsLng}/{storeCd}")
	@JsonView(AppControllerJsonViewForStoreList.class)
	public Map<String, Object> getStoreList(@PathVariable String lanId, @PathVariable String storeTp,
			@PathVariable double gpsLat, @PathVariable double gpsLng, @PathVariable Integer storeCd, Principal principal) {
		appService.chkAndSettingLan(lanId);
		
		Map<String, Object> result = new HashMap<>();
		List<Store> stores = new ArrayList<>();
		
		
		Set<Store> favourites = null;
		
		if (principal != null) {
			AppUser appUser = appUserService.findOne(principal.getName());
			favourites = appUser.getFavourites();
		}
		
		List<Store> storeList = null;
		
		if ("00".equals(storeTp)) {
			storeList = storeService.findStoreList("Y");
		} else {
			storeList = storeService.findStoreList("Y", storeTp);
		}
		
		for (Store store : storeList) {
			storeService.settingStoreListDataForApp(store);
			
			// gps를 통해 거리 계산
			store.setDistance(GpsUtil.getDistance(store.getGpsLat(), store.getGpsLng(), gpsLat, gpsLng));
			
			for (Store favouriteStore : favourites) {
				if (favouriteStore.getStoreCd() == store.getStoreCd()) {
					store.setFavourite(true);
				}
			}
		}
		
		// 가까운 거리로 소트
		Collections.sort(storeList, new Comparator<Store>() {
			@Override
			public int compare(Store s1, Store s2) {
				double distance = s1.getDistance() - s2.getDistance();
				
				if (distance > 0) {
					return 1;
				} else if (distance < 0) {
					return -1;
				}
				
				return 0;
			}
		});
		
		result.put("total", storeList.size());
		
		int jumpCnt = 0;
		boolean begin = 0 == storeCd ? true : false;
		
		for (Store storeTmp : storeList) {
			// 시작점 찾기
			if (!begin) {
				jumpCnt++;
				
				if (storeTmp.getStoreCd().equals(storeCd)) {
					begin = true;
				}
				continue;
			}
			
			// 한번에 30개 씩 내려줌
			if (stores.size() >= 30) {
				break;
			}
			
			jumpCnt++;
			
			stores.add(storeTmp);
		}
		
		result.put("list", stores);
		result.put("remain", storeList.size() - jumpCnt);
		
		return result;
	}
	
	
	
	/**
	 * 상점 데이타 조회
	 * @param clientTpVal
	 * @return 언어 데이타 리스트
	 */
//	@GetMapping("/getStore/{storeId}/{lanId}")
//	@Transactional
//	//@JsonView(AppControllerJsonView.class)
//	public Store getStoreInfo(@PathVariable String storeId, @PathVariable String lanId, Principal principal) {
//		appService.chkAndSettingLan(lanId);
//		
//		// 찜 조회
//		Set<Store> favourites = null;
//		
//		if (principal != null) {
//			AppUser appUser = appUserService.findOne(principal.getName());
//			favourites = appUser.getFavourites();
//		}
//		
//		Store store = storeService.findOneByStoreId(storeId);
//		
//		if (!ObjectUtils.isEmpty(store)) {
//			storeService.settingStoreDataForApp(store);	// 앱에서 필요한 데이타 설정
//			
//			// 찜 설정
//			for (Store favouriteStore : favourites) {
//				if (favouriteStore.getStoreCd() == store.getStoreCd()) {
//					store.setFavourite(true);
//				}
//			}
//		}
//		
//		return store;
//	}
	
	
	
	/**
	 * 배너 조회
	 * @return 배너 이미지 주소
	 */
//	@GetMapping("/getBanner")
//	public List<String> getBanner() {
//		List<String> result = new ArrayList<>();
//		
//		List<Banner> bannerList = bannerService.findBannerList("Y");
//		
//		for (Banner banner : bannerList) {
//			result.add(banner.getBannerImg());
//		}
//		
//		return result;
//	}
	
	
	/**
	 * 찜 정보 추가
	 * @param storeId
	 * @throws Exception 
	 */
//	@PostMapping("/favourite/{storeId}")
//	@Transactional
//	public void favourite(@PathVariable String storeId, Principal principal, HttpServletResponse res) throws Exception {
//		if (principal != null) {
//			AppUser appUser = appUserService.findOne(principal.getName());
//			
//			Set<Store> favourites = appUser.getFavourites();
//			
//			// 찜 정보 있는지 체크 없으면 새로 생성
//			if (ObjectUtils.isEmpty(favourites)) {
//				favourites = new HashSet<>();
//			}
//			
//			Store store = storeService.findOneByStoreId(storeId);
//			
//			// 상점 있는지 체크
//			if (ObjectUtils.isEmpty(store)) {
//				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//				res.setCharacterEncoding("UTF-8");
//				res.setContentType("application/json; charset=utf-8");
//				
//				Map<String, Object> errorAttribute = new HashMap<String, Object>();
//				errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
//				errorAttribute.put("error", "Bad Request");
//				
//				List<Map<String, Object>> errorsMessages = new ArrayList<>();
//				
//				Map<String, Object> errorsMessage = new HashMap<String, Object>();
//				errorsMessage.put("code", "storeIdIsNotExist");
//				errorsMessage.put("field", "storeId");
//				errorsMessage.put("value", storeId);
//				errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
//				
//				errorsMessages.add(errorsMessage);
//				
//				errorAttribute.put("errorsMessage", errorsMessages);
//				errorAttribute.put("message", "Validation failed for storeId. Error count: 1");
//				errorAttribute.put("path", "/api/app/favourite/" + storeId);
//				
//				ObjectMapper om = new ObjectMapper();
//				
//				String json = om.writeValueAsString(errorAttribute);
//				
//				res.getWriter().print(json);
//			} else {
//				favourites.add(store);
//				appUser.setFavourites(favourites);
//				
//				appUserService.save(appUser);
//			}
//		}
//	}
	
	
	/**
	 * 찜 리스트 가져오기
	 * @return 찜 리스트
	 */
//	@GetMapping("/favourite")
//	@JsonView(AppControllerJsonViewForFavourite.class)
//	public Map<String, Set<Store>> getFavourite(Principal principal) {
//		
//		Map<String, Set<Store>> result = new HashMap<>();
//		
//		Set<Store> favourites = null;
//		
//		if (principal != null) {
//			AppUser appUser = appUserService.findOne(principal.getName());
//			favourites = appUser.getFavourites();
//		}
//		
//		favourites.forEach(store -> {
//			storeService.settingStoreListDataForApp(store);
//		});
//		
//		result.put("storeList", favourites);
//		
//		return result;
//	}
	
	
	/**
	 * 찜 정보 추가
	 * @param storeId
	 * @throws Exception 
	 */
//	@PostMapping("/delFavourite/{storeId}")
//	@Transactional
//	public void delFavourite(@PathVariable String storeId, Principal principal, HttpServletResponse res) throws Exception {
//		if (principal != null) {
//			AppUser appUser = appUserService.findOne(principal.getName());
//			
//			Store store = storeService.findOneByStoreId(storeId);
//			
//			// 상점 있는지 체크
//			if (ObjectUtils.isEmpty(store)) {
//				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//				res.setCharacterEncoding("UTF-8");
//				res.setContentType("application/json; charset=utf-8");
//				
//				Map<String, Object> errorAttribute = new HashMap<String, Object>();
//				errorAttribute.put("timestamp", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
//				errorAttribute.put("status", HttpServletResponse.SC_BAD_REQUEST);
//				errorAttribute.put("error", "Bad Request");
//				
//				List<Map<String, Object>> errorsMessages = new ArrayList<>();
//				
//				Map<String, Object> errorsMessage = new HashMap<String, Object>();
//				errorsMessage.put("code", "storeIdIsNotExist");
//				errorsMessage.put("field", "storeId");
//				errorsMessage.put("value", storeId);
//				errorsMessage.put("errorMsg", lanDataService.getLanData("매장 정보가 존재 하지 않습니다.", LocaleContextHolder.getLocale()));
//				
//				errorsMessages.add(errorsMessage);
//				
//				errorAttribute.put("errorsMessage", errorsMessages);
//				errorAttribute.put("message", "Validation failed for storeId. Error count: 1");
//				errorAttribute.put("path", "/api/app/favourite/" + storeId);
//				
//				ObjectMapper om = new ObjectMapper();
//				
//				String json = om.writeValueAsString(errorAttribute);
//				
//				res.getWriter().print(json);
//			} else {
//				Set<Store> favourites = appUser.getFavourites();
//				
//				// 찜 정보 있는지 체크 없으면 새로 생성
//				if (!ObjectUtils.isEmpty(favourites)) {
//					favourites.removeIf(storeTmp -> store.getStoreId().equals(storeTmp.getStoreId()));
//				}
//				
//				appUser.setFavourites(favourites);
//				
//				appUserService.save(appUser);
//			}
//		}
//	}
	
	
}
