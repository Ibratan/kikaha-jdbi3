package kikaha.jdbi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.*;
import javax.sql.DataSource;

import kikaha.core.cdi.ProviderContext;
import kikaha.core.cdi.helpers.CustomClassConstructor;
import kikaha.db.DataSourceProducer;
import lombok.extern.slf4j.Slf4j;
import org.jdbi.v3.core.Jdbi;
import kikaha.jdbi.serializers.AnnotatedEntityMapperFactory;

/**
 *
 */
@Slf4j
@Singleton
public class KikahaJDBIExtension implements CustomClassConstructor {

	final Map<Class<?>, Object> cache = new ConcurrentHashMap<>();

	@Inject DataSourceProducer dataSourceProducer;

	@Override
	public boolean isAbleToInstantiate( final Class<?> aClass, final ProviderContext providerContext) {
		return aClass.isAnnotationPresent( JDBI.class );
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T instantiate(final Class<T> clazz, final ProviderContext providerContext) throws IllegalAccessException, InstantiationException {
		return (T)cache.computeIfAbsent( clazz, c-> {
			final String dataSourceName = inferDataSourceName( providerContext );
			final Jdbi jdbi = createAJdbiConnection(dataSourceName);
			log.debug( "Creating Jdbi DAO Proxy for " + clazz.getCanonicalName() );
			return QueryInterfaceInvocationHandler.create( jdbi, clazz );
		});
	}

	private String inferDataSourceName(final ProviderContext providerContext ){
		final Named named = providerContext.getAnnotation(Named.class);
		String dataSourceName = "default";
		if(named != null)
			dataSourceName = named.value();
		return dataSourceName;
	}

	private Jdbi createAJdbiConnection( String dataSourceName ){
		final DataSource dataSource = this.dataSourceProducer.getCachedDataSources().get(dataSourceName);
		if ( dataSource == null )
			throw new NullPointerException( "No DataSource found named '"+ dataSourceName +"'" );

		final Jdbi db = Jdbi.create( dataSource );
		db.registerColumnMapper( new AnnotatedEntityMapperFactory() );
		db.installPlugins();
		return db;
	}
}
