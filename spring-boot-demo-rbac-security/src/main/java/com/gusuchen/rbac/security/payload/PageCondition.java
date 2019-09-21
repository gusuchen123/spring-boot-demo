package com.gusuchen.rbac.security.payload;

import lombok.Data;

/**
 * <p>
 * 分页请求参数
 * </p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 15:21
 */
@Data
public class PageCondition {
    /**
     * 当前页码
     */
    private Integer currentPage;
    /**
     * 每页条数
     */
    private Integer pageSize;
}
