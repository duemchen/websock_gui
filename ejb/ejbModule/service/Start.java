/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import database.Ziel;

/**
 *
 * @author duemchen
 *
 *         Datenbank füllen Messwerte einfüllen mqtt telegramme persist
 *         verschicken den Status regelmässig aktualisieren(wetter) Als
 *         voraussetzung für webseite
 *
 *         Der Regler selbst kann auch mit mqtt oder mit db arbeiten.
 *
 */
public class Start {

	public static void main(String[] args) {
		System.out.println("Start...");
		Database db = new Database();
		MqttConnector mq = new MqttConnector();
		db.setMqtt(mq);
		mq.registerMqttListener(db);
		mq.subscribe("simago/system");
		mq.subscribe("simago/command");
		// mq.sendMqtt("simago/system", "Hallo Welt " + new Date());
		//

		db.fillExamples();
		db.fileToPositions("74-DA-38-3E-E8-3C.txt");
		db.fileToPositions("80-1F-02-ED-FD-A6.txt");
		//
		String mac;
		JSONObject data;
		mac = "80-1F-02-ED-FD-A6";
		List<Ziel> listeZiele = db.getListeZiele();
		for (Ziel ziel : listeZiele) {
			data = db.getPositions(ziel.getId().intValue());
			mq.sendMqttPersist("simago/position/" + ziel.getId().intValue(), data.toString()); // +
																								// mac,
																								// data.toString());
		}
		// data = db.getPositions(3);
		// mq.sendMqttPersist("simago/position/3", data.toString()); //+ mac,
		// data.toString());
		// mac = "74-DA-38-3E-E8-3C";
		// data = db.getPositions(6);
		// mq.sendMqttPersist("simago/position/6", data.toString());// + mac,
		// data.toString());
		data = db.getKundenSpiegelZiele();
		mq.sendMqttPersist("simago/kunden", data.toString());
		//
		System.out.println("DB erzeugt.\n\n");
		while (true) {
			try {
				Thread.sleep(5000);
				System.out.print(".");
			} catch (InterruptedException ex) {
				Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
