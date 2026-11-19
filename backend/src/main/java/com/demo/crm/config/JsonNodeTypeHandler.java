package com.demo.crm.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;

/**
 * JSON 列与 JsonNode 互相转换的类型处理器，mapper XML 里通过 typeHandler 指定
 */
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode value, JdbcType type) throws SQLException {
        ps.setString(i, value.toString());
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String name) throws SQLException { return parse(rs.getString(name)); }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int index) throws SQLException { return parse(rs.getString(index)); }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int index) throws SQLException { return parse(cs.getString(index)); }

    private JsonNode parse(String value) throws SQLException {
        try {
            return value == null ? null : JSON.readTree(value);
        } catch (JsonProcessingException e) {
            throw new SQLException("Invalid stored JSON", e);
        }
    }
}
