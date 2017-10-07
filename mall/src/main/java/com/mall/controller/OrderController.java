package com.mall.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mall.pojo.Ceshi;
import com.mall.pojo.EvaluateOrder;
import com.mall.pojo.Goods;
import com.mall.pojo.Money;
import com.mall.pojo.Order;
import com.mall.pojo.Settlement;
import com.mall.pojo.Shop;
import com.mall.pojo.Shopping_cart;
import com.mall.pojo.User;
import com.mall.service.EvaluateOrderService;
import com.mall.service.GoodsService;
import com.mall.service.MoneyService;
import com.mall.service.OrderService;
import com.mall.service.SettlementService;
import com.mall.service.ShopService;
import com.mall.service.Shopping_cartService;
import com.mall.service.UserService;
import com.mall.util.tool.FengeString;
import com.mall.util.tool.JsonUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/")
public class OrderController {
	
	@Resource
	private OrderService orderService;
	@Resource
	private GoodsService goodsService;
	@Resource
	private Shopping_cartService shopping_cartService;
	@Resource
	private UserService userService;
	@Resource
	private ShopService shopService;
	@Resource
	private EvaluateOrderService evaluateOrderService;
	@Resource
	private SettlementService settlementService;
	@Resource
	private MoneyService moneyService;
	
	/**
	 * 后台管理获取所有订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/seleteOrder",method = RequestMethod.GET)
    protected String seleteOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Order> order = orderService.selectOrder();
		request.setAttribute("order",order);
		return "admin/order.jsp";
		
	}
	
	/**
	 * 根据状态查询订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/seleteByStatus",method = RequestMethod.GET)
    protected String seleteByStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		//订单状态（status）  1为待发货	  2为待收货	3为待评价   4为已完成
		int status = Integer.parseInt(request.getParameter("status"));
		List<Order> order = orderService.selectByStatus(status);
		request.setAttribute("order",order);
		return "admin/order.jsp";
		
	}
	
	/*@RequestMapping(value ="/admin/seleteByCategory",method = RequestMethod.GET)
    protected String seleteByCategory(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String category = request.getParameter("category");
		List<Order> order = orderService.selectByCategory(category);
		request.setAttribute("order",order);
		return "admin/order.jsp";
		
	}*/
	
	/**
	 * 后台获取订单详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/selectOrderDetails",method = RequestMethod.GET)
    protected String selectAdminOrderDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");//订单ID
		Order order = orderService.selectOrderById(id);
		String goods_id = order.getGoods_id();
		String numbers = order.getNumber();
		List<String> number = FengeString.FengeStr(numbers);
		List<String> goodss = FengeString.FengeGoods_id(goods_id);
		List<Goods> goods = goodsService.selectGoods1(goodss);
		User user = userService.selectByid(order.getUser_id());
		String name = user.getName();
		request.setAttribute("name",name);
		request.setAttribute("number",number);
		request.setAttribute("goods",goods);
		request.setAttribute("order",order);
		return "admin/orderdetail.jsp";
		
	}
	
	/**
	 * 货发出去后填写快递公司名字和订单号在订单里,状态改为待收货
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/admin/updateOrder",method = RequestMethod.GET)
    protected String updateOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");//订单ID
	    String express_name = new String(request.getParameter("express_name").getBytes("ISO-8859-1"),"UTF-8");//快递公司名字
	    String order_number = new String(request.getParameter("order_number").getBytes("ISO-8859-1"),"UTF-8");//运单号
	    int status = 2;//1为待发货  2为待收货  3为待评价 4为已成功
	    Order order1 = new Order();
	    order1.setId(id);
	    order1.setExpress_name(express_name);
	    order1.setOrder_number(order_number);
	    order1.setStatus(status);
	    orderService.updateOrder(order1);
		return "admin/seleteOrder";
		
	}
	
	/**
	 * 查看全部订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/seleteAll",method = RequestMethod.GET)
    protected String seleteAll(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		if(orderService.seleteAllOrder(user_id)!=null && !orderService.seleteAllOrder(user_id).isEmpty()){
			System.out.println("该用户有订单");
			Map<String, Object> json = this.seleteAllOrder(request, response, user_id);
//			System.out.println(json);
			request.setAttribute("json",json);
			request.setAttribute("user_id", user_id);
			return "order/allOrder.jsp";
		}else{
			System.out.println("该用户没有订单");
			return "order/nopaid.jsp";
		}
		
	}	
	/**
	 * 根据用户ID查询该用户的所有订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/seleteAllOrder",method = RequestMethod.GET)
    protected Map<String, Object> seleteAllOrder(HttpServletRequest request, HttpServletResponse response,int user_id) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		List<Order> order = orderService.seleteAllOrder(user_id);
		StringBuffer sb = new StringBuffer();
		for(Order obj:order){
			sb.append(obj.getId()+",");	
			}
		List<String> id = FengeString.FengeGoods_id(sb.toString());//遍历List转为String,每个参数通过","号隔开
		int size = id.size();
		/*System.out.println("size:"+size);*/
		String[] arr = (String[])id.toArray(new String[size]);
		Object[] s =  new Object[arr.length];
	
