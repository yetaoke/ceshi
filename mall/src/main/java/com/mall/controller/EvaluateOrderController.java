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
public class EvaluateOrderController {
	
	@Resource
	private AdminService adminService;
	@Resource
	private OrderService orderService;
    
}
