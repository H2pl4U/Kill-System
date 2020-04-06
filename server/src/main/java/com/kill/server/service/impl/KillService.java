package com.kill.server.service.impl;

import com.kill.model.entity.ItemKill;
import com.kill.model.entity.ItemKillSuccess;
import com.kill.model.mapper.ItemKillMapper;
import com.kill.model.mapper.ItemKillSuccessMapper;
import com.kill.server.enums.SysConstant;
import com.kill.server.service.KillServiceImpl;
import com.kill.server.service.RabbitSenderService;
import com.kill.server.utils.RandomUtil;
import com.kill.server.utils.SnowFlake;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ilovejava1314
 * @date 2020/4/5 19:34
 */

@Service
public class KillService implements KillServiceImpl {

    private static final Logger log= LoggerFactory.getLogger(KillService.class);

    private SnowFlake snowFlake = new SnowFlake(2,3);

    @Autowired
    private ItemKillMapper itemKillMapper;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private RabbitSenderService rabbitSenderService;

    @Override
    public Boolean killItem(Integer killId, Integer userId) throws Exception {
        boolean result = false;
        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0) {
            //TODO:查询待秒杀商品详情
            ItemKill itemKill = itemKillMapper.selectById(killId);
            //TODO:判断是否可以被秒杀canKill=1?
            if(itemKill != null && itemKill.getCanKill() == 1) {
                //TODO:扣减库存-减一
                int res = itemKillMapper.updateKillItem(killId);

                //TODO:扣减是否成功?是的话生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res > 0) {
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result = true;
                }
            }
        } else {
            throw new Exception("您已经秒杀过该商品了，给别的小伙伴机会哦");
        }
        return result;
    }

    /**
     * 通用的方法-记录用户秒杀成功后生成的订单-并进行异步邮件消息的通知
     * @param itemKill
     * @param userId
     * @throws Exception
     */
    private void commonRecordKillSuccessInfo(ItemKill itemKill, Integer userId) throws Exception{
        //TODO:记录抢购成功后生成的秒杀订单记录
        ItemKillSuccess itemKillSuccess = new ItemKillSuccess();
        String orderNo = String.valueOf(snowFlake.nextId());
        itemKillSuccess.setCode(orderNo);
        itemKillSuccess.setItemId(itemKill.getItemId());
        itemKillSuccess.setKillId(itemKill.getId());
        itemKillSuccess.setUserId(userId.toString());
        itemKillSuccess.setStatus(SysConstant.OrderStatus.SuccessNotPayed.getCode().byteValue());
        itemKillSuccess.setCreateTime(DateTime.now().toDate());
        int res = itemKillSuccessMapper.insertSelective(itemKillSuccess);
        if (res > 0) {
            //TODO:异步发送邮件消息通知 rabbitMQ+mail实现
            rabbitSenderService.sendKillSuccessEmailMsg(orderNo);

            //TODO:创建死信队列，用于"失效"超过指定的TTL时间时任然未支付的订单
            rabbitSenderService.sendKillSuccessOrderExpireMsg(orderNo);
        }

    }

    @Override
    public Boolean killItemV2(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        return null;
    }

    @Override
    public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        return null;
    }
}
