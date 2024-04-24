package org.lf.app.models.tools.seqBuilder;

import java.util.Date;
import java.util.Optional;

import org.lf.app.utils.system.DateUtil;
import org.lf.app.utils.system.LogUtil;
import org.lf.app.utils.system.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;



/**
 * 키 관리
 * 
 * @author LF
 *
 */
@Service
public class SeqBuilderService {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	@Autowired
	private SeqBuilderRepository repository;
	
	
	public String getSeq(String seqName, String prefix, String datePrefix, int seqLen, String seqPad, long nextStep) {
		StringBuilder sb = new StringBuilder();
		
		SeqBuilder seqBuilder = null;
		
		Optional<SeqBuilder> optionalSeqBuilder = repository.findById(seqName);
		
		if (optionalSeqBuilder.isPresent()) {
			seqBuilder = optionalSeqBuilder.get();
			seqBuilder.setNextSeq(seqBuilder.getNextSeq() + nextStep);
		} else {
			seqBuilder = new SeqBuilder();
			seqBuilder.setSeqName(seqName);
			seqBuilder.setPrefix(prefix);
			seqBuilder.setDatePrefix(datePrefix);
			seqBuilder.setNextSeq(nextStep);
			seqBuilder.setSeqLen(seqLen);
			seqBuilder.setSeqPad(seqPad);
		}
		
		repository.save(seqBuilder);
		
		sb.append(StringUtils.isEmpty(seqBuilder.getPrefix()) ? "" : seqBuilder.getPrefix());
		sb.append(StringUtils.isEmpty(seqBuilder.getDatePrefix()) ? "" : DateUtil.dateToStr(new Date(), seqBuilder.getDatePrefix()));
		
		if (!StringUtils.isEmpty(seqBuilder.getSeqPad())) {
			sb.append(StringUtil.lpad(String.valueOf(seqBuilder.getNextSeq()), seqBuilder.getSeqLen(), seqBuilder.getSeqPad()));
		} else {
			sb.append(seqBuilder.getNextSeq());
		}
		
		return sb.toString();
	}
	
	
	public String getCustId() {
		return getSeq("CustId", "C", "yy", 7, "0", 1);
	}
	
	public String getCustUserId() {
		return getSeq("CustUserId", "CU", "yy", 3, "0", 1);
	}
	
	public String getStoreId() {
		return getSeq("StoreId", "S", "yy", 7, "0", 1);
	}
	
	public String getStoreUserId() {
		return getSeq("StoreUserId", "SU", "yy", 3, "0", 1);
	}
	
	public String getCategoryId() {
		return getSeq("CategoryId", "CT", "yy", 7, "0", 1);
	}
	
	public String getAppUserId() {
		return getSeq("AppUserId", "AU", "yyyyMMdd", 5, "0", 1);
	}
	
	public String getAppSocialUserId() {
		return getSeq("AppUserId", "ASU", "yyyyMMdd", 5, "0", 1);
	}
	
	public String getOrderId(String prefix) {
		return getSeq("OrderId" + DateUtil.dateToStr(new Date(), "yyyyMMdd"), prefix + "-", "", 7, "", 1);
	}
	
}
