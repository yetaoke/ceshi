package com.mall.controller;

import java.io.IOException;
import java.io.PrintWriter;
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
import com.mall.pojo.Order;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.AdminService;
import com.mall.service.CollectionService;
import com.mall.service.GoodsService;
import com.mall.service.OrderService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class AdminController {
	
	@Resource
	private AdminService adminService;
	@Resource
	private OrderService orderService;
    
	/**
	 * 管理员登录
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/adminLogin",method = RequestMethod.GET)
    protected String adminLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  String admin = request.getParameter("admin");//用户ID
      String password = request.getParameter("password");
      if(adminService.Login(admin, password)!=null){
    	  List<Order> orderlist =  orderService.selectOrder();//获取所有订单
    	  request.setAttribute("orderlist",orderlist);
    	  return "admin/index.jsp";
      }else{
    	  request.setAttribute("error","您输入的账号或者密码错误！");
    	  return "admin/login.jsp";  
      }   
    }
}
