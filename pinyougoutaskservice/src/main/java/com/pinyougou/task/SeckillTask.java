package com.pinyougou.task;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SeckillTask {


    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){

        System.out.println("执行了任务调度"+new Date());

    }



}
