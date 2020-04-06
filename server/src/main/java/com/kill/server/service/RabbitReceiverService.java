package com.kill.server.service;

import com.kill.model.dto.KillSuccessUserInfo;
import com.kill.model.entity.ItemKillSuccess;
import com.kill.model.mapper.ItemKillSuccessMapper;
import com.kill.server.dto.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * RabbitMQ接收消息服务
 * @author ilovejava1314
 * @date 2020/4/5 21:28
 */

@Service
public class RabbitReceiverService {

    private static final Logger log = LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private MailService mailService;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    @Autowired
    private Environment env;

    /**
     * 秒杀异步邮件通知-接收消息
     */
    @RabbitListener(queues = {"${mq.kill.item.success.email.queue}"},containerFactory = "singleListenerContainer")
    public void consumeEmailMsg(KillSuccessUserInfo info) {
        try {
            log.info("秒杀异步邮件通知-接收消息：{}",info);

            //TODO:真正的发送邮件
            final String content = String.format(env.getProperty("mail.kill.item.success.content"),info.getItemName(),info.getCode());
            MailDto dto = new MailDto(env.getProperty("mail.kill.item.success.subject"),content,new String[]{info.getEmail()});

            mailService.sendSimpleEmail(dto);

        } catch (Exception e) {
            log.error("秒杀异步邮件通知-接收消息-发生异常：",e.fillInStackTrace());
        }
    }

    /**
     * 用户秒杀成功后超时未支付-监听者
     */
    @RabbitListener(queues = {"${mq.kill.item.success.kill.dead.real.queue}"},containerFactory = "singleListenerContainer")
    public void consumeExpireOrder(KillSuccessUserInfo info) {
        try {
            log.info("用户秒杀成功后超时未支付-监听者：{}",info);
            if (info != null) {
                ItemKillSuccess entity = itemKillSuccessMapper.selectByPrimaryKey(info.getCode());
                if (entity != null && entity.getStatus() == 0) {
                    itemKillSuccessMapper.expireOrder(info.getCode());
                }
            }
        } catch (Exception e) {
            log.error("用户秒杀成功后超时未支付-监听者-发生异常：",e.fillInStackTrace());
        }
    }

}
