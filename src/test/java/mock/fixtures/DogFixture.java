package mock.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mock.domains.Dog;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.mvc.fixtures.Fixture;

public class DogFixture implements Fixture {

	public static List<Dog> dogs = new ArrayList<Dog>(); // in a real use case it would be the database for example
	
	private final static Random rand = new Random();
	
	@Override
	public void startUp(Vertx vertx, Future<Void> future) {
		Dog snoopy = new Dog("Snoopy", "Beagle");
		Dog bill = new Dog("Bill", "Cocker");
		Dog rantanplan = new Dog("Rantanplan", "German shepherd");
		Dog milou = new Dog("Milou", "Fox terrier");
		Dog idefix = new Dog("Idefix", "Westy");
		Dog pluto = new Dog("Pluto", "Mutt");
		dogs.add(snoopy);
		dogs.add(bill);
		dogs.add(rantanplan);
		dogs.add(milou);
		dogs.add(idefix);
		dogs.add(pluto);
		future.complete();
	}

	@Override
	public void tearDown(Vertx vertx, Future<Void> future) {
		dogs.clear();
		future.complete();
	}
	
	public static Dog someDog() {
		return dogs.get(rand.nextInt(dogs.size()));
	}

}
