/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import database.Kunde;
import database.Position;
import database.Spiegel;
import database.Ziel;
import de.horatio.common.HoraFile;
import de.horatio.common.HoraTime;

/**
 *
 * @author duemchen
 */
public class Database implements MqttListener {

	EntityManagerFactory emf;
	EntityManager em;
	private MqttConnector mqtt;

	public Database() {
		emf = javax.persistence.Persistence.createEntityManagerFactory("xyz");
		em = emf.createEntityManager();
	}

	public void persist(Object object) {
		em.getTransaction().begin();
		try {
			em.persist(object);
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().rollback();
		} finally {
			// em.close();
		}
	}

	void fillExamples() {
		Database start = new Database();
		Kunde kunde = new Kunde();
		kunde.setName("Wolfgang Duemchen");
		kunde.setKennung("123abc");
		kunde.setLatitude(0);
		kunde.setLongitude(0);
		kunde.setOrt("Rheinsberg");
		start.persist(kunde);
		//
		Spiegel spiegel = new Spiegel();
		spiegel.setKunde(kunde);
		spiegel.setName("Ost");
		spiegel.setMac("80-1F-02-ED-FD-A6");
		spiegel.setRuhe(0);
		spiegel.setSonnenhoehe(10);
		spiegel.setSonnenwinkelMorgens(0);
		spiegel.setSonnenwinkelAbends(0);
		spiegel.setWindmax(5.1);
		spiegel.setWolkenmax(74);
		spiegel.setWind(180);
		start.persist(spiegel);
		//
		Ziel ziel;
		//
		ziel = new Ziel();
		ziel.setSpiegel(spiegel);
		ziel.setName("Waermekollektor");
		start.persist(ziel);
		ziel = new Ziel();
		ziel.setSpiegel(spiegel);
		ziel.setName("Kueche");
		start.persist(ziel);
		//
		spiegel = new Spiegel();
		spiegel.setKunde(kunde);
		spiegel.setName("West");
		spiegel.setMac("74-DA-38-3E-E8-3C");
		spiegel.setRuhe(0);
		spiegel.setSonnenhoehe(10);
		spiegel.setSonnenwinkelMorgens(0);
		spiegel.setSonnenwinkelAbends(0);
		spiegel.setWindmax(5.1);
		spiegel.setWolkenmax(74);
		spiegel.setWind(180);
		start.persist(spiegel);
		//
		ziel = new Ziel();
		ziel.setSpiegel(spiegel);
		ziel.setName("Waermekollektor");
		start.persist(ziel);
		ziel = new Ziel();
		ziel.setSpiegel(spiegel);
		ziel.setName("Kueche");
		start.persist(ziel);
		//
		Position pos = new Position();
		pos.setZiel(ziel);
		pos.setDatum(new Date());
		pos.setData("ein json String");
		// .deflate() ggf json inhalt gleich in die felder umfüllen, um dann
		// schneller zu öffnen
		// start.persist(pos);
	}

	void fileToPositions(String filename) {

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
				Ziel ziel = macToZiel(s);
				pos.setZiel(ziel);
				int x = jo.getInt("dir");
				pos.setX(x);
				int y = jo.getInt("pitch");
				pos.setY(y);
				int z = jo.getInt("roll");
				pos.setZ(z);
				pos.setData(zeile);
				persist(pos);
			} catch (Exception e) {
				System.out.println("ERROR ++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				System.out.println(e);
			}
		}
	}

	private Ziel macToZiel(String s) {
		Query q = em.createQuery("select s from Spiegel as s where s.mac=:MAC");
		q.setParameter("MAC", s);
		List<Spiegel> list = q.getResultList();
		if (list.isEmpty()) {
			return null;
		}
		Spiegel sp = list.get(0);

		// ein oder das Ziel dieses Spiegels
		q = em.createQuery("select z from Ziel as z where z.spiegel=:SPIEGEL");
		q.setParameter("SPIEGEL", sp);
		List<Ziel> listz = q.getResultList();
		if (listz.isEmpty()) {
			return null;
		}
		Ziel ziel = listz.get(0);

		return ziel;
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
			// jaa.put("a", -pos.getY() - Math.abs(pos.getZ()));
			// Projektion auf die xz Ebene
			double a = Math
					.asin(Math.sin(Math.toRadians(-pos.getY())) * Math.cos(Math.toRadians(Math.PI * pos.getZ())));
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

	private Ziel getZiel(int zielid) {
		return em.find(Ziel.class, new Long(zielid));
	}

	List<Ziel> getListeZiele() {
		Query q = em.createQuery("select z from Ziel as z");
		List<Ziel> result = q.getResultList();
		return result;
	}

	@Override
	public void onMessage(String topic, MqttMessage message) {
		System.out.println("msg: " + topic + " " + new String(message.getPayload()));
		// Löschkommando (LOESCH=true/false)
		if ("simago/command".equals(topic)) {
			System.out.println("del " + new String(message.getPayload()));
			JSONObject jo = new JSONObject(new String(message.getPayload()));
			System.out.println(jo.getInt("del"));
			int id = jo.getInt("del");
			Position pos = em.find(Position.class, new Long(id));
			pos.setLoesch(true);
			persist(pos);
			Ziel ziel = pos.getZiel();
			// dieses Ziel aktualisieren
			JSONObject data = getPositions(ziel.getId().intValue());
			mqtt.sendMqttPersist("simago/position/" + ziel.getId().intValue(), data.toString());

		}

	}

	void setMqtt(MqttConnector mq) {
		mqtt = mq;
	}

}

// 2017: 26.03.2017 02:00 bis 29.10.2017 03:00
