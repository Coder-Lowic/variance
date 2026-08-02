package com.lowic.data.analysis.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lowic.data.analysis.entity.SpAdRp;
import com.lowic.data.analysis.service.ISpAdRpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SP广告报表", description = "Sponsored Products Ad Report CRUD")
@RestController
@RequestMapping("/api/v1/sp-ad-rp")
public class SpAdRpController {
    private static final Logger log = LoggerFactory.getLogger(SpAdRpController.class);

    private final ISpAdRpService service;

    public SpAdRpController(ISpAdRpService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "查询列表")
    public ResponseEntity<List<SpAdRp>> list(
            @RequestParam(required = false) String campaignName,
            @RequestParam(required = false) String advertisedAsin) {
        LambdaQueryWrapper<SpAdRp> qw = new LambdaQueryWrapper<>();
        if (campaignName != null) qw.eq(SpAdRp::getCampaignName, campaignName);
        if (advertisedAsin != null) qw.eq(SpAdRp::getAdvertisedAsin, advertisedAsin);
        qw.orderByDesc(SpAdRp::getDate);
        return ResponseEntity.ok(service.list(qw));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询详情")
    public ResponseEntity<SpAdRp> get(@PathVariable Integer id) {
        SpAdRp entity = service.getById(id);
        if (entity == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(entity);
    }

    @PostMapping
    @Operation(summary = "新增")
    public ResponseEntity<SpAdRp> create(@RequestBody SpAdRp entity) {
        service.save(entity);
        log.info("Created SpAdRp id={}", entity.getId());
        return ResponseEntity.ok(entity);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新")
    public ResponseEntity<SpAdRp> update(@PathVariable Integer id, @RequestBody SpAdRp entity) {
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
