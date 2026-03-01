package com.healthydiet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.entity.User;
import com.healthydiet.vo.HealthGoalVO;
import com.healthydiet.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 手机号登录
     */
    String loginByPhone(String phone, String code);

    /**
     * 微信登录
     */
    String loginByWx(String code);

    /**
     * 根据ID获取用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, UserInfoVO userInfoVO);

    /**
     * 更新头像
     */
    String updateAvatar(Long userId, MultipartFile file);

    /**
     * 发送验证码
     */
    void sendSmsCode(String phone);

    /**
     * 设置健康目标
     */
    void setHealthGoal(Long userId, HealthGoalVO healthGoalVO);

    /**
     * 获取健康目标
     */
    HealthGoalVO getHealthGoal(Long userId);
}
