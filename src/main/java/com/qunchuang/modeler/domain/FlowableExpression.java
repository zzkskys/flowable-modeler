package com.qunchuang.modeler.domain;

/**
 * Create Time : 2020/01/09
 *
 * @author zzk
 */
public class FlowableExpression {

    private String expression;

    private String description;

    public FlowableExpression(String expression, String description) {
        this.expression = expression;
        this.description = description;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
