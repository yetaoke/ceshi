package com.mall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mall.pojo.Admin;
import com.mall.pojo.Collection;
import com.mall.pojo.EvaluateOrder;
import com.mall.pojo.Goods;


public interface EvaluateOrderDao {

	public List<EvaluateOrder> selectEvaluateOrder(int user_id,int status);
	public int selectCategoryNumber(int user_id,int status);
	public void insertselectEvaluateOrder(EvaluateOrder evaluateOrder);
	public void updateEvaluateOrder(EvaluateOrder evaluateOrder);
}
