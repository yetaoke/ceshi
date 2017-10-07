package com.mall.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mall.pojo.Admin;
import com.mall.pojo.Collection;
import com.mall.pojo.Goods;


public interface AdminDao {

	public Admin Login(String admin,String password);
}
