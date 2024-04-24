package org.lf.app.models.tools.seqBuilder;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 키 관리
 * 
 * @author LF
 * 
 */
public interface SeqBuilderRepository extends JpaRepository<SeqBuilder, Serializable> {
	
}
