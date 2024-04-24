package org.lf.app.models.system.dictionary;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.lf.app.models.Base;
import org.lf.app.models.system.code.Code;
import org.lf.app.models.system.dictionary.DictionaryController.DictionaryControllerJsonView;
import org.lf.app.models.system.lan.Lan;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 언어 데이타
 * 
 * @author LF
 * 
 */
@Entity
@Table(name = "SYS_DICTIONARY", indexes = { @Index(name = "DICTIONARY_INDEX_id_lan", columnList = "id,lan_id", unique = true) })
@Data
@EqualsAndHashCode(callSuper = false, exclude = { "lan", "dictionarys" })
@ToString(callSuper = false, exclude = { "lan", "dictionarys" })
public class Dictionary extends Base {

	/** serialVersionUID. */
	private static final long serialVersionUID = -1152988699453233716L;

	

	public interface DictionaryValid {}
	
	/** 코드 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(DictionaryControllerJsonView.class)
	private Integer cd;
	
	/** 값 */
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "언어 코드,{max}", groups = { DictionaryValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "언어 코드", groups = { DictionaryValid.class })
	@JsonView(DictionaryControllerJsonView.class)
	private String id;
	
	/** 내용 한국어 일때는 ID와 내용 동일 */
	@Column(length = 250, nullable = false)
	@Size(max = 250, message = "{0}는 {1}자 이내로 입력 하여 주십시오." + SPLIT + "언어,{max}", groups = { DictionaryValid.class })
	@NotBlank(message = "{0}는 필수 입력 항목입니다." + SPLIT + "언어", groups = { DictionaryValid.class })
	@JsonView(DictionaryControllerJsonView.class)
	private String nm;
	
	/** 구분 */
	@ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY)
	@Valid
	@JsonView(DictionaryControllerJsonView.class)
	private Code dictionaryTp;
	
	/** 언어 구분 */
	@ManyToOne(cascade = CascadeType.REFRESH)
	@Valid
	@JsonView(DictionaryControllerJsonView.class)
	private Lan lan;
	
	/** 언어 집합 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@Valid
	@JsonView(DictionaryControllerJsonView.class)
	private List<Dictionary> dictionarys;
	
	
}
