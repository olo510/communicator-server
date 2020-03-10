package wostal.call.of.code.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import wostal.call.of.code.entity.User;
import wostal.call.of.code.rowmapper.ConversationRowMapper;

@Repository
public class ConversationDaoImpl implements AbstractDao<Conversation> {
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public Long create(Conversation o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO conversation (name, id, uuid) VALUES(:name, :id, :uuid)";
		namedParamJdbcTemplate.update(sqlQuery, beanParams, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}

	public Conversation get(Long id) {
		SqlParameterSource params = new MapSqlParameterSource("ID", id);
		String sqlQuery = "SELECT name, id, uuid FROM conversation WHERE id = :ID";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new ConversationRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public Conversation get(String hash) {
		SqlParameterSource params = new MapSqlParameterSource("hash", hash);
		String sqlQuery = "SELECT name, id, uuid FROM conversation WHERE uuid = :hash";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new ConversationRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Conversation> getAll() {
		String sqlQuery = "SELECT * FROM conversation";
		List<Conversation> list = namedParamJdbcTemplate.query(sqlQuery, new ConversationRowMapper());
		return list;
	}
	
	public List<Conversation> getUserConferences(User user) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(user);
		String sqlQuery = "SELECT c.* FROM conversation c join conversation_user cu on (cu.id_conversation=c.id AND cu.id_user=:id) where 2<(select count(wcu.id) from conversation_user wcu where c.id=wcu.id_conversation)";
		List<Conversation> list = namedParamJdbcTemplate.query(sqlQuery, beanParams, new ConversationRowMapper());
		return list;
	}

	public boolean delete(Conversation o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "DELETE FROM conversation WHERE id = :id";
		String sqlQuery2 = "DELETE FROM conversation_user WHERE id_conversation = :id";
		namedParamJdbcTemplate.update(sqlQuery2, beanParams);
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public boolean update(Conversation o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "UPDATE conversation SET name=:name, id=:id, uuid=:uuid WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}
	
	public Long addUserToConversation(Long idUser, Long idConversation) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("idUser", idUser);
		values.put("idConversation", idConversation);
		SqlParameterSource params = new MapSqlParameterSource(values);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO conversation_user (id_user, id_conversation) VALUES(:idUser, :idConversation)";
		namedParamJdbcTemplate.update(sqlQuery, params, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}
	
	public boolean deleteUserFromConversation(Conversation o, Long idUser) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("idUser", idUser);
		values.put("idConversation", o.getId());
		SqlParameterSource params = new MapSqlParameterSource(values);
		String sqlQuery = "DELETE FROM conversation_user WHERE id_conversation=:idConversation AND id_user=:idUser";
		return namedParamJdbcTemplate.update(sqlQuery, params) == 1;
	}

	public void cleanup() {
		String sqlQuery = "TRUNCATE TABLE conversation ";
		namedParamJdbcTemplate.getJdbcOperations().execute(sqlQuery);
	}
}
