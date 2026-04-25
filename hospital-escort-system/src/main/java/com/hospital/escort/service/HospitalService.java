package com.hospital.escort.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.common.PageResult;
import com.hospital.escort.entity.Hospital;
import com.hospital.escort.mapper.HospitalMapper;
import com.hospital.escort.vo.HospitalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医院Service
 */
@Service
@RequiredArgsConstructor
public class HospitalService extends ServiceImpl<HospitalMapper, Hospital> {

    /**
     * 分页查询医院列表
     */
    public PageResult<HospitalVO> getHospitalList(Long current, Long size, String keyword) {
        Page<Hospital> page = new Page<>(current, size);

        IPage<Hospital> pageResult = lambdaQuery()
                .eq(Hospital::getStatus, 1)
                .and(keyword != null && !keyword.isEmpty(),
                        wrapper -> wrapper.like(Hospital::getName, keyword)
                                .or()
                                .like(Hospital::getAddress, keyword))
                .orderByDesc(Hospital::getCreateTime)
                .page(page);

        List<HospitalVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageResult.getTotal(), voList, current, size);
    }

    /**
     * 获取所有医院列表（不分页）
     */
    public List<HospitalVO> getAllHospitals() {
        List<Hospital> hospitals = lambdaQuery()
                .eq(Hospital::getStatus, 1)
                .orderByDesc(Hospital::getCreateTime)
                .list();

        return hospitals.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取医院详情
     */
    public HospitalVO getHospitalById(Long id) {
        Hospital hospital = getById(id);
        if (hospital == null) {
            throw new BusinessException("医院不存在");
        }
        return convertToVO(hospital);
    }

    /**
     * 转换为VO
     */
    private HospitalVO convertToVO(Hospital hospital) {
        HospitalVO vo = new HospitalVO();
        BeanUtils.copyProperties(hospital, vo);

        // 解析科室JSON
        if (hospital.getDepartments() != null && !hospital.getDepartments().isEmpty()) {
            vo.setDepartments(JSONUtil.toList(hospital.getDepartments(), String.class));
        }

        return vo;
    }
}