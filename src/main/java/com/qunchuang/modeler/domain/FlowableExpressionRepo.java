package com.qunchuang.modeler.domain;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Create Time : 2020/01/09
 *
 * @author zzk
 */
@Repository
public class FlowableExpressionRepo {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public FlowableExpressionRepo(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FlowableExpression> findAll() {
        return jdbcTemplate.query("select * from flowable_expression", new FlowableExpressionMapper());
    }

    public FlowableExpression save(FlowableExpression flowableExpression) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("expression", flowableExpression.getExpression());
        map.put("description", flowableExpression.getDescription());

        String updateSql = "update flowable_expression set description = :description where expression = :expression";
        boolean updated = jdbcTemplate.update(updateSql, map) > 0;
        if (!updated) {
            String insertSql = "insert flowable_expression set description = :description,expression = :expression";
            jdbcTemplate.update(insertSql, map);
        }
        return flowableExpression;
    }

    public void deleteByExpression(String expression) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("expression", expression);
        jdbcTemplate.update("delete from flowable_expression where expression = :expression", map);
    }
}

class FlowableExpressionMapper implements RowMapper<FlowableExpression> {

    @Override
    public FlowableExpression mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new FlowableExpression(rs.getString("expression"),
                rs.getString("description"));
    }
}