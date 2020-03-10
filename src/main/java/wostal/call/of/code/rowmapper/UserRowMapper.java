package wostal.call.of.code.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import wostal.call.of.code.entity.User;

public class UserRowMapper implements RowMapper<User> {
	public User mapRow(ResultSet rs, int rownum) throws SQLException {
		User user = new User();
		user.setPassword(rs.getString("password"));
		user.setNick(rs.getString("nick"));
		user.setId(rs.getLong("id"));
		return user;
	}
}