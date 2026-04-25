package com.hospital.escort.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.escort.common.BusinessException;
import com.hospital.escort.entity.Patient;
import com.hospital.escort.mapper.PatientMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 就诊人Service
 */
@Slf4j
@Service
public class PatientService extends ServiceImpl<PatientMapper, Patient> {

    /**
     * 获取用户的就诊人列表
     */
    public List<Patient> getPatientList(Long userId) {
        return lambdaQuery()
                .eq(Patient::getUserId, userId)
                .orderByDesc(Patient::getIsDefault)
                .orderByDesc(Patient::getCreateTime)
                .list();
    }

    /**
     * 添加就诊人
     */
    public void addPatient(Long userId, Patient patient) {
        // 检查数量限制（最多5个）
        long count = lambdaQuery()
                .eq(Patient::getUserId, userId)
                .count();
        if (count >= 5) {
            throw new BusinessException("最多添加5个就诊人");
        }

        patient.setUserId(userId);

        // 如果是第一个，设为默认
        if (count == 0) {
            patient.setIsDefault(1);
        } else {
            patient.setIsDefault(0);
        }

        save(patient);
    }

    /**
     * 更新就诊人
     */
    public void updatePatient(Long userId, Patient patient) {
        Patient existing = getById(patient.getId());
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("就诊人不存在");
        }
        updateById(patient);
    }

    /**
     * 删除就诊人
     */
    public void deletePatient(Long userId, Long patientId) {
        Patient existing = getById(patientId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            throw new BusinessException("就诊人不存在");
        }
        removeById(patientId);
    }

    /**
     * 设为默认就诊人
     */
    public void setDefault(Long userId, Long patientId) {
        // 取消所有默认
        lambdaUpdate()
                .eq(Patient::getUserId, userId)
                .set(Patient::getIsDefault, 0)
                .update();

        // 设置新默认
        lambdaUpdate()
                .eq(Patient::getId, patientId)
                .set(Patient::getIsDefault, 1)
                .update();
    }
}