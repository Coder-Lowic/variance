package com.lowic.data.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lowic.data.analysis.entity.SbInfo;
import com.lowic.data.analysis.service.ISbInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SB广告基础信息", description = "Sponsored Brands Info CRUD")
@RestController
@RequestMapping("/api/v1/sb-info")
public class SbInfoController {
    private static final Logger log = LoggerFactory.getLogger(SbInfoController.class);

    private final ISbInfoService service;

    public SbInfoController(ISbInfoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "查询列表")
    public ResponseEntity<List<SbInfo>> list(
            @RequestParam(required = false) String campaignName,
            @RequestParam(required = false) String brand) {
        LambdaQueryWrapper<SbInfo> qw = new LambdaQueryWrapper<>();
        if (campaignName != null) qw.eq(SbInfo::getCampaignName, campaignName);
        if (brand != null) qw.eq(SbInfo::getBrand, brand);
        return ResponseEntity.ok(service.list(qw));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询详情")
    public ResponseEntity<SbInfo> get(@PathVariable Integer id) {
        SbInfo entity = service.getById(id);
        if (entity == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(entity);
    }

    @PostMapping
    @Operation(summary = "新增")
    public ResponseEntity<SbInfo> create(@RequestBody SbInfo entity) {
        service.save(entity);
        log.info("Created SbInfo id={}", entity.getId());
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新")
    public ResponseEntity<SbInfo> update(@PathVariable Integer id, @RequestBody SbInfo entity) {
        entity.setId(id);
        service.updateById(entity);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.removeById(id);
        return ResponseEntity.noContent().build();
    }
}
