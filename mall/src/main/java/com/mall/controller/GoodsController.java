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

import com.mall.pojo.Evaluation;
import com.mall.pojo.Goods;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.CollectionService;
import com.mall.service.EvaluationService;
import com.mall.service.GoodsService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;
import com.mall.util.tool.FengeString;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class GoodsController {
	
	@Resource
	private GoodsService goodsService;
	@Resource
	private EvaluationService evaluationService;
	@Resource
	private CollectionService collectionService;
    
	/**
	 * 进入商品详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/goods",method = RequestMethod.GET)
    protected String doPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*System.out.println("JVM MAX MEMORY: " + Runtime.getRuntime().maxMemory()/1024/1024+"M");
	    System.out.println("JVM IS USING MEMORY:" + Runtime.getRuntime().totalMemory()/1024/1024+"M");
	    System.out.println("JVM IS FREE MEMORY:" + Runtime.getRuntime().freeMemory()/1024/1024+"M");*/
	  int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
	  String goods_id = request.getParameter("goods_id");//商品ID
	  List<Evaluation> evaluation = evaluationService.selectEvaluation(goods_id);//获取该商品的所有评价
	  int evaluationNumber = evaluationService.selectEvaluationNumber(goods_id);//查询该商品的所有评价数量
      Goods goods = goodsService.selectGoods(goods_id);    
      String detalls = goods.getDetails();     
      String cover = goods.getCover();
      String name = goods.getName();
      double price = goods.getPrice();
      String category = goods.getCategory();
      int in_stock = goods.getIn_stock();
      int sales_volume = goods.getSales_volume();
      
      List<String> goodss = FengeString.FengeImg(detalls);
      
      if(collectionService.selectByUserIdAndGoodsId(user_id, goods_id)!=null){
    	  int shouchang = 1;
    	  request.setAttribute("shouchang",shouchang);
          request.setAttribute("user_id",user_id);
          request.setAttribute("goods_id",goods_id);
    	  request.setAttribute("evaluation",evaluation);
    	  request.setAttribute("evaluationNumber",evaluationNumber);
          request.setAttribute("goodss",goodss);
          request.setAttribute("cover",cover); 
          request.setAttribute("name",name); 
          request.setAttribute("price",price);
          request.setAttribute("category",category);
          request.setAttribute("in_stock",in_stock);
          request.setAttribute("sales_volume",sales_volume);
          return "goods.jsp";
      }else{
    	  int shouchang = 0;
      request.setAttribute("shouchang",shouchang);
      request.setAttribute("user_id",user_id);
      request.setAttribute("goods_id",goods_id);
	  request.setAttribute("evaluation",evaluation);
	  request.setAttribute("evaluationNumber",evaluationNumber);
      request.setAttribute("goodss",goodss);
      request.setAttribute("cover",cover); 
      request.setAttribute("name",name); 
      request.setAttribute("price",price);
      request.setAttribute("category",category);
      request.setAttribute("in_stock",in_stock);
      request.setAttribute("sales_volume",sales_volume);
      return "goods.jsp";
      }
    }
	
	/*测试*/
	@ResponseBody
	@RequestMapping(value ="/goods1",method = RequestMethod.GET)
    protected List<Goods> doPost1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id = "1,2,4";
		List<String> goodss = FengeString.FengeGoods_id(goods_id);
		List<Goods> goods = goodsService.selectGoods1(goodss);
		return goods;	
	}
	
	/**
	 * 分类模块
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectAllGoods",method = RequestMethod.GET)
    protected String selectAllGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		//商品分类 1茶叶 2其它 3牙膏 4皂
		String category1 = "1";
		String category2 = "2";
		String category3 = "3";
		String category4 = "4";
		String status = "1";
		List<Goods> goods1 = goodsService.selectGoodsByCategory(category1,status);
		List<Goods> goods2 = goodsService.selectGoodsByCategory(category2,status);
		List<Goods> goods3 = goodsService.selectGoodsByCategory(category3,status);
		List<Goods> goods4 = goodsService.selectGoodsByCategory(category4,status);
		request.setAttribute("goods1",goods1);
		request.setAttribute("goods2",goods2);
		request.setAttribute("goods3",goods3);
		request.setAttribute("goods4",goods4);
		request.setAttribute("user_id",user_id);
		return "classify.jsp";
		
	}
	
	/**
	 * 客户端首页模糊查询
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/searchGoods",method = RequestMethod.GET)
    protected String searchGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID		
//		String n = new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");//评价内容
		String n = request.getParameter("name");
		System.out.println(n);
		String name = "%"+n+"%";
		
		List<Goods> goods = goodsService.searchGoods(name);
		request.setAttribute("goods",goods);
		request.setAttribute("user_id",user_id);
		return "search.jsp";	
	}

	/*----------------------------------------下面是管理模块------------------------------------------------*/
	
	/**
	 * 初始化商品信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/adminAllGoods",method = RequestMethod.GET)
    protected String adminAllGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Goods> listGoods = goodsService.selectAllGoods();
		request.setAttribute("listGoods",listGoods);
		return "admin/goods.jsp";	
	}
	
	/**
	 * 根据条件查询商品
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/adminByIdOrName",method = RequestMethod.GET)
    protected String adminByIdOrName(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id = request.getParameter("goods_id");
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");
		/*String name = request.getParameter("name");*/
