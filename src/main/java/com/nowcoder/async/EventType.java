package com.nowcoder.async;

/**
 * Created by 周杰伦 on 2018/5/8.
 */
//事件类型枚举类
public enum  EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3),
    SCORE(4);

    private int value;
    EventType(int value) {
        this.value = value;
    }
}
