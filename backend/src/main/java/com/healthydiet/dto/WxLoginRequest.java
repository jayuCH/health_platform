package com.healthydiet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信登录请求DTO
 */
@Data
public class WxLoginRequest {
    @NotBlank(message = "code不能为空")
    private String code;
}
