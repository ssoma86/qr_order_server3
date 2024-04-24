package org.lf.app.models.system.error;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.lf.app.models.Base;
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
@Table(name="SYS_ERROR")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class Error extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -1028311258924063109L;

	/** 코드 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer seq;

	/** URL */
	@Column(length = 250)
	private String url;
	
	/** 복호화전 파라메타 */
	@Column(columnDefinition="LONGTEXT")
	private String session;
	
	/** 복호화전 파라메타 */
	@Column(columnDefinition="LONGTEXT")
	private String param;
	
	/** 파라메타 */
	@Column(columnDefinition="LONGTEXT")
	private String dparam;
	
	/** 오류 내용 */
	@Column(columnDefinition="LONGTEXT")
	private String errorMsg;
	
	/** 리턴 상태 값 */
	@Column(length = 3)
	private Integer status;
	
	/** 리턴 메세지 */
	@Column(columnDefinition="LONGTEXT")
	private String resultMsg;
	
	
}
