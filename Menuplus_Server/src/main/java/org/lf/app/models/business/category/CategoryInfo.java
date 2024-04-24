package org.lf.app.models.business.category;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.lf.app.models.Base;
import org.lf.app.models.business.category.CategoryController.CategoryControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 카테고리 언어별 상세 정보
 * 
 * @author LF
 * 
 */
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name="TBL_CATEGORY_INFO")
@Data
@EqualsAndHashCode(exclude={ "lanTp" })
@ToString(exclude={ "lanTp" })
public class CategoryInfo implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = 9151321244844677924L;

	
	public interface CategoryInfoValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ CategoryControllerJsonView.class })
	private Integer categoryInfoCd;

	/** 언어 구분 */
//	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@Valid
	@JsonView({ CategoryControllerJsonView.class })
	private Lan lanTp;
	
	/** 명 */
	@Column(length = 50, nullable = false)
	@NotBlank(message = "{0}은 필수 입력 항목입니다." + Base.SPLIT + "카테고리명", groups = { CategoryInfoValid.class })
	@Size(max = 50, message = "{0}은 {1}자 이내로 입력 하여 주십시오." + Base.SPLIT + "카테고리명,{max}", groups = { CategoryInfoValid.class })
	@JsonView({ CategoryControllerJsonView.class })
	private String categoryInfoNm;
	
	
}
