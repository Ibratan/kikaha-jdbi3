package kikaha.jdbi.serializers;

import java.util.List;

import kikaha.jdbi.JDBI;
import kikaha.jdbi.MyCustomException;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

@JDBI
public interface UserQueries {

	default void initializeDatabase() {
		deleteUsersTableIfExists();
		deleteRolesTableIfExists();
		createUsersTable();
		createRolesTable();
		populateUserTable();
		populateRoleTable();
	}

	@SqlUpdate("DROP TABLE IF EXISTS users")
	void deleteUsersTableIfExists();

	@SqlUpdate("DROP TABLE IF EXISTS roles")
	void deleteRolesTableIfExists();

	@SqlUpdate("CREATE TABLE users( id INT UNIQUE, user_name VARCHAR(200) )")
	void createUsersTable();

	@SqlUpdate("CREATE TABLE roles( id INT UNIQUE, user_id INT, role_name VARCHAR(200) )")
	void createRolesTable();

	@SqlUpdate( "INSERT INTO users(id,user_name) VALUES (1,'Joseph'), (2,'John')" )
	void populateUserTable();

	@SqlUpdate( "INSERT INTO roles(user_id, role_name) VALUES (1,'Manager'), (2,'Executive')" )
	void populateRoleTable();

	@Transaction
	default void insertUserAndRole( User user, String role ) {
		setUserRole( user.id, role );
		insertUser( user );
	}

	default void insertUserAndRoleNonTransactional( User user, String role ) {
		setUserRole( user.id, role );
		insertUser( user );
	}

	@SqlUpdate( "INSERT INTO users(id, user_name) VALUES (:id, :name)" )
	void insertUser(@BindBean User user);

	@SqlUpdate( "INSERT INTO roles(user_id, role_name) VALUES (:user_id, :name)" )
	void setUserRole(@Bind("user_id") long userId, @Bind("name") String name );

	@SqlQuery( "SELECT * FROM users WHERE id = :id" )
	User retrieveUserById( @Bind("id") long id );

	@SqlQuery( "SELECT role_name FROM roles WHERE user_id = :id" )
	List<String> retrieveUserRoleByUserId(@Bind("id") long id );

	@SqlQuery( "SELECT u.id, u.user_name, r.role_name FROM users u JOIN roles r ON r.user_id = u.id WHERE u.id = :id" )
	User retrieveUserFullProfileById( @Bind("id") long id );

	default void aMethodThatThrowsMyCustomException(){
		throw new MyCustomException();
	}
}
