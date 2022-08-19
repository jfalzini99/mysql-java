package projects.exception;

/*
 * WEEK 7 CODING ASSIGNMENT- DbException class
 * EDITTED BY - Joseph Falzini
 */

@SuppressWarnings("serial")
public class DbException extends RuntimeException {

	public DbException(String message) {
		super(message);
	}

	public DbException(Throwable cause) {
		super(cause);
	}

	public DbException(String message, Throwable cause) {
		super(message, cause);
	}

}