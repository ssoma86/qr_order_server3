package org.lf.app.models.tools.fileMan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.lf.app.models.Base;
import org.lf.app.models.tools.fileMan.FileManController.FileManControllerJsonView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 오류
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TOL_FILE_MAN")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class FileMan extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = 2574089499317851102L;

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView({ FileManControllerJsonView.class })
	private Integer seq;

	/** 풀더 */
	@Column(length = 30)
	@JsonView({ FileManControllerJsonView.class })
	private String folder;
	
	/** URL */
	@Column(length = 100, nullable = false)
	@JsonView({ FileManControllerJsonView.class })
	private String url;
	
	/** 파일 명 */
	@Column(length = 50)
	@JsonView({ FileManControllerJsonView.class })
	private String name;
	
	/** Suffix */
	@Column(length = 10)
	@JsonView({ FileManControllerJsonView.class })
	private String suffix;
	
	@Transient
	@JsonView({ FileManControllerJsonView.class })
	private String status = "success";
	
	
}