//		System.out.println("name:"+name);
		if(!goods_id.equals("") && name.equals("")){
			List<Goods> listGoods = goodsService.selectByGoodsId(goods_id);
			request.setAttribute("listGoods",listGoods);
			return "admin/goods.jsp";
		}else if(goods_id.equals("") && !name.equals("")){
			List<Goods> listGoods = goodsService.selectByName(name);
			request.setAttribute("listGoods",listGoods);
			return "admin/goods.jsp";
		}else if(!goods_id.equals("") && !name.equals("")){
			List<Goods> listGoods = goodsService.selectByNameAndGoodsId(goods_id, name);
			request.setAttribute("listGoods",listGoods);
			return "admin/goods.jsp";
		}else{
			return "admin/adminAllGoods";
		}
	}
	
	/**
	 * 管理员查看商品详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectDetails",method = RequestMethod.GET)
    protected String selectDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id = request.getParameter("goods_id");
		Goods goods = goodsService.selectGoods(goods_id);
		String details = goods.getDetails();
		List<String> img = FengeString.FengeImg(details);
		request.setAttribute("goods",goods);
		request.setAttribute("img",img);
		return "admin/goodsdetail.jsp";	
	}
	
	/**
	 * 添加商品
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/insertGoods",method = RequestMethod.GET)
    protected String insertGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String name = request.getParameter("name");//商品名字
		double price = Integer.parseInt(request.getParameter("price"));//商品价格
		String category = request.getParameter("category");//商品分类 1茶叶 2其它 3牙膏 4皂
		String cover = request.getParameter("cover");//商品封面
		String details = request.getParameter("details");//商品详情
		Integer in_stock = Integer.parseInt(request.getParameter("in_stock"));//库存
		Goods goods  = new Goods();
		goods.setName(name);
		goods.setPrice(price);
		goods.setCategory(category);
		goods.setCover(cover);
		goods.setDetails(details);
		goods.setIn_stock(in_stock);
		goods.setStatus("1");
		goodsService.insert(goods);
		return "/admin/adminAllGoods";

	}
	
	/**
	 * 可批量删除商品
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/deleteGoods",method = RequestMethod.GET)
    protected String deleteGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String goods_id = request.getParameter("goods_id");
		List<String> gs = FengeString.FengeGoods_id(goods_id);
		goodsService.delete(gs);
		return "/admin/adminAllGoods";

	}
	
	/**
	 * 可批量修改商品状态  上架或者下架
	 * @param goods_id
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/updateStatus",method = RequestMethod.GET)
    protected String updateStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
			String goods_id = request.getParameter("goods_id");
			String status = request.getParameter("status");
//			System.out.println("!:"+goods_id);
			List<String> goodsId = FengeString.FengeGoods_id(goods_id);
			int size = goodsId.size();  
			String[] arr = (String[])goodsId.toArray(new String[size]);
			for(int i=0;i<arr.length;i++){
				goodsService.updateStatus(arr[i],status);			
			}
		return "/admin/adminAllGoods";
	}
	
	/**
	 * 修改商品信息
	 * @param goods
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/updateGoods",method = RequestMethod.GET)
    protected String updateGoods(Goods goods) throws Exception {
		goodsService.updateGoods(goods);
		return "/admin/adminAllGoods";
		
	}
}