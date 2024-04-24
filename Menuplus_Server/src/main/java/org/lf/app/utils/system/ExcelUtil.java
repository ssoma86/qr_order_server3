package org.lf.app.utils.system;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * Excel Util
 * 
 * @author LF
 *
 */
public class ExcelUtil {

	@SuppressWarnings("unused")
	private LogUtil log = new LogUtil(getClass());
	
	
	// 해더 짜르기 구분 문자열
	public static final String SPLIT = "_HSPLIT_";
	
	// 한 시트에 최대 데이타 개수
	private int rowCnt = 30000; 
	
	
	
	/**
	 * 엑셀 출력
	 * @param headers 해더 정보 [테스트SPLIT컬럼명SPLIT정열방식]
	 * @param dataList 데이타 리스트
	 * @param res 리스폰스
	 * @throws Exception
	 */
    public void exportExcel(String[] headers, List<Map<String, Object>> dataList, HttpServletResponse res) throws Exception {
    	// 설정한 데이타 개수보다 많을 시 압축 파일로 다운 한다
    	if (dataList.size() > rowCnt) {
    		exportExcelZip(headers, dataList, res, "yyyy-MM-dd");
    	} else {
    		exportExcel(headers, dataList, res, "yyyy-MM-dd");
    	}
    }
    
    /**
	 * 엑셀 출력
	 * @param headers 해더 정보 [테스트SPLIT컬럼명SPLIT정열방식]
	 * @param dataList 데이타 리스트
	 * @param res 리스폰스
	 * @param pattern 날자형식
	 * @throws Exception
	 */
    public void exportExcel(String[] headers, List<Map<String, Object>> dataList, HttpServletResponse res, String pattern) throws Exception {
    	res.addHeader("Content-Disposition", "attachment; filename=data.xls");
		res.setContentType("application/octet-stream; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = null;
        
        // 컬럼정보 설정
        String[] fields = new String[headers.length];
        List<HSSFCellStyle> alignStyles = new ArrayList<>();
        
        settingColumnInfo(workbook, headers, fields, alignStyles);
        
        // 시트 생성
        sheet = workbook.createSheet("Sheet Data Count " + dataList.size());
        sheet.setDefaultColumnWidth((short) 40);
        
        // 해더설정
        settingHeader(workbook, sheet, headers);
        
        // 데이타 설정
        for (int i = 0, len = dataList.size(); i < len; i++) {
        	// 행 데이타 설정
        	settingData(workbook, sheet.createRow(i + 1), fields, alignStyles, dataList.get(i), pattern);
        }
        
		OutputStream ous = new BufferedOutputStream(res.getOutputStream());
		
		workbook.write(ous);
		
		ous.flush();
		ous.close();
    }
    
    /**
	 * 엑셀 출력
	 * @param headers 해더 정보 [테스트SPLIT컬럼명SPLIT정열방식]
	 * @param dataList 데이타 리스트
	 * @param res 리스폰스
	 * @param pattern 날자형식
	 * @throws Exception
	 */
    public void exportExcelZip(String[] headers, List<Map<String, Object>> dataList, HttpServletResponse res, String pattern) throws Exception {
    	res.addHeader("Content-Disposition", "attachment; filename=download.zip");
		res.setContentType("application/octet-stream; charset=UTF-8");
		res.setCharacterEncoding("UTF-8");
		
    	ZipOutputStream zipos = new ZipOutputStream(new BufferedOutputStream(res.getOutputStream()));
    	zipos.setMethod(ZipOutputStream.DEFLATED);
    	BufferedOutputStream os = null;
    	
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = null;
        
        // 컬럼정보 설정
        String[] fields = new String[headers.length];
        List<HSSFCellStyle> alignStyles = new ArrayList<>();
        
        settingColumnInfo(workbook, headers, fields, alignStyles);
        
        int num = 0;
        for (int i = 0, len = dataList.size(); i < len; i++) {
            // 카운터 개수 초과시 새로운 시트 해더 설정
            if (num == 0) {
            	// 시트가 있으면 삭제
            	if (i > 0 && workbook.iterator().hasNext()) {
            		workbook.removeSheetAt(0);
            	}
            	int endNum = i / rowCnt == len / rowCnt ? len : (i / rowCnt + 1) * rowCnt;
            	
            	sheet = workbook.createSheet("Sheet " + (i+1) + " ~ " + endNum);
                sheet.setDefaultColumnWidth((short) 40);
                
                // 해더설정
                settingHeader(workbook, sheet, headers);
                num++;
            }
            
        	settingData(workbook, sheet.createRow(num), fields, alignStyles, dataList.get(i), pattern);
        	
        	if (num == rowCnt || (i+1) == len) {
            	zipos.putNextEntry(new ZipEntry((i+1) + ".xls"));
				os = new BufferedOutputStream(zipos);
				
				workbook.write(os);
				
				zipos.closeEntry();
				
				num = 0;
            } else {
            	num++;
            }
        }
		
		zipos.close();
		os.flush();
		os.close();
    }
    
    /**
     * 컬럼 정보 설정
     * @param workbook 엑셀
     * @param headers 해더 리스트
     * @param fields 컬럼 정보
     * @param alignStyles 컬럼 스타일 
     */
    private void settingColumnInfo(HSSFWorkbook workbook, String[] headers, String[] fields, List<HSSFCellStyle> alignStyles) {
    	// 글자 사이즈
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 10);
        
    	for (short i = 0; i < headers.length; i++) {
            String[] head = headers[i].split(SPLIT);
            
            fields[i] = head[1];
            
            // 데이타 정열 설정
            if (head.length > 2) {
            	HSSFCellStyle style = workbook.createCellStyle();
            	
            	if ("l".equals(head[2])) {
            		style.setAlignment(HorizontalAlignment.LEFT);
            	} else if ("c".equals(head[2])) {
            		style.setAlignment(HorizontalAlignment.CENTER);
            	} else if ("r".equals(head[2])) {
            		style.setAlignment(HorizontalAlignment.RIGHT);
            	}
                
                // 테두리
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderTop(BorderStyle.THIN);
                
                style.setFont(font);
            	alignStyles.add(style);
            }
        }
    }
    
