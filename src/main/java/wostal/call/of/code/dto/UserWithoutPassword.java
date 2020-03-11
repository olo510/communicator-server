package wostal.call.of.code.dto;

public class UserWithoutPassword {
	private Long id;
	private String nick;

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