package service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

@Startup
@Singleton

public class Subcriber implements MqttListener {

	// @EJB private DBSession dbsession;

	@Inject
	private Test test;

	@Inject
	private MqttConnector mq;

	@PostConstruct
	private void init() {
		System.out.println("deploy app Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		mq.registerMqttListener(this);
		mq.subscribe("simago/system"); //
		// mq.subscribe("simago/command");
		mq.subscribe("simago/zustand");
		mq.subscribe("simago/compass/#");
		System.out.println("suscibe ok");

		// mq.subscibe("simago/compass/#");
		mq.sendMqtt("simago/system", "Hallo Welt " + new Date());
		System.out.println("send ok");

		JSONObject jo = new JSONObject();
		jo.put("name", "Duemchen");
		System.out.println("jo " + jo);
		test.doit();

		// db.setMqtt(mq);
		// mq.registerMqttListener(db);

	}

	@PreDestroy
	private void cleanUp() {
		mq.unSubscribe("simago/system");
		mq.unSubscribe("simago/command");
		mq.unSubscribe("simago/compass/#");
		System.out.println(
				"undeploy app Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public Subcriber() {
		System.out.println("+++++unnötig+++++++++");
	}

	@Override
	public void onMessage(String topic, MqttMessage mm) {
		if (mm != null) {
			byte[] b = mm.getPayload();
			System.out.println("subscriber msg: " + new String(b) + ", topic: " + topic);
			// hier jetzt regeln.
			// Datenbankzugriff, senden CMD, info an Webanwendung
			// System.out.println(dbsession.getKundenSpiegelZiele());
			/**
			 * topic zu regler (oder anderem: Joystick topic zu MAC zu ID ID +
			 * Sonnenstand + Formel = Sollstellung Joy Kommando
			 * 
			 * 
			 */
		}
	}

}
