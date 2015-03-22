package mock.controllers.filters;

import io.vertx.mvc.annotations.AfterFilter;
import io.vertx.mvc.annotations.BeforeFilter;
import io.vertx.mvc.annotations.Route;
import io.vertx.mvc.controllers.AbstractController;

public class FilteredController extends AbstractController {

	@BeforeFilter
	public void readUserLocale(){
		
	}
	
	@Route(path="/test")
	public void test(){
		
	}
	
	
	@AfterFilter
	public void writeContentType(){
		
	}
	
}
