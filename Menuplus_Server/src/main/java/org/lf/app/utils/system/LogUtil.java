package org.lf.app.utils.system;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.lf.app.models.Base;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * 打印工具类
 * @author LF
 * 
 */
public class LogUtil {

	// 로그 Level
	private static Level level = Level.toLevel(Level.DEBUG_INT);
	private Logger logger = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	
	/**
	 * 设置logger变量
	 * @param clazz Class
	 */
	public LogUtil(Class<?> clazz) {
		logger = (Logger) LoggerFactory.getLogger(clazz);
		logger.setLevel(level);
	}
	
	/**
	 * 取得Logger
	 * @return Logger
	 */
	public Logger getLogger() {
		return logger;
	}
	
	/**
	 * 动态设置Log Level
	 * @param levelInt
	 */
	public static void setLogLevel(int levelInt) {
		level = Level.toLevel(levelInt);
	}
	
	/**
	 * 打印Bean
	 * @param obj Object
	 */
	public void print(Object obj) {
		logger.info("@@@@@ Object+++++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.info(null != obj ? obj.toString() : "This object is null!");
		logger.info("*****--------------------------------------------------*****");
	}
	
	/**
	 * 打印Bean
	 * @param obj Base
	 */
	public void print(Base obj) {
		if (null == obj) {
			logger.info("@@@@@ Bean+++++++++++++++++++++++++++++++++++++++++++++@@@@@");
			logger.info("This object is null!");
			logger.info("*****--------------------------------------------------*****");
		} else {
			logger.info("@@@@@ Bean+++++++++++++++++++++++++++++++++++++++++++++@@@@@");
			logger.info("     extends " + obj.getClass().getSuperclass() + " column and value");
			logger.info(" ");

			String fieldName = null;
			Field field = null;
			Method method = null;
			String methodType = "get";

			try {
				// 부모컬럼 가져오기
				Field[] superFields = obj.getClass().getSuperclass().getDeclaredFields();

				// 부모 컬럼 값 출력
				for(int i = 0; i < superFields.length; i++) {
					field = superFields[i];

					fieldName = field.getName();

					// static 변수이면 바로 get 함수 없이 필드로 출력
					if (Modifier.isStatic(field.getModifiers())) {
						field.setAccessible(true);
						logger.info("          " + (fieldName + "                    ").substring(0, 15) + " : " + field.get(obj.getClass()).toString());
					} else {
						// boolean 값은 is + 필드 명으로 가져온다
						if (field.getType().getName().equals("boolean")) {
							methodType = "is";
						} else {
							methodType = "get";
						}
						method = obj.getClass().getMethod(methodType + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
						logger.info("          " + (fieldName + "                    ").substring(0, 15) + " : " + method.invoke(obj));
					}
				}
				logger.info("     ====================     ");
				logger.info("     this" + obj.getClass() + " column and value");

				// 자신의 컬럼 가져오기
				Field[] fields = obj.getClass().getDeclaredFields();

				// 자신의 컬럼 출력
				for(int i = 0; i < fields.length; i++) {
					field = fields[i];

					fieldName = field.getName();

					// static 변수이면 바로 get 함수 없이 필드로 출력
					if (Modifier.isStatic(field.getModifiers())) {
						field.setAccessible(true);
						logger.info("          " + (fieldName + "                    ").substring(0, 15) + " : " + field.get(obj.getClass()).toString());
					} else {
						// boolean 값은 is + 필드 명으로 가져온다
						if (field.getType().getName().equals("boolean")) {
							methodType = "is";
						} else {
							methodType = "get";
						}
						method = obj.getClass().getMethod(methodType + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
						logger.info("          " + (fieldName + "               ").substring(0, 10) + " : " + method.invoke(obj));
					}
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			logger.info(" ");
			logger.info("*****--------------------------------------------------*****");
		}
	}
	
	/**
	 * 打印Map
	 * @param map Map
	 */
	public void print(Map<String, Object> map) {
		logger.info("@@@@@ Map +++++++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.info("     Map type " + map.getClass());
		logger.info("     " + ("Key                                   ").substring(0, 30) + "  =  Value");

		Map.Entry<String, Object> mapEntry = null;
		for (Iterator<Map.Entry<String, Object>> mapEntryIterator = map.entrySet().iterator(); mapEntryIterator.hasNext();) {
			mapEntry = mapEntryIterator.next();
			logger.info("     " + (mapEntry.getKey() + "                                   ").substring(0, 30) + "  =  " + mapEntry.getValue());
		}

		logger.info("*****--------------------------------------------------*****");
	}
	
	/**
	 * 打印List
	 * @param list List
	 */
	public void print(List<Map<String, Object>> list) {
		logger.info("@@@@@ List+++++++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.info("  List type : " + list.getClass());
		logger.info("  This count : " + list.size());
		int i = 1;
		for(Map<String, Object> map : list) {
			logger.info("  Count : " + i + "  ==============================     ");
			logger.info("     Map type " + map.getClass());
			logger.info("     " + ("Key                                   ").substring(0, 30) + "  =  Value");
			Map.Entry<String, Object> mapEntry = null;
			for (Iterator<Map.Entry<String, Object>> mapEntryIterator = map.entrySet().iterator(); mapEntryIterator.hasNext();) {
				mapEntry = mapEntryIterator.next();
	            logger.info("     " + (mapEntry.getKey() + "                                   ").substring(0, 30) + "  =  " + mapEntry.getValue());
	        }
			i++;
		}
		logger.info("*****--------------------------------------------------*****");
	}
	
	/**
	 * Start log message
	 * @param obj Object
	 * @return long
	 */
	public long startLog(Object obj) {
		long time = System.currentTimeMillis();
		
		logger.info("@@@@@START DEBUG++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.info(obj.toString() + "     START     " + dateFormat.format(new Date()).toString());
		
		return time;
	}
	
	/**
	 * Record end log message
	 * @param obj Object
	 * @param time long
	 */
	public void endLog(Object obj, long time) {
		logger.info(obj.toString() + "     END     " + dateFormat.format(new Date()).toString());
		logger.info("    EXCUTE TIME    " + (System.currentTimeMillis() - time));
		logger.info("***** END DEBUG----------------------------------------*****");
	}
	
	/**
	 * 打印异常信息
	 * @param msg String
	 * @param e Exception
	 */
	public void errorLog(String msg, Exception e) {
		logger.error("@@@@@START ERROR++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.error(msg, e);
		logger.error("***** END  ERROR----------------------------------------*****");
	}
	
	/**
	 * logger.info
	 * @param msg String
	 */
	public void print(String msg) {
		logger.info("@@@@@START ++++++++++++++++++++++++++++++++++++++++++++@@@@@");
		logger.info(msg);
		logger.info("***** END  --------------------------------------------*****");
	}
	
	/**
	 * 打印Request参数信息
	 * @param msg String
	 */
	public void printRequest(HttpServletRequest request) {
		logger.info("@@@@@START ++++++++++++++++++++++++++++++++++++++++++++@@@@@");
		
		Enumeration<String> parameterNames = request.getParameterNames();
		Enumeration<String> attributeNames = request.getAttributeNames();
		
		while (parameterNames.hasMoreElements()) {
			String parameterName = parameterNames.nextElement();
			logger.info("parameterName = " + parameterName + "  |  " + request.getParameter(parameterName));
		}
		
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			logger.info("attributeName = " + attributeName + "  |  " + request.getAttribute(attributeName));
		}
		
		logger.info("***** END  --------------------------------------------*****");
	}
	
	/**
	 * 打印Session参数信息
	 * @param msg String
	 */
	public void printSession(HttpSession session) {
		logger.info("@@@@@START ++++++++++++++++++++++++++++++++++++++++++++@@@@@");
		
		Enumeration<String> attributeNames = session.getAttributeNames();
		
		while (attributeNames.hasMoreElements()) {
			String attributeName = attributeNames.nextElement();
			logger.info("attributeName = " + attributeName + "  |  " + session.getAttribute(attributeName));
		}
		
		logger.info("***** END  --------------------------------------------*****");
	}
	
}
