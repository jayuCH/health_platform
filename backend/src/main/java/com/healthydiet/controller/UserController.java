package com.healthydiet.controller;

import com.healthydiet.annotation.CurrentUserId;
import com.healthydiet.common.Result;
import com.healthydiet.dto.LoginRequest;
import com.healthydiet.dto.LoginResponse;
import com.healthydiet.dto.WxLoginRequest;
import com.healthydiet.entity.User;
import com.healthydiet.service.UserService;
import com.healthydiet.vo.HealthGoalVO;
import com.healthydiet.vo.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "发送验证码")
    @PostMapping("/sms/send")
    public Result<Void> sendSmsCode(@RequestParam String phone) {
        userService.sendSmsCode(phone);
        return Result.success();
    }

    @Operation(summary = "手机号登录")
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = userService.loginByPhone(request.getPhone(), request.getCode());
        User user = userService.lambdaQuery()
                .eq(User::getPhone, request.getPhone())
                .one();

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setPhone(user.getPhone());
        response.setUserInfo(userInfo);

        return Result.success(response);
    }

    @Operation(summary = "微信登录")
    @PostMapping("/wx/login")
    public Result<LoginResponse> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        String token = userService.loginByWx(request.getCode());
        Long userId = userService.lambdaQuery()
                .eq(User::getOpenid, "mock_openid") // 临时处理
                .one()
                .getId();

        User user = userService.getById(userId);

        LoginResponse response = new LoginResponse();
        response.setToken(token);

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setNickname(user.getNickname());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setPhone(user.getPhone());
        response.setUserInfo(userInfo);

        return Result.success(response);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(@CurrentUserId Long userId) {
        return Result.success(userService.getUserInfo(userId));
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@CurrentUserId Long userId,
                                       @RequestBody UserInfoVO userInfoVO) {
        userService.updateUserInfo(userId, userInfoVO);
        return Result.success();
    }

    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> updateAvatar(@CurrentUserId Long userId,
                                       @RequestParam("file") MultipartFile file) {
        String avatarUrl = userService.updateAvatar(userId, file);
        return Result.success(avatarUrl);
    }

    @Operation(summary = "设置健康目标")
    @PostMapping("/health-goal")
    public Result<Void> setHealthGoal(@CurrentUserId Long userId,
                                       @RequestBody HealthGoalVO healthGoalVO) {
        userService.setHealthGoal(userId, healthGoalVO);
        return Result.success();
    }

    @Operation(summary = "获取健康目标")
    @GetMapping("/health-goal")
    public Result<HealthGoalVO> getHealthGoal(@CurrentUserId Long userId) {
        return Result.success(userService.getHealthGoal(userId));
    }
}
