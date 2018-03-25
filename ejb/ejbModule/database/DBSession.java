package database;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;

import service.SunPos;

@Stateless
public class DBSession {

	@PersistenceContext
	EntityManager em;

	public Kunde getKunde(int id) {
		return em.find(Kunde.class, new Long(id));

	}

	public DBSession() {
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

	public JSONObject getPositions(int zielid) {

		Query q = em.createQuery("select p from Position as p where p.loesch=False and p.ziel=:ZIEL");
		Ziel ziel = getZiel(zielid);
		q.setParameter("ZIEL", ziel);
		List<Position> list = q.getResultList();
		// Sonnenstand zu Zielstand darstellen. Müsste ja rein linear sein.
		// Dazu die Sonnenformel
		// 2 Diagramme: x, y
		SunPos sp = new SunPos();
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
			double a;
			a = Math.asin(Math.sin(Math.toRadians(-pos.getY())) * Math.cos(Math.toRadians(Math.PI * pos.getZ())));
			a = Math.toDegrees(a);
			a = Math.round(100.0 * a) / 100.0;
			jaa.put("a", a);
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

	private Ziel getZiel(int zielid) {
		return em.find(Ziel.class, new Long(zielid));
	}

}

/*
 * //@ P ersistenceContext(unitName = "persistencemytma")
 */
