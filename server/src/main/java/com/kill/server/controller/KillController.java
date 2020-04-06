package com.kill.server.controller;

import com.kill.api.enums.StatusCode;
import com.kill.api.response.BaseResponse;
import com.kill.server.dto.KillDto;
import com.kill.server.service.impl.KillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 秒杀Controller
 * @author ilovejava1314
 * @date 2020/4/5 19:28
 */
@Controller
public class KillController {
    private static final Logger log = LoggerFactory.getLogger(KillController.class);

    private static final String prefix = "kill";

    @Autowired
    private KillService killService;

    /***
     * 商品秒杀核心业务逻辑
     * @param dto
     * @param result
     * @return
     */
    @RequestMapping(value = prefix+"/execute",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public BaseResponse execute(@RequestBody @Validated KillDto dto, BindingResult result, HttpSession session) {
        if (result.hasErrors() || dto.getKillId() <= 0) {
            return new BaseResponse(StatusCode.InvalidParams);
        }
//        Object uId = session.getAttribute("uid");
//        if(uId == null) {
//            return new BaseResponse(StatusCode.UserNotLogin);
//        }
//        Integer userId = (Integer)uId;
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            boolean res = killService.killItem(dto.getKillId(),dto.getUserId());
            if (!res) {
                return new BaseResponse(StatusCode.Fail.getCode(),"该商品秒杀活动已失效，感谢您的参与~");
            }
        } catch (Exception e) {
           response = new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

    /**
     * 跳转到秒杀成功页面
     * @return
     */
    @RequestMapping(value = prefix+"/execute/success",method = RequestMethod.GET)
    public String executeSuccess(){
        return "executeSuccess";
    }

    /**
     * 跳转到秒杀失败页面
     * @return
     */
    @RequestMapping(value = prefix+"/execute/fail",method = RequestMethod.GET)
    public String executeFail(){
        return "executeFail";
    }

}
