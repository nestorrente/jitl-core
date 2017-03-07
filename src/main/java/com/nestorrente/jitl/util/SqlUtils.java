package com.nestorrente.jitl.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Collection;

public class SqlUtils {

	public static void setObject(PreparedStatement statement, int index, Object value) throws SQLException {

		if(value == null) {
			try {
				statement.setObject(index, null);
			} catch(SQLException ex) {
				statement.setNull(index, Types.OTHER);
			}
		} else if(value instanceof Byte) {
			statement.setByte(index, (Byte) value);
		} else if(value instanceof Short) {
			statement.setShort(index, (Short) value);
		} else if(value instanceof Integer) {
			statement.setInt(index, (Integer) value);
		} else if(value instanceof Long) {
			statement.setLong(index, (Long) value);
		} else if(value instanceof Float) {
			statement.setFloat(index, (Float) value);
		} else if(value instanceof Double) {
			statement.setDouble(index, (Double) value);
		} else if(value instanceof Integer) {
			statement.setInt(index, (Integer) value);
		} else if(value instanceof Integer) {
			statement.setInt(index, (Integer) value);
		} else if(value instanceof String) {
			statement.setString(index, (String) value);
		} else if(value instanceof Date) {
			statement.setDate(index, (Date) value);
		} else if(value instanceof Time) {
			statement.setTime(index, (Time) value);
		} else if(value instanceof Timestamp) {
			statement.setTimestamp(index, (Timestamp) value);
		} else if(value instanceof java.util.Date) {
			statement.setDate(index, new Date(((java.util.Date) value).getTime()));
		} else {
			statement.setObject(index, value);
		}

	}

	public static Object getObject(ResultSet resultSet, int index) throws SQLException {

		// TODO contemplar todos los tipos
		// Los binarios hacerlos con getBytes o getBinaryStream?
		switch(resultSet.getMetaData().getColumnType(index)) {
			case Types.ARRAY:
				return resultSet.getArray(index);
			case Types.BIGINT:
				return resultSet.getInt(index);
			case Types.BINARY:
				return resultSet.getBytes(index);
			case Types.BOOLEAN:
				return resultSet.getBoolean(index);
			case Types.BLOB:
				return resultSet.getBlob(index);
			case Types.DOUBLE:
				return resultSet.getDouble(index);
			case Types.FLOAT:
				return resultSet.getFloat(index);
			case Types.INTEGER:
				return resultSet.getInt(index);
			case Types.NVARCHAR:
				return resultSet.getNString(index);
			case Types.VARCHAR:
				return resultSet.getString(index);
			case Types.TINYINT:
				return resultSet.getInt(index);
			case Types.SMALLINT:
				return resultSet.getInt(index);
			case Types.DATE:
				return resultSet.getDate(index);
			case Types.TIMESTAMP:
				return resultSet.getTimestamp(index);
			default:
				return resultSet.getObject(index);
		}

	}

	public static PreparedStatement prepareStatement(Connection connection, String sql, Collection<?> parameters, boolean returnGeneratedKeys) throws Exception {

		PreparedStatement statement = connection.prepareStatement(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);

		int index = 1;

		for(Object value : parameters) {
			SqlUtils.setObject(statement, index++, value);
		}

		return statement;

	}

}
