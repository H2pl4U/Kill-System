package com.kill.server.service.impl;

import com.kill.model.entity.ItemKill;
import com.kill.model.mapper.ItemKillMapper;
import com.kill.server.service.ItemServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ilovejava1314
 * @date 2020/4/5 14:26
 */
@Service
public class ItemService implements ItemServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    @Autowired
    private ItemKillMapper itemKillMapper;

    /**
     * 获取待秒杀商品列表
     * @return
     * @throws Exception
     */
    @Override
    public List<ItemKill> getKillItems() throws Exception {
        return itemKillMapper.selectAll();
    }

    /**
     * 获取秒杀商品详情
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    public ItemKill getKillDetail(Integer id) throws Exception {
        ItemKill entity = itemKillMapper.selectById(id);
        if(entity == null) {
            throw new Exception("获取秒杀详情记录不存在");
        }
        return entity;
    }
}
