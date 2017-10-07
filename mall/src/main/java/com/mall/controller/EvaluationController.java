package com.mall.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.pojo.Collection;
import com.mall.pojo.EvaluateOrder;
import com.mall.pojo.Evaluation;
import com.mall.pojo.Goods;
import com.mall.pojo.Order;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.CollectionService;
import com.mall.service.EvaluateOrderService;
import com.mall.service.EvaluationService;
import com.mall.service.GoodsService;
import com.mall.service.OrderService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;
import com.mall.util.tool.FengeString;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class EvaluationController {
	
	@Resource
	private EvaluationService evaluationService;
	@Resource
	private OrderService orderService;
	@Resource
	private EvaluateOrderService evaluateOrderService;

/*	
	*//**
	 * 获取所有商品评价
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 *//*
	@RequestMapping(value ="/selectEvaluation",method = RequestMethod.GET)
    protected String selectEvaluation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		Integer goods_id = Integer.parseInt(request.getParameter("goods_id"));//商品ID
		List<Evaluation> evaluation = evaluationService.selectEvaluation(user_id, goods_id);
		request.setAttribute("evaluation",evaluation); 		
		return "evaluation.jsp";
		
	}*/
	
	/**
	 * 用户点击评价后，跳转到评价页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/evaluation",method = RequestMethod.GET)
    protected String selectEvaluation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Integer user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		Integer goods_id = Integer.parseInt(request.getParameter("goods_id"));//商品ID
		Integer id = Integer.parseInt(request.getParameter("id"));//评价商品ID
		String order_id = request.getParameter("order_id");
		request.setAttribute("user_id",user_id);
		request.setAttribute("id",id);
		request.setAttribute("order_id",order_id);
		request.setAttribute("goods_id",goods_id);
		return "evaluation.jsp";
		
	}
	
	/**
	 * 添加评价
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/insertEvaluation",method = RequestMethod.GET)
    protected String insertEvaluation(HttpServletRequest request, HttpServletResponse response) throws Exception {
		/*String number = request.getParameter("number");//商品数量 
*/		String number = "1";
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		int id = Integer.parseInt(request.getParameter("id"));//评价商品ID
//		System.out.println("id:"+id);
		String order_id = request.getParameter("order_id");//订单ID
		String goods_id = request.getParameter("goods_id");//商品ID
		String star = request.getParameter("star");//星级		
		String content = new String(request.getParameter("content").getBytes("ISO-8859-1"),"UTF-8");//评价内容
		
//		System.out.println("goods_id"+goods_id);
//		System.out.println("star"+star);
//		System.out.println("content"+content);
		
		EvaluateOrder evaluateOrder = new EvaluateOrder();
		evaluateOrder.setId(id);
		evaluateOrder.setStatus(2);
		evaluateOrderService.updateEvaluateOrder(evaluateOrder);
		
		/*String id = request.getParameter("id");//订单ID
		//根据传过来的订单ID查找订单，把订单中的goods_id(商品ID)查出来，然后遍历goods_id，当遍历到goods_id等于用户传过来的商品ID时把这个商品ID所在的下标取出来
		//然后遍历evaluationStatus(评论状态)，根据遍历goods_id所得到的下标取出evaluationStatus，然后改为2。
		Order od = orderService.selectOrderById(id);
		List<String> gs = FengeString.FengeGoods_id(od.getGoods_id());
		List<String> es = FengeString.FengeGoods_id(od.getEvaluationStatus());
		int size = gs.size();//获取评论状态数组size
		int sz = es.size();//获取商品ID数组size
		String[] s = (String[])gs.toArray(new String[size]);//得到状态数组
		String[] g = (String[])es.toArray(new String[sz]);//得到商品数组
		int xb = 0;
		for(int i=0;i<g.length;i++){
			if(g[i].equals(goods_id)){
				xb = i;
			}
		}
		for(int j=0;j<s.length;j++){
			if(j==xb){
				s[j] = "2";
			}
		}
		String evaluationStatus = StringUtils.join(s, ",");//数组转换成字符串,以逗号分割	
		Order order = new Order();
		order.setId(id);
		order.setEvaluationStatus(evaluationStatus);
		orderService.updateOrder(order);*/
		evaluationService.insertEvaluation(number,user_id, goods_id, star, content);	
		return "my?user_id="+user_id;
		
	}
	
	public static void main(String[] args) {
		/*int[] a = new int[]{1,2,3,4,5,7,4,9};
		for(int i = 0; i < a.length; i++) {
			if(a[i] == 3){
				a[i] = 0;
			}
		}
		for(int i = 0; i < a.length; i++) {
				System.out.println(""+a[i]);
			}*/
		
		/*String[] c = new String[]{"1","2","3","4","5","7","4","9"};
		String evaluationStatus = StringUtils.join(c, ",");//数组转换成字符串,以逗号分割	
		System.out.println(evaluationStatus);*/
		
		}
}
