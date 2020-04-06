package com.kill.server.controller;

import com.kill.model.entity.ItemKill;
import com.kill.server.service.impl.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 待秒杀商品controller
 * @author ilovejava1314
 * @date 2020/4/5 14:17
 */
@Controller
public class ItemController {

    private static final Logger log = LoggerFactory.getLogger(ItemController.class);

    private static final String prefix = "item";

    @Autowired
    private ItemService itemService;

    /**
     * 获取待秒杀商品列表
     * @param modelMap
     * @return
     */
    @RequestMapping(value = {"/","/index",prefix+"/list",prefix+"/index.html"},method = RequestMethod.GET)
    public String list(ModelMap modelMap) {
        try{
            List<ItemKill> list = itemService.getKillItems();
            System.out.println(list.get(0).toString()+"------"+list.size());
            list.get(0);
            modelMap.put("list",list);
            log.info("获取待秒杀商品列表数据:",list);
        }catch (Exception e) {
            log.error("获取待秒杀商品列表发生异常:",e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "list";
    }

    @RequestMapping(value = prefix+"/detail/{id}",method = RequestMethod.GET)
    public String detail(@PathVariable Integer id,ModelMap modelMap) {
        if(id == null || id <= 0) {
            return "redirect:/base/error";
        }
        try {
            ItemKill detail = itemService.getKillDetail(id);
            modelMap.put("detail",detail);
        } catch (Exception e) {
            log.error("获取待秒杀商品详情发生异常:",e.fillInStackTrace());
            return "redirect:/base/error";
        }
        return "info";
    }

}
