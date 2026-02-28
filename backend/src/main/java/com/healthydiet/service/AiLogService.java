package com.healthydiet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.healthydiet.entity.AiLog;

/**
 * AI日志服务接口
 */
public interface AiLogService extends IService<AiLog> {

    void logRequest(Long userId, String requestType, String requestParams, String response, Integer duration, String status, String errorMsg, String aiModel);
}
