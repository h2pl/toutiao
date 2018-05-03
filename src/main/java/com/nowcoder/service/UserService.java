package com.nowcoder.service;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 周杰伦 on 2018/5/3.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    public void addUser(User user) {
        userDAO.addUser(user);
    }
    public User getUser(int userId) {
        return userDAO.selectById(userId);
    }
    public void updateUser(User user) {
        userDAO.updatePassword(user);
    }
    public void deleteUser(int userId) {
        userDAO.deleteById(userId);
    }

}
