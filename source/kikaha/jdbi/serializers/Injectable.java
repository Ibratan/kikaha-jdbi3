package kikaha.jdbi.serializers;

import java.lang.reflect.*;
import java.sql.*;
import lombok.RequiredArgsConstructor;
import org.jdbi.v3.core.result.ResultSetException;

@RequiredArgsConstructor
@SuppressWarnings( "rawtypes" )
class Injectable {

	final Retriever dataRetriever;
	final Setter setter;
	final String name;
	final Class fieldType;
	final boolean optional;
	
	public void injectOn( Object instance, ResultSet rs ) throws SQLException {
		try {
			final Object value = dataRetriever.retrieve( fieldType, rs, name );
			setFieldValue( instance, value );
		} catch ( final ResultSetException | SQLException cause ) {
			if ( !optional )
				throw cause;
		}
	}

	private void setFieldValue( Object instance, final Object value ) {
		try {
			setter.set( instance, value );
		} catch ( final Exception e ) {
			throw new IllegalArgumentException(
					String.format( "Unable to access property, %s", name ), e );
		}
	}

	static Injectable from( Class<?> targetClass, Field field ) {
		Injectable injectable = null;

		final Column column = field.getAnnotation( Column.class );
		if ( column != null )
			injectable = createInjectable( targetClass, field, column );

		return injectable;
	}

	private static Injectable createInjectable( Class<?> targetClass, Field field, final Column column ) {
		final String name = column.value().isEmpty() ? field.getName() : column.value();
		final Setter setter = createSetterFor( targetClass, field );
		final boolean optional = field.isAnnotationPresent( Optional.class );
		final Class<?> fieldType = field.getType();
		final Retriever retriever = ResultSetDataRetriever.getDataRetrieverFor( fieldType );
		return new Injectable( retriever, setter, name, fieldType, optional );
	}

	private static Setter createSetterFor( Class<?> targetClass, Field field ) {
		Setter setter = null;
		final Method method = retrieveSetterMethodForField( targetClass, field );
		if ( method == null ) {
			field.setAccessible( true );
			setter = new FieldSetter( field );
		} else {
			setter = new MethodSetter( method );
		}
		return setter;
	}

	static Method retrieveSetterMethodForField( Class<?> targetClass, Field field ) {
		try {
			final String fieldName = field.getName();
			final String name = "set" + Character.toUpperCase( fieldName.charAt( 0 ) ) + fieldName.substring( 1 );
			return targetClass.getMethod( name, field.getType() );
		} catch ( NoSuchMethodException | SecurityException e ) {
			return null;
		}
	}
}

interface Setter {
	<T> void set(Object instance, T value) throws Exception;
}

@RequiredArgsConstructor
class FieldSetter implements Setter {

	final Field field;

	@Override
	public <T> void set( Object instance, T value ) throws Exception {
		field.set( instance, value );
	}
}

@RequiredArgsConstructor
class MethodSetter implements Setter {

	final Method method;

	@Override
	public <T> void set( Object instance, T value ) throws Exception {
		method.invoke( instance, value );
	}
}