		for(int i=0;i<arr.length;i++){
			/*System.out.println("a"+arr[i]);*/
			Order order1 = orderService.selectOrderById(arr[i]);
			String goods_id = order1.getGoods_id();
			List<String> goodss = FengeString.FengeGoods_id(goods_id);
			List<Goods> goods = goodsService.selectGoods1(goodss);
			request.setAttribute("goods",goods);
			s[i] = goods;
			//System.out.println("s[i]"+s[i]);
						
		//System.out.println("s~"+s);	
		resultMap.put("s", s);
		resultMap.put("order", order);
		request.setAttribute("user_id", user_id);
		}
		return resultMap;
	}
	
	/**
	 * 查询所有待发货订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/*@RequestMapping(value ="/selectStatus1",method = RequestMethod.GET)
    protected String selectStatus1(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		List<Order> order = orderService.selectStatus1(user_id);
		StringBuffer sbf = new StringBuffer();
		for(Order obj:order){
			sbf.append(obj.getGoods_id()+",");
			}
		System.out.println("sbf"+sbf.toString());//遍历List转为String,每个参数通过","号隔开
		List<String> goodss = FengeString.FengeGoods_id(sbf.toString());
		List<Goods> goods = goodsService.selectGoods1(goodss);

		request.setAttribute("goods",goods);
		request.setAttribute("order",order);
		return "order/paid.jsp";
		
	}*/
	
	/**
	 * 待发货订单  json
	 * @param request
	 * @param response
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/selectStatus1",method = RequestMethod.GET)
    protected Map<String, Object> selectStatus1(HttpServletRequest request, HttpServletResponse response,int user_id) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		//int user_id1 = Integer.parseInt(request.getParameter("user_id"));//用户ID
		List<Order> order = orderService.selectStatus1(user_id);	
			StringBuffer sb = new StringBuffer();
			for(Order obj:order){
				sb.append(obj.getId()+",");	
				}
			List<String> id = FengeString.FengeGoods_id(sb.toString());//遍历List转为String,每个参数通过","号隔开
			int size = id.size();
			/*System.out.println("size:"+size);*/
			String[] arr = (String[])id.toArray(new String[size]);
			Object[] s =  new Object[arr.length];
		
			for(int i=0;i<arr.length;i++){
				/*System.out.println("a"+arr[i]);*/
				Order order1 = orderService.selectOrderById(arr[i]);
				String goods_id = order1.getGoods_id();
				List<String> goodss = FengeString.FengeGoods_id(goods_id);
				List<Goods> goods = goodsService.selectGoods1(goodss);
				request.setAttribute("goods",goods);
				s[i] = goods;
				//System.out.println("s[i]"+s[i]);
							
			}
			//System.out.println("s~"+s);	
			resultMap.put("s", s);
			resultMap.put("order", order);
			request.setAttribute("user_id", user_id);
			return resultMap;
		
	}
	
	/**
	 * 待收货订单 JSON
	 * @param request
	 * @param response
	 * @param user_id
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value ="/selectStatus2",method = RequestMethod.GET)
    protected Map<String, Object> selectStatus2(HttpServletRequest request, HttpServletResponse response,int user_id) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		//int user_id1 = Integer.parseInt(request.getParameter("user_id"));//用户ID
		List<Order> order = orderService.selectStatus2(user_id);
		StringBuffer sb = new StringBuffer();
		for(Order obj:order){
			sb.append(obj.getId()+",");
			}
		List<String> id = FengeString.FengeGoods_id(sb.toString());//遍历List转为String,每个参数通过","号隔开
		int size = id.size();  
		String[] arr = (String[])id.toArray(new String[size]);
		/*System.out.println("arr:"+arr)*/;
		Object[] s =  new Object[arr.length];
		for(int i=0;i<arr.length;i++){
			/*System.out.println("a"+arr[i]);*/
			Order order1 = orderService.selectOrderById(arr[i]);
			String goods_id = order1.getGoods_id();
			List<String> goodss = FengeString.FengeGoods_id(goods_id);
			List<Goods> goods = goodsService.selectGoods1(goodss);
			request.setAttribute("goods",goods);
			s[i] = goods;
			//System.out.println("s[i]"+s[i]);
						
		}
		//System.out.println("s~"+s);
		StringBuffer sbf = new StringBuffer();
		for(Order obj:order){
			sbf.append(obj.getGoods_id()+",");
			}
		
		List<String> goodss = FengeString.FengeGoods_id(sbf.toString());//遍历List转为String,每个参数通过","号隔开
		List<Goods> goods = goodsService.selectGoods1(goodss);
		resultMap.put("s", s);
		resultMap.put("order", order);
		resultMap.put("goods", goods);
		request.setAttribute("user_id", user_id);
		return resultMap;
		
	}
	
	/**
	 * 查询所有待发货订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectStatusOne",method = RequestMethod.GET)
    protected String selectStatus(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id1 = Integer.parseInt(request.getParameter("user_id"));//用户ID
		if(orderService.selectStatus1(user_id1)!=null && !orderService.selectStatus1(user_id1).isEmpty()){
			System.out.println("有待发货订单");
			Map<String, Object> json = this.selectStatus1(request, response, user_id1);
//			System.out.println(json);
			request.setAttribute("json",json);
			request.setAttribute("user_id", user_id1);
			return "order/paid.jsp";
		}else{
			System.out.println("没有待发货订单");
			return "order/nopaid.jsp";
		}
	}
	/**
	 * 查询所有待收货订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectStatusTwo",method = RequestMethod.GET)
    protected String selectStatus2(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		if(orderService.selectStatus2(user_id)!=null && !orderService.selectStatus2(user_id).isEmpty()){
			Map<String, Object> json = this.selectStatus2(request, response, user_id);
			System.out.println("有待收货订单");
			request.setAttribute("json",json);
			request.setAttribute("user_id", user_id);
			return "order/wait.jsp";
		}else{
			System.out.println("没有待收货订单");
			return "order/nopaid.jsp";
		}
	}
	
	/**
	 * 查询所有待评价订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectStatus3",method = RequestMethod.GET)
    protected String selectStatus3(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		int status=1;
		List<EvaluateOrder> ed = evaluateOrderService.selectEvaluateOrder(user_id, status);
		/*List<Order> order = orderService.selectStatus3(user_id);	
		StringBuffer sb = new StringBuffer();
		for(Order obj:order){
			sb.append(obj.getGoods_id()+",");
			System.out.println("sb:"+obj.getGoods_id());
			}	
		
		StringBuffer sbf = new StringBuffer();
		for(Order obj:order){
			sbf.append(obj.getEvaluationStatus()+",");
			System.out.println("sb1:"+obj.getEvaluationStatus());
			}
		List<String> id = FengeString.FengeGoods_id(sb.toString());//遍历List转为String,每个参数通过","号隔开
		List<String> zt = FengeString.FengeGoods_id(sbf.toString());//遍历List转为String,每个参数通过","号隔开
		//List<Goods> goods = goodsService.selectGoods1(id);//根据List商品ID查询
		int size = zt.size();//获取状态数组size
		int sz = id.size();//获取商品ID数组size
		String[] status = (String[])zt.toArray(new String[size]);//得到状态数组
		String[] goods_id = (String[])id.toArray(new String[sz]);//得到商品数组
		//String status[] = {};//new一个新的数组
		List<Goods> goodsa = new ArrayList<Goods>();
		for(int j=0;j<status.length;j++){
			System.out.println("gs:"+goods_id[j]);
			System.out.println("status:"+status[j]);
			if(status[j].equals("1")){
				System.out.println("!!!!!!");
				goodsa.add(goodsService.selectGoods(goods_id[j]));
			}
		}		
		
		StringBuffer strbf = new StringBuffer();
		for(Order obj:order){
			strbf.append(obj.getOrder_number());
			}
		List<String> on = FengeString.FengeGoods_id(strbf.toString());//遍历List转为String,每个参数通过","号隔开
		int i = on.size();
		String[] ss = (String[])on.toArray(new String[i]);//得到状态数组
		String orderNumber = StringUtils.join(ss, ",");//数组转换成字符串,以逗号分割	
		//request.setAttribute("goods",goods);
		request.setAttribute("orderNumber",orderNumber);
		request.setAttribute("goods",goodsa);
		request.setAttribute("user_id",user_id);*/
		request.setAttribute("goods",ed);
		request.setAttribute("user_id",user_id);
		return "order/leaveWords.jsp";
		
	}
	
	/**
	 * 确定收货
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/receivedGoods",method = RequestMethod.GET)
    protected String receivedGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
		String id = request.getParameter("id");//订单ID
		Order order = orderService.selectOrderById(id);
		String goodsId = order.getGoods_id();
		int status = 1;
		List<String> gs = FengeString.FengeGoods_id(goodsId);
		int sz = gs.size();
		String[] str = (String[])gs.toArray(new String[sz]);
		for(int i=0;i<str.length;i++){
			evaluateOrderService.insertselectEvaluateOrder(user_id, status, str[i], id);
		}
		int status1 = 3;
		Order order1 = new Order();
		order1.setStatus(status1);
		order1.setId(id);
		orderService.updateOrder(order1);
		
		return "selectStatus3?user_id="+user_id;
		
	}
	
	/**
	 * 立即购买
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/orderaaa",method = RequestMethod.GET)
    protected String Order(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String goods_id1 = request.getParameter("goods_id");//商品ID  多个商品用逗号隔开
//		System.out.println("goods_id1:"+goods_id1);
	    String number1 =  request.getParameter("number");//商品数量	多个商品用逗号隔开
//	    System.out.println("number1:"+number1);
	    String name1 =  request.getParameter("name");//商品名字	多个商品用逗号隔开
//	    System.out.println("name1:"+name1);
	    String cover1 =  request.getParameter("cover");//商品封面	多个商品用逗号隔开
//		System.out.println("cover1:"+cover1);
	    String price1 = request.getParameter("price"); //商品单价		多个商品用逗号隔开
//	    System.out.println("price1:"+ price1);
	    String user_id1 = request.getParameter("user_id");//用户ID
//	    System.out.println("user_id:"+user_id1);
	    //String[] number = number1.split(",");
	    /*List<String> cover = FengeString.FengeStr(cover1);
	    List<String> name = FengeString.FengeStr(name1);
	    List<String> number = FengeString.FengeStr(number1);
	    List<String> price = FengeString.FengeStr(price1);
	    List<String> goods_id = FengeString.FengeStr(goods_id1);//通过商品ID，把对应商品ID的商品信息拿出来
*/	     	    
	    List<String> number = FengeString.FengeStr(number1);
	    List<String> price = FengeString.FengeStr(price1);
	    
	    List<Double> lint1 = new ArrayList<Double>();
	    for(String str2:price){    	 
	    	  double i = Double.parseDouble(str2);
	    	  	lint1.add(i); 
	    	}
	    
	    List<Double> lint2 = new ArrayList<Double>();
		for(String str2:number){
			  double i = Double.parseDouble(str2);
			  	lint2.add(i); 
		}
	    
	    double[] a = new double[lint1.size()];
	    for(int i = 0;i<number.size();i++){
	        a[i] = lint1.get(i)*lint2.get(i);
	    }
	    double db = 0;//总价
	    for(int i=0;i<a.length;i++){
	    	db += a[i];
	    }
	    
	    List<Integer> lint = new ArrayList<Integer>();
		for(String str:number){
		  int i = Integer.parseInt(str); 
		  lint.add(i); 
		}
		int[] n = new int[lint.size()];
        for(int i=0;i<lint.size();i++){
            n[i] = lint.get(i);
        }
        int nb =0;//总数量
        for(int i=0;i<n.length;i++){
        	nb+=n[i];
        }
	    
	    List<String> goods_id = FengeString.FengeStr(goods_id1);
	    Goods[] goodsList = new Goods[(goods_id.size())];
	    for(int i=0;i<goods_id.size();i++){
	    	goodsList[i] = goodsService.selectGoods(goods_id.get(i));
	    }
	    
	    List<Shop> shop = shopService.selectAllShop();
		request.setAttribute("allnumber",nb);
		request.setAttribute("allprice",db);
		request.setAttribute("shop",shop);
	    request.setAttribute("goodsList",goodsList);
	    request.setAttribute("user_id",user_id1);
	    request.setAttribute("number",number1);
	    request.setAttribute("goods_id",goods_id1);
		return "orderaaa.jsp";
		
	}
	
	/**
	 * 添加订单
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/insertOrder",method = RequestMethod.GET)
    protected String insertOrder(HttpServletRequest request, HttpServletResponse response) throws Exception {
		  /*Integer category = Integer.parseInt(request.getParameter("category"));//1为商家配送 2为到店自提
		  Integer payment = Integer.parseInt(request.getParameter("payment"));//支付方式 1为在线支付  2为到店支付
*/		  String goods_id = request.getParameter("goods_id");//商品ID  多个商品用逗号隔开
	      String number = request.getParameter("number");//商品数量	多个商品用逗号隔开
	      int user_id = Integer.parseInt(request.getParameter("user_id"));//用户ID
