package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by 周杰伦 on 2018/5/8.
 */
@Component
public class LikeHandler implements EventHandler{
    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    //实际业务上需要信息服务通知被点赞的人
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(eventModel.getActorId());
        message.setToId(eventModel.getEntityOwnerId());
        User user = userService.getUser(eventModel.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的资讯"
        + ",http://127.0.0.1:8088/news/" + eventModel.getEntityId());
        message.setCreatedDate(new Date());
        messageService.addMessage(message);

        System.out.println("like");
        logger.info(eventModel.toString());
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
