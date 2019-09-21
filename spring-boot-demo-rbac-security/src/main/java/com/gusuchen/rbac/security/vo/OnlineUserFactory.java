package com.gusuchen.rbac.security.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.gusuchen.rbac.security.common.Consts;
import com.gusuchen.rbac.security.model.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-18 20:16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OnlineUserFactory {

    public static OnlineUser create(User user) {
        OnlineUser onlineUser = new OnlineUser();
        BeanUtil.copyProperties(user, onlineUser);

        // 脱敏
        onlineUser.setPhone(StrUtil.hide(user.getPhone(), 3, 7));
        onlineUser.setEmail(StrUtil.hide(user.getEmail(), 1, StrUtil.indexOfIgnoreCase(user.getEmail(), Consts.SYMBOL_EMAIL)));
        return onlineUser;
    }
}
