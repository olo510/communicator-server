package wostal.call.of.code.entity;

public class User {
	private String password;
	private Long id;
	private String nick;

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return this.password;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return this.nick;
	}

	@Override
	public String toString() {
		return nick;
	}
	
	
	
	
}