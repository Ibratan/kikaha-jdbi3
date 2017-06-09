package kikaha.jdbi;

/**
 *
 */
public class DatabaseException extends RuntimeException {

	private static final long serialVersionUID = -1439721948L;

	public DatabaseException( final String message ) {
		super( message );
	}

	public DatabaseException( final Throwable rootCause ) {
		super( rootCause );
	}

	public DatabaseException( final String message, final Throwable rootCause ) {
		super( message, rootCause );
	}
}
