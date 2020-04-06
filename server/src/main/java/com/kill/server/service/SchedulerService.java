package com.kill.server.service;

import com.kill.model.entity.ItemKillSuccess;
import com.kill.model.mapper.ItemKillSuccessMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务服务
 * @author ilovejava1314
 * @date 2020/4/6 13:08
 */
@Service
public class SchedulerService {

    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 定时获取status=0的订单并判断是否超过TTL，然后进行失效
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void schedulerExpireOrders() {
        try {
            List<ItemKillSuccess> list = itemKillSuccessMapper.selectExpireOrders();
            for (ItemKillSuccess entity : list) {
                System.out.println(entity);
                log.info("当前获取记录：{}-----",entity);
            }
        } catch (Exception e) {
            log.error("定时获取status=0的订单并判断是否超过TTL，然后进行失效-发生异常",e.fillInStackTrace());
        }
    }
}
