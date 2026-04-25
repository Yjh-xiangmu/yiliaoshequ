package com.hospital.escort.controller;

import com.hospital.escort.common.Result;
import com.hospital.escort.entity.Patient;
import com.hospital.escort.service.PatientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 就诊人Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * 获取就诊人列表
     */
    @GetMapping("/list")
    public Result<List<Patient>> getList(@RequestAttribute Long userId) {
        log.info("获取就诊人列表，用户ID：{}", userId);
        List<Patient> list = patientService.getPatientList(userId);
        return Result.success(list);
    }

    /**
     * 添加就诊人
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Patient patient,
                            @RequestAttribute Long userId) {
        log.info("添加就诊人，用户ID：{}，就诊人：{}", userId, patient);
        patientService.addPatient(userId, patient);
        return Result.success();
    }

    /**
     * 更新就诊人
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Patient patient,
                               @RequestAttribute Long userId) {
        log.info("更新就诊人，用户ID：{}，就诊人：{}", userId, patient);
        patientService.updatePatient(userId, patient);
        return Result.success();
    }

    /**
     * 删除就诊人
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestAttribute Long userId) {
        log.info("删除就诊人，用户ID：{}，就诊人ID：{}", userId, id);
        patientService.deletePatient(userId, id);
        return Result.success();
    }

    /**
     * 设为默认
     */
    @PostMapping("/set-default/{id}")
    public Result<Void> setDefault(@PathVariable Long id,
                                   @RequestAttribute Long userId) {
        log.info("设为默认就诊人，用户ID：{}，就诊人ID：{}", userId, id);
        patientService.setDefault(userId, id);
        return Result.success();
    }
}