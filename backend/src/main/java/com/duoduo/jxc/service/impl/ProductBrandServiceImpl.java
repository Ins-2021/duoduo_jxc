package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.ProductBrand;
import com.duoduo.jxc.mapper.ProductBrandMapper;
import com.duoduo.jxc.service.ProductBrandService;
import org.springframework.stereotype.Service;

@Service
public class ProductBrandServiceImpl extends ServiceImpl<ProductBrandMapper, ProductBrand> implements ProductBrandService {
}

