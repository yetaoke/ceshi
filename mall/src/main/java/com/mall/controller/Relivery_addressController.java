package com.mall.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.pojo.Collection;
import com.mall.pojo.Goods;
import com.mall.pojo.Relivery_address;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.CollectionService;
import com.mall.service.GoodsService;
import com.mall.service.Relivery_addressService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class Relivery_addressController {
	
	@Resource
	private Relivery_addressService relivery_addressService;
	
	
	
	/**
	 * 订单页面获取用户的收获地址  Json
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/selectRelivery_addressOrder",method = RequestMethod.GET)
    protected Map<String, Object> selectRelivery_addressOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		//Integer user_id = 1;
		Map<String, Object> resultMap = relivery_addressService.selectCollectionByIdOrder(user_id);
		return resultMap;	
	}
	
	/**
	 * 订单页面添加用户收获地址  Json
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/insertRelivery_addressOrder",method = RequestMethod.GET)
    protected Map<String, Object> insertRelivery_addressOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		System.out.println("开始");
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		String recipient_name = request.getParameter("recipient_name");
		String recipient_tel = request.getParameter("recipient_tel");
		String recipient_address = request.getParameter("recipient_address");
		//String name =new String(recipient_name.getBytes("ISO-8859-1"),"UTF-8");
		//String address =new String(recipient_address.getBytes("ISO-8859-1"),"UTF-8");
		relivery_addressService.insertRelivery_address(recipient_name, recipient_tel,recipient_address,user_id);
		Map<String, Object> resultMap = new HashMap();
		List<Relivery_address> relivery_addressList = relivery_addressService.selectCollectionById(user_id);
		resultMap.put("relivery_addressList", relivery_addressList);
		resultMap.put("result", "0");
		resultMap.put("message", "添加成功!");
		return resultMap;	
	}
	
	/**
	 * 订单页面删除用户收获地址  Json
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/deleteRelivery_addressOrder",method = RequestMethod.GET)
    protected Map<String, Object> deleteRelivery_addressOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int relivery_id = Integer.parseInt(request.getParameter("relivery_id"));//收货地址的ID
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		relivery_addressService.deleteRelivery_address(relivery_id);
		Map<String, Object> resultMap = new HashMap();
		List<Relivery_address> relivery_addressList = relivery_addressService.selectCollectionById(user_id);
		if(relivery_addressList!=null){
			resultMap.put("relivery_addressList", relivery_addressList);
			resultMap.put("result", "0");
			resultMap.put("message", "删除成功!");
			return resultMap;
		}else{
			resultMap.put("result", "1");
			resultMap.put("message", "没有收货地址!");
			return resultMap;	
		}
		
	}
	
	/**
	 * 获取用户所有的收货地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectRelivery_address",method = RequestMethod.GET)
    protected String selectRelivery_address(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		//Integer user_id = 1;
		List<Relivery_address> relivery_address = relivery_addressService.selectCollectionById(user_id);
		request.setAttribute("relivery_address",relivery_address);
		request.setAttribute("user_id",user_id);
		return "address.jsp";	
	}
	
	/**
	 * 添加收货地址
	 * @param request
	 * @param response
	 * @param relivery_address
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/insertRelivery_address",method = RequestMethod.GET)
    protected String insertRelivery_address(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		String recipient_name = request.getParameter("recipient_name");
		String recipient_tel = request.getParameter("recipient_tel");
		String recipient_address = request.getParameter("recipient_address");
		/*String name =new String(recipient_name.getBytes("ISO-8859-1"),"UTF-8");
		String address =new String(recipient_address.getBytes("ISO-8859-1"),"UTF-8");*/
		/*System.out.println("recipient_name="+recipient_name);
		System.out.println("user_id="+user_id);	
		System.out.println("recipient_tel="+recipient_tel);	
		System.out.println("recipient_address="+recipient_address);	*/
		relivery_addressService.insertRelivery_address(recipient_name, recipient_tel, recipient_address,user_id);	
		return "selectRelivery_address?"+user_id;	
	}
	
	/**
	 * 删除收货地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/deleteRelivery_address",method = RequestMethod.GET)
    protected String deleteRelivery_address(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer relivery_id = Integer.parseInt(request.getParameter("relivery_id"));//收货地址的ID
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID  返回到另一个Action
		relivery_addressService.deleteRelivery_address(relivery_id);
		return "selectRelivery_address?"+user_id;
		
	}
	
	/**
	 * 修改收货地址
	 * @param request
	 * @param response
	 * @param relivery_address
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/updateRelivery_address",method = RequestMethod.GET)
    protected String updateRelivery_address(HttpServletRequest request, HttpServletResponse response,Relivery_address relivery_address) throws Exception {
		relivery_addressService.updateRelivery_address(relivery_address);
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID  返回到另一个Action		
		return "selectRelivery_address?"+user_id;	
		
	}
	
	/**
	 * 点击添加收货地址的跳转
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/addad",method = RequestMethod.GET)
    protected String addad(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		System.out.println("~~~~~!!!!");
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID  返回到另一个Action
		request.setAttribute("user_id",user_id);
		return "addad.jsp";
		
	}
}
