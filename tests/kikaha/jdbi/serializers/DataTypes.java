package kikaha.jdbi.serializers;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Created by ronei on 09/07/17.
 */
@Getter
@Setter
@Entity
public class DataTypes {

    @Column
    LocalDateTime myLocalDateTime;

    @Column
    LocalDate myLocalDate;

    @Column
    LocalTime myLocalTime;

}
