package mock.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.aesteve.vertx.nubes.services.Service;

import mock.domains.Dog;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class DogService implements Service {

    private static final Random rand = new Random();
    private List<Dog> dogs; // in a real use case it would be the database for example

    @Override
    public void init(Vertx vertx) {
    }

    @Override
    public void start(Future<Void> future) {
        dogs = new ArrayList<Dog>();
        future.complete();
    }

    @Override
    public void stop(Future<Void> future) {
        dogs.clear();
        future.complete();
    }

    public void add(Dog dog) {
        dogs.add(dog);
    }

    public Dog someDog() {
        return dogs.get(rand.nextInt(dogs.size()));
    }

    public boolean isEmpty() {
        return dogs.isEmpty();
    }

    public int size() {
        return dogs.size();
    }

}
