package wostal.call.of.code.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import wostal.call.of.code.abstracts.AbstractDao;
import wostal.call.of.code.entity.Conversation;
import wostal.call.of.code.entity.Message;
import wostal.call.of.code.rowmapper.MessageRowMapper;

@Repository
public class MessageDaoImpl implements AbstractDao<Message> {
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public Long create(Message o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO message (write_time, id_conversation, id, id_user, content) VALUES(:writeTime, :idConversation, :id, :idUser, :content)";
		namedParamJdbcTemplate.update(sqlQuery, beanParams, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}

	public Message get(Long id) {
		SqlParameterSource params = new MapSqlParameterSource("ID", id);
		String sqlQuery = "SELECT m.write_time, m.id_conversation, m.id, m.id_user, m.content, u.nick FROM message m join user u on (m.id_user= u.id) WHERE id = :ID";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new MessageRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Message> search(Long idConversation, String content) {
		SqlParameterSource params = new MapSqlParameterSource("content", content).addValue("idConversation", idConversation);
		String sqlQuery = "SELECT m.write_time, m.id_conversation, m.id, m.id_user, m.content, u.nick FROM message m join user u on (m.id_user= u.id) WHERE m.id_conversation = :idConversation AND MATCH (content) AGAINST (:content)";
		List<Message> list = namedParamJdbcTemplate.query(sqlQuery, params, new MessageRowMapper());
		return list;
	}

	public List<Message> getAll() {
		String sqlQuery = "SELECT * FROM message";
		List<Message> list = namedParamJdbcTemplate.query(sqlQuery, new MessageRowMapper());
		return list;
	}
	
	public List<Message> getMessages(int offset, int limit, Long idConversation) {
		SqlParameterSource params = new MapSqlParameterSource("ID", idConversation).addValue("offset", offset).addValue("limit", limit);
		String sqlQuery = "SELECT m.write_time, m.id_conversation, m.id, m.id_user, m.content, u.nick FROM message m join user u on (m.id_user= u.id) WHERE id_conversation = :ID ORDER by m.write_time DESC LIMIT :limit offset :offset";
		List<Message> list = namedParamJdbcTemplate.query(sqlQuery,params, new MessageRowMapper());
		return list;
	}

	public boolean delete(Message o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "DELETE FROM message WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}
	
	public boolean delete(Conversation o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "DELETE FROM message WHERE id_conversation = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public boolean update(Message o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "UPDATE message SET write_time=:writeTime, id_conversation=:idConversation, id=:id, id_user=:idUser, content=:content, status=:status WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public void cleanup() {
		String sqlQuery = "TRUNCATE TABLE message ";
		namedParamJdbcTemplate.getJdbcOperations().execute(sqlQuery);
	}
}
