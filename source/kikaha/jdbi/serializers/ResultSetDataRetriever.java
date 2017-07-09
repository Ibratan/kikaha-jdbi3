package kikaha.jdbi.serializers;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.*;

@SuppressWarnings( { "unchecked", "rawtypes" } )
public abstract class ResultSetDataRetriever {

	static final Map<Class<?>, Retriever> RETRIEVERS = new HashMap<>();

	static {
		RETRIEVERS.put( Boolean.class, ( c, rs, nm ) -> rs.getBoolean( nm ) );
		RETRIEVERS.put( boolean.class, ( c, rs, nm ) -> rs.getBoolean( nm ) );
		RETRIEVERS.put( Character.class, ResultSetDataRetriever::retrieveCharacter );
		RETRIEVERS.put( char.class, ResultSetDataRetriever::retrieveCharacter );
		RETRIEVERS.put( Byte.class, ( c, rs, nm ) -> rs.getByte( nm ) );
		RETRIEVERS.put( byte.class, ( c, rs, nm ) -> rs.getByte( nm ) );
		RETRIEVERS.put( Short.class, ( c, rs, nm ) -> rs.getShort( nm ) );
		RETRIEVERS.put( short.class, ( c, rs, nm ) -> rs.getShort( nm ) );
		RETRIEVERS.put( Integer.class, ( c, rs, nm ) -> rs.getInt( nm ) );
		RETRIEVERS.put( int.class, ( c, rs, nm ) -> rs.getInt( nm ) );
		RETRIEVERS.put( Long.class, ( c, rs, nm ) -> rs.getLong( nm ) );
		RETRIEVERS.put( long.class, ( c, rs, nm ) -> rs.getLong( nm ) );
		RETRIEVERS.put( Float.class, ( c, rs, nm ) -> rs.getFloat( nm ) );
		RETRIEVERS.put( float.class, ( c, rs, nm ) -> rs.getFloat( nm ) );
		RETRIEVERS.put( Double.class, ( c, rs, nm ) -> rs.getDouble( nm ) );
		RETRIEVERS.put( double.class, ( c, rs, nm ) -> rs.getDouble( nm ) );
		RETRIEVERS.put( BigDecimal.class, ( c, rs, nm ) -> rs.getBigDecimal( nm ) );
		RETRIEVERS.put( Timestamp.class, ( c, rs, nm ) -> rs.getTimestamp( nm ) );
		RETRIEVERS.put( LocalDateTime.class, ( c, rs, nm ) -> rs.getTimestamp(nm).toLocalDateTime() );
		RETRIEVERS.put( LocalDate.class, ( c, rs, nm ) -> rs.getTimestamp( nm ).toLocalDateTime().toLocalDate() );
		RETRIEVERS.put( LocalTime.class, ( c, rs, nm ) -> rs.getTimestamp( nm ).toLocalDateTime().toLocalTime() );
		RETRIEVERS.put( Time.class, ( c, rs, nm ) -> rs.getTime( nm ) );
		RETRIEVERS.put( Date.class, ( c, rs, nm ) -> rs.getTimestamp( nm ) );
		RETRIEVERS.put( java.sql.Date.class, ( c, rs, nm ) -> rs.getDate( nm ) );
		RETRIEVERS.put( String.class, ( c, rs, nm ) -> rs.getString( nm ) );
	}

	static Character retrieveCharacter( Class<?> t, ResultSet rs, String nm ) throws SQLException {
		final String string = rs.getString( nm );
		return ( string != null && !string.isEmpty() ) ? string.charAt( 0 ) : null;
	}

	public static Retriever getDataRetrieverFor( Class<?> type ) {
		Retriever retriever = RETRIEVERS.get( type );
		if ( retriever != null )
			return retriever;
		if ( type.isEnum() )
			return findAndRegisterEnumRetriever( type );
		return ResultSetDataRetriever::throwExceptionForUnknowType;
	}

	private static Retriever findAndRegisterEnumRetriever(Class<?> type) {
		final Retriever retriever = ResultSetDataRetriever::retrieveEnum;
		registerRetrieverFor(type, retriever);
		return retriever;
	}

	static Object retrieveEnum( Class<?> enumType, ResultSet rs, String nm ) throws SQLException {
		final String string = rs.getString( nm );
		return ( string != null && !string.isEmpty() ) ? Enum.valueOf( (Class<? extends  Enum>)enumType, string ) : null;
	}

	static Object throwExceptionForUnknowType( Class<?> enumType, ResultSet rs, String nm ) throws SQLException {
		throw new SQLException( "No retriever defined for type " + enumType );
	}

	public static void registerRetrieverFor( Class<?> type, Retriever retriever ) {
		RETRIEVERS.put( type, retriever );
	}
}