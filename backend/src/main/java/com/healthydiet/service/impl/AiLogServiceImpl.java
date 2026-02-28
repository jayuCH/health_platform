package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.entity.AiLog;
import com.healthydiet.mapper.AiLogMapper;
import com.healthydiet.service.AiLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AI日志服务实现
 */
@Service
@RequiredArgsConstructor
public class AiLogServiceImpl extends ServiceImpl<AiLogMapper, AiLog> implements AiLogService {

    @Override
    public void logRequest(Long userId, String requestType, String requestParams, String response, Integer duration, String status, String errorMsg, String aiModel) {
        AiLog log = new AiLog();
        log.setUserId(userId);
        log.setRequestType(requestType);
        log.setRequestParams(requestParams);
        log.setResponse(response);
        log.setDuration(duration);
        log.setStatus(status);
        log.setErrorMsg(errorMsg);
        log.setAiModel(aiModel);
        save(log);
    }
}
