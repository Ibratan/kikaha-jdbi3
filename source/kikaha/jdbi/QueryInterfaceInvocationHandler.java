package kikaha.jdbi;

import java.lang.reflect.*;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.*;

/**
 * Wraps the Query Interface and execute method in a transaction.
 */
@RequiredArgsConstructor
public class QueryInterfaceInvocationHandler implements InvocationHandler {

	final Jdbi dbi;
	final Class<?> targetClazz;

	@Override
	public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
		final Handle h = dbi.open();
		try {
			final Object query = h.attach( targetClazz );
			return method.invoke( query, args );
		}
		catch ( final Exception cause ) { throw databaseException( method, cause ); }
		finally { h.close(); }
	}

	private RuntimeException databaseException( final Method method, Throwable cause ) {
		Throwable rootCause = cause;
		while( rootCause.getCause() != null )
			rootCause = rootCause.getCause();
		if ( rootCause instanceof RuntimeException )
			throw (RuntimeException)rootCause;
		return new DatabaseException( "Could no execute " + method, cause );
	}

	@SuppressWarnings("unchecked")
	public static <T> T create( final Jdbi dbi, final Class<T> targetClazz ){
		final InvocationHandler handler = new QueryInterfaceInvocationHandler( dbi, targetClazz );
		return (T) Proxy.newProxyInstance(
				targetClazz.getClassLoader(),
				new Class<?>[] { targetClazz },
				handler
			);
	}
}
