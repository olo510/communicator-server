package wostal.call.of.code.entity;

public class Conversation {
	private String name;
	private Long id;
	private String uuid;
	private boolean asConference;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return this.id;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUuid() {
		return this.uuid;
	}

	public boolean isAsConference() {
		return asConference;
	}

	public void setAsConference(boolean asConference) {
		this.asConference = asConference;
	}

}