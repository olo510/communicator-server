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
import wostal.call.of.code.dto.UserWithoutPassword;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.rowmapper.UserRowMapper;
import wostal.call.of.code.rowmapper.UserWithoutPasswordRowMapper;

@Repository
public class UserDaoImpl implements AbstractDao<User> {
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public Long create(User o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO user (password, nick) VALUES(:password, :nick)";
		namedParamJdbcTemplate.update(sqlQuery, beanParams, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}

	public User get(Long id) {
		SqlParameterSource params = new MapSqlParameterSource("ID", id);
		String sqlQuery = "SELECT id, password, nick FROM user WHERE id = :ID";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new UserRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public User get(String nick) {
		SqlParameterSource params = new MapSqlParameterSource("nick", nick);
		String sqlQuery = "SELECT id, password, nick FROM user WHERE nick = :nick";
		try {
			User user =namedParamJdbcTemplate.queryForObject(sqlQuery, params, new UserRowMapper());
			return user;
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	

	public List<User> getAll() {
		String sqlQuery = "SELECT * FROM user";
		List<User> list = namedParamJdbcTemplate.query(sqlQuery, new UserRowMapper());
		return list;
	}
	
	public List<UserWithoutPassword> getConversationUsers(Long idConversation) {
		SqlParameterSource params = new MapSqlParameterSource("idConversation", idConversation);
		String sqlQuery = "SELECT u.id, u.nick FROM user u join conversation_user cu on (cu.id_conversation=:idConversation and cu.id_user=u.id)";
		List<UserWithoutPassword> list = namedParamJdbcTemplate.query(sqlQuery,params, new UserWithoutPasswordRowMapper());
		return list;
	}
	
	public List<UserWithoutPassword> getContacts(User user) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(user);
		String sqlQuery = "SELECT id, nick FROM user where id != :id";
		List<UserWithoutPassword> list = namedParamJdbcTemplate.query(sqlQuery, beanParams, new UserWithoutPasswordRowMapper());
		return list;
	}

	public boolean delete(User o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "DELETE FROM user WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public boolean update(User o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "UPDATE user SET password=:password, nick=:nick WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}
}
