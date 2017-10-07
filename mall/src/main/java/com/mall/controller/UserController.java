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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.aspectj.bridge.MessageUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.pojo.Order;
import com.mall.pojo.Relivery_address;
import com.mall.pojo.User;
import com.mall.pojo.WeixinUserInfo;
import com.mall.service.EvaluateOrderService;
import com.mall.service.OrderService;
import com.mall.service.Relivery_addressService;
import com.mall.service.UserService;
import com.mall.util.tool.HttpRequestor;
import com.mall.util.weixinTest.CommonUtil;
import com.mall.wxsdk.WXPay;
import com.mall.wxsdk.WXPayConstants;
import com.mall.wxsdk.WXPayUtil;

import net.sf.json.JSONObject;


@Controller
@RequestMapping("/")
public class UserController {
	//private static Logger log = LoggerFactory.getLogger(CommonUtil.class);
	@Resource
	private UserService userService;
	@Resource
	private OrderService orderService;
	@Resource
	private Relivery_addressService relivery_addressService;
	@Resource
	private EvaluateOrderService evaluateOrderService;
    
	/**
	 * 后台管理获取所有用户信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectAllUser",method = RequestMethod.GET)
    protected String selectAllUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<User> user = userService.selectAllUser();
		request.setAttribute("user",user);
		return "admin/user.jsp";
		
	}
	
	/** 没做
	 * 根据用户ID获取该用户的所有订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectUserOrder",method = RequestMethod.GET)
    protected String selectUserOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		List<Order> order = orderService.seleteAllOrder(user_id);
		request.setAttribute("order",order);
		return "admin/user.jsp";
		
	}
	
	/**
	 * 后台管理查看用户详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectUserDetail",method = RequestMethod.GET)
    protected String selectUserDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));
		User user = userService.selectByid(user_id);
		List<Relivery_address> relivery_address = relivery_addressService.selectCollectionById(user_id);
		request.setAttribute("relivery_address",relivery_address);
		request.setAttribute("user",user);
		return "admin/userdetail.jsp";
		
	}
	
	/**
	 * 后台管理根据NAME查找用户
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectUser",method = RequestMethod.GET)
    protected String selectById(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//String name = request.getParameter("name");
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");
//		System.out.println("name:"+name);
		if(name==null || name.equals("")){
//			System.out.println("没有");
			return "admin/selectAllUser";
		}else{
			List<User> user = userService.selectUser(name);
//			System.out.println("uyser:"+user);
			request.setAttribute("user",user);
			return "admin/user.jsp";
		}		
	}
	
	/**
	 * 商城入口
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectOpenid",method = RequestMethod.GET)
    protected String doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String code = request.getParameter("code");
        System.out.println("code为:"+code);
        if(code.equals("")||code==null){
        	return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx4fe37f6b639a23c3&redirect_uri=http://www.jianlemei520.cn/mall/selectOpenid&response_type=code&scope=snsapi_userinfo&state=123#wechat_redirect";
        }
        String appid = "wx4fe37f6b639a23c3";
        String secret = "d400544170d6e42811310e187a2757fe";
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+secret+"&code="+code+"&grant_type=authorization_code";
        String  oppid = new HttpRequestor().doGet(requestUrl);
        JSONObject oppidObj =JSONObject.fromObject(oppid);
        String access_token = (String) oppidObj.get("access_token");
        String openid = (String) oppidObj.get("openid");
        /*if(openid==null || openid.equals("")){
        	return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx4fe37f6b639a23c3&redirect_uri=http://jianlemei520.cn/mall/selectOpenid&response_type=code&scope=snsapi_userinfo&state=STATE&connect_redirect=1#wechat_redirect";
        }*/
        //String openid = "123";
        //String openid = "oiVLZwJ_rt7TwLti0ZvSUJZ4G7rA";
        //String openid = "oiVLZwCthILN5ArbjyxS0k6gC2uo";
//        System.out.println("openid:"+openid);
        String requestUrl2 = "https://api.weixin.qq.com/sns/userinfo?access_token="+access_token+"&openid="+openid+"&lang=zh_CN";
        String userInfoStr = new HttpRequestor().doGet(requestUrl2);
        JSONObject wxUserInfo =JSONObject.fromObject(userInfoStr);
//        System.out.println("wxUserInfo:"+wxUserInfo);
        
