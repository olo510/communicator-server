package wostal.call.of.code.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import wostal.call.of.code.entity.Message;

public class MessageRowMapper implements RowMapper<Message> {
	public Message mapRow(ResultSet rs, int rownum) throws SQLException {
		Message message = new Message();
		message.setWriteTime(rs.getTimestamp("write_time"));
		message.setIdConversation(rs.getLong("id_conversation"));
		message.setId(rs.getLong("id"));
		message.setIdUser(rs.getLong("id_user"));
		message.setNick(rs.getString("nick"));
		message.setContent(rs.getString("content"));
		return message;
	}
}