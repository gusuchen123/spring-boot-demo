package com.gusuchen.rbac.security.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-16 20:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    /**
     * 当前页数据
     */
    private List<T> rows;
    /**
     * 总页数
     */
    private Long total;

    public static <T> PageResult<T> of(List<T> rows, Long total) {
        return new PageResult<>(rows, total);
    }
}
