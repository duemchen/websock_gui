package websockets;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.json.JSONObject;

import database.Position;
import database.PositionService;

@ApplicationScoped
public class PositionController {
	@EJB
	private PositionService service;

	private Position position;
	private List<Position> positions;

	@PostConstruct
	public void init() {
		position = new Position();
		positions = service.list();
	}

	public void add(Position s) {
		position = s;
		service.save(position);
		init();
	}

	public void update(Position s) {
		position = s;
		service.update(position);
		init();
	}

	public Position getPosition(Long id) {
		return service.find(id);
	}

	public List<Position> getPositions() {
		return positions;
	}

	public String add(JSONObject o) {
		System.out.println(o);
		return null;
	}

}
