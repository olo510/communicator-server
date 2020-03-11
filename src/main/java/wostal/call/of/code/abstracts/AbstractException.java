package wostal.call.of.code.abstracts;

public class AbstractException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1480149945012020065L;
	private String message;

	public AbstractException() {
	}

	public AbstractException(String message) {
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
