package kikaha.jdbi.serializers;

import java.lang.annotation.*;

@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface Column {

	String value() default "";
}
