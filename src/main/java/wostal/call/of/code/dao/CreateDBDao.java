package wostal.call.of.code.dao;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CreateDBDao {
	private NamedParameterJdbcTemplate namedParamJdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		namedParamJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	@Transactional
	public void create() {

		try (Connection connection = namedParamJdbcTemplate.getJdbcTemplate().getDataSource().getConnection()) {

			ScriptUtils.executeSqlScript(connection,
					new EncodedResource(new ClassPathResource("create.sql"), StandardCharsets.UTF_8));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
