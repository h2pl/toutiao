package com.nowcoder.async;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 周杰伦 on 2018/5/8.
 */
//用来处理事件的接口
@Component
public interface EventHandler {
    //处理事件，不同实现类处理方式不同
    void doHandle(EventModel eventModel);
    //获取该handler可以处理的所有事件类型
    List<EventType> getSupportEventTypes();
}
