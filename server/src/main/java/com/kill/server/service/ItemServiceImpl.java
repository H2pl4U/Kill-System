package com.kill.server.service;

import com.kill.model.entity.ItemKill;

import java.util.List;

/**
 * @author ilovejava1314
 * @date 2020/4/5 14:25
 */
public interface ItemServiceImpl {

    List<ItemKill> getKillItems() throws Exception;

    ItemKill getKillDetail(Integer id) throws  Exception;
}
