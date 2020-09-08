# kikaha-jdbi
Provide tight integration of Jdbi3 for the Kikaha 2.1.10.Final version.

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.skullabs.kikaha/kikaha-jdbi3/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.skullabs.kikaha/kikaha-jdbi3)

## Dependencies
If you use Maven:
```xml
<dependency>
    <groupId>io.skullabs.kikaha</groupId>
    <artifactId>kikaha-jdbi3</artifactId>
    <version>0.4.0</version>
</dependency>
```
If you use Gradle:
```gradle
dependencies {
    compile group: 'io.skullabs.kikaha', name: 'kikaha-jdbi3', version: '0.4.0'
}
```
If you are using the Kikaha's command line tool:
```bash
kikaha project add_dep 'io.skullabs.kikaha:kikaha-jdbi3:0.4.0'
```

## Getting Started
This plugin was made with _simplicity_ in mind, so it forces developers to use the Jdbi3's
[SQL Object API](http://jdbi.org/sql_object_overview/), once it is stable and easier to use.
It requires Java 8 and does not support Jdbi's DAO made with Abstract Classes, despite the fact
it has full support of DAOs made with Interface and its default methods.

### Injecting your DAOs
Basically, once you have setup 'kikaha-jdbi' as dependency, you can inject any Jdbi DAO
on your Kikaha's managed services, all you need is annotate your DAO _interface_ with the
```kikaha.jdbi.JDBI``` annotation.

```java
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import kikaha.jdbi.JDBI;

@JDBI
public interface StatisticsDAO {

  @SqlQuery( "SELECT COUNT(*) FROM users" )
  long countHowManyUsersIsSavedOnMyProduct();
}

import javax.inject.*;
import kikaha.urouting.api.*;

@Path( "stats" )
@Singleton
public class StatisticsResource {

  @Inject StatisticsDAO dao;

  @GET
  public long countHowManyUsersIsSavedOnMyProduct() {
    return dao.countHowManyUsersIsSavedOnMyProduct();
  }
}
```

### Mapping Entities
Out-of-box 'kikaha-jdbi' has a very simple mapping system configured. Despite the fact it does not handle
One To Many/Many To Many/Many To One aggregations, it is very simple and easy to use. Basically, you have three annotations:
- ```kikaha.jdbi.serializers.Entity```: (mandatory) let Jdbi know that 'kikaha-jdbi' will be the Column Mapper of this class.
- ```kikaha.jdbi.serializers.Column```: identify an field that it should be mapped.
- ```kikaha.jdbi.serializers.Optional```: make a column optional. Columns not marked as optional will raise exception
during the mapping process if no column with its name was found at the query.

By default, it will bind any field mapped with the ```@Column``` annotation. It will use the field's name as column
identification. If want to use annotation name as identifier, you can defined a name through the ```Column.value```.

[Here](https://github.com/Skullabs/kikaha-jdbi-sample/blob/master/source/test/User.java) you can see a simple example application
using the custom mapper.

## Contributing
This software is released as Open Source to help people to take advantage of Jdbi simplicity on its Kikaha development
enviroment. We will be glad with anyone that use, improve and suggest enhancements on this API.

## License
Apache License 2

