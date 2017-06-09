package kikaha.jdbi.serializers;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * Created by ronei.gebert on 09/06/2017.
 */
@Getter
@Setter
@Entity
public class Log {

    @Column
    ZonedDateTime date;

    @Column
    String text;

}
