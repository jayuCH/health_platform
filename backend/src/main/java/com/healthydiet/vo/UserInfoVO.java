package com.healthydiet.vo;

import lombok.Data;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO {
    private Long id;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer gender;
    private String birthday;
    private Integer height;
    private HealthGoalVO healthGoal;
}