    /**
     * 엑셀 해더 설정
     * @param workbook 엑셀
     * @param row 행
     * @param headers 해더 리스트
     */
    private void settingHeader(HSSFWorkbook workbook, HSSFSheet sheet, String[] headers) {
    	HSSFCellStyle style = workbook.createCellStyle();
    	
    	// 글자 사이즈
        HSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        
        // 중간 정열
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_TURQUOISE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        HSSFRow row = sheet.createRow(0);
        
        for (short i = 0; i < headers.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            String[] head = headers[i].split(SPLIT);
            HSSFRichTextString text = new HSSFRichTextString(head[0]);
            cell.setCellValue(text);
        }
    }
    
    /**
     * 데이타 설정
     * @param workbook 엑셀
     * @param row 행
     * @param fields 필드
     * @param alignStyles 정열
     * @param pattern 날자 형식
     */
    private void settingData(HSSFWorkbook workbook, HSSFRow row, String[] fields,
    		List<HSSFCellStyle> alignStyles, Map<String, Object> data, String pattern) {
    	
    	for (int i = 0, len = fields.length; i < len; i++) {
    		HSSFCell cell = row.createCell(i);
            cell.setCellStyle(alignStyles.get(i));
            
    		Object value =  data.get(fields[i]);
    		
    		String textValue =  null;
    		
    		if (value instanceof Integer) {
    			int intValue =  (Integer) value;
                cell.setCellValue(intValue);
    		} else if (value instanceof Long) {
    			long longValue =  (Long) value;
                cell.setCellValue(longValue);
    		} else if (value instanceof Boolean) {
    			boolean bValue =  (Boolean) value;
    			textValue =  "1";
                if (!bValue) {
                	textValue =  "0";
                }
    		} else if (value instanceof Date) {
    			Date date = (Date) value;
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                textValue =  sdf.format(date);
    		} else {
    			if(value == null) {
    				textValue =  "";
                } else {
                	textValue =  value.toString();
                }
    		}
    		
    		if (textValue != null) {
    			Pattern p = Pattern.compile("^//d+(//.//d+)?$");
                Matcher matcher = p.matcher(textValue);
                if (matcher.matches()) {
                    cell.setCellValue(Double.parseDouble(textValue));
                } else {
                    HSSFRichTextString richString = new HSSFRichTextString(textValue);
                    cell.setCellValue(richString);
                }
    		}
    	}
    }
    
    
    
    /**
     * 엑셀 읽기 함수
     * @param workbook 엑셀
     * @param headers 엑셀 해더
     * @return
     */
    public List<Map<String, Object>> readExcel(HSSFWorkbook workbook, String[] headers) {
    	List<Map<String, Object>> datas = new ArrayList<>();
    	
    	HSSFSheet sheet = workbook.getSheetAt(0);
		// 첫행  
        int firstRowNum  = sheet.getFirstRowNum();
        // 마지막행
        int lastRowNum = sheet.getLastRowNum();
        
        String[] fields = new String[headers.length];
        
        HSSFRow row = sheet.getRow(0);
        
        // 첫줄
        int firstCellNum = row.getFirstCellNum();
        // 마지막줄
        int lastCellNum = row.getPhysicalNumberOfCells();
        
        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
        	HSSFCell cell = row.getCell(cellNum);
        	for (short i = 0; i < headers.length; i++) {
        		String[] head = headers[i].split(SPLIT);
        		if (getCellValue(cell).equals(head[0])) {
        			fields[cellNum] = head[1];
                }
            }
        }
        
        for (int rowNum = firstRowNum + 1; rowNum <= lastRowNum; rowNum++) {
        	row = sheet.getRow(rowNum);
        	
            if (null == row) {
                continue;
            }
            
            // 첫줄
            firstCellNum = row.getFirstCellNum();
            // 마지막줄
            lastCellNum = row.getLastCellNum();
            
            Map<String, Object> data = new HashMap<>();
            
            for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
            	HSSFCell cell = row.getCell(cellNum);
                data.put(fields[cellNum], getCellValue(cell));
            }
            
            datas.add(data);
        }
        
        return datas;
    }
    
    /**
     * 엑셀 값 읽어 오기
     * @param cell 셀
     * @return 셀 값
     */
    public static String getCellValue(HSSFCell cell) {
        String cellValue =  "";
        if (cell == null) {
            return cellValue;
        }
        
        if (cell.getCellType() == CellType.NUMERIC) {
            cell.setCellType(CellType.STRING);
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                cellValue =  String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                cellValue =  String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN:
                cellValue =  String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue =  String.valueOf(cell.getCellFormula());
                break;
            case BLANK:
                cellValue =  "";
                break;
            case ERROR:
                cellValue =  "Type error";
                break;
            default:
                cellValue =  "Unknown type";
                break;
        }
        
        return cellValue;
    }  
    
    
}
