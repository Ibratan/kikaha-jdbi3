package kikaha.jdbi;

import kikaha.core.test.KikahaRunner;
import kikaha.jdbi.serializers.*;
import lombok.SneakyThrows;
import lombok.val;
import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class TransactionWithoutProxyTest {

	@Inject
	DataSource dataSource;

	Jdbi db;

	@Before
	@SneakyThrows
	public void initializeDatabase(){
		db = Jdbi.create(dataSource);
		db.registerColumnMapper( new AnnotatedEntityMapperFactory() );
		db.installPlugins();
		final UserQueries userQueries = db.onDemand( UserQueries.class );
		userQueries.initializeDatabase();
		final LogQueries logQueries = db.onDemand(LogQueries.class);
		logQueries.initializeDatabase();
		final DataTypesQueries dataTypesQueries = db.onDemand(DataTypesQueries.class);
		dataTypesQueries.initializeDatabase();
	}

	@Test
	@SneakyThrows
	public void ensureThatTransactionalMethodWorksIfSomeQueryFails(){
		final User paul = new User();
		paul.id = 2l;
		paul.name = "Paul";
		savePaulInDB( paul );
		ensureThatDoesNotSavedARoleForPaul( paul );
	}

	private void savePaulInDB( User paul ){
		try {
			final UserQueries q = db.onDemand(UserQueries.class);
			q.insertUserAndRole(paul, "Developer");
		}
		catch ( Throwable c ) {}
	}

	private void ensureThatDoesNotSavedARoleForPaul( User paul ) throws IOException {
		final UserQueries q2 = db.onDemand( UserQueries.class );
		assertEquals( q2.retrieveUserRoleByUserId(paul.id).size(), 1 );
	}

	@Test
	public void ensureThatWorksWithRegisteredColumnType(){
		ResultSetDataRetriever.registerRetrieverFor(ZonedDateTime.class, this::zonedDateTimeRetriever);
		val log = new Log();
		log.setDate( ZonedDateTime.now() );
		log.setText( "Log Text" );
		db.onDemand(LogQueries.class).insert(log);
		ensureLogIfExistsInDbAndDateTimeIsCorrect(log);
	}

	private Object zonedDateTimeRetriever(Class<?> t, ResultSet resultSet, String name) throws SQLException {
		val instant = new Date( resultSet.getTimestamp(name).getTime() ).toInstant();
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	private void ensureLogIfExistsInDbAndDateTimeIsCorrect( Log log ){
		final LogQueries q = db.onDemand(LogQueries.class);
		val dbLogs = q.selectAll();
		assertEquals( 1, dbLogs.size() );
		assertEquals( log.getDate(), dbLogs.get(0).getDate() );
	}

	@Test
	public void ensureThatWorksWithDataTypes(){
		final DataTypes object = new DataTypes();
		object.setMyLocalDateTime(LocalDateTime.now());
		object.setMyLocalDate(LocalDate.now());
		object.setMyLocalTime(LocalTime.now().withNano(0));
		final DataTypesQueries dataTypesQueries = db.onDemand(DataTypesQueries.class);
		dataTypesQueries.insert(object);
		final List<DataTypes> results = dataTypesQueries.find();
		assertEquals(1, results.size());
		assertEquals(object.getMyLocalDateTime(), results.get(0).getMyLocalDateTime());
		assertEquals(object.getMyLocalDate(), results.get(0).getMyLocalDate());
		assertEquals(object.getMyLocalTime(), results.get(0).getMyLocalTime());
	}

}
