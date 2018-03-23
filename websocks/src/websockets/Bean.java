package websockets;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;

import org.json.JSONObject;

import database.DBSession;
import database.Kunde;

@RequestScoped

public class Bean {

	@EJB
	private DBSession dbsession;

	public Kunde getKunde(int id) {

		System.out.println("getkunde");
		System.out.println("dbSession=" + dbsession);
		if (null != dbsession)
			return dbsession.getKunde(id);
		return null;

	}

	public JSONObject getAllData() {
		return dbsession.getKundenSpiegelZiele();
	}

	public JSONObject getPositions(int zielid) {
		return dbsession.getPositions(zielid);
	}

	public Bean() {
		System.out.println(" create bean");
	}

}
