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
import wostal.call.of.code.entity.Role;
import wostal.call.of.code.entity.User;
import wostal.call.of.code.rowmapper.RoleRowMapper;

@Repository
public class RoleDaoImpl implements AbstractDao<Role> {
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public Long create(Role o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO role (name, id) VALUES(:name, :id)";
		namedParamJdbcTemplate.update(sqlQuery, beanParams, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}

	public Role get(Long id) {
		SqlParameterSource params = new MapSqlParameterSource("ID", id);
		String sqlQuery = "SELECT name, id FROM role WHERE id = :ID";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new RoleRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public Role get(String name) {
		SqlParameterSource params = new MapSqlParameterSource("name", name);
		String sqlQuery = "SELECT name, id FROM role WHERE name like :name";
		try {
			return namedParamJdbcTemplate.queryForObject(sqlQuery, params, new RoleRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Role> getAll() {
		String sqlQuery = "SELECT * FROM role";
		List<Role> list = namedParamJdbcTemplate.query(sqlQuery, new RoleRowMapper());
		return list;
	}

	public boolean delete(Role o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "DELETE FROM role WHERE id = :id";
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public boolean update(Role o) {
		SqlParameterSource beanParams = new BeanPropertySqlParameterSource(o);
		String sqlQuery = "UPDATE role SET role:=role, id:=id WHERE id = :id";
		namedParamJdbcTemplate.update(sqlQuery, beanParams);
		return namedParamJdbcTemplate.update(sqlQuery, beanParams) == 1;
	}

	public void cleanup() {
		String sqlQuery = "TRUNCATE TABLE role ";
		namedParamJdbcTemplate.getJdbcOperations().execute(sqlQuery);
	}

	public List<Role> getUserRoles(User user) {
			SqlParameterSource beanParams = new BeanPropertySqlParameterSource(user);
			String sqlQuery = "SELECT * FROM role r join role_user ur on (ur.id_role = r.id AND ur.id_user=:id)";
			List<Role> list = namedParamJdbcTemplate.query(sqlQuery,beanParams, new RoleRowMapper());
			return list;
	}
	
	public Long addRoleToUser(User user, String name) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("idUser", user.getId());
		values.put("name", name);
		SqlParameterSource params = new MapSqlParameterSource(values);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO role_user (id_user, id_role) VALUES(:idUser, (select id from role where name like :name))";
		if(get(name)==null) {
			Role newRole = new Role();
			newRole.setName(name);
			create(newRole);
		}
		namedParamJdbcTemplate.update(sqlQuery, params, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}
	
	public Long addRoleToUser(User user, Long idRole) {
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("idUser", user.getId());
		values.put("idRole", idRole);
		SqlParameterSource params = new MapSqlParameterSource(values);
		final KeyHolder holder = new GeneratedKeyHolder();
		String sqlQuery = "INSERT INTO role_user (id_user, id_role) VALUES(:idUser, :idRole)";
		namedParamJdbcTemplate.update(sqlQuery, params, holder, new String[] { "id" });
		return holder.getKey().longValue();
	}
	
	public Long addRoleToUser(Long idUser, String role) {
		User user = new User();
		user.setId(idUser);
		return addRoleToUser(user, role);
	}
}
