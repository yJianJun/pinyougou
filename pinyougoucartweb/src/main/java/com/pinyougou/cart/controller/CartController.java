package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference(timeout = 6000)
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping("/queryCartList")
    public List<Cart> queryCartList(){

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");


        if (cartListString==null||cartListString.equals("")){
            cartListString="[]";
        }
        List<Cart> cartsCookie = JSON.parseArray(cartListString, Cart.class);
        if (name.equals("anonymousUser")){

            System.out.println("从cookie中提取购物车");

            return cartsCookie;
        }else {


            List<Cart> carts_redis = cartService.queryCartListFromRedis(name);

            if (cartsCookie.size()>0){
                List<Cart> cartList = cartService.mergeCartList(cartsCookie, carts_redis);
                cartService.saveCartListToRedis(name,cartList);
                System.out.println("merge cart running");
                CookieUtil.deleteCookie(request,response,"cartList");
                return cartList;

            }


            return carts_redis;

        }
        /*
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
	if(username.equals("anonymousUser")){//如果未登录
		//读取本地购物车//
		..........
		return cartList_cookie;
	}else{//如果已登录
		List<Cart> cartList_redis =cartService.findCartListFromRedis(username);//从redis中提取
		return cartList_redis;

         */

    }


    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105")
    public Result addGoodsToCartList(Long itemId,Integer num){

//        response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
//        response.setHeader("Access-Control-Allow-Credentials","true");

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            List<Cart> carts = queryCartList();
        carts = cartService.addGoodsToCartList(carts, itemId, num);
        if (name.equals("anonymousUser")){
            System.out.println("向cookie存储购物车");
            String cartsString = JSON.toJSONString(carts);
            CookieUtil.setCookie(request,response,"cartList",cartsString,3600*24,"UTF-8");

        }else {

            cartService.saveCartListToRedis(name,carts);

        }

            return new Result(true,"存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"存入购物车失败");
        }

    }






}
