package kikaha.jdbi.serializers;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface Retriever {
    Object retrieve(Class<?> t, ResultSet resultSet, String name) throws SQLException;
}