package database;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@Named
@ApplicationScoped
public class SpiegelController {

	private Spiegel spiegel;
	private List<Spiegel> spiegels;

	@EJB
	private SpiegelService service;

	@PostConstruct
	public void init() {
		spiegel = new Spiegel();
		spiegels = service.list();
	}

	public void add() {
		service.save(spiegel);
		init();
	}

	public void update(Spiegel s) {
		spiegel = s;
		service.update(spiegel);
		init();
	}

	public Spiegel getSpiegel() {
		return spiegel;
	}

	public List<Spiegel> getSpiegels() {
		return spiegels;
	}

}
