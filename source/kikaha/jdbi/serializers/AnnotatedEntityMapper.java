package kikaha.jdbi.serializers;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

@RequiredArgsConstructor
class AnnotatedEntityMapper<T> implements ColumnMapper<T> {

	final Class<T> targetClazz;
	final Iterable<Injectable> injectables;

	@Override
	public T map(ResultSet rs, int i, StatementContext statementContext) throws SQLException {
		final T newInstance = newInstanceOfTargetClass();
		for ( final Injectable injectable : injectables )
			injectable.injectOn( newInstance, rs );
		return newInstance;
	}

	private T newInstanceOfTargetClass() {
		try {
			return targetClazz.newInstance();
		} catch ( InstantiationException | IllegalAccessException e ) {
			throw new IllegalStateException( e );
		}
	}

	public static <T> AnnotatedEntityMapper<T> from( Class<T> clazz ) {
		final List<Injectable> injectables = readInjectableFields( clazz );
		return new AnnotatedEntityMapper<>( clazz, injectables );
	}

	private static List<Injectable> readInjectableFields( Class<?> aClass ) {
		final List<Injectable> injectables = new ArrayList<>();
		while ( aClass != null ) {
			for ( final Field field : aClass.getDeclaredFields() ) {
				final Injectable injectable = Injectable.from( aClass, field );
				if ( injectable != null )
					injectables.add( injectable );
			}
			aClass = aClass.getSuperclass();
		}
		return injectables;
	}
}

