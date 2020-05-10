package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;


@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {

        HashMap param = new HashMap();
        param.put("appid",appid);
        param.put("mch_id",partner);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("body","品优购");
        param.put("out_trade_no",out_trade_no);
        param.put("total_fee",total_fee);
        param.put("spbill_create_ip","127.0.0.1");
        param.put("notify_url","http://www.baidu.com");
        param.put("trade_type","NATIVE");

        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
            System.out.println("请求的参数"+xml);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xml);
            httpClient.post();

            String xmlResult = httpClient.getContent();
            Map<String, String> mapResult = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("微信返回的结果：" + mapResult);

            Map map = new HashMap<>();
            map.put("code_url",mapResult.get("code_url"));
            map.put("out_trade_no",out_trade_no);
            map.put("total_fee",total_fee);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {

        Map map = new HashMap();
        map.put("appid",appid);
        map.put("mch_id",partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("out_trade_no",out_trade_no);

        try {
            String paramXml = WXPayUtil.generateSignedXml(map, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(paramXml);
            httpClient.post();

            String xmlResult = httpClient.getContent();
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(xmlResult);

            return xmlToMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
