package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by 周杰伦 on 2018/5/4.
 */
//保存当前访问的用户信息，通过拦截器拦截时注入
@Component
public class HostHolder {
    //使用threadlocal，保证多线程中的数据独占。
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser() {
        return users.get();
    }

    public void setUsers(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
