package wostal.call.of.code.abstracts;

import java.util.List;

public class AbstractService<T, D extends AbstractDao<T>> {

	protected D dao;
	
	public Long create(T o) {
		return dao.create(o);
	}
	
	public T get(Long id) {
		return (T) dao.get(id);
	}
	
	public List<T> getAll(){
		return dao.getAll();
		
	}

	public boolean delete(T o) {
		return dao.delete(o);
	}
	
	public boolean update(T o) {
		return dao.update(o);
	}

}