//	      double ap = Double.parseDouble(request.getParameter("all_price"));
	      String ap = request.getParameter("all_price");//总价
	      double all_price = Double.valueOf(ap);
//	      double all_price =1;
	      
	      /*String recipient_tel = request.getParameter("recipient_tel");//收件人电话
	      String recipient_name = new String(request.getParameter("recipient_name").getBytes("ISO-8859-1"),"UTF-8");//收件人电话
	      String recipient_address = new String(request.getParameter("recipient_address").getBytes("ISO-8859-1"),"UTF-8");//收件人地址  
	      String order_message = new String(request.getParameter("order_message").getBytes("ISO-8859-1"),"UTF-8");//订单留言
*/	     
	      
	      /*------------------修改库存---------------------------*/
	      List<String> gs = FengeString.FengeGoods_id(goods_id);
	      List<Goods> Listgoods = goodsService.selectGoods1(gs);
	      List<String> nb = FengeString.FengeGoods_id(number);      
	      List<Integer> lint = new ArrayList<Integer>();
			for(String str:nb){
			if(!str.matches("^([0-9])+$")){
			     continue;
			  }
			  int i = Integer.parseInt(str); 
			  lint.add(i); 
			}
	      
	      int sz = gs.size();//获取商品ID数组size
	      String[] g = (String[])gs.toArray(new String[sz]);//得到商品数组
	      int[] n = new int[lint.size()];
	        for(int i=0;i<lint.size();i++){
	            n[i] = lint.get(i);
	        }
	        
	        StringBuffer sb = new StringBuffer();//库存
	        StringBuffer str = new StringBuffer();//销量
			for(Goods obj:Listgoods){
				sb.append(obj.getIn_stock()+",");
				str.append(obj.getSales_volume()+",");
			}
		  List<String> in_stock = FengeString.FengeGoods_id(sb.toString());
		  List<String> sales_volume = FengeString.FengeGoods_id(str.toString());
		  List<Integer> lint1 = new ArrayList<Integer>();
			for(String str1:in_stock){
			if(!str1.matches("^([0-9])+$")){
			     continue;
			  }
			  int i = Integer.parseInt(str1); 
			  lint1.add(i); 
			}
		
		  List<Integer> lint2 = new ArrayList<Integer>();
			for(String str2:sales_volume){
			if(!str2.matches("^([0-9])+$")){
			     continue;
			  }
			  int i = Integer.parseInt(str2); 
			  lint2.add(i); 
			}
		  int[] is = new int[lint1.size()];//库存数组
		  int[] sv = new int[lint2.size()];//销量数组
		  for(int i=0;i<lint1.size();i++){
			  is[i] = lint1.get(i);
		  }
		  for(int i=0;i<lint2.size();i++){
			  sv[i] = lint2.get(i);
		  }
		  
	      for(int i=0;i<g.length;i++){
	    	  /*System.out.println("修改库存和销量...");
	    	  System.out.println("1:"+(g[i]));
	    	  System.out.println("2:"+(is[i]));
	    	  System.out.println("3:"+(sv[i]));
	    	  System.out.println("4:"+(n[i]));*/
	    	 goodsService.updateNumber(g[i], is[i]-n[i],sv[i]+n[i]);
	      }
	      
	      /*System.out.println("添加订单...");
	      	System.out.println("category:"+category);
		    System.out.println("payment:"+payment);
		    System.out.println("goods_id:"+goods_id);
		    System.out.println("number:"+number);
		    System.out.println("order_message:"+order_message);
		    System.out.println("all_price:"+all_price);
		    System.out.println("recipient_name:"+recipient_name);
		    System.out.println("recipient_tel:"+recipient_tel);
		    System.out.println("recipient_address:"+recipient_address);
		    System.out.println("user_id:"+user_id);*/
	      	Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String time = dateFormat.format(date);
			int status = 1;//1为待发货  2为待收货  3为待评价 4为已成功
			Order order = new Order();
			order.setGoods_id(goods_id);
			order.setNumber(number);
