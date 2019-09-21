package com.gusuchen.security.authentication.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 用于存放模拟的用户数据
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-19 20:44
 */
@Data
public class MyUser implements Serializable {
    private String username;
    private String password;

}
