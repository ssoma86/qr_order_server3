package org.lf.app.models;

import java.io.Serializable;
import java.util.List;

import org.lf.app.models.Base.BaseJsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * @JsonView와 기본 제공되는 Page<T> 같이 상요 시 충돌 문제 해결하기위한 별도 클래스
 * 
 * 
 * @author lwj
 *
 * @param <T>
 */
public class PageJsonView<T> implements Serializable {

	private static final long serialVersionUID = 7712654364248666397L;
	
	private Page<T> page;
	
	public PageJsonView(Page<T> page) {
		this.page = page;
	}

	@JsonView(BaseJsonView.class)
	public int getTotalPages() {
	    return page.getTotalPages();
	}

	@JsonView(BaseJsonView.class)
	public long getTotalElements() {
	    return page.getTotalElements();
	}

	@JsonView(BaseJsonView.class)
	public boolean hasNext() {
	    return page.hasNext();
	}

	@JsonView(BaseJsonView.class)
	public boolean isFirst() {
	    return page.isFirst();
	}
	
	@JsonView(BaseJsonView.class)
	public boolean isLast() {
	    return page.isLast();
	}

	@JsonView(BaseJsonView.class)
	public boolean hasContent() {
	    return page.hasContent();
	}

	@JsonView(BaseJsonView.class)
	public List<T> getContent() {
	    return page.getContent();
	}
	
	@JsonView(BaseJsonView.class)
	public Pageable getPageable() {
	    return page.getPageable();
	}
	
	@JsonView(BaseJsonView.class)
	public int getNumber() {
	    return page.getNumber();
	}
	
	@JsonView(BaseJsonView.class)
	public int getSize() {
	    return page.getSize();
	}
	
	@JsonView(BaseJsonView.class)
	public Sort getSort() {
	    return page.getSort();
	}
	
	@JsonView(BaseJsonView.class)
	public int getNumberOfElements() {
	    return page.getNumberOfElements();
	}
	
	@JsonView(BaseJsonView.class)
	public boolean isEmpty() {
	    return page.isEmpty();
	}
			
}