//			order.setOrder_message(order_message);
			order.setUser_id(user_id);
			order.setStatus(status);
			order.setOrder_time(time);
			/*order.setRecipient_name(recipient_name);
			order.setRecipient_tel(recipient_tel);
			order.setRecipient_address(recipient_address);*/
			order.setAll_price(all_price);
	      orderService.insertOrder(order);//添加订单表
	      for(int i=0;i<g.length;i++){
	    	  //把购买的商品从购物车清除掉
	    	  System.out.println("~~~:"+user_id+"@:"+g[i]);
	    	  shopping_cartService.delectByGidAndUid(user_id, g[i]);
	      }
	      String id = orderService.selectByTimeAndUserId(time, user_id);//查询订单ID
	      Settlement settlement = new Settlement();
	      settlement.setOrder_id(id);
	      settlement.setGoods_id(goods_id);
	      settlement.setOrder_time(time);
	      settlement.setStatus("1");
	      settlement.setPrice(all_price);
	      settlement.setUser_id(user_id);
	      settlement.setNumber(number);
	      settlementService.insertSettlement(settlement);//添加到结算表
	      Money money = moneyService.select();
	      double income = money.getIncome();//收入
	      money.setIncome(income+all_price);
	      moneyService.update(money);//修改收入
//	      return "selectCategory1?"+user_id;
	      return "selectStatusOne?"+user_id;
    }
	
	
	
	
	/**
	 * 订单详情
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value ="/selectOrderDetails",method = RequestMethod.GET)
    protected String selectOrderDetails(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id");//订单ID
		Order order = orderService.selectOrderById(id);
		String goods_id = order.getGoods_id();
		String numbers = order.getNumber();
		List<String> number = FengeString.FengeStr(numbers);
		List<String> goodss = FengeString.FengeGoods_id(goods_id);
		List<Goods> goods = goodsService.selectGoods1(goodss);
		request.setAttribute("number",number);
		request.setAttribute("goods",goods);
		request.setAttribute("order",order);
		if(order.getStatus()==1){
			return "orderDetail_successStatus1.jsp";
		}else{
			return "orderDetail_success.jsp";
		}		
	}
	
	public static void jsonStrToJava(){
        //定义两种不同格式的字符串
        String objectStr="{\"name\":\"number\",\"goods_id\":\"24\",\"all_price\":\"北京市西城区\"},{\"name\":\"number\",\"goods_id\":\"242\",\"all_price\":\"北京市西城区123123\"}";
        String arrayStr="[{\"name\":\"number\",\"goods_id\":\"25\",\"all_price\":\"北京市西城区111\"},{\"name\":\"number\",\"goods_id\":\"20\",\"all_price\":\"北京市西城区111\"}]";
    
        //1、使用JSONObject
        JSONObject jsonObject=JSONObject.fromObject(objectStr);
        Ceshi stu=(Ceshi)JSONObject.toBean(jsonObject, Ceshi.class);
        
        //2、使用JSONArray
        JSONArray jsonArray=JSONArray.fromObject(arrayStr);
        //获得jsonArray的第一个元素
        Object o=jsonArray.get(1);
        JSONObject jsonObject2=JSONObject.fromObject(o);
        Ceshi stu2=(Ceshi)JSONObject.toBean(jsonObject2, Ceshi.class);
       
        System.out.println("stu:"+stu.getGoods_id());
        System.out.println("stu2:"+stu2.getGoods_id());
	}
public static void main(String[] args) {
	/*jsonStrToJava();*/
	/*String number = "1,2,3,4,5";
	List<String> gs = FengeString.FengeGoods_id(number);
	List<Integer> nb = FengeString.Fenge(number);
    int sz = gs.size();//获取商品ID数组size
    String[] g = (String[])gs.toArray(new String[sz]);//得到商品数组
    int[] n = new int[nb.size()];
      for(int i=0;i<nb.size();i++){
          n[i] = (Integer) nb.get(i);
          System.out.println(nb.get(i));
      }*/
	String number1 = "2,1,6";
	String price1 = "11.5,10.5,10";
	List<String> number = FengeString.FengeStr(number1);
    List<String> price = FengeString.FengeStr(price1);
    
    List<Integer> lint = new ArrayList<Integer>();
	for(String str:number){
	  int i = Integer.parseInt(str); 
	  lint.add(i); 
	}
	int[] n = new int[lint.size()];
    for(int i=0;i<lint.size();i++){
        n[i] = lint.get(i);
    }
    int nb =0;
    for(int i=0;i<n.length;i++){
    	nb+=n[i];
    }
    System.out.println(nb);
}
}
 