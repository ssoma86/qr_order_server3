package org.If.app.models.business.pushAlarm;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.lf.app.models.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * push관리
 * 
 * @author LF
 * 
 */

@Component
public interface PushLogRepository extends BaseRepository<PushLog, Serializable> {
	
	
	
	
}
