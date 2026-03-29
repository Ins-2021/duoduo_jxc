import os

base_dir = "/Users/zxh/mycoding/duoduo_jxc/backend/src/main/java/com/duoduo/jxc"

entities = {
    "Process": {
        "table": "jxc_process",
        "fields": [
            ("Long", "processId", "@TableId(type = IdType.AUTO)"),
            ("String", "processCode", ""),
            ("String", "processName", ""),
            ("String", "processType", ""),
            ("java.math.BigDecimal", "standardPrice", ""),
            ("Integer", "status", "")
        ],
        "query_fields": ["processCode", "processName", "processType"]
    },
    "Bundle": {
        "table": "jxc_bundle",
        "fields": [
            ("Long", "bundleId", "@TableId(type = IdType.AUTO)"),
            ("String", "bundleNo", ""),
            ("Long", "orderId", ""),
            ("Long", "currentProcessId", ""),
            ("String", "status", "")
        ],
        "query_fields": ["bundleNo", "orderId", "status"]
    },
    "ProcessRecord": {
        "table": "jxc_process_record",
        "fields": [
            ("Long", "recordId", "@TableId(type = IdType.AUTO)"),
            ("Long", "workerId", ""),
            ("Long", "processId", ""),
            ("Long", "bundleId", ""),
            ("Integer", "quantity", ""),
            ("java.math.BigDecimal", "amount", "")
        ],
        "query_fields": ["workerId", "processId", "bundleId"]
    }
}

