package com.duoduo.jxc.service.rbac.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.duoduo.jxc.entity.rbac.SysDept;
import com.duoduo.jxc.mapper.SysDeptMapper;
import com.duoduo.jxc.service.rbac.SysDeptService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    public List<SysDept> selectDeptList(SysDept dept) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeleted, 0);
        if (dept != null) {
            if (StringUtils.hasText(dept.getDeptName())) {
                wrapper.like(SysDept::getDeptName, dept.getDeptName());
            }
            if (dept.getStatus() != null) {
                wrapper.eq(SysDept::getStatus, dept.getStatus());
            }
        }
        wrapper.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getOrderNum);
        return list(wrapper);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = new ArrayList<>();
        for (SysDept dept : depts) {
            tempList.add(dept.getDeptId());
        }
        for (SysDept dept : depts) {
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    private void recursionFn(List<SysDept> list, SysDept t) {
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    private boolean hasChild(List<SysDept> list, SysDept t) {
        return getChildList(list, t).size() > 0;
    }

    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        for (SysDept n : list) {
            if (n.getParentId().equals(t.getDeptId())) {
                tlist.add(n);
            }
        }
        return tlist;
    }
}