package com.duoduo.jxc.service.rbac;

import com.baomidou.mybatisplus.extension.service.IService;
import com.duoduo.jxc.entity.rbac.SysDept;
import java.util.List;

public interface SysDeptService extends IService<SysDept> {
    List<SysDept> selectDeptList(SysDept dept);
    List<SysDept> buildDeptTree(List<SysDept> depts);
}