package service;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import database.DBSessionRegler;
import database.PositionController;
import de.horatio.common.HoraTime;

@Startup
@Singleton
@ApplicationScoped

public class Subcriber implements MqttListener {

	final long abstand = 8 * HoraTime.C1SEKUNDE;

	@Inject
	private DBSessionRegler dbSession;
	@Inject
	private PositionController positionController;

	@Inject
	private MqttConnector mq;

	@Resource(lookup = "java:jboss/ee/concurrency/factory/MyManagedThreadFactory")
	private ManagedThreadFactory threadFactory;

	HashMap<String, Date> lastCall = new HashMap<String, Date>();

	@PostConstruct
	private void init() {
		System.out.println("init Subcriber ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		positionController.fillpositons();
		mq.registerMqttListener(this);
		mq.subscribe("simago/system"); //
		mq.subscribe("simago/zustand");
		mq.subscribe("simago/compass/#");
		mq.subscribe("simago/camera");
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
		mq.subscribe("simago/camera");
		System.out.println("cleanUp Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public Subcriber() {
		// System.out.println("+++++unnötig+++++++++");
	}

	@Override
	public void onMessage(String topic, MqttMessage mm) {
		if (mm != null) {
			if (topic.contains("simago/compass")) {
				byte[] b = mm.getPayload();
				try {
					JSONObject istPosition = new JSONObject(new String(b));
					// System.out.println("subscriber msg: " + new String(b) +
					// ",
					// topic:
					// " + topic);
					String mac = topicToMac(topic);
					// nur alle x sek stellen. in einer Liste das nächste datum
					if (!isTime(mac))
						return;
					Controller r = new Controller(mac, istPosition, dbSession, mq);
					Thread thread = threadFactory.newThread(r);
					thread.start();
				} catch (Exception e) {
					System.out.println("nojson");
				}
			}

		}
	}

	private boolean isTime(String mac) {
		Date next = lastCall.get(mac);
		if (next == null) {
			next = new Date();
			lastCall.put(mac, next);
		}
		Date now = new Date();
		boolean result = now.after(next);
		if (result) {
			next.setTime(now.getTime() + abstand);
			lastCall.put(mac, next);
		}
		return result;
	}

	private String topicToMac(String topic) {
		String result = topic.replaceFirst("simago/compass/", "");
		return result;
	}

}
