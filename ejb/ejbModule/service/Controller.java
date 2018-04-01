package service;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.json.JSONObject;

import database.DBSessionRegler;
import database.Position;
import database.Spiegel;
import database.Ziel;
import kurven.KurvenFormel;
import kurven.Point;

/**
 * @author duemchen
 * 
 * 
 *         // eine lineare Verbindung zwischen Höhe Sonne und höhe Spiegel aus
 *         // der Summe der Messwerte berechnen.
 * 
 *         // hier jetzt regeln. // Datenbankzugriff, senden CMD, info an
 *         Webanwendung
 * 
 *         // System.out.println(dbSession.getKundenSpiegelZiele());
 * 
 *         topic zu regler (oder anderem: Joystick topic zu MAC zu ID ID +
 *         Sonnenstand + Formel = Sollstellung Joy Kommando
 * 
 *
 * 
 */
public class Controller implements Runnable {

	enum CMD {

		HOCH, LINKS, RECHTS, RUNTER, NEUTRAL
	}

	private final String MQTTPATH = "simago/joy/xx-";

	private String mac;
	private DBSessionRegler dbSession;
	private MqttConnector mq;
	private JSONObject istPosition; // {"roll":5,"mirrorid":"2","pitch":-8,"dir":347}

	private Point istPoint;

	public Controller(String mac, JSONObject istPosition, DBSessionRegler dbSession, MqttConnector mq) {
		// System.out.println(istPosition);
		this.istPosition = istPosition;
		this.mac = mac;
		this.dbSession = dbSession;
		this.mq = mq;
		istPoint = getIstPoint(istPosition);
		// System.out.println(istPoint);
	}

	private Point getIstPoint(JSONObject istPosition) {
		// {"roll":5,"dir":0,"pitch":-54}
		int dir = istPosition.getInt("dir");
		// in Grad. Drehen um 180 grad. 0 heisst eigenlich 180
		dir = -dir;
		dir += 180;
		if (dir < 0) {
			dir += 360;
		}
		if (dir >= 360) {
			dir -= 360;
		}
		int heigth = istPosition.getInt("pitch");
		heigth = -heigth;
		Point result = new Point(dir, heigth);
		return result;
	}

	private Position getPosition(JSONObject jo, Ziel ziel) {
		Position pos = new Position();
		Date datum = new Date();
		pos.setDatum(datum);
		pos.setZiel(ziel);
		int x = jo.getInt("dir");
		pos.setX(x);
		int y = jo.getInt("pitch");
		pos.setY(y);
		int z = jo.getInt("roll");
		pos.setZ(z);
		pos.setData(jo.toString());
		return pos;
	}

	@Override
	public void run() {

		Spiegel sp = dbSession.getSpiegelByMAC(mac);
		if (sp == null)
			return;
		Double zielID = sp.getZiel();
		if (zielID == null)
			return;
		int zielInt = (int) zielID.doubleValue();
		// ist berechnen
		Ziel ziel = dbSession.getZiel(zielInt);
		Position istPos = getPosition(istPosition, ziel);
		// soll berechnen
		SunPos sun = new SunPos();
		List<Position> list = dbSession.getPositionsList(zielID.intValue());
		PolynomialFunction fAzimuth = KurvenFormel.getKurveAzimuth(list, sun);
		PolynomialFunction fZenith = KurvenFormel.getKurveZenith(list, sun);
		Date d = new Date();
		double x = sun.getAzimuth(d);
		double y = sun.getZenith(d);
		// System.out.println("Aktueller Sonnenstand azimuth: " + x + ", zenith:
		// " + y);
		double xSoll = fAzimuth.value(x);
		xSoll = Math.round(100.0 * xSoll) / 100.0;
		double ySoll = fZenith.value(y);
		ySoll = Math.round(100.0 * ySoll) / 100.0;

		// System.out.println("Aktueller SOLL azimuth: " + xSoll + ", zenith: "
		// + ySoll);
		// System.out.println("Aktueller IST AZ " + istPos.toStringAZ());
		CMD cmd = CMD.LINKS;
		// erst x, dann y stellen.
		cmd = getCmd(xSoll, ySoll, istPos.getX180(), istPos.getProjectionXy());
		if (cmd == CMD.NEUTRAL)
			return;
		JSONObject jo = new JSONObject();
		jo.put("cmd", cmd.ordinal());
		jo.put("source", 0); // der Regler sendet das Kommando selbst
		// "{'cmd':0}"
		String s = jo.toString();
		try {
			mq.sendMqtt(MQTTPATH + mac, s);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * erst azimuth, dann zenith
	 * 
	 * @param azimuthSoll
	 * @param zenithSoll
	 * @param azimuthIst
	 * @param zenithIst
	 * @return
	 */
	private CMD getCmd(double azimuthSoll, double zenithSoll, double azimuthIst, double zenithIst) {
		CMD result = CMD.NEUTRAL;
		if (Math.abs(azimuthSoll - azimuthIst) > 2) {
			if (azimuthSoll > azimuthIst) {
				result = CMD.RECHTS;
			} else {
				result = CMD.LINKS;
			}
		} else {
			if (Math.abs(zenithSoll - zenithIst) > 2) {
				if (zenithSoll > zenithIst) {
					result = CMD.HOCH;
				} else {
					result = CMD.RUNTER;
				}
			}
		}
		// System.out.println(result);
		return result;
	}

}
