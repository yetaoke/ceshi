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

import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;
import com.mall.util.tool.FengeString;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class Shopping_cartController {
	
	@Resource
	private Shopping_cartService shopping_cartService;
	
	/**
	 * 查询购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/shoppingCart",method = RequestMethod.GET)
    protected String selectShoppingCart(HttpServletRequest request, HttpServletResponse response) throws Exception {
      
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		//测试
		  //Integer id = 1;
	      List<Shopping_cart> shopping = shopping_cartService.selectShoppingCart(user_id);	      
	      request.setAttribute("shopping",shopping); 
	      request.setAttribute("user_id",user_id);
	      return "basket.jsp";
    }
	
	/**
	 * 根据购物车ID删除购物车商品
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/deleteShoppingCart",method = RequestMethod.GET)
    protected String delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String shoppingCart_id = request.getParameter("bid");
		//String id = new String(request.getParameter("id").getBytes("GBK"),"UTF-8");	
		System.out.println("!!:"+shoppingCart_id);
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		List<String> shoppingCartId = FengeString.FengeGoods_id(shoppingCart_id);
		shopping_cartService.deleteShoppingCartGoods(shoppingCartId);		
		return "shoppingCart?"+user_id;
		
	}
	
	/**
	 * 添加到购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/insertShoppingCart",method = RequestMethod.GET)
    protected String insertShoppingCart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id =request.getParameter("goods_id");
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		Integer num = Integer.parseInt(request.getParameter("number"));
//		System.out.println("num"+num);
		Shopping_cart shoppingCart = shopping_cartService.selectByUserIdAndGoodsId(user_id, goods_id);
		if(shoppingCart==null){
			shopping_cartService.insertShoppingCartGoods(user_id, goods_id, num);
			return "goods?" + goods_id;
		}else{
			/*Integer number1 = Integer.parseInt(request.getParameter("number"));
			int number =  shoppingCart.getNumber()+number1;*/
			//int number = 1;//测试
			int number = num+shoppingCart.getNumber();
			shopping_cartService.updateShoppingCart(user_id, goods_id, number);
			return "goods?" + goods_id;
			
		}
	}
}
