package database;

import java.util.Date;
import java.util.List;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.json.JSONArray;
import org.json.JSONObject;

import kurven.KurvenFormel;
import service.SunPos;

public class SpiegelGeometrie {
	private int zielid;
	private List<Position> list;
	private PolynomialFunction fAzimuth;
	private PolynomialFunction fZenith;
	private SunPos sp;

	public SpiegelGeometrie(int zielid, List<Position> list) {
		this.list = list;
		this.sp = new SunPos();
		this.fAzimuth = KurvenFormel.getKurveAzimuth(list, sp);
		this.fZenith = KurvenFormel.getKurveZenith(list, sp);

	}

	public JSONObject getDiagramm() {
		//
		JSONArray jx = new JSONArray();
		JSONArray jy = new JSONArray();
		JSONArray jz = new JSONArray();
		JSONArray ja = new JSONArray();
		JSONArray jzeit = new JSONArray();

		for (Position pos : list) {
			Date d = pos.getDatum();
			// sp.printSonnenstand(d);
			JSONObject jo = new JSONObject();
			jo.put("X", sp.getAzimuth(d));
			jo.put("x", pos.getX180()); // gemessene Sollpos des Spiegels
			jo.put("xx", fAzimuth.value(sp.getAzimuth(d))); // Azimuth zu diesem
															// Zeitpunkt
			jo.put("id", pos.getId());
			jx.put(jo);
			//
			JSONObject joo = new JSONObject();
			joo.put("Y", sp.getZenith(d));
			joo.put("y", pos.getY() * -1);
			joo.put("id", pos.getId());
			joo.put("zp", pos.getZP());
			jy.put(joo);

			JSONObject jooo = new JSONObject();
			jooo.put("X", sp.getAzimuth(d));
			jooo.put("z", pos.getZ());
			jooo.put("id", pos.getId());
			jz.put(jooo);

			JSONObject jaa = new JSONObject();
			jaa.put("Y", sp.getZenith(d));
			// Projektion auf die xz Ebene
			double a = pos.getProjectionXy(zielid); // gemessene Sollpos xy des
													// Spiegels wird umgerechnet
			jaa.put("a", a); // umgerechnete Messwerte
			jaa.put("aa", fZenith.value(sp.getZenith(d))); // berechnete Wert
															// aus CalcKurve
			jaa.put("id", pos.getId());
			jaa.put("zp", pos.getZP());
			ja.put(jaa);

			// Uhrzeit zu Höhe/Kipp mit tooltip datum
			JSONObject jzeitWerte = new JSONObject();
			jzeitWerte.put("T", pos.getUhrzeitDez()); // Stunden des Tages
			jzeitWerte.put("x", pos.getX180());
			// jzeitWerte.put("y", pos.getY() * -1);
			jzeitWerte.put("y", pos.getY() / sp.getZenith(d));
			jzeitWerte.put("z", pos.getZ());

			jzeitWerte.put("zp", pos.getZP());
			jzeit.put(jzeitWerte);

			// TODO dateformat mit rein. Ziel: Punkt markieren und in den Kurven
			// anzeigen
			// Xx yY Tagesverlauf XY
		}

		JSONObject j = new JSONObject();
		j.put("cmd", "positionen");
		j.put("x", jx);
		j.put("y", jy);
		j.put("z", jz);
		j.put("a", ja);
		j.put("zeit", jzeit);

		// System.out.println("\n");
		// sp.printSonnenstand(HoraTime.strToDateTime("24.03.2017 12:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("25.03.2017 12:00"));
		// //winterzeit
		// sp.printSonnenstand(HoraTime.strToDateTime("26.03.2017 13:00"));
		// //sommerzeit
		// sp.printSonnenstand(HoraTime.strToDateTime("27.03.2017 13:00"));
		// System.out.println("\n");
		//
		// sp.printSonnenstand(HoraTime.strToDateTime("24.03.2017 11:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("24.03.2017 12:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("24.03.2017 13:00"));
		// System.out.println("\n");
		// sp.printSonnenstand(HoraTime.strToDateTime("27.03.2017 11:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("27.03.2017 12:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("27.03.2017 13:00"));
		// sp.printSonnenstand(HoraTime.strToDateTime("27.03.2017 14:00"));
		return j;

	}

	public Position getSollpos(Date d) {
		Position pos = new Position();
		pos.setDatum(d);
		double x = fAzimuth.value(sp.getAzimuth(d));
		pos.setX((long) x);
		double y = fZenith.value(sp.getZenith(d));
		pos.setY((long) y);
		return pos;
	}

}
