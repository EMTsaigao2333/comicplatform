package com.example.comicplatform_springbootweb.service;

import com.example.comicplatform_springbootweb.entity.User;

public interface UserService {
    User register(User user);//注册用户
    User login(String username, String password);
    User updateUser(User user);
    void deleteUser(Integer id);
    void resetPassword(Integer id, String newPassword);//重置密码
    User getUserById(Integer id);//根据ID查询用户
}
