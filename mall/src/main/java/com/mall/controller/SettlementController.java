package com.mall.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import com.mall.pojo.Goods;
import com.mall.pojo.Money;
import com.mall.pojo.Order;
import com.mall.pojo.Settlement;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.AdminService;
import com.mall.service.CollectionService;
import com.mall.service.GoodsService;
import com.mall.service.MoneyService;
import com.mall.service.OrderService;
import com.mall.service.SettlementService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;
import com.mall.util.tool.FengeString;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class SettlementController {
	
	@Resource
	private SettlementService settlementService;
	@Resource
	private MoneyService moneyService;
	@Resource
	private GoodsService goodsService;
	@Resource
	private OrderService orderService;
    
	/**
	 * 退款
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/refund",method = RequestMethod.GET)
    protected String refund(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));//订单ID
		String goods_id = request.getParameter("goods_id");
		Settlement settlement = settlementService.selectById(id);
		List<String> fg = FengeString.FengeGoods_id(goods_id);
		List<String> fenge = FengeString.FengeGoods_id(settlement.getGoods_id());
		List<String> number = FengeString.FengeGoods_id(settlement.getNumber());
		List<Integer> lint = new ArrayList<Integer>();
		for(String str:number){
		if(!str.matches("^([0-9])+$")){
		     continue;
		  }
		  int i = Integer.parseInt(str); 
		  lint.add(i); 
		}
		int[] a = new int[lint.size()];
        for(int i=0;i<lint.size();i++){
            a[i] = lint.get(i);
        }
		
		String[] str = new String[fenge.size()];
		int[] num = new int[fg.size()];
		for(int i=0;i<fenge.size();i++){
			str[i] = fenge.get(i);
		}	 
		for(int i=0;i<fenge.size();i++){
			for(int j=0;j<fg.size();j++){
				if(fenge.get(i).equals(fg.get(j))){
					str[i] = "2";	
					num[i] = lint.get(i) ;
				}
			}		
		}
		List<Goods> goods = goodsService.selectGoods1(fg);//查询要修改的商品信息
		List<Integer> p = new ArrayList<Integer>();
		for(Goods g:goods){
			p.add((int) g.getPrice());
		}
		
		int[] n = new int[p.size()];
	       for(int i=0;i<p.size();i++){
	           n[i] = p.get(i);
	       }
		
		double db = 0;
		for(int i=0;i>n.length;i++){
			db = db+(n[i]*num[i]);//通过for循环，去除数组中的元素，累加到db中
		}
		String str1=StringUtils.join(str, ",");
		settlement.setOrder_time(settlement.getOrder_time());
		settlement.setStatus(str1);
		settlement.setGoods_id(settlement.getGoods_id());
		settlement.setUser_id(settlement.getUser_id());
		settlement.setOrder_id(settlement.getOrder_id());
		settlement.setPrice(db-db*2);
		settlementService.insertSettlement(settlement);
		/*settlementService.update(id,str1);*/
		Money money = moneyService.select();
		money.setId(money.getId());
		money.setBalance(money.getIncome()-db);//总收入减去要退货的商品价格总和  等于余额
		moneyService.update(money);
		return "admin/selectSettlement";	  
    }
	
	/**
	 * 后台管理结算模块显示
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectAllSettlement",method = RequestMethod.GET)
    protected String selectAllSettlement(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Settlement> settlementList = settlementService.selectAllSettlement();
		Money money = moneyService.select();
		request.setAttribute("settlementList",settlementList); 
		request.setAttribute("money",money); 
		return "admin/account.jsp";	  
    }
	
	/**
	 * 结算订单详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectSettlement",method = RequestMethod.GET)
    protected String selectSettlement(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int id = Integer.parseInt(request.getParameter("id"));//结算表ID	
		String order_id = request.getParameter("order_id");//订单ID
		Settlement settlement = settlementService.selectById(id);
		List<String> gs = FengeString.FengeGoods_id(settlement.getGoods_id());
		List<String> status = FengeString.FengeGoods_id(settlement.getStatus());
//		System.out.println("11"+status);
		List<String> number = FengeString.FengeGoods_id(settlement.getNumber());
		List<Goods> goods = goodsService.selectGoods1(gs);
		Order order = orderService.selectOrderById(order_id);
		request.setAttribute("settlement",settlement);
		request.setAttribute("goods",goods);
		request.setAttribute("status",status);
		request.setAttribute("order",order);
		request.setAttribute("number",number);
		return "admin/managedetail.jsp";	  
    }
	
	@RequestMapping(value ="/admin/selectpriceStatus",method = RequestMethod.GET)
    protected String selectpriceStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("category");//1为支出  2为收入
		if(category.equals("1")){
			List<Settlement> settlementList = settlementService.selectpriceless();
			Money money = moneyService.select();
			request.setAttribute("settlementList",settlementList); 
			request.setAttribute("money",money);
			return "admin/account.jsp";
		}else{
			List<Settlement> settlementList = settlementService.selectpricegreater();
			Money money = moneyService.select();
			request.setAttribute("settlementList",settlementList); 
			request.setAttribute("money",money);
			return "admin/account.jsp";
		}
			  
    }
	
	public static void main(String[] args) {
		/*String[] ary = {"1", "2", "1","1","2"};
		String str1=StringUtils.join(ary, ",");
		System.out.println(str1);*/
		/*int sum=0;//定义一个变量
		int arr[] = {1,2,3,4};//定义一个需要累加的数组
		for(int i=0;i<arr.length;i++){
			  sum = sum+arr[i];//通过for循环，去除数组中的元素，累加到sum中
			}
		System.out.println(sum);*/
		double a = 20;
		System.out.println(a-a*2);
	}
}
