package wostal.call.of.code.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import wostal.call.of.code.entity.Conversation;

public class ConversationRowMapper implements RowMapper<Conversation> {
	public Conversation mapRow(ResultSet rs, int rownum) throws SQLException {
		Conversation conversation = new Conversation();
		conversation.setName(rs.getString("name"));
		conversation.setId(rs.getLong("id"));
		conversation.setUuid(rs.getString("uuid"));
		conversation.setAsConference(rs.getBoolean("is_conference"));
		return conversation;
	}
}