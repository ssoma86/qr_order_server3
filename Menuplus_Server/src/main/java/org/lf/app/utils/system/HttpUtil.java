package org.lf.app.utils.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;

/**
 * HTTP GET, POST util
 * @author lwj
 *
 */
public class HttpUtil {
	
	/**
	 * GET
	 * @param httpurl
	 * @param headers
	 * @return 리턴 값
	 * @throws IOException 
	 */
	public static String doGet(String httpurl, Map<String, Object> headers) throws IOException {
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8;");
            
            // 해더 설정
            if (null != headers) {
            	Set<String> keySet = headers.keySet();
            	Iterator<String> iterator = keySet.iterator();
            	
            	while (iterator.hasNext()) {
            		String key = iterator.next();
            		connection.setRequestProperty(key, MapUtils.getString(headers, key));
            	}
            }
            
            connection.connect();
            
            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            result = sbf.toString();
            
        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException e) {
        	throw e;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if (null != connection) {
            	connection.disconnect();
            }
        }

        return result;
    }

	/**
	 * POST
	 * @param httpUrl
	 * @param param
	 * @param headers
	 * @return 리턴 값
	 * @throws IOException 
	 */
    public static String doPost(String httpUrl, String param, Map<String, Object> headers) throws IOException {

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8;");
            
            // 해더 설정
            if (null != headers) {
            	Set<String> keySet = headers.keySet();
            	Iterator<String> iterator = keySet.iterator();
            	
            	while (iterator.hasNext()) {
            		String key = iterator.next();
            		connection.setRequestProperty(key, MapUtils.getString(headers, key));
            	}
            }
            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            os = connection.getOutputStream();
            os.write(param.getBytes());

            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            result = sbf.toString();
        } catch (MalformedURLException e) {
        	throw e;
        } catch (IOException e) {
        	throw e;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
            	connection.disconnect();
            }
        }
        return result;
    }
    
    /**
	 * POST
	 * @param httpUrl
	 * @param param
	 * @param headers
	 * @return 리턴 값
	 * @throws IOException 
	 */
    public static String doPostNoJson(String httpUrl, String param) throws IOException {

        HttpURLConnection connection = null;
        InputStream is = null;
        OutputStream os = null;
        BufferedReader br = null;
        String result = null;
        try {
            URL url = new URL(httpUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(60000);
            
            connection.setDoOutput(true);
            connection.setDoInput(true);
            
            os = connection.getOutputStream();
            os.write(param.getBytes());

            is = connection.getInputStream();
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuffer sbf = new StringBuffer();
            String temp = null;
            while ((temp = br.readLine()) != null) {
                sbf.append(temp);
                sbf.append("\r\n");
            }
            result = sbf.toString();
        } catch (MalformedURLException e) {
        	throw e;
        } catch (IOException e) {
        	throw e;
        } finally {
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != connection) {
            	connection.disconnect();
            }
        }
        return result;
    }
    
}