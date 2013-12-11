package eu.thermz.minilibs.simplernate.exceptions;

public class SimplernateException extends RuntimeException {

	private static final long serialVersionUID = -560483942625268443L;
	
	public SimplernateException() {
		super();
	}

	public SimplernateException(String message, Throwable cause) {
		super(message, cause);
	}

	public SimplernateException(String message) {
		super(message);
	}

	public SimplernateException(Throwable cause) {
		super(cause);
	}

}
