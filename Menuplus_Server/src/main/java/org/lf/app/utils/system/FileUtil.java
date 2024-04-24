package org.lf.app.utils.system;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장 유틸
 * 
 * @author lwj
 *
 */
@Component
public class FileUtil {

	@Value("${upload_path}") private String upload_path;
	@Value("${upload_folder}") private String upload_folder;
	
	/**
	 * 풀더 생성
	 * 
	 * @param folder 풀더명
	 * @return 풀더 주소
	 * @throws Exception
	 */
	public String createFile(String folder) throws Exception {
		File uploadPath = new File(upload_path + upload_folder + File.separator + folder);
		
		if (!uploadPath.exists()) {
			uploadPath.mkdirs();
		}
		
		return uploadPath.getAbsolutePath();
	}
	
	/**
	 * 파일 저장
	 * 
	 * @param folder 파일 저장 풀더
	 * @param fileName 파일명
	 * @param multipartFile 저장 할 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
//	public String saveFile(String folder, String fileName, MultipartFile multipartFile) throws Exception {
//		
//		String savePath = createFile(folder);
//		
//		fileName = fileName + "_" + UUID.randomUUID().toString();
//		
//		multipartFile.transferTo(new File(savePath + File.separator + fileName));
//		
//		return File.separator + upload_folder + File.separator + folder + File.separator + fileName;
//	}
	
	/**
	 * 파일 저장
	 * 
	 * @param folder 파일 저장 풀더
	 * @param fileName 파일명
	 * @param multipartFile 저장 할 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
//	public String saveFile(String folder, String fileName, File file) throws Exception {
//		
//		String savePath = createFile(folder);
//		
//		fileName = fileName + "_" + UUID.randomUUID().toString();
//		
//		Files.copy(file.toPath(), new File(savePath + File.separator + fileName).toPath());
//		
//		return File.separator + upload_folder + File.separator + folder + File.separator + fileName;
//	}
	
	/**
	 * Base64형식의 파일 저장
	 * @param folder 풀더명
	 * @param fileName 파일명
	 * @param base64 텍스트 형식의 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
	@SuppressWarnings("resource")
	public String saveFile(String folder, String fileName, String base64) throws Exception {
		String savePath = createFile(folder);
		
		fileName = fileName + "_" + UUID.randomUUID().toString();
		
		String data = base64.split(",")[1];
		String imgFormat = base64.split(",")[0];
		
		imgFormat = imgFormat.split(";")[0];
		
		String type = imgFormat.split("/")[0];
		
		imgFormat = imgFormat.split("/")[1];
		
		byte[] bytes = DatatypeConverter.parseBase64Binary(data);
		
		if (type.contains("image")) {
			BufferedImage bufImg = ImageIO.read(new ByteArrayInputStream(bytes));
			File imgFile = new File(savePath + File.separator + fileName + "." + imgFormat);
			ImageIO.write(bufImg, imgFormat , imgFile);
		} else {
			File file = new File(savePath + File.separator + fileName + "." + imgFormat);
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			if (null != fileOutputStream) {
				new BufferedOutputStream(fileOutputStream).write(bytes);
			}
		}
		
		return File.separator + upload_folder + File.separator + folder + File.separator + fileName + "." + imgFormat;
	}
	
	/**
	 * 파일 삭제
	 * 
	 * @param oldFile 파일 주소
	 */
	public void delFile(String oldFile) {
		if (!StringUtils.isEmpty(oldFile)) {
			File uploadPath = new File(upload_path + upload_folder + File.separator);
			
			if (uploadPath.exists()) {
				File file = new File(upload_path + oldFile);
				
				if (file.exists()) {
					file.delete();
				}
			}
		}
	}
	
	/**
	 * 파일 삭제 및 저장
	 * 
	 * @param oldFile 삭제 할 파일 주소
	 * @param folder 풀더
	 * @param fileName 파일명
	 * @param multipartFile 저장 할 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
//	public String delAndSaveFile(String oldFile, String folder, String fileName, MultipartFile multipartFile) throws Exception {
//		delFile(oldFile);
//		return saveFile(folder, fileName, multipartFile);
//	}
	
	/**
	 * 파일 삭제 및 저장
	 * 
	 * @param oldFile 삭제 할 파일 주소
	 * @param folder 풀더
	 * @param fileName 파일명
	 * @param multipartFile 저장 할 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
//	public String delAndSaveFile(String oldFile, String folder, String fileName, File file) throws Exception {
//		delFile(oldFile);
//		return saveFile(folder, fileName, file);
//	}
	
	/**
	 * 파일 삭제 및 저장
	 * @param oldFile 삭제 할 파일 주소
	 * @param folder 풀더
	 * @param fileName 파일명
	 * @param 텍스트 형식의 파일
	 * @return 저장된 파일 주소
	 * @throws Exception 
	 */
	public String delAndSaveFile(String oldFile, String folder, String fileName, String base64) throws Exception {
		delFile(oldFile);
		return saveFile(folder, fileName, base64);
	}
	
	public String imageToBase64(InputStream inputStream) {
		InputStream is = inputStream;
		byte[] data = null;
		try {
			data = new byte[is.available()];
			is.read(data);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return DatatypeConverter.printBase64Binary(data);
	}
	
	
	/**
	 * 
	 * @param folder
	 * @param fnm
	 * @param multipartFile
	 * @return String
	 * @throws Exception
	 */
	public Map<String, String> saveZipFile(String folder, String fileName, MultipartFile file) throws Exception {
		Map<String, String> result = new HashMap<>();
		
		String savePath = createFile(folder);
		
//		System.out.println(codeString(file));
		
		ZipInputStream zis = new ZipInputStream(file.getInputStream(), Charset.forName(codeString(file)));
//		ZipInputStream zis = new ZipInputStream(file.getInputStream());
		BufferedInputStream bis = new BufferedInputStream(zis);
		
		ZipEntry entry;
		
		while ((entry = zis.getNextEntry()) != null && !entry.isDirectory()) {
			
			String[] prefixs = entry.getName().split("\\.");
			
			String fileNameTmp = fileName + "_" + UUID.randomUUID().toString() + "." + prefixs[1];
			
			File tmpFile = new File(savePath, fileNameTmp);
			
			if (!tmpFile.exists()) {
				tmpFile.createNewFile();
			}
			
			FileOutputStream fos = new FileOutputStream(tmpFile);
			
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			int b;
			while ((b = bis.read()) != -1) {
				bos.write(b);
			}
			
			bos.close();
			fos.close();
			
			result.put(entry.getName(), File.separator + upload_folder + File.separator + folder + File.separator + fileNameTmp);
		}
		
		bis.close();
		zis.close();
		
		return result;
	}
	
	
	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public String codeString(MultipartFile file) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(file.getInputStream());
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		// 其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
		
		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		case 0x5c75:
			code = "ANSI|ASCII";
			break;
		case 0x504b:
			code = "EUC-KR";
			break;
		default:
			code = "GBK";
		}

		return code;
	}
	
}
