package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;

import com.pinyougou.pojo.TbPayLog;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference(timeout = 8000)
    private WeixinPayService weixinPayService;

    @Autowired
    private IdWorker idWorker;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFromRedis(username);
        if (tbPayLog!=null){
            return weixinPayService.createNative( tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee()+"");
        }else {
            return new HashMap();
        }

    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) {

        Result result = null;
        int n = 0;
        while (true) {

            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                result = new Result(false, "支付发生错误");
                break;
            }
            if (map.get("trade_state").equals("SUCCESS")) {
                result = new Result(true, "制服成功");
                orderService.updateOrderStatus(out_trade_no,map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            n++;
            if (n >= 100) {
                result = new Result(false, "支付超时");
                break;
            }
        }
        return result;
    }


}
