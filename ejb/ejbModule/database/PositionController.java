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

	/**
	 *  
	 */
	public void fillPositions() {
		String dir = getStandaloneDir();
		System.out.println("Import Dir für Messwerte Neuimport: \n" + dir);
		String f1 = dir + "/" + "74-DA-38-3E-E8-3C.txt";
		String f2 = dir + "/" + "80-1F-02-ED-FD-A6.txt";
		System.out.println(f1);
		System.out.println(f2);
		if (!HoraFile.fileExists(f1))
			return;
		if (!HoraFile.fileExists(f2))
			return;
		System.out.println("beides wird einmalig neu importieren und dann umbenannt.");
		// beides einmalig neu importieren und dann umbenennen
		service.deleteAllPositions();
		fileToPositions(f1);
		fileToPositions(f2);

	}

	private String getStandaloneDir() {
		// System.out.println("jboss.server.home.dir " +
		// System.getProperty("jboss.server.home.dir"));
		// System.out.println("jboss.server.base.dir " +
		// System.getProperty("jboss.server.base.dir"));
		return System.getProperty("jboss.server.base.dir");
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
				Date datum = HoraTime.strToDateTimeBuchungsSatz(s);// sekunden
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
		HoraFile.renameFile(filename, filename + ".bak");
	}

}
