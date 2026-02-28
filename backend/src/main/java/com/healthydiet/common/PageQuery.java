package com.healthydiet.common;

import lombok.Data;

/**
 * 分页查询参数
 */
@Data
public class PageQuery {

    private Integer current = 1;

    private Integer size = 10;

    public void setCurrent(Integer current) {
        this.current = current != null && current > 0 ? current : 1;
    }

    public void setSize(Integer size) {
        this.size = size != null && size > 0 ? size : 10;
        this.size = Math.min(this.size, 100);
    }
}
