package weather;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Startup;
import javax.inject.Inject;

import database.SpiegelController;

@javax.ejb.Singleton
@Startup
public class WeatherServiceStarter {

	WeatherService service;
	@EJB
	ServiceAsynchWorker asyncWorker;

	@Inject
	private SpiegelController spiegelcontroller;

	@PostConstruct
	private void init() {
		service = new WeatherService(spiegelcontroller);
		asyncWorker.startService(service);
	}

	@PreDestroy
	private void destroy() {
		service.stopService();
	}
}