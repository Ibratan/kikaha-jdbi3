package kikaha.jdbi;

import kikaha.core.test.KikahaRunner;
import kikaha.jdbi.serializers.*;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
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
public class TransactionWithProxyTest {

	@Inject
	UserQueries userQueries;

	@Inject
	LogQueries logQueries;

	@Inject
	DataTypesQueries dataTypesQueries;

	@Before
	public void initializeDatabase(){
		userQueries.initializeDatabase();
		logQueries.initializeDatabase();
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
			userQueries.insertUserAndRole(paul, "Developer");
		}
		catch ( Throwable c ) {}
	}

	private void ensureThatDoesNotSavedARoleForPaul( User paul ) throws IOException {
		assertEquals( userQueries.retrieveUserRoleByUserId( paul.id ).size(), 1 );
	}

	@Test(expected = MyCustomException.class)
	public void ensureThatIsAbleToReceiveTheSameExceptionThrownOnTheQueryObject(){
		userQueries.aMethodThatThrowsMyCustomException();
	}


	@Test
	public void ensureThatWorksWithRegisteredColumnType(){
		ResultSetDataRetriever.registerRetrieverFor(ZonedDateTime.class, this::zonedDateTimeRetriever);
		val log = new Log();
		log.setDate( ZonedDateTime.now() );
		log.setText( "Log Text" );
		logQueries.insert( log );
		ensureLogIfExistsInDbAndDateTimeIsCorrect( log );
	}

	private Object zonedDateTimeRetriever(Class<?> t, ResultSet resultSet, String name) throws SQLException {
		val instant = new Date( resultSet.getTimestamp(name).getTime() ).toInstant();
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	private void ensureLogIfExistsInDbAndDateTimeIsCorrect( Log log ){
		val dbLogs = logQueries.selectAll();
		assertEquals( 1, dbLogs.size() );
		assertEquals( log.getDate(), dbLogs.get(0).getDate() );
	}

	@Test
	public void ensureThatWorksWithDataTypes(){
		final DataTypes object = new DataTypes();
		object.setMyLocalDateTime(LocalDateTime.now());
		object.setMyLocalDate(LocalDate.now());
		object.setMyLocalTime(LocalTime.now().withNano(0));
		dataTypesQueries.insert(object);
		final List<DataTypes> results = dataTypesQueries.find();
		assertEquals(1, results.size());
		assertEquals(object.getMyLocalDateTime(), results.get(0).getMyLocalDateTime());
		assertEquals(object.getMyLocalDate(), results.get(0).getMyLocalDate());
		assertEquals(object.getMyLocalTime(), results.get(0).getMyLocalTime());
	}

}
