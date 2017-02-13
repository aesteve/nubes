package mock.domains;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Dog {

  public Long age;
  private String name;
  private String breed;
  private Boolean puppy;

  public Dog() {
    this.puppy = false;
  }

  public Dog(String name, String breed) {
    this.name = name;
    this.breed = breed;
    this.puppy = false;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBreed() {
    return breed;
  }

  public void setBreed(String breed) {
    this.breed = breed;
  }

  public Boolean isPuppy() {
    return puppy;
  }

  public void setPuppy(Boolean puppy) {
    this.puppy = puppy;
  }

  @Override
  public String toString() {
    return "My name is : " + name + " and I'm a " + breed + (puppy?("puppy"):(""));
  }
}
