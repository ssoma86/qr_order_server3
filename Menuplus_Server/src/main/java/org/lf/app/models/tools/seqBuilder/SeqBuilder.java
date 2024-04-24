package org.lf.app.models.tools.seqBuilder;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 키 생성 관리
 * 
 * @author LF
 * 
 */
@Entity
@Table(name="TOL_SEQ_BUILD")
@Data
public class SeqBuilder implements Serializable {

	/** serialVersionUID. */
	private static final long serialVersionUID = -3434323099570292758L;

	/** 키 이름 */
	@Id
	private String seqName;

	/** 문자 */
	@Column(length = 5)
	private String prefix;
	
	/** 일자 yyyyMMdd */
	@Column(length = 20)
	private String datePrefix = "yy";
	
	/** 순번 */
	@Column(length = 50, nullable = false)
	private Long nextSeq = 0L;
	
	/** 문자를 제외한 키 사이즈 */
	@Column(length = 20, nullable = false)
	private Integer seqLen;
	
	/** PAD 문자 */
	@Column(length = 1, nullable = false)
	private String seqPad = "0";
	
}
