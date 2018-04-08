package websockets;

import java.io.IOException;

import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jboss.logging.Logger;
import org.json.JSONObject;

import database.Ziel;
import service.MqttConnector;

@Stateful
@LocalBean // wegen implements
@ServerEndpoint("/ws")

public class WSEndpoint implements SpeicherCallback { // implements MqttListener
	// {
	Logger log = Logger.getLogger(this.getClass());

	@Inject
	private Bean bean;
	@Inject
	private PositionController positionController;

	@Inject
	MqttConnector mq;

	@Resource(lookup = "java:jboss/ee/concurrency/factory/MyManagedThreadFactory")
	private ManagedThreadFactory threadFactory;

	private Session session = null;

	// @PostConstruct
	// private void init() {
	// System.out.println("wsEndpoint Created");
	// mq.registerMqttListener(this);
	// }

	@OnMessage
	public String receiveMessage(String message, Session session) {
		// Received : {"cmd":"save","ziel":"3","spiegel":"2"}
		log.info("Received : " + message);// + ", session:" + session.getId());
		// System.out.println(positionController.getPositions());
		this.session = session;
		JSONObject o = new JSONObject(message);
		String s = o.getString("cmd");
		if (s.equals("positionen")) {
			return bean.getPositions(o.getInt("zielid")).toString();
		}
		if (s.equals("save")) {
			// zu diesem Spiegel die Stellung speichern
			// {"spiegel":"3","cmd":"save","ziel":"3"}
			int spID = o.getInt("spiegel");
			String mac = bean.getSpiegelMAC(spID);
			System.out.println(mac);
			PositionSpeichern ps = new PositionSpeichern(positionController, mq, mac, this);
			Ziel ziel = bean.getZielBySpiegel(spID);
			ps.setZiel(ziel);
			Thread thread = threadFactory.newThread(ps);
			thread.start();
			JSONObject j = new JSONObject();
			j.put("cmd", "save"); // speichern läuft
			return j.toString();
		}
		if (s.equals("control")) {
			int spID = o.getInt("spiegel");
			int dir = o.getInt("direction");
			String mac = bean.getSpiegelMAC(spID);
			System.out.println("spiegel:" + spID + ", dir: " + dir + ", mac: " + mac);
			JSONObject j = new JSONObject();
			j.put("control", "true");
			new ControlSpiegel(mq, mac, dir);
			return j.toString();
		}

		JSONObject j = new JSONObject();
		j.put("cmd", "unbekannt");
		return j.toString();

	}

	@OnOpen
	public void open(Session session) throws IOException, EncodeException {
		// log.info("Open Websession:" + session.getId());
		this.session = session;
		JSONObject data = bean.getAllData();
		session.getBasicRemote().sendText(data.toString());
	}

	@OnClose
	public void close(Session session, CloseReason c) {
		// log.info("Closing WebSession:" + session.getId());
		this.session = null;
	}

	public void sendMessage(String message) {
		this.session.getAsyncRemote().sendText(message);
	}

	// @Override
	public void onMessage(String topic, MqttMessage message) throws IOException {
		// alle Positionsmeldungen mitschneiden
		// wenn speichern gedrückt wird, die nächste Position dieses Spiegels
		// speichern.
		// Zeitfenster 10 sek.

		// simago/compass/80-1F-02-ED-FD-A6
		// {"roll":6,"mirrorid":"2","pitch":-12,"dir":347}

		System.out.println(topic + " onMessage " + message);
	}

	// public void handleUser(@Observes UserEvent event) {
	//
	// }

	@Override
	public void callbackSpeichern(boolean erfolg) {
		System.out.println("callbackSpeichern");
		JSONObject j = new JSONObject();
		j.put("cmd", "save"); // Speichern
		j.put("erfolg", erfolg); // Speichern erfolgreich.

		this.session.getAsyncRemote().sendText(j.toString());

	}

}

/*
 * 
 * database create und fill mqtt kommandos absetzen mqtt kommandos empfangen und
 * weiterverarbeiten json gui kurven erzeugen kommando r�ckmeldung dauerdienst
 * mqtt bei start
 * 
 * 
 * 
 * 
 */
