package org.lf.app.models.system.banner;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * 배너 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes({ "sa_account" })
@RequestMapping("/banner")
public class BannerController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private BannerService service;
	
	@Autowired
	private FileUtil fileUtil;
	
	
	// JsonView
	public interface BannerControllerJsonView extends BaseJsonView {}
	
	
	/**
	 * 추가
	 * 
	 * @param banner
	 * @throws Exception 
	 */
	@PostMapping
	public void add(@RequestBody Banner banner) throws Exception {
		
		if (!StringUtils.isEmpty(banner.getBannerImgFile())) {
			banner.setBannerImg(fileUtil.saveFile("banner", "BANNER", banner.getBannerImgFile()));
		}
		
		banner.setUseYn("N");
		
		service.save(banner);
	}
	
	
	/**
	 * 정보 조회
	 * 
	 * @param bannerCd
	 * @return 옵션 정보
	 */
	@GetMapping("/{bannerCd}")
	@JsonView(BannerControllerJsonView.class)
	public Banner get(@PathVariable Integer bannerCd) {
		return service.findOne(bannerCd);
	}
	
	
	/**
	 * 수정
	 * 
	 * @param banner
	 * @return 옵션 정보
	 * @throws Exception 
	 */
	@PutMapping("/{bannerCd}")
	public void up(@RequestBody Banner banner, @PathVariable Integer bannerCd) throws Exception {
		banner.setBannerCd(bannerCd);
		
		if (!StringUtils.isEmpty(banner.getBannerImgFile()) && banner.getBannerImgFile().contains("base64")) {
			banner.setBannerImg(fileUtil.delAndSaveFile(banner.getBannerImg(), "banner", "BANNER", banner.getBannerImgFile()));
		}
		
		service.save(banner);
	}
	
	
	/**
	 * 삭제
	 * 
	 * @param bannerCds
	 */
	@DeleteMapping("/{bannerCds}")
	public void del(@PathVariable Integer[] bannerCds) {
		service.useYn(bannerCds, "N");
	}
	
	
	/**
	 * 사용 설정
	 * 
	 * @param bannerCds
	 */
	@PatchMapping("/{bannerCd}/{useYn}")
	public void setting(@PathVariable Integer bannerCd, @PathVariable String useYn) {
		service.findOne(bannerCd).setUseYn(useYn);
	}
	
	
	/**
	 * 조회
	 * 
	 * @param banner
	 * @return 옵션 정보 리스트
	 */
	@GetMapping
	@JsonView(BannerControllerJsonView.class)
	public List<Banner> search(String useYn) {
		return service.findBannerList(useYn);
	}
	
	
	
}
