package database;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.json.JSONArray;
import org.json.JSONObject;

@ApplicationScoped
public class DBSessionRegler {

	@PersistenceContext
	EntityManager em;

	public Kunde getKunde(int id) {
		return em.find(Kunde.class, new Long(id));

	}

	public DBSessionRegler() {
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

	public List<Position> getPositionsList(int zielid) {

		Query q = em.createQuery("select p from Position as p where p.loesch=False and p.ziel=:ZIEL");
		Ziel ziel = getZiel(zielid);
		q.setParameter("ZIEL", ziel);
		List<Position> result = q.getResultList();
		return result;
	}

	public Ziel getZiel(int zielid) {
		return em.find(Ziel.class, new Long(zielid));
	}

	public Spiegel getSpiegelByMAC(String mac) {
		Query q = em.createQuery("select s from Spiegel as s where s.mac=:MAC");
		q.setParameter("MAC", mac);
		List<Spiegel> list = q.getResultList();
		for (Spiegel sp : list) {
			return sp;
		}
		return null;
	}

	public List<Spiegel> getSpiegelAll() {
		Query q = em.createQuery("select s from Spiegel as s");
		List<Spiegel> result = q.getResultList();
		return result;
	}

	public void saveSpiegel(Spiegel s) {
		updateObject(s);

	}

	public boolean updateObject(Object o) {
		try {
			em.merge(o);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}

/*
 * //@ P ersistenceContext(unitName = "persistencemytma")
 */
