package com.kill.server.service;

public interface KillServiceImpl {

    Boolean killItem(Integer killId,Integer userId) throws Exception;

    Boolean killItemV2(Integer killId,Integer userId) throws Exception;

    Boolean killItemV3(Integer killId,Integer userId) throws Exception;

    Boolean killItemV4(Integer killId,Integer userId) throws Exception;

    Boolean killItemV5(Integer killId,Integer userId) throws Exception;

}
