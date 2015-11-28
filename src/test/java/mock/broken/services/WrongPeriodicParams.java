package mock.broken.services;

import com.github.aesteve.vertx.nubes.annotations.services.PeriodicTask;

public class WrongPeriodicParams {
	
	@PeriodicTask(300)
	public void sendPeriodic(String something) {
		
	}
}
