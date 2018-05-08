package com.nowcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by 周杰伦 on 2018/5/8.
 */
@Service
//要使用上下文要实现ApplicationContextAware接口
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    @Autowired
    private JedisAdapter jedisAdapter;

    //Spring上下文
    //consumer需要一个事件映射，也就是需要事先知道是哪个handler来处理
    private ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //事件统一处理，需要把一个事件给所有符合的handler处理，需要一个map来存储所有handler
    private Map<EventType,List<EventHandler>> config = new HashMap<>();

    //初始化完成后一直监听队列等待处理事件
    @Override
    public void afterPropertiesSet() throws Exception {
        //需要遍历所有handler，也就是找到所有实现handler接口的实现类
        //spring自带的bean容器已经提供了这么一个方法、
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            //遍历所有eventhandler，找出每个handler支持的type。
            for (Map.Entry<String, EventHandler> entry: beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                //根据type和handler形成映射表，完成map。
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                        config.get(type).add(entry.getValue());
                    }else {
                        config.get(type).add(entry.getValue());
                    }
                }

            }
        }

        //根据前面的map可以知道取出的时间由谁来处理
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);
                    for (String msg : events) {
                        //redis自带消息key要过滤掉
                        if (msg.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject(msg, EventModel.class);
                        if (!config.containsKey(eventModel.getEventType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                        for (EventHandler eventHandler : config.get(eventModel.getEventType())) {
                            eventHandler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
