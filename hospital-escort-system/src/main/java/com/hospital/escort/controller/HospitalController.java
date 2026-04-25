package com.hospital.escort.controller;

import com.hospital.escort.common.PageResult;
import com.hospital.escort.common.Result;
import com.hospital.escort.service.HospitalService;
import com.hospital.escort.vo.HospitalVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医院Controller
 */
@RestController
@RequestMapping("/api/hospital")
@RequiredArgsConstructor
public class HospitalController {

    private final HospitalService hospitalService;

    /**
     * 分页查询医院列表
     */
    @GetMapping("/list")
    public Result<PageResult<HospitalVO>> getHospitalList(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword) {
        PageResult<HospitalVO> result = hospitalService.getHospitalList(current, size, keyword);
        return Result.success(result);
    }

    /**
     * 获取所有医院（不分页）
     */
    @GetMapping("/all")
    public Result<List<HospitalVO>> getAllHospitals() {
        List<HospitalVO> list = hospitalService.getAllHospitals();
        return Result.success(list);
    }

    /**
     * 获取医院详情
     */
    @GetMapping("/detail/{id}")
    public Result<HospitalVO> getHospitalDetail(@PathVariable Long id) {
        HospitalVO vo = hospitalService.getHospitalById(id);
        return Result.success(vo);
    }
}