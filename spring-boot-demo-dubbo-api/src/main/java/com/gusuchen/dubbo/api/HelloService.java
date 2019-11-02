package com.gusuchen.dubbo.api;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-29 17:08
 */
public interface HelloService {

    /**
     * say hello
     *
     * @param name 名字
     * @return 字符串
     */
    String sayHello(String name);
}
