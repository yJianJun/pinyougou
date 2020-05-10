package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;


    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartlist, Long itemId, Integer num) {

        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item==null){
            throw new RuntimeException("商品不存在");
        }
        if (!item.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        String sellerId = item.getSellerId();
        Cart cart = searchCartBySellerId(cartlist, sellerId);

        if (cart==null){
             cart = new Cart();
             cart.setSellerId(sellerId);
             cart.setSellerName(item.getSeller());
            List<TbOrderItem> orderItemList = new ArrayList();
            TbOrderItem orderItem = createOrderItem(item, num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList );
            cartlist.add(cart);

        }else {

            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
            if (tbOrderItem==null){

                tbOrderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(tbOrderItem);

            }else {
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getPrice().doubleValue()*tbOrderItem.getNum()));
                if(tbOrderItem.getNum()<=0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }

                if(cart.getOrderItemList().size()==0){
                    cartlist.remove(cart);
                }
            }
        }


        return cartlist;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> queryCartListFromRedis(String username) {

        System.out.println("从redis中提取购物车" + username);
        List<Cart> cartlist = (List<Cart>) redisTemplate.boundHashOps("cartlist").get(username);
        if (cartlist==null){

            cartlist=new ArrayList();
        }
        return cartlist;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {

        System.out.println("向redis中存入购物车" + username);
        redisTemplate.boundHashOps("cartlist").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {

        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {

                cartList1 = addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return cartList1;


    }


    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId){
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }


    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId){

        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }

    private TbOrderItem createOrderItem(TbItem item,Integer num){
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return  orderItem;
        
    }


}
