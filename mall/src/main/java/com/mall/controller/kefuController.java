package com.mall.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.validation.Validator;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mall.util.tool.HttpRequestor;
import com.mall.wxsdk.WXPayUtil;
import com.thoughtworks.xstream.XStream;

import net.sf.json.JSONObject;



@Controller
@RequestMapping("/")
public class kefuController {

	@RequestMapping(value ="/kefu",method = RequestMethod.GET)
    protected String kefu(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("~!~!");
		Enumeration enu=request.getParameterNames();  
		while(enu.hasMoreElements()){  
		String paraName=(String)enu.nextElement();  
		System.out.println(paraName+": "+request.getParameter(paraName));  
		}	
			
	  String admin = request.getParameter("admin");//用户ID
      String password = request.getParameter("password");
      Map<String, String> map = new HashMap<String, String>();
      long a = new Date().getTime();
      String time = String.valueOf(a);
      map.put("ToUserName", "123");
      map.put("FromUserName", "8652683252");
      map.put("CreateTime", time);
      map.put("MsgType", "transfer_customer_service");
      String xml =  WXPayUtil.mapToXml(map);
      String result = UserController.httpsRequest(  
              "https://api.weixin.qq.com/customservice/msgrecord/getmsglist?access_token=ACCESS_TOKEN", "POST",  xml);     
      return result;
      
    }
	public static void main(String[] args) throws Exception {
//		Map<String, String> map = new HashMap<String, String>();
//		  String str = RandomStringUtils.randomAlphanumeric(32);
//		  String s = UUID.randomUUID().toString().replace("-","32");
//		  System.out.println(s);
//	      long a = new Date().getTime();
//	      String time = String.valueOf(a);
//	      map.put("ToUserName", "jianlemei520");
//	      map.put("FromUserName", "oiVLZwMI8ZSL10weEN5R-vGOIvmI");
//	      map.put("CreateTime", time);
//	      map.put("MsgType", "text");
//	      map.put("Content", "哈喽");
//	      map.put("MsgId", "123456789012345612323121564151854");
//	      String xml =  WXPayUtil.mapToXml(map);
//	        System.out.println(jsonObject);
	      /*String result = UserController.httpsRequest(  
	              "https://api.weixin.qq.com/customservice/kfsession/getsession?access_token=ACCESS_TOKEN&openid=OPENID", "POST",  abc);
	      System.out.println(result);*/
//		long epoch = new java.text.SimpleDateFormat ("dd/MM/yyyy HH:mm:ss").parse("08/9/20017 13:30:00").getTime();
		Map<String, String> map = new HashMap<String, String>();
        map.put("starttime", "1252639886");
        map.put("endtime", "69529984600000");
        map.put("msgid", "1");
        map.put("number", "10000");
        JSONObject jsonObject = JSONObject.fromObject(map);
        String jo = ""+jsonObject;
        System.out.println(jsonObject);
	        URL url = new URL("https://api.weixin.qq.com/customservice/msgrecord/getmsglist?access_token=ACCESS_TOKEN");  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            // 设置请求方式（GET/POST）  
            conn.setRequestMethod("POST");  
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            OutputStream outputStream = conn.getOutputStream();  
            // 注意编码格式  
            outputStream.write(jo.getBytes("UTF-8"));  
            outputStream.close();
         // 从输入流读取返回内容  
            InputStream inputStream = conn.getInputStream();  
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");  
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);  
            String str = null;  
            StringBuffer buffer = new StringBuffer();  
            while ((str = bufferedReader.readLine()) != null) {  
                buffer.append(str);  
            }  
            // 释放资源  
            bufferedReader.close();  
            inputStreamReader.close();  
            inputStream.close();  
            inputStream = null;  
            conn.disconnect();  
            System.out.println(buffer.toString()); 
	}
	
}