package org.lf.app.models.business.member;

import java.util.HashSet;  
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.lf.app.models.business.address.Address;
import org.lf.app.models.business.cust.CustController.CustControllerCommonJsonView;
import org.lf.app.models.business.member.Member.MemberValid;
import org.lf.app.models.business.user.User;
import org.lf.app.models.business.user.UserService;
import org.lf.app.models.system.account.Account;
import org.lf.app.models.system.auth.Auth;
import org.lf.app.models.system.auth.AuthService;
import org.lf.app.models.system.code.CodeController.CodeControllerCommonJsonView;
import org.lf.app.models.system.lan.Lan;
import org.lf.app.models.system.lan.LanController.LanControllerCommonJsonView;
import org.lf.app.models.system.lan.LanController.LanControllerJsonView;
import org.lf.app.models.tools.fileMan.FileManController.FileManControllerJsonView;
import org.lf.app.models.tools.fileMan.FileManService;
import org.lf.app.models.tools.seqBuilder.SeqBuilderService;
import org.lf.app.utils.system.FileUtil;
import org.lf.app.utils.system.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import kr.co.infinisoft.menuplus.util.InnopayPasswordEncoder;

/**
 * 매장 관리
 * 
 * @author LF
 *
 */
@RestController
@Transactional
@SessionAttributes("sa_account")
@RequestMapping("/member")
public class MemberController {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private MemberService service;
	
	@Autowired
	private SeqBuilderService seqBuilderService;
	
	@Autowired
	private FileUtil fileUtil;
	
	@Autowired
	private FileManService fileManService;
	
	// JsonView
	public interface MemberControllerJsonView{}
		

		
	
	
	
	
 
	
	/**
	 * 삭제
	 * 
	 * @param storeCd
	 */
	
	
}
