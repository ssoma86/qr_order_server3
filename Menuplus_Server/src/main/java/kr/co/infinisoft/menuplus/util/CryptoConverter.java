package kr.co.infinisoft.menuplus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * JPA AttributeConverter 인터페이스를 활용하여  Entity side에서 암복호화 구현
 * @author 박영근
 *
 */
@Converter
public class CryptoConverter implements AttributeConverter<String, String> {
	
		private static final Logger logger = LoggerFactory.getLogger(CryptoConverter.class);
		
		@Value("${crypto_key}") 
		private String crypto_key;
	
		/**
		 * Entity 값을 암호화하여 해당 DB 컬럼에 저장
		 */
		@Override
	    public String convertToDatabaseColumn(String attribute) {
			
			String result = null;
			
			if(attribute != null) {
				
				try {
					AESCrypto aes = new AESCrypto(crypto_key);
					result = aes.AesECBEncode(attribute);
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
			
	        return result;
	    }
	 
		/**
		 * DB 데이터를 복호화 하여 해당 Entity 변수에 저장
		 */
	    @Override
	    public String convertToEntityAttribute(String dbData) {
	       
	    	String result = null;
			
			if(dbData != null) {
				
				try {
					AESCrypto aes = new AESCrypto(crypto_key);
					result = aes.AesECBDecode(dbData);
				}catch(Exception e) {
					logger.info(e.getMessage());
				}
			}
			
	        return result;
	    }

} // end class
