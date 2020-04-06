package com.kill.server.controller;

import com.kill.api.enums.StatusCode;
import com.kill.api.response.BaseResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 基础Contrller
 * @author ilovejava1314
 * @date 2020/4/5 12:10
 */
@Controller
@RequestMapping("base")
public class BaseController {

    private static Logger log = LoggerFactory.getLogger(BaseController.class);

    @GetMapping(value = "welcome")
    public String welcome(String name, ModelMap modelMap) {
        if (StringUtils.isBlank(name)) {
            name = "jjh";
        }
        modelMap.put("name",name);
        return "welcome";
    }

    @RequestMapping(value = "/data",method = RequestMethod.GET)
    @ResponseBody
    public String data(String name) {
        if(StringUtils.isBlank(name)) {
            name = "welcome";
        }
        return name;
    }

    @RequestMapping(value = "/response",method = RequestMethod.GET)
    @ResponseBody
    public BaseResponse response(String name) {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        if(StringUtils.isBlank(name)) {
            name = "welcome";
        }
        response.setData(name);
        return response;
    }

    @RequestMapping(value = "/error",method = RequestMethod.GET)
    public String error() {
        return "error";
    }
}
