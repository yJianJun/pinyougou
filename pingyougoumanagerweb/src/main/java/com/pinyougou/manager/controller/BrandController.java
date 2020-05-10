package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageBean;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {


    @Reference
    private BrandService brandService;

    @RequestMapping("/queryAll")
    public List<TbBrand> queryAll(){
        return brandService.queryAll();
    }


    @RequestMapping("/fillPage")
    public PageBean fillPage(int page,int size) {
        return brandService.fillPage(page, size);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new Result(true,"新增成功");
        } catch (Exception e) {
            return new Result(false,"新增失败");
        }
    }

    @RequestMapping("/queryOne")
    public TbBrand queryOne(long id){
       return brandService.queryOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    public PageBean search(@RequestBody TbBrand brand,int page,int size) {
        return brandService.fillPage(brand,page,size);
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }

}
