package com.healthydiet.vo;

import lombok.Data;

import java.util.List;

/**
 * 健康趋势VO
 */
@Data
public class HealthTrendVO {
    private List<String> dates;
    private List<Object> values;
}
