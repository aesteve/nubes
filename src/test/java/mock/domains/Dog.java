package mock.domains;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Dog {

	private String name;
	private String breed;

	public Dog() {}

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

	public void setBreed(String breed) {
		this.breed = breed;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "My name is : " + name + " and I'm a " + breed;
	}
}
