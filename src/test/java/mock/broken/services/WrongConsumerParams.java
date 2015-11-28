package mock.broken.services;

import com.github.aesteve.vertx.nubes.annotations.services.Consumer;

public class WrongConsumerParams {
	
	@Consumer("somewhere")
	public void consume(String something) {
		
	}
}
