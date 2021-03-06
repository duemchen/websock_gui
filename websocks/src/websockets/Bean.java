package websockets;

import java.util.Date;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;

import org.json.JSONObject;

import database.DBSessionWeb;
import database.Kunde;
import database.Spiegel;
import database.Ziel;

@RequestScoped

public class Bean {

	@EJB
	private DBSessionWeb dbsession;

	public Kunde getKunde(int id) {

		if (null != dbsession)
			return dbsession.getKunde(id);
		return null;
	}

	public JSONObject getAllData() {
		// System.out.println(positionController.getPositions());
		return dbsession.getKundenSpiegelZiele();
	}

	public JSONObject getPositionsDiagramm(int zielid, Date von, Date bis) {
		return dbsession.getPositionsDiagramm(zielid, von, bis);
	}

	public JSONObject getPositionsTableData(int zielid) {
		return dbsession.getPositionsTableData(zielid);
	}

	public Bean() {
		// System.out.println(" create bean");

	}

	public String getSpiegelMAC(int spID) {
		Spiegel spiegel = dbsession.getSpiegel(spID);
		if (spiegel == null)
			return null;
		return spiegel.getMac();
	}

	public Ziel getZielBySpiegel(int spID) {
		Spiegel spiegel = dbsession.getSpiegel(spID);
		if (spiegel == null)
			return null;
		Double d = spiegel.getZiel();
		if (d == null)
			return null;
		int id = spiegel.getZiel().intValue();
		return dbsession.getZiel(id);
	}

}
