package service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import database.DBSession2;

@Startup
@Singleton
@ApplicationScoped

public class Subcriber implements MqttListener {

	@Inject
	private DBSession2 dbSession;

	@Inject
	private MqttConnector mq;

	@PostConstruct
	private void init() {
		System.out.println("init Subcriber ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		mq.registerMqttListener(this);
		mq.subscribe("simago/system"); //
		// mq.subscribe("simago/command");
		mq.subscribe("simago/zustand");
		mq.subscribe("simago/compass/#");
		System.out.println("subscibe ok");

		// mq.subscibe("simago/compass/#");
		mq.sendMqtt("simago/system", "Hallo Welt, MQTT " + new Date());
		System.out.println("send ok");
	}

	@PreDestroy
	private void cleanUp() {
		mq.unSubscribe("simago/system");
		mq.unSubscribe("simago/command");
		mq.unSubscribe("simago/compass/#");
		System.out.println("cleanUp Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public Subcriber() {
		// System.out.println("+++++unnötig+++++++++");
	}

	@Override
	public void onMessage(String topic, MqttMessage mm) {
		if (mm != null) {
			byte[] b = mm.getPayload();
			System.out.println("subscriber msg: " + new String(b) + ", topic: " + topic);
			// hier jetzt regeln.
			// Datenbankzugriff, senden CMD, info an Webanwendung
			System.out.println(dbSession.getKundenSpiegelZiele());
			/**
			 * topic zu regler (oder anderem: Joystick topic zu MAC zu ID ID +
			 * Sonnenstand + Formel = Sollstellung Joy Kommando
			 * 
			 * 
			 */
		}
	}

}
