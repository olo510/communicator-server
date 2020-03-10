package wostal.call.of.code.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import wostal.call.of.code.entity.Role;

public class RoleRowMapper implements RowMapper<Role> {
	public Role mapRow(ResultSet rs, int rownum) throws SQLException {
		Role role = new Role();
		role.setName(rs.getString("name"));
		role.setId(rs.getLong("id"));
		return role;
	}
}