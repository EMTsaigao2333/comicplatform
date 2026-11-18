package com.example.comicplatform_springbootweb.controller;

import com.example.comicplatform_springbootweb.common.Result;
import com.example.comicplatform_springbootweb.entity.User;
import com.example.comicplatform_springbootweb.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户相关的 API 接口
 * @author 黑炎
 * @since 2026-07-05
 *
 * 二次修改：
 * 把 login 方法改成返回 Map，并统一 role 字段
 */


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     *响应注册接口也需要改：
     * 注册成功后应该像登录一样返回 Map（带 role），而不是直接返回 User 实体
     */
    @PostMapping("/register")
//    public Result<User> register(@RequestBody User user) {
//        return Result.success(userService.register(user));
//    }
    public Result<Map<String, Object>> register(@RequestBody User user) {
        User saved = userService.register(user);
        Map<String, Object> data = new HashMap<>();
        data.put("userid", saved.getUserid());
        data.put("username", saved.getUsername());
        data.put("role", saved.getUsertype());
        data.put("phone", saved.getPhone());
        data.put("email", saved.getEmail());
        return Result.success(data);
    }

    /**
     * 用户登录
     * 已注释第一次返回代码
     *
     * 二次修改，时间：2026-07-06
     * 用户登录
     * 企业标准：不返回敏感字段（password 已被 @JsonIgnore），
     * 并把数据库的 usertype 统一转换为前端使用的 role 字段。
     *
     * 实体类序列化 JSON 时，直接忽略该字段，不返回给前端，示例：
     * {
     *   "id": 1,
     *   "name": "张三"
     *   // 没有pwd字段
     * }
     *
     */
    @PostMapping("/login")
//    public Result<User> login(@RequestParam String username, @RequestParam String password) {
//        return Result.success(userService.login(username, password));
    public Result<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        User user = userService.login(username, password);
        Map<String, Object> data = new HashMap<>();
        data.put("userid", user.getUserid());
        data.put("username", user.getUsername());
        // 前后端字段统一：后端 usertype → 前端 role
        data.put("role", user.getUsertype());
        data.put("phone", user.getPhone());
        data.put("email", user.getEmail());
        return Result.success(data);
    }

    @PutMapping
    public Result<User> update(@RequestBody User user) {
        return Result.success(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @PutMapping("/{id}/password")
    public Result<Void> resetPassword(@PathVariable Integer id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        return Result.success(userService.getUserById(id));
    }
}
