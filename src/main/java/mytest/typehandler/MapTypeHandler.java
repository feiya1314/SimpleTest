package mytest.typehandler;

import com.alibaba.fastjson.JSON;
import org.apache.ibatis.type.*;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

//@MappedTypes(Map.class)
/*@MappedJdbcTypes(JdbcType.VARCHAR,JdbcType.VARCHAR)*/
@MappedJdbcTypes({JdbcType.VARCHAR,JdbcType.VARCHAR})
public class MapTypeHandler extends BaseTypeHandler<Map<String,String>> {
  /*  @Override
    public void setParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public Map<String, String> getResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public Map<String, String> getResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public Map<String, String> getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        return JSON.parseObject(result,Map.class);
    }*/
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Map<String, String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public Map<String, String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return JSON.parseObject(result,Map.class);
    }

    @Override
    public Map<String, String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        return JSON.parseObject(result,Map.class);
    }
}