for entity, data in entities.items():
    table = data["table"]
    fields = data["fields"]
    query_fields = data["query_fields"]
    
    # Entity
    entity_code = f"""package com.duoduo.jxc.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("{table}")
public class {entity} extends BaseEntity {{
"""
    for field in fields:
        if field[2]:
            entity_code += f"    {field[2]}\n"
        entity_code += f"    private {field[0]} {field[1]};\n"
    entity_code += "}\n"
    
    with open(f"{base_dir}/entity/{entity}.java", "w") as f:
        f.write(entity_code)
        
    # Mapper
    mapper_code = f"""package com.duoduo.jxc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.duoduo.jxc.entity.{entity};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface {entity}Mapper extends BaseMapper<{entity}> {{
}}
"""
    with open(f"{base_dir}/mapper/{entity}Mapper.java", "w") as f:
        f.write(mapper_code)
        
    # DTO
    dto_code = f"""package com.duoduo.jxc.dto;

import lombok.Data;

@Data
public class {entity}DTO {{
"""
    for field in fields:
        dto_code += f"    private {field[0]} {field[1]};\n"
    dto_code += "}\n"
    
    with open(f"{base_dir}/dto/{entity}DTO.java", "w") as f:
        f.write(dto_code)

    # Query DTO
    query_dto_code = f"""package com.duoduo.jxc.dto;

import com.duoduo.jxc.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class {entity}Query extends PageQuery {{
"""
    for field in fields:
        if field[1] in query_fields:
            query_dto_code += f"    private {field[0]} {field[1]};\n"
    query_dto_code += "}\n"
    
    with open(f"{base_dir}/dto/{entity}Query.java", "w") as f:
        f.write(query_dto_code)
        
    # Service
    service_code = f"""package com.duoduo.jxc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.{entity}DTO;
import com.duoduo.jxc.dto.{entity}Query;
import com.duoduo.jxc.entity.{entity};

public interface {entity}Service extends IService<{entity}> {{
    PageResult<{entity}DTO> pageQuery({entity}Query query);
    {entity}DTO getDetail(Long id);
    Long create({entity}DTO dto);
    void update({entity}DTO dto);
    void delete(Long id);
}}
"""
    with open(f"{base_dir}/service/{entity}Service.java", "w") as f:
        f.write(service_code)
        
    # ServiceImpl
    service_impl_code = f"""package com.duoduo.jxc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.dto.{entity}DTO;
import com.duoduo.jxc.dto.{entity}Query;
import com.duoduo.jxc.entity.{entity};
import com.duoduo.jxc.mapper.{entity}Mapper;
import com.duoduo.jxc.service.{entity}Service;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class {entity}ServiceImpl extends ServiceImpl<{entity}Mapper, {entity}> implements {entity}Service {{

    @Override
    public PageResult<{entity}DTO> pageQuery({entity}Query query) {{
        Page<{entity}> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<{entity}> wrapper = new LambdaQueryWrapper<>();
        
"""
    for field in fields:
        if field[1] in query_fields:
            if field[0] == "String":
                service_impl_code += f"        wrapper.like(StringUtils.hasText(query.get{field[1][0].upper() + field[1][1:]}()), {entity}::get{field[1][0].upper() + field[1][1:]}, query.get{field[1][0].upper() + field[1][1:]}());\n"
            else:
                service_impl_code += f"        wrapper.eq(query.get{field[1][0].upper() + field[1][1:]}() != null, {entity}::get{field[1][0].upper() + field[1][1:]}, query.get{field[1][0].upper() + field[1][1:]}());\n"

    service_impl_code += f"""
        page(page, wrapper);

        List<{entity}DTO> dtoList = page.getRecords().stream().map(this::convertToDTO).collect(Collectors.toList());
        return new PageResult<>(page.getTotal(), dtoList);
    }}

    @Override
    public {entity}DTO getDetail(Long id) {{
        {entity} entity = getById(id);
        return entity != null ? convertToDTO(entity) : null;
    }}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create({entity}DTO dto) {{
        {entity} entity = new {entity}();
        BeanUtils.copyProperties(dto, entity);
        save(entity);
        return entity.get{fields[0][1][0].upper() + fields[0][1][1:]}();
    }}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update({entity}DTO dto) {{
        {entity} entity = new {entity}();
        BeanUtils.copyProperties(dto, entity);
        updateById(entity);
    }}

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {{
        removeById(id);
    }}

    private {entity}DTO convertToDTO({entity} entity) {{
        {entity}DTO dto = new {entity}DTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }}
}}
"""
    with open(f"{base_dir}/service/impl/{entity}ServiceImpl.java", "w") as f:
        f.write(service_impl_code)

    # Controller
    controller_code = f"""package com.duoduo.jxc.controller;

import com.duoduo.jxc.common.PageResult;
import com.duoduo.jxc.common.Result;
import com.duoduo.jxc.dto.{entity}DTO;
import com.duoduo.jxc.dto.{entity}Query;
import com.duoduo.jxc.service.{entity}Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/{entity.lower()}")
@RequiredArgsConstructor
public class {entity}Controller {{

    private final {entity}Service {entity[:1].lower() + entity[1:]}Service;

    @GetMapping("/list")
    public Result<PageResult<{entity}DTO>> pageQuery({entity}Query query) {{
        return Result.success({entity[:1].lower() + entity[1:]}Service.pageQuery(query));
    }}

    @GetMapping("/{{id}}")
    public Result<{entity}DTO> getDetail(@PathVariable("id") Long id) {{
        return Result.success({entity[:1].lower() + entity[1:]}Service.getDetail(id));
    }}

    @PostMapping
    public Result<Long> create(@RequestBody {entity}DTO dto) {{
        return Result.success({entity[:1].lower() + entity[1:]}Service.create(dto));
    }}

    @PutMapping("/{{id}}")
    public Result<Void> update(@PathVariable("id") Long id, @RequestBody {entity}DTO dto) {{
        dto.set{fields[0][1][0].upper() + fields[0][1][1:]}(id);
        {entity[:1].lower() + entity[1:]}Service.update(dto);
        return Result.success();
    }}

    @DeleteMapping("/{{id}}")
    public Result<Void> delete(@PathVariable("id") Long id) {{
        {entity[:1].lower() + entity[1:]}Service.delete(id);
        return Result.success();
    }}
}}
"""
    with open(f"{base_dir}/controller/{entity}Controller.java", "w") as f:
        f.write(controller_code)
