package database;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.json.JSONArray;
import org.json.JSONObject;

import kurven.KurvenFormel;
import service.SunPos;

@Stateless
public class DBSessionWeb {

	@PersistenceContext
	EntityManager em;

	public Kunde getKunde(int id) {
		return em.find(Kunde.class, new Long(id));

	}

	public DBSessionWeb() {
		// System.out.println("create DBSession");
	}

	public JSONObject getKundenSpiegelZiele() {
		Query q;
		q = em.createQuery("select s from Kunde as s");
		List<Kunde> listK = q.getResultList();
		if (listK.isEmpty()) {
			return null;
		}
		JSONArray jKunden = new JSONArray();
		for (Kunde kunde : listK) {
			q = em.createQuery("select p from Spiegel as p where p.kunde=:KUNDE");
			q.setParameter("KUNDE", kunde);
			List<Spiegel> list = q.getResultList();
			JSONArray jl = new JSONArray();
			for (Spiegel sp : list) {
				JSONObject jo = new JSONObject();
				jo.put("name", sp.getName());
				jo.put("mac", sp.getMac());
				jo.put("id", sp.getId());
				jo.put("ziele", getZiele(sp));
				jl.put(jo);
			}
			JSONObject jk = new JSONObject();
			jk.put("name", kunde.getName());
			jk.put("id", kunde.getId());
			jk.put("spiegel", jl);
			jKunden.put(jk);
		}
		JSONObject result = new JSONObject();
		result.put("kunden", jKunden);
		result.put("cmd", "kunden");
		return result;
	}

	JSONArray getZiele(Spiegel spiegel) {
		Query q = em.createQuery("select z from Ziel as z where z.spiegel=:SPIEGEL");
		q.setParameter("SPIEGEL", spiegel);
		List<Ziel> list = q.getResultList();
		JSONArray result = new JSONArray();
		for (Ziel z : list) {
			JSONObject jo = new JSONObject();
			jo.put("id", z.getId());
			jo.put("name", z.getName());
			result.put(jo);
		}
		return result;
	}

	public JSONObject getPositionsDiagramm(int zielid) {
		Query q = em.createQuery("select p from Position as p where p.loesch=False and p.ziel=:ZIEL");
		Ziel ziel = getZiel(zielid);
		q.setParameter("ZIEL", ziel);
		List<Position> list = q.getResultList();
		// Sonnenstand zu Zielstand darstellen. Müsste ja rein linear sein.
		// Dazu die Sonnenformel
		SunPos sp = new SunPos();

		PolynomialFunction fAzimuth = KurvenFormel.getKurveAzimuth(list, sp);
		PolynomialFunction fZenith = KurvenFormel.getKurveZenith(list, sp);

		//
		JSONArray jx = new JSONArray();
		JSONArray jy = new JSONArray();
		JSONArray jz = new JSONArray();
		JSONArray ja = new JSONArray();

		for (Position pos : list) {
			Date d = pos.getDatum();
			// sp.printSonnenstand(d);
			JSONObject jo = new JSONObject();
			jo.put("X", sp.getAzimuth(d));
			jo.put("x", pos.getX180());
			jo.put("xx", fAzimuth.value(sp.getAzimuth(d)));
			jo.put("id", pos.getId());
			jx.put(jo);
			//
			JSONObject joo = new JSONObject();
			joo.put("Y", sp.getZenith(d));
			joo.put("y", pos.getY() * -1);
			joo.put("id", pos.getId());
			jy.put(joo);

			JSONObject jooo = new JSONObject();
			jooo.put("X", sp.getAzimuth(d));
			jooo.put("z", pos.getZ());
			jooo.put("id", pos.getId());
			jz.put(jooo);

			JSONObject jaa = new JSONObject();
			jaa.put("Y", sp.getZenith(d));
			// Projektion auf die xz Ebene
			double a = pos.getProjectionXy();
			jaa.put("a", a);
			jaa.put("aa", fZenith.value(sp.getZenith(d)));
			jaa.put("id", pos.getId());
			ja.put(jaa);

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

	public Spiegel getSpiegel(int id) {
		return em.find(Spiegel.class, new Long(id));
	}

	public Ziel getZiel(int id) {
		return em.find(Ziel.class, new Long(id));
	}

	/**
	 * für das Ziel Auch die gelöschten Messungen
	 * 
	 * @param zielid
	 * @return
	 */
	public JSONObject getPositionsTableData(int zielid) {
		Ziel ziel = getZiel(zielid);

		// nur die aktiven Messpunkte für die Näherungsfunktion
		Query q = em.createQuery("select p from Position as p where (p.loesch=0) and p.ziel=:ZIEL order by p.id");
		q.setParameter("ZIEL", ziel);
		List<Position> listFunktion = q.getResultList();
		System.out.println("listFunktion: " + listFunktion.size());
		// alle Messpunkte für die Anzeige
		q = em.createQuery("select p from Position as p where p.ziel=:ZIEL order by p.id");
		q.setParameter("ZIEL", ziel);
		List<Position> listShow = q.getResultList();
		System.out.println("listShow: " + listShow.size());
		// Sonnenstand zu Zielstand darstellen. Müsste ja rein linear sein.
		// Dazu die Sonnenformel
		SunPos sp = new SunPos();
		PolynomialFunction fAzimuth = KurvenFormel.getKurveAzimuth(listFunktion, sp);
		PolynomialFunction fZenith = KurvenFormel.getKurveZenith(listFunktion, sp);

		JSONArray positionen = new JSONArray();
		for (Position pos : listShow) {
			Date d = pos.getDatum();
			JSONObject jo = new JSONObject();
			jo.put("id", pos.getId());
			jo.put("zeit", Position.simpleDatetimeFormatZeit.format(d));
			jo.put("datum", Position.simpleDatetimeFormatDatum.format(d));

			double af = fZenith.value(sp.getZenith(d));
			double a = pos.getProjectionXy();
			double delta = af - a;
			jo.put("delta", String.format("%.1f", delta));
			jo.put("loesch", pos.isLoesch());
			positionen.put(jo);
		}

		JSONObject result = new JSONObject();
		result.put("cmd", "positionenTable");
		result.put("positionen", positionen);
		return result;
	}

}

/*
 * //@ P ersistenceContext(unitName = "persistencemytma")
 */
