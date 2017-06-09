package kikaha.jdbi.serializers;

import java.math.BigDecimal;
import java.sql.*;
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
		RETRIEVERS.put( Time.class, ( c, rs, nm ) -> rs.getTime( nm ) );
		RETRIEVERS.put( Date.class, ( c, rs, nm ) -> rs.getTimestamp( nm ) );
		RETRIEVERS.put( java.sql.Date.class, ( c, rs, nm ) -> rs.getDate( nm ) );
		RETRIEVERS.put( String.class, ( c, rs, nm ) -> rs.getString( nm ) );
	}

	static Character retrieveCharacter( Class<?> t, ResultSet rs, String nm ) throws SQLException {
		final String string = rs.getString( nm );
		return ( string != null && !string.isEmpty() ) ? string.charAt( 0 ) : null;
	}

	static <T> T retrieveOtherKindOfObjects( Class<T> type, ResultSet rs, String name ) throws SQLException {
		T t = null;
		if ( type.isEnum() )
			t = (T)retrieveEnum( (Class)type, rs, name );
		else if ( rs.wasNull() && !type.isPrimitive() )
			t = null;
		else
			t = (T)rs.getObject( name );
		return t;
	}

	static <T extends Enum<T>> Enum<T> retrieveEnum( Class<T> enumType, ResultSet rs, String nm ) throws SQLException {
		final String string = rs.getString( nm );
		return ( string != null && !string.isEmpty() ) ? Enum.valueOf( enumType, string ) : null;
	}

	public static Retriever getDataRetrieverFor( Class<?> type ) {
		Retriever retriever = RETRIEVERS.get( type );
		if ( retriever == null )
			retriever = ResultSetDataRetriever::retrieveOtherKindOfObjects;
		return retriever;
	}

	public static void registerRetrieverFor( Class<?> type, Retriever retriever ) {
		RETRIEVERS.put( type, retriever );
	}
}