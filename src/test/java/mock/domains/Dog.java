package mock.domains;

import io.vertx.mvc.domains.DomainObject;

public class Dog extends DomainObject {
	
	private final String name;
	private final String breed;
	
	public Dog(String name, String breed) {
		this.name = name;
		this.breed = breed;
	}

	public String getName() {
		return name;
	}

	public String getBreed() {
		return breed;
	}
	
	public boolean validate(){
		return name != null && breed != null;
	}
}
