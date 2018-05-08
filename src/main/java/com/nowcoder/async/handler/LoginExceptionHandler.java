package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by 周杰伦 on 2018/5/8.
 */
@Component
public class LoginExceptionHandler implements EventHandler{

    @Autowired
    MailSender mailSender;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setToId(eventModel.getActorId());
        message.setCreatedDate(new Date());
        message.setContent("登录异常");
        message.setFromId(eventModel.getEntityOwnerId());
        messageService.addMessage(message);

        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.get("username"));
        mailSender.sendWithHTMLTemplate(eventModel.get("email"),"登录异常","mails/welcome.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
