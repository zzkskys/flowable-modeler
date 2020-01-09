package com.qunchuang.modeler.rest;

import com.qunchuang.modeler.domain.FlowableExpression;
import com.qunchuang.modeler.domain.FlowableExpressionRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Create Time : 2020/01/09
 *
 * @author zzk
 */
@RestController
@RequestMapping("/expressions")
public class FlowableExpressionController {

    private final FlowableExpressionRepo expressionRepo;

    public FlowableExpressionController(FlowableExpressionRepo expressionRepo) {
        this.expressionRepo = expressionRepo;
    }

    @GetMapping
    public List<FlowableExpression> findAll() {
        return expressionRepo.findAll();
    }

    @PostMapping
    public FlowableExpression save(@RequestBody FlowableExpression flowableExpression) {
        return expressionRepo.save(flowableExpression);
    }

    @DeleteMapping
    public void delete(String expression) {
        expressionRepo.deleteByExpression(expression);
    }
}
