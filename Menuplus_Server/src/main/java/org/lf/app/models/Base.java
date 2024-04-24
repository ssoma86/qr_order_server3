package org.lf.app.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

/**
 * 모든 객체 공통 필드 설정
 * 
 * @author LF
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Data
public class Base implements Serializable {
	
	public interface BaseJsonView {}
	
	
	/*
	@Column(
		name = 可选，列名（默认值为属性名）。
		unique = 可选，是否在该列上设置唯一约束（默认false）。
		nullable = 可选，是否设置该列的值可以为空（默认true）。
		insertable = 可选，该列是否作为生成的insert语句中的一列（默认true）。
		updateable = 可选，该列是否作为生成的update语句中的一列（默认true）。
		length  = 可选，列长度（默认255）。
		precision = 可选，列十进制精度（默认0）。
		scale = 可选，如果列十进制数值范围可用，在此设置（默认0）。
	)*/
	
	
	/** 사용여부 */
	@Column(length = 1, nullable = false)
	@JsonView(BaseJsonView.class)
	public String useYn = "Y";
	
	/** Validator 구분 값 */
	@Transient
	public static final String SPLIT = "___";
	
	@Transient
	public static final String SPLIT_FILE = "_F_I_L_E_";
	
}
