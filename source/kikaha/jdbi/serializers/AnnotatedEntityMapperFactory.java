package kikaha.jdbi.serializers;

import java.lang.reflect.Type;
import java.util.*;
import java.util.Optional;

import org.jdbi.v3.core.config.ConfigRegistry;
import org.jdbi.v3.core.generic.GenericTypes;
import org.jdbi.v3.core.mapper.*;

@SuppressWarnings( { "rawtypes", "unchecked" } )
public class AnnotatedEntityMapperFactory implements ColumnMapperFactory {

	final Map<Class<?>, AnnotatedEntityMapper<?>> mappers = new HashMap<>();

	private AnnotatedEntityMapper<?> getMapperForClass( Class type ) {
		AnnotatedEntityMapper<?> entityMapper = mappers.get( type );
		if ( entityMapper == null ) {
			synchronized ( mappers ) {
				entityMapper = mappers.get( type );
				if ( entityMapper == null )
					mappers.put( type, entityMapper = AnnotatedEntityMapper.from( type ) );
			}
		}
		return entityMapper;
	}

	@Override
	public Optional<ColumnMapper<?>> build(Type type, ConfigRegistry configRegistry) {
		final Class<?> clazz = GenericTypes.getErasedType(type);
		if ( clazz.isAnnotationPresent( Entity.class ) )
			return Optional.of( getMapperForClass( clazz ) );
		return Optional.empty();
	}
}
