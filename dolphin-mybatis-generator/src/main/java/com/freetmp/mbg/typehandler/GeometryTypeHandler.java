package com.freetmp.mbg.typehandler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.geolatte.geom.Geometry;
import org.geolatte.geom.Point;
import org.geolatte.geom.codec.Wkt;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/*
 * Postgis中Point类型的类型解析器
 * @author Pin Liu
 */
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({Geometry.class,Point.class})
public class GeometryTypeHandler extends BaseTypeHandler<Geometry> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, parameter.asText());
	}

	@Override
	public Geometry getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String value = rs.getString(columnName);
		if(!StringUtils.isEmpty(value)){
			return Wkt.fromWkt(value);
		}
		return null;
	}

	@Override
	public Geometry getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		String value = rs.getString(columnIndex);
		if(!StringUtils.isEmpty(value)){
			return Wkt.fromWkt(value);
		}		
		return null;
	}

	@Override
	public Geometry getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String value = cs.getString(columnIndex);
		if(!StringUtils.isEmpty(value)){
			return Wkt.fromWkt(value);
		}			
		return null;
	}

}
