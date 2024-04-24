package org.lf.app.models.system.banner;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.system.banner.BannerController.BannerControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 배너 관리
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="SYS_BANNER")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Banner extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -6828317952767929473L;


	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView(BannerControllerJsonView.class)
	private Integer bannerCd;

	/** 이미지 */
	@Column(length = 100, nullable = false)
	@JsonView(BannerControllerJsonView.class)
	private String bannerImg;

	@Transient
	@JsonView(BannerControllerJsonView.class)
	private String bannerImgFile;
	
	
}
