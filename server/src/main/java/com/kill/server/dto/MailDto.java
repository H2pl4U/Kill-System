package com.kill.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 邮件统一信息
 * @author ilovejava1314
 * @date 2020/4/5 19:58
 */

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MailDto implements Serializable {
    //邮件主题
    private String subject;
    //邮件内容
    private String content;
    //接收人
    private String[] tos;
}