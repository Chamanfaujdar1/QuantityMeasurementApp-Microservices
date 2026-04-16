package com.chaman.history.controller;

import com.chaman.history.entity.QuantityHistoryEntity;
import com.chaman.history.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quantities")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/history/operation/{operation}")
    public Page<QuantityHistoryEntity> getHistory(
            @PathVariable String operation,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return historyService.getHistoryByOperation(operation, PageRequest.of(page, size));
    }

    @GetMapping("/count/{operation}")
    public long getCount(@PathVariable String operation) {
        return historyService.getOperationCount(operation);
    }

    @GetMapping("/history/all")
    public List<QuantityHistoryEntity> getAllHistory() {
        return historyService.getAllHistory();
    }

    @PostMapping("/history")
    public QuantityHistoryEntity saveHistory(@RequestBody QuantityHistoryEntity entity) {
        return historyService.saveHistory(entity);
    }
}