        String accessToken = CommonUtil.getToken("wx4fe37f6b639a23c3", "d400544170d6e42811310e187a2757fe").getAccessToken();
        WeixinUserInfo ceshi = getUserInfo(accessToken, openid);
        /*System.out.println("OpenID：" + ceshi.getOpenId());
         * 
        System.out.println("关注状态：" + ceshi.getSubscribe());
        System.out.println("关注时间：" + ceshi.getSubscribeTime());
        System.out.println("昵称：" + ceshi.getNickname());
        System.out.println("性别：" + ceshi.getSex());
        System.out.println("国家：" + ceshi.getCountry());
        System.out.println("省份：" + ceshi.getProvince());
        System.out.println("城市：" + ceshi.getCity());
        System.out.println("语言：" + ceshi.getLanguage());
        System.out.println("头像：" + ceshi.getHeadImgUrl());*/
        
        String headImgUrl = ceshi.getHeadImgUrl();
        String name = ceshi.getNickname();
        if(userService.selectOpenid(openid)==null){
        	userService.insert(openid,headImgUrl,name);
        	User user = userService.selectOpenid(openid);
        	int user_id = user.getUser_id();
        	request.setAttribute("user_id",user_id);
        	return "index.jsp";
        }else{
        	User user = userService.selectOpenid(openid);
        	int user_id = user.getUser_id();
            request.setAttribute("user_id",user_id);
    		return "index.jsp";
        }
        
    }
	
	/**
	 * 客户端 “我的”模块
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/my",method = RequestMethod.GET)
    protected String My(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));
		User user  = userService.selectByid(user_id);
		int order1 = orderService.selectCategory1Number(user_id);
		int order2 = orderService.selectCategory2Number(user_id);
		int order3 = evaluateOrderService.selectCategoryNumber(user_id, 1);
			
		request.setAttribute("user",user);
		request.setAttribute("order1",order1);
		request.setAttribute("order2",order2);
		request.setAttribute("order3",order3);
		request.setAttribute("user_id",user_id);
		return "mine.jsp";
		
	}
	
	
	/*public static void main(String args[]) {
        // 获取接口访问凭证
        String accessToken = CommonUtil.getToken("wx4fe37f6b639a23c3", "d400544170d6e42811310e187a2757fe").getAccessToken();
        *//**
         * 获取用户信息
         *//*
        WeixinUserInfo user = getUserInfo(accessToken, "oiVLZwCthILN5ArbjyxS0k6gC2uo");
        System.out.println("OpenID：" + user.getOpenId());
        System.out.println("关注状态：" + user.getSubscribe());
        System.out.println("关注时间：" + user.getSubscribeTime());
        System.out.println("昵称：" + user.getNickname());
        System.out.println("性别：" + user.getSex());
        System.out.println("国家：" + user.getCountry());
        System.out.println("省份：" + user.getProvince());
        System.out.println("城市：" + user.getCity());
        System.out.println("语言：" + user.getLanguage());
        System.out.println("头像：" + user.getHeadImgUrl());
    }*/
	
	
	
	
	/**
     * 获取用户信息
     * 
     * @param accessToken 接口访问凭证
     * @param openId 用户标识
     * @return WeixinUserInfo
     */
    public static WeixinUserInfo getUserInfo(String accessToken, String openId) {
        WeixinUserInfo weixinUserInfo = null;
        // 拼接请求地址
        String requestUrl = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
        requestUrl = requestUrl.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
        // 获取用户信息
        JSONObject jsonObject = CommonUtil.httpsRequest(requestUrl, "GET", null);

        if (null != jsonObject) {
            try {
                weixinUserInfo = new WeixinUserInfo();
                // 用户的标识
                weixinUserInfo.setOpenId(jsonObject.getString("openid"));
                // 关注状态（1是关注，0是未关注），未关注时获取不到其余信息
                weixinUserInfo.setSubscribe(jsonObject.getInt("subscribe"));
                // 用户关注时间
                weixinUserInfo.setSubscribeTime(jsonObject.getString("subscribe_time"));
                // 昵称
                weixinUserInfo.setNickname(jsonObject.getString("nickname"));
                // 用户的性别（1是男性，2是女性，0是未知）
                weixinUserInfo.setSex(jsonObject.getInt("sex"));
                // 用户所在国家
                weixinUserInfo.setCountry(jsonObject.getString("country"));
                // 用户所在省份
                weixinUserInfo.setProvince(jsonObject.getString("province"));
                // 用户所在城市
                weixinUserInfo.setCity(jsonObject.getString("city"));
                // 用户的语言，简体中文为zh_CN
                weixinUserInfo.setLanguage(jsonObject.getString("language"));
                // 用户头像
                weixinUserInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            } catch (Exception e) {
                if (0 == weixinUserInfo.getSubscribe()) {
                   System.out.println("用户{"+weixinUserInfo.getOpenId()+"}已取消关注");
                } else {
                    int errorCode = jsonObject.getInt("errcode");
                    String errorMsg = jsonObject.getString("errmsg");
                    System.out.println("获取用户信息失败+errorcode:{"+errorCode +"}"+"errmsg:{"+errorMsg+"}");
                }
            }
        }
        return weixinUserInfo;
    }
    
    /**
     * 微信支付返回
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/paymentReturn",method = RequestMethod.POST)
    protected String paymentReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("支付返回");	
		BufferedReader br = null;
		StringBuilder result = new StringBuilder();
		br = request.getReader();
    	
		for(String line;(line=br.readLine())!=null;){
			if(result.length()>0){
				result.append("\n");
			}
			result.append(line);
		}
		String a = result.toString();//xml字符串
		Map<String, String> m =  WXPayUtil.xmlToMap(a);//xml字符串转map
    	return "返回："+result.toString();
    	
    }

   /**
     * 支付接口
     * @param request
     * @param response
     * @return
     * 
     * @throws Exception
     */
    @RequestMapping(value ="/payment",method = RequestMethod.POST)
    protected String payment(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	//订单参数
    	Integer category = Integer.parseInt(request.getParameter("category"));//1为商家配送 2为到店自提
    	Integer payment = Integer.parseInt(request.getParameter("payment"));//支付方式 1为在线支付  2为到店支付
    	String goods_id = request.getParameter("goods_id");//商品ID  多个商品用逗号隔开
	    String number = request.getParameter("number");//商品数量	多个商品用逗号隔开
	    String order_message = request.getParameter("order_message");//订单留言
//	    double all_price = Double.parseDouble(request.getParameter("all_price"));//总价
	    String all_price = request.getParameter("all_price");
	    int a = Integer.parseInt(all_price);
	    int price = a*100;
	    String p = String.valueOf(price);
	    String recipient_name = request.getParameter("recipient_name");//收件人姓名
	    String recipient_tel = request.getParameter("recipient_tel");//收件人电话
	    String recipient_address = request.getParameter("recipient_address");//收件人地址      
	    int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
	    
	    //String recipient_name = new String(request.getParameter("recipient_name").getBytes("GBK"),"UTF-8");//收件人姓名
	    //String recipient_address = new String(request.getParameter("recipient_address").getBytes("GBK"),"UTF-8");//收件人地址  
	    
	/*    System.out.println("category:"+category);
	    System.out.println("payment:"+payment);
	    System.out.println("goods_id:"+goods_id);
	    System.out.println("number:"+number);
	    System.out.println("order_message:"+order_message);
	    System.out.println("all_price:"+all_price);
	    System.out.println("recipient_name:"+recipient_name);
	    System.out.println("recipient_tel:"+recipient_tel);
	    System.out.println("recipient_address:"+recipient_address);
	    System.out.println("user_id:"+user_id);*/
	    
	    
    	//定死的参数
    	String key = "S5DE1NpKQy2IqGozbUBBISDq5kbwxu1y";//key 商户密匙
    	String appid = "wx4fe37f6b639a23c3";//公众账号ID
    	String mch_id="1363258202";//商户号
    	String trade_type="JSAPI";//交易类型
    	String body="膳本善杜仲系列专营店";//商品描述      	
    	Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");	
		String out_trade_no = dateFormat.format(date);//商户订单号
		String nonce_str = WXPayUtil.generateNonceStr();//随机字符串 	
    	String spbill_create_ip = "121.42.169.210";//终端IP
    	String notify_url = "http://121.42.169.210/mall/paymentReturn";//通知地址  
    	
    	//String user_id = request.getParameter("user_id");//前端传递 用户ID
    	//String price = request.getParameter("price");//前段传递  总价
    	User user = userService.selectByid(user_id);//根据用户ID找到openId
    	String openId = user.getOpenId();
    	String total_fee = p;//标价金额
    	//String openid = "oiVLZwJ_rt7TwLti0ZvSUJZ4G7rA";
    	String openid = openId;
    	
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("appid", appid);
    	map.put("mch_id", mch_id);
    	map.put("nonce_str", nonce_str);
    	map.put("body", body);
    	map.put("out_trade_no", out_trade_no);
    	map.put("spbill_create_ip", spbill_create_ip);
    	map.put("notify_url", notify_url);
    	map.put("trade_type", trade_type);
    	map.put("total_fee", total_fee);
    	map.put("openid", openid);
    	
    	String sign = WXPayUtil.generateSignedXml(map, key);
    	String result = httpsRequest(  
                "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",  sign);
//    	System.out.println("-----"+result);
    	Map<String, String> m =  WXPayUtil.xmlToMap(result);//xml字符串转map
    	String prepay_id ="prepay_id="+ m.get("prepay_id");
    	//System.out.println("获取prepay_id："+m.get("prepay_id"));
    	String str = WXPayUtil.generateNonceStr();//随机字符串   
    	long currentTimeMillis = System.currentTimeMillis();//生成时间戳
    	long second = currentTimeMillis / 1000L;//（转换成秒）
    	String seconds = String.valueOf(second).substring(0, 10);//（截取前10位）
    	Map<String, String> aa = new HashMap<String, String>();
    	aa.put("appId", appid);
    	aa.put("timeStamp", seconds);
    	aa.put("nonceStr", str);
    	aa.put("package",prepay_id);
    	aa.put("signType", "MD5");
    	
    	String sign2 = WXPayUtil.generateSignature(aa, key);
    /*	System.out.println("appid:"+appid);
    	System.out.println("timeStamp:"+seconds);
    	System.out.println("nonceStr:"+str);
    	System.out.println("package:"+prepay_id);
    	System.out.println("sign:"+sign2);*/
    	request.setAttribute("appid",appid);//公众号ID
    	request.setAttribute("timeStamp",seconds);//时间戳
    	request.setAttribute("nonceStr",str);//随机字符串
    	request.setAttribute("packagea",prepay_id);//订单详情扩展字符串
    	request.setAttribute("signType","MD5");//签名方式
    	request.setAttribute("paySign",sign2);//签名 
    	//request.setAttribute("user_id",user_id);
    	
    	request.setAttribute("category",category);
 	    request.setAttribute("payment",payment);
 	    request.setAttribute("goods_id",goods_id);
 	    request.setAttribute("number",number);
 	    request.setAttribute("user_id",user_id);
 	    request.setAttribute("order_message",order_message);
 	    request.setAttribute("all_price",all_price);
 	    request.setAttribute("recipient_name",recipient_name);
 	    request.setAttribute("recipient_tel",recipient_tel);
 	    request.setAttribute("recipient_address",recipient_address);
    	return "payment.jsp";
    	
    }
    
    public static void main(String[] args) throws Exception {
    	String key = "S5DE1NpKQy2IqGozbUBBISDq5kbwxu1y";
    	String appid = "wx4fe37f6b639a23c3";//公众账号ID
    	String mch_id="1363258202";//商户号
    	String nonce_str = WXPayUtil.generateNonceStr();//随机字符串   	
    	String body="250ml";//商品描述
    	Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");	
		String out_trade_no = dateFormat.format(date);//商户订单号
		System.out.println("out_trade_no:"+out_trade_no);
    	String total_fee = "1";//标价金额
    	String spbill_create_ip = "121.42.169.210";//终端IP
    	String notify_url = "http://121.42.169.210/mall/ceshi.jsp";//通知地址
    	String trade_type="JSAPI";//交易类型
    	String openid = "oiVLZwCthILN5ArbjyxS0k6gC2uo";
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("appid", appid);
    	map.put("mch_id", mch_id);
    	map.put("nonce_str", nonce_str);
    	map.put("body", body);
    	map.put("out_trade_no", out_trade_no);
    	map.put("spbill_create_ip", spbill_create_ip);
    	map.put("notify_url", notify_url);
    	map.put("trade_type", trade_type);
    	map.put("total_fee", total_fee);
    	map.put("openid", openid);
       	
    	/*String url1 = formatUrlMap(map, true, true); //字典序 	
//        System.out.println("!!"+url1);
    	System.out.println("++:"+url1);
    	String stringSignTemp=url1+"&key=jianlemeijianlemeijianlemei52020";
    	String a = WXPayUtil.MD5(stringSignTemp);//签名
    	System.out.println("a"+a);*/
    	
    	String sign = WXPayUtil.generateSignedXml(map, key);
    	String result = httpsRequest(  
                "https://api.mch.weixin.qq.com/pay/unifiedorder", "POST",  sign);
    	System.out.println("-----"+result);
    	Map<String, String> m =  WXPayUtil.xmlToMap(result);//xml字符串转map
    	//System.out.println("获取prepay_id："+m.get("prepay_id"));
    	String str = WXPayUtil.generateNonceStr();//随机字符串   
    	long currentTimeMillis = System.currentTimeMillis();//生成时间戳
    	long second = currentTimeMillis / 1000L;//（转换成秒）
    	String seconds = String.valueOf(second).substring(0, 10);//（截取前10位）
    	System.out.println("seconds:"+seconds);
    	Map<String, String> aa = new HashMap<String, String>();
    	aa.put("appid", appid);
    	aa.put("timeStamp", seconds);
    	aa.put("nonceStr", str);
    	aa.put("package",m.get("prepay_id"));
    	aa.put("signType", "MD5");
    	
    	String a = formatUrlMap(aa, true, true);
    	System.out.println("a:"+a);
    	
    	
    	String sign2 = WXPayUtil.generateSignature(aa, key);
    	System.out.println("~~"+"prepay_id=" + m.get("prepay_id"));
    	System.out.println("!!"+sign2);
    	System.out.println("appid"+appid);
    	/*request.setAttribute("appid",appid);//公众号ID
    	request.setAttribute("timeStamp",seconds);//时间戳
    	request.setAttribute("nonceStr",str);//随机字符串
    	request.setAttribute("package",m.get("prepay_id"));//订单详情扩展字符串
    	request.setAttribute("signType","MD5");//签名方式
    	request.setAttribute("paySign",sign2);//签名
*/   	
    	
	}
    
    
    
    //请求方法
    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {  
        try {  
             
            URL url = new URL(requestUrl);  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            
            conn.setDoOutput(true);  
            conn.setDoInput(true);  
            conn.setUseCaches(false);  
            // 设置请求方式（GET/POST）  
            conn.setRequestMethod(requestMethod);  
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");  
            // 当outputStr不为null时向输出流写数据  
            if (null != outputStr) {  
                OutputStream outputStream = conn.getOutputStream();  
                // 注意编码格式  
                outputStream.write(outputStr.getBytes("UTF-8"));  
                outputStream.close();  
            }  
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
            return buffer.toString();  
        } catch (ConnectException ce) {  
            System.out.println("连接超时：{}"+ ce);  
        } catch (Exception e) {  
            System.out.println("https请求异常：{}"+ e);  
        }  
        return null;  
    }
    
    /**
     * 首页
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value ="/index",method = RequestMethod.GET)
    protected String index(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
    	request.setAttribute("user_id",user_id);
    	return "index.jsp";
    	
    }
    
    @ResponseBody
	@RequestMapping(value ="/ceshi",method = RequestMethod.GET)
	public Map<String, Object> IOS(){
		Map<String, Object> resultMap = new HashMap();
		String status = "yes";
		resultMap.put("status", status);
		resultMap.put("result", "0");
		resultMap.put("message", "成功!");
		return resultMap;	
	}
    
    /**
     * ASCII码从小到大排序（字典序）
     * @param paraMap
     * @param urlEncode
     * @param keyToLower
     * @return
     */
    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower)  
    {  
        String buff = "";  
        Map<String, String> tmpMap = paraMap;  
        try  
        {  
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(tmpMap.entrySet());  
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）  
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>()  
            {  
   
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2)  
                {  
                    return (o1.getKey()).toString().compareTo(o2.getKey());  
                }  
            });  
            // 构造URL 键值对的格式  
            StringBuilder buf = new StringBuilder();  
            for (Map.Entry<String, String> item : infoIds)  
            {  
                if (StringUtils.isNotBlank(item.getKey()))  
                {  
                    String key = item.getKey();  
                    String val = item.getValue();  
                    if (urlEncode)  
                    {  
                        val = URLEncoder.encode(val, "utf-8");  
                    }  
                    if (keyToLower)  
                    {  
                        buf.append(key.toLowerCase() + "=" + val);  
                    } else  
                    {  
                        buf.append(key + "=" + val);  
                    }  
                    buf.append("&");  
                }  
   
            }  
            buff = buf.toString();  
            if (buff.isEmpty() == false)  
            {  
                buff = buff.substring(0, buff.length() - 1);  
            }  
        } catch (Exception e)  
        {  
           return null;  
        }  
        return buff;  
    }
    
	/*@ResponseBody
	@RequestMapping(value ="/selectOpenid111",method = RequestMethod.GET)
    protected void token(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce= request.getParameter("nonce");
		String echostr=request.getParameter("echostr");
		PrintWriter out=response.getWriter();
		System.out.println(signature+ signature);
		System.out.println(timestamp+ timestamp);
		System.out.println(nonce+ nonce);
		System.out.println(echostr+ echostr);
		//��֤����ȷ�ϳɹ�ԭ������echostr�������ݣ��������Ч����Ϊ�����߳ɹ�������
		if(ValidationUtil.chechSignature(signature, timestamp, nonce))
		{
		    out.print(echostr);
		}
		out.close();
		
	}*/
}
