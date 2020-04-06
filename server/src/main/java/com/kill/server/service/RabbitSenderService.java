package com.kill.server.service;

import com.kill.model.dto.KillSuccessUserInfo;
import com.kill.model.mapper.ItemKillSuccessMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.AbstractJavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @author ilovejava1314
 * @date 2020/4/5 21:27
 */

/**
 * RabbitMQ发送消息的服务
 */
@Service
public class RabbitSenderService {

    private static final Logger log = LoggerFactory.getLogger(RabbitSenderService.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Environment env;

    @Autowired
    private ItemKillSuccessMapper itemKillSuccessMapper;

    /**
     * 秒杀成功异步发送邮件通知消息
     */
    public void sendKillSuccessEmailMsg(String orderNo) {
        log.info("秒杀成功异步发送邮件通知消息-准备发送消息：{}",orderNo);

        try {
            if(StringUtils.isNotBlank(orderNo)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderNo);
                if (info != null) {
                    //TODO:rabbitMQ发送消息的逻辑
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.email.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.email.routing.key"));
                    //TODO:将info充当消息发送至队列
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties = message.getMessageProperties();
                            //设置消息持久化
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            //设置消息头，保存为对象格式
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,KillSuccessUserInfo.class);
                            return message;
                        }
                    });
                }
            }


        } catch (Exception e) {
            log.error("秒杀成功异步发送邮件通知消息-发送异常：{}",orderNo,e.fillInStackTrace());
        }

    }

    /**
     * 秒杀成功后生成抢购订单-发送信息进入死信队列，等待超时未支付后失效订单
     * @param orderCode
     */
    public void sendKillSuccessOrderExpireMsg(final String orderCode) {
        try {
            if (StringUtils.isNotBlank(orderCode)) {
                KillSuccessUserInfo info = itemKillSuccessMapper.selectByCode(orderCode);
                if (info != null) {
                    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
                    rabbitTemplate.setExchange(env.getProperty("mq.kill.item.success.kill.dead.prod.exchange"));
                    rabbitTemplate.setRoutingKey(env.getProperty("mq.kill.item.success.kill.dead.prod.routing.key"));
                    rabbitTemplate.convertAndSend(info, new MessagePostProcessor() {
                        @Override
                        public Message postProcessMessage(Message message) throws AmqpException {
                            MessageProperties messageProperties = message.getMessageProperties();
                            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            messageProperties.setHeader(AbstractJavaTypeMapper.DEFAULT_KEY_CLASSID_FIELD_NAME,KillSuccessUserInfo.class);
                            //TODO:设置动态TTL
                            messageProperties.setExpiration(env.getProperty("mq.kill.item.success.kill.expire"));
                            return message;
                        }
                    });

                }
            }
        } catch (Exception e) {
            log.error("秒杀成功后生成抢购订单-发送信息进入死信队列，等待超时未支付后失效订单-发生异常,消息为：",orderCode,e.fillInStackTrace());
        }
    }
}
