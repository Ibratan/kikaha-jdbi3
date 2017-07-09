package kikaha.jdbi.serializers;

import kikaha.jdbi.JDBI;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

/**
 * Created by ronei on 09/07/17.
 */
@JDBI
public interface DataTypesQueries {

    default void initializeDatabase() {
        deleteDataTypesTableIfExists();
        createDataTypesTable();
    }

    @SqlUpdate("DROP TABLE IF EXISTS DataTypes")
    void deleteDataTypesTableIfExists();

    @SqlUpdate("CREATE TABLE DataTypes( myLocalDateTime DATETIME, myLocalDate DATE, myLocalTime TIME )")
    void createDataTypesTable();

    @SqlUpdate("INSERT INTO DataTypes (myLocalDateTime, myLocalDate, myLocalTime) VALUES (:myLocalDateTime, :myLocalDate, :myLocalTime)")
    void insert( @BindBean DataTypes dataTypes );

    @SqlQuery("SELECT * FROM DataTypes")
    List<DataTypes> find();

}
