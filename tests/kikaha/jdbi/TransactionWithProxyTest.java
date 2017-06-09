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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 *
 */
@RunWith(KikahaRunner.class)
public class TransactionWithProxyTest {

	@Inject
	UserQueries q;

	@Inject
	LogQueries logQueries;

	@Before
	public void initializeDatabase(){
		q.initializeDatabase();
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
			q.insertUserAndRole(paul, "Developer");
		}
		catch ( Throwable c ) {}
	}

	private void ensureThatDoesNotSavedARoleForPaul( User paul ) throws IOException {
		assertEquals( q.retrieveUserRoleByUserId( paul.id ).size(), 1 );
	}

	@Test(expected = MyCustomException.class)
	public void ensureThatIsAbleToReceiveTheSameExceptionThrownOnTheQueryObject(){
		q.aMethodThatThrowsMyCustomException();
	}


	@Test
	public void ensureThatWorksWithRegisteredColumnType(){
		ResultSetDataRetriever.registerRetrieverFor(ZonedDateTime.class, this::zonedDateTimeRetriever);
		val log = new Log();
		log.setDate( ZonedDateTime.now() );
		log.setText( "Log Text" );
		saveLogInDb( log );
		ensureLogIfExistsInDbAndDateTimeIsCorrect( log );
	}

	private Object zonedDateTimeRetriever(Class<?> t, ResultSet resultSet, String name) throws SQLException {
		val instant = new Date( resultSet.getTimestamp(name).getTime() ).toInstant();
		return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
	}

	private void saveLogInDb( Log log ){
		logQueries.initializeDatabase();
		logQueries.insert( log );
	}

	private void ensureLogIfExistsInDbAndDateTimeIsCorrect( Log log ){
		val dbLogs = logQueries.selectAll();
		assertEquals( 1, dbLogs.size() );
		assertEquals( log.getDate(), dbLogs.get(0).getDate() );
	}

}
