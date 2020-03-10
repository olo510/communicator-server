package wostal.call.of.code.abstracts;

import java.util.List;

import javax.sql.DataSource;

public interface AbstractDao<T> {

	public void setDataSource(DataSource ds);
	
	public Long create(T o);
	
	public T get(Long id);
	
	public List<T> getAll();

	public boolean delete(T o);
	
	public boolean update(T o);
}
