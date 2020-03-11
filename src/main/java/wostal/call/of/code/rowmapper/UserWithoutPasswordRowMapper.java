package wostal.call.of.code.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import wostal.call.of.code.dto.UserWithoutPassword;

public class UserWithoutPasswordRowMapper implements RowMapper<UserWithoutPassword> {
	public UserWithoutPassword mapRow(ResultSet rs, int rownum) throws SQLException {
		UserWithoutPassword user = new UserWithoutPassword();
		user.setNick(rs.getString("nick"));
		user.setId(rs.getLong("id"));
		return user;
	}
}