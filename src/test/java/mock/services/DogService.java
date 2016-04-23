package mock.services;

import com.github.aesteve.vertx.nubes.annotations.services.Consumer;
import com.github.aesteve.vertx.nubes.annotations.services.PeriodicTask;
import com.github.aesteve.vertx.nubes.services.Service;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import mock.domains.Dog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DogService implements Service {

  private static final Random rand = new Random();
  private List<Dog> dogs; // in a real use case it would be the database for example
  private Vertx vertx;

  @Override
  public void init(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
  }

  @Override
  public void start(Future<Void> future) {
    dogs = new ArrayList<>();
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

  public Dog getDog(int i) {
    return dogs.get(i);
  }

  public boolean isEmpty() {
    return dogs.isEmpty();
  }

  public int size() {
    return dogs.size();
  }

  public void clear() {
    dogs.clear();
  }

  @PeriodicTask(300)
  public void sendPeriodic() {
    vertx.eventBus().publish("dogService.periodic", "periodic");
  }

  @Consumer("dogService.echo")
  public void echoBack(Message<String> message) {
    message.reply(message.body());
  }

}
