package com.pinyougou.sellergoods.service;


import com.pinyougou.pojo.TbBrand;
import entity.PageBean;

import java.util.List;
import java.util.Map;

public interface BrandService {


    public List<TbBrand> queryAll();


    public PageBean fillPage(int pageNum,int pageSize);

    public void add(TbBrand brand);

    public TbBrand queryOne(long id);

    public void update(TbBrand brand);

    public void delete(long[] ids);

    public PageBean fillPage(TbBrand brand,int pageNum,int pageSize);

    public List<Map> selectOptionList();

}
