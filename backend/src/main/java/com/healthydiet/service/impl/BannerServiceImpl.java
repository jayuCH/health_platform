package com.healthydiet.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.healthydiet.entity.Banner;
import com.healthydiet.mapper.BannerMapper;
import com.healthydiet.service.BannerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 轮播图服务实现
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Override
    public List<Banner> getActiveBanners() {
        return list(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, "active")
                .orderByAsc(Banner::getSort));
    }
}
