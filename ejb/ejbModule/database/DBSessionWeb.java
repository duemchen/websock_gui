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
			q = em.createQuery("select p from Spiegel as p where p.kunde=:KUNDE order by name");
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

	public JSONObject getPositionsDiagramm(int zielid, Date von, Date bis) {
		Query q = em.createQuery(
				"select p from Position as p where p.loesch=False and p.ziel=:ZIEL and datum >= :VON and datum <= :BIS ");
		Ziel ziel = getZiel(zielid);
		q.setParameter("ZIEL", ziel);
		q.setParameter("VON", von);
		q.setParameter("BIS", bis);
		List<Position> list = q.getResultList();
		SpiegelGeometrie sg = new SpiegelGeometrie(zielid, list);
		JSONObject result = sg.getDiagramm();
		return result;
	}

	/**
	 * @param zielid
	 * @param zeitPunkt
	 * @param von
	 * @param bis
	 * @return für den Regler die berechnete Sollpos holen.
	 */
	public Position getSollPos(int zielid, Date zeitPunkt, Date von, Date bis) {
		Query q = em.createQuery(
				"select p from Position as p where p.loesch=False and p.ziel=:ZIEL and datum >= :VON and datum <= :BIS ");
		Ziel ziel = getZiel(zielid);
		q.setParameter("ZIEL", ziel);
		q.setParameter("VON", von);
		q.setParameter("BIS", bis);
		List<Position> list = q.getResultList();
		SpiegelGeometrie sg = new SpiegelGeometrie(zielid, list);
		return sg.getSollpos(zeitPunkt);
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
			double a = pos.getProjectionXy(zielid);
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
