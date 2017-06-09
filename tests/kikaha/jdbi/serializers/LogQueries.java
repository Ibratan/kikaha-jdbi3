package kikaha.jdbi.serializers;

import kikaha.jdbi.JDBI;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

/**
 * Created by ronei.gebert on 09/06/2017.
 */
@JDBI
public interface LogQueries {

    default void initializeDatabase() {
        deleteLogsTableIfExists();
        createLogsTable();
    }

    @SqlUpdate("DROP TABLE IF EXISTS logs")
    void deleteLogsTableIfExists();

    @SqlUpdate("CREATE TABLE logs( date DATETIME, text VARCHAR(200) )")
    void createLogsTable();

    @SqlUpdate("INSERT INTO logs(date, text) VALUES (:date, :text)")
    void insert(@BindBean Log log);

    @SqlQuery("SELECT * FROM logs")
    List<Log> selectAll();

}
