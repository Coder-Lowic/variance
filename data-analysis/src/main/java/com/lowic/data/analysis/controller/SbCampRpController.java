package com.lowic.data.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lowic.data.analysis.entity.SbCampRp;
import com.lowic.data.analysis.service.ISbCampRpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SB广告活动报表", description = "Sponsored Brands Campaign Report CRUD")
@RestController
@RequestMapping("/api/v1/sb-camp-rp")
public class SbCampRpController {
    private static final Logger log = LoggerFactory.getLogger(SbCampRpController.class);

    private final ISbCampRpService service;

    public SbCampRpController(ISbCampRpService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "查询列表")
    public ResponseEntity<List<SbCampRp>> list(
            @RequestParam(required = false) String campaignName) {
        LambdaQueryWrapper<SbCampRp> qw = new LambdaQueryWrapper<>();
        if (campaignName != null) qw.eq(SbCampRp::getCampaignName, campaignName);
        qw.orderByDesc(SbCampRp::getDate);
        return ResponseEntity.ok(service.list(qw));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询详情")
    public ResponseEntity<SbCampRp> get(@PathVariable Integer id) {
        SbCampRp entity = service.getById(id);
        if (entity == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(entity);
    }

    @PostMapping
    @Operation(summary = "新增")
    public ResponseEntity<SbCampRp> create(@RequestBody SbCampRp entity) {
        service.save(entity);
        log.info("Created SbCampRp id={}", entity.getId());
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新")
    public ResponseEntity<SbCampRp> update(@PathVariable Integer id, @RequestBody SbCampRp entity) {
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
