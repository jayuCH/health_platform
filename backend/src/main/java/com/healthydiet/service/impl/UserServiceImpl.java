package com.healthydiet.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.common.BusinessException;
import com.healthydiet.entity.HealthGoal;
import com.healthydiet.entity.User;
import com.healthydiet.mapper.UserMapper;
import com.healthydiet.service.HealthGoalService;
import com.healthydiet.service.UserService;
import com.healthydiet.util.JwtUtil;
import com.healthydiet.vo.HealthGoalVO;
import com.healthydiet.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final HealthGoalService healthGoalService;

    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final int SMS_CODE_EXPIRE_MINUTES = 5;

    @Override
    @Transactional
    public String loginByPhone(String phone, String code) {
        // 验证验证码
        String cacheKey = SMS_CODE_PREFIX + phone;
        String cacheCode = redisTemplate.opsForValue().get(cacheKey);
        if (cacheCode == null || !cacheCode.equals(code)) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 删除验证码
        redisTemplate.delete(cacheKey);

        // 查找用户
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone));

        if (user == null) {
            // 创建新用户
            user = new User();
            user.setPhone(phone);
            user.setNickname("用户" + phone.substring(7));
            user.setStatus(1);
            save(user);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成JWT Token
        return jwtUtil.generateToken(user.getId(), user.getOpenid());
    }

    @Override
    @Transactional
    public String loginByWx(String code) {
        // TODO: 调用微信API获取openid和session_key
        // 这里先模拟
        String openid = "mock_openid_" + System.currentTimeMillis();

        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getOpenid, openid));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("微信用户");
            user.setStatus(1);
            save(user);
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        return jwtUtil.generateToken(user.getId(), user.getOpenid());
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);

        // 获取健康目标
        HealthGoal healthGoal = healthGoalService.getByUserId(userId);
        if (healthGoal != null) {
            HealthGoalVO goalVO = new HealthGoalVO();
            BeanUtils.copyProperties(healthGoal, goalVO);
            userInfoVO.setHealthGoal(goalVO);
        }

        return userInfoVO;
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, UserInfoVO userInfoVO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (userInfoVO.getNickname() != null) {
            user.setNickname(userInfoVO.getNickname());
        }
        if (userInfoVO.getGender() != null) {
            user.setGender(userInfoVO.getGender());
        }
        if (userInfoVO.getBirthday() != null) {
            user.setBirthday(userInfoVO.getBirthday());
        }
        if (userInfoVO.getHeight() != null) {
            user.setHeight(userInfoVO.getHeight());
        }

        updateById(user);
    }

    @Override
    @Transactional
    public String updateAvatar(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = "avatar/" + userId + "_" + System.currentTimeMillis() + extension;

            // 保存文件
            File uploadDir = new File("/data/uploads/avatar/");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File destFile = new File("/data/uploads/" + filename);
            file.transferTo(destFile);

            // 更新数据库
            String avatarUrl = "http://localhost:8080/api/file/" + filename;
            User user = getById(userId);
            user.setAvatar(avatarUrl);
            updateById(user);

            return avatarUrl;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败");
        }
    }

    /**
     * 发送验证码
     */
    public void sendSmsCode(String phone) {
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException("手机号格式不正确");
        }

        String code = RandomUtil.randomNumbers(6);
        String cacheKey = SMS_CODE_PREFIX + phone;

        // 存储验证码到Redis
        redisTemplate.opsForValue().set(cacheKey, code, SMS_CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // TODO: 调用短信服务发送验证码
        System.out.println("发送验证码到 " + phone + ": " + code);
    }
}
