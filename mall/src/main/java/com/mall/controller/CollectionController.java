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
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.CollectionService;
import com.mall.service.GoodsService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class CollectionController {
	
	@Resource
	private CollectionService collectionService;
	@Resource
	private Shopping_cartService shopping_cartService;
    
	/**
	 * 在商品详情添加商品收藏和取消收藏
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/collection",method = RequestMethod.GET)
    protected String insertAndDelectCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
	  int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
      String goods_id = request.getParameter("goods_id");
      Integer category = Integer.parseInt(request.getParameter("category"));     
      //category 1为收藏	2为取消收藏
      if(category==1){
    	  System.out.println("正在添加收藏...");
    	  collectionService.insertCollection(user_id, goods_id);
    	  return "goods?goods_id="+goods_id;
      }else{
    	  System.out.println("正在取消收藏...");
    	  collectionService.deleteCollection(user_id, goods_id);
    	  return "goods?goods_id="+goods_id;
      }
      
    }
	
	/**
	 * 获取用户的收藏
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectCollection",method = RequestMethod.GET)
    protected String selectCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		List<Collection> collection = collectionService.selectCollection(user_id);
		request.setAttribute("collection",collection);
		request.setAttribute("user_id",user_id);
		return "myEnshrine.jsp";
		
	}
	
	/**
	 * 在我的收藏删除收藏
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/deleteCollection",method = RequestMethod.GET)
    protected String deleteCollection(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
	    String goods_id = request.getParameter("goods_id");    
	    collectionService.deleteCollection(user_id, goods_id);
	    return "selectCollection?user_id="+user_id;     
    }
	
	
	/**
	 * 在我的收藏添加到购物车
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/addShoppingCart",method = RequestMethod.GET)
    protected String InsertShoppingCart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id =request.getParameter("goods_id");
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		Shopping_cart shoppingCart = shopping_cartService.selectByUserIdAndGoodsId(user_id, goods_id);
		if(shoppingCart==null){
			int number = 1;
			shopping_cartService.insertShoppingCartGoods(user_id, goods_id, number);
			return "selectCollection?user_id="+user_id;
		}else{
			int number = shoppingCart.getNumber()+1;
			shopping_cartService.updateShoppingCart(user_id, goods_id, number);
			return "selectCollection?user_id="+user_id;
			
		}
		
	}
}
