package kr.co.infinisoft.menuplus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;


/**
 * AES 암복화 
 */
public class AESCrypto{
	
		private static final Logger logger = LoggerFactory.getLogger(AESCrypto.class);
	
		private SecretKeySpec secretKey;

		public AESCrypto(String reqSecretKey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
			
			//바이트 배열로부터 SecretKey를 구축
			this.secretKey = new SecretKeySpec(reqSecretKey.getBytes("UTF-8"), "AES");
		}

		/**
		 * AES ECB PKCS5Padding 암호화(Hex)
		 * @param plainText
		 * @return
		 * @throws Exception
		 */
	   	public String AesECBEncode(String plainText) throws Exception {
			
			//Cipher 객체 인스턴스화(Java에서는 PKCS#5 = PKCS#7이랑 동일)
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			//Cipher 객체 초기화
			c.init(Cipher.ENCRYPT_MODE, secretKey);
			
			//Encrpytion/Decryption
			byte[] encrpytionByte = c.doFinal(plainText.getBytes("UTF-8"));
			
			//Hex Encode
			return Hex.encodeHexString(encrpytionByte);
		
		}

	   	/**
	   	 * AES ECB PKCS5Padding 복호화(Hex)
	   	 * @param encodeText
	   	 * @return
	   	 * @throws Exception
	   	 */
		public String AesECBDecode(String encodeText) throws Exception {

			//Cipher 객체 인스턴스화(Java에서는 PKCS#5 = PKCS#7이랑 동일)
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			//Cipher 객체 초기화
			c.init(Cipher.DECRYPT_MODE, secretKey);
			
			//Decode Hex
			byte[] decodeByte = Hex.decodeHex(encodeText.toCharArray());
			
			return new String(c.doFinal(decodeByte), "UTF-8");
		}
		
//		public static void main(String[] args) throws Exception {
//			
//			/*
//			 * 키 값의 바이트 수에 따라서 달라집니다.
//			 * AES128 : 키값 16bytes
//			 * AES192 : 키값 24bytes
//			 * AES256 : 키값 32bytes 
//			 */
//			
//			String plainText = "01091489514";
//			
//			String key_128 = "aeskey1234567898";//AES-128는 128비트(16바이트)의 키
//			String key_192 = "aeskey12345678987654321a";//AES-192는 192비트(24바이트)의 키
//			String key_256 = "aeskey12345678987654321asekey987";//AES-256는 256비트(32바이트)의 키
//			
//			String iv = "aesiv12345678912";
//			
//			AESCrypto ase_128_ecb = new AESCrypto(key_128);
//			String aes128EcbEncode = ase_128_ecb.AesECBEncode(plainText);
//			String aes128EcbDeocde = ase_128_ecb.AesECBDecode(aes128EcbEncode);
//			
////			AESCrypto ase_192_ecb = new AESCrypto(key_192);
////			String aes192EcbEncode = ase_192_ecb.AesECBEncode(plainText);
////			String aes192EcbDeocde = ase_192_ecb.AesECBDecode(aes192EcbEncode);
////			
////			AESCrypto ase_256_ecb = new AESCrypto(key_256);
////			String aes256EcbEncode = ase_256_ecb.AesECBEncode(plainText);
////			String aes256EcbDeocde = ase_256_ecb.AesECBDecode(aes256EcbEncode);
//
//			System.out.println("plainText: " + plainText);			
//			System.out.println();
//			System.out.println("Aes128 Encode ECB: " + aes128EcbEncode);			
//			System.out.println("Aes128 Decode ECB: " + aes128EcbDeocde);			
////			System.out.println();
////			System.out.println("Aes192 Encode ECB: " + aes192EcbEncode);			
////			System.out.println("Aes192 Decode ECB: " + aes192EcbDeocde);			
////			System.out.println();
////			System.out.println("Aes256 Encode ECB: " + aes256EcbEncode);			
////			System.out.println("Aes256 Decode ECB: " + aes256EcbDeocde);					
//
//		}
		

} // end class
