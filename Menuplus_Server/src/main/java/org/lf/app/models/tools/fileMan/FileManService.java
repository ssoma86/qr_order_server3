package org.lf.app.models.tools.fileMan;

import java.util.List;

import javax.transaction.Transactional;

import org.lf.app.models.BaseService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 파일관리
 * 
 * @author LF
 *
 */
@Service
@Transactional
public class FileManService extends BaseService<FileMan> {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private FileManRepository repository;
	
	@Autowired
	private FileUtil fileUtil;
	
	private static final String ready = "ready";
	private static final String change = "change";
	private static final String del = "del";
	
	/**
	 * 파일 저장
	 * @return
	 * @throws Exception 
	 */
	public List<FileMan> saveFiles(String folder, List<FileMan> fileManList) throws Exception {
		for (FileMan fileMan : fileManList) {
			if (fileMan.getStatus().equals(ready)) {
				saveFile(folder, fileMan);
			} else if (fileMan.getStatus().equals(change)) {
				changeFile(fileMan);
			} else if (fileMan.getStatus().equals(del)) {
				delFile(fileMan);
			}
		}
		
		return fileManList;
	}
	
	private void saveFile(String folder, FileMan fileMan) throws Exception {
		fileMan.setFolder(folder);
		fileMan.setUrl(fileUtil.saveFile(folder, "", fileMan.getUrl()));
		repository.save(fileMan);
	}
	
	private void changeFile(FileMan fileMan) {
		repository.save(fileMan);
	}
	
	private void delFile(FileMan fileMan) {
		fileMan.setUseYn("N");
		repository.save(fileMan);
	}
	
	
	
}
