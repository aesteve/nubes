package mock.fixtures;

import com.github.aesteve.vertx.nubes.annotations.services.Service;
import com.github.aesteve.vertx.nubes.fixtures.Fixture;

import integration.TestVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import mock.domains.Dog;
import mock.services.DogService;

public class DogFixture extends Fixture {

    @Service(TestVerticle.DOG_SERVICE_NAME)
    private DogService dogs;

    @Override
    public int executionOrder() {
        return 1;
    }

    @Override
    public void startUp(Vertx vertx, Future<Void> future) {
        Dog snoopy = new Dog("Snoopy", "Beagle");
        Dog bill = new Dog("Bill", "Cocker");
        Dog rantanplan = new Dog("Rantanplan", "German_shepherd");
        Dog milou = new Dog("Milou", "Fox_terrier");
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

}
