package kikaha.jdbi.serializers;

import lombok.Getter;

@Getter
@Entity
public class User {

	@Column
	public Long id;

	@Column( "user_name" )
	public String name;

	@Optional
	@Column( "role_name" )
	public String role;

	boolean calledSetterMethodOfDomainField = false;

	public void setName( String name ) {
		calledSetterMethodOfDomainField = true;
		this.name = name;
	}
}
