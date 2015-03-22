package reflections;

import static org.junit.Assert.*;

import java.util.List;

import io.vertx.mvc.VertxMVC;
import io.vertx.mvc.MVCRoute;

import org.junit.Test;

public class RouteCreationTest {
	
	@Test
	public void simpleController() {
		List<MVCRoute> routes = VertxMVC.extractRoutesFromControllers("mock.controllers");
		assertTrue(routes.size() > 1); // subpackage scanned
		routes.forEach(route -> {
			assertNotNull(route.path());
			assertNotNull(route.method());
			assertTrue(route.path().startsWith("/"));
		});
	}
	
	@Test
	public void basePath() {
		List<MVCRoute> routes = VertxMVC.extractRoutesFromControllers("mock.controllers.basepath");
		assertTrue(routes.size() == 1);
		MVCRoute route = routes.get(0);
		assertNotNull(route.path());
		assertNotNull(route.method());
		assertEquals(route.path(),"/base/test");
	}
}
