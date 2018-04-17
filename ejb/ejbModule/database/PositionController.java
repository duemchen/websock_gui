package database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;

import org.json.JSONObject;

import de.horatio.common.HoraFile;
import de.horatio.common.HoraTime;

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

	public void fillpositons() {
		service.deleteAllPositions();
		fileToPositions("74-DA-38-3E-E8-3C.txt");
		fileToPositions("80-1F-02-ED-FD-A6.txt");

	}

	void fileToPositions(String filename) {
		System.out.println(HoraFile.getCanonicalPath(filename));
		ArrayList<String> list = new ArrayList<String>();
		HoraFile.FillDateiToArrayList(filename, list, "UTF-8");
		String s;
		System.out.println("count " + list.size());
		for (String zeile : list) {

			try {
				// System.out.println("z: " + zeile);
				JSONObject jo = new JSONObject(zeile);

				Position pos = new Position();
				s = jo.getString("time");
				Date datum = HoraTime.strToDateTime(s);
				pos.setDatum(datum);
				s = jo.getString("topic");
				int i = s.lastIndexOf("/");
				i++;
				s = s.substring(i);
				Ziel ziel = service.macToZiel(s);
				pos.setZiel(ziel);
				int x = jo.getInt("dir");
				pos.setX(x);
				int y = jo.getInt("pitch");
				pos.setY(y);
				int z = jo.getInt("roll");
				pos.setZ(z);
				pos.setData(zeile);
				service.save(pos);

			} catch (Exception e) {
				System.out.println("ERROR ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(e);
			}
		}

	}

}
