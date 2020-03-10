package wostal.call.of.code.entity;

import java.sql.Timestamp;

public class Message {
	private Timestamp writeTime;
	private Long idConversation;
	private String uuidConversation;
	private Long id;
	private Long idUser;
	private String nick;
	private String content;

	public void setWriteTime(Timestamp writeTime) {
		this.writeTime = writeTime;
	}

	public Timestamp getWriteTime() {
		return this.writeTime;
	}

	public void setIdConversation(Long idConversation) {
		this.idConversation = idConversation;
	}

	public Long getIdConversation() {
		return this.idConversation;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public Long getIdUser() {
		return this.idUser;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return this.content;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getUuidConversation() {
		return uuidConversation;
	}

	public void setUuidConversation(String uuidConversation) {
		this.uuidConversation = uuidConversation;
	}
}