package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

@Component
public class PageListener implements MessageListener {

    @Autowired
    private ItemPageService pageService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage =(ObjectMessage)message;
        try {
            Long goodsId= (Long) objectMessage.getObject();
            System.out.println("接收到消息：" + goodsId);
            pageService.genItemHtml(goodsId);
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
