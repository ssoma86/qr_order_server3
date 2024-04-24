package org.lf.app.models.tools.fileMan;

import javax.transaction.Transactional;

import org.lf.app.models.Base.BaseJsonView;
import org.lf.app.utils.system.LogUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 파일 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@RequestMapping("/fileMan")
public class FileManController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	public interface FileManControllerJsonView extends BaseJsonView {}
	
}