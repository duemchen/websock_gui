package websockets;

import java.io.IOException;
import java.util.Date;

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

import database.PositionController;
import database.Ziel;
import de.horatio.common.HoraTime;
import service.MqttConnector;
import service.MqttListener;

@Stateful
// @SessionScoped
@LocalBean // wegen implements
@ServerEndpoint("/ws")

public class WSEndpoint implements SpeicherCallback, MqttListener { // implements
																	// MqttListener
	// {
	Logger log = Logger.getLogger(this.getClass());
	// aus diesem Zeitraum die Messwerte benutzen
	// private Date von = HoraTime.strToDate("8.9.2018");
	// private Date bis = HoraTime.strToDate("14.9.2018");

	@Inject
	private Bean bean;
	@Inject
	private PositionController positionController;

	private MqttConnector mq;

	@Inject
	public void setMqttConnector(MqttConnector mq) {
		this.mq = mq;
		mq.registerMqttListener(this);
	}

	@Resource(lookup = "java:jboss/ee/concurrency/factory/MyManagedThreadFactory")
	private ManagedThreadFactory threadFactory;

	private Session session = null;

	private boolean firsttime = true;;

	// @PostConstruct
	// private void init() {
	// System.out.println("wsEndpoint Created. mqtt");
	// // mq.registerMqttListener(this);
	// }

	@Override
	public void onMessage(String topic, MqttMessage message) throws IOException {
		if (topic.contains("simago/camera")) {
			// System.out.println(topic + " WSEndpoint onMessageMQTT " +
			// message);
			// System.out.println("Bild " + System.currentTimeMillis());
			JSONObject j = new JSONObject();
			j.put("cmd", "bild"); // Bild weiterreichen via WebSock
			j.put("data", new String(message.getPayload()));
			sendMessage(j.toString());
		}
		if (topic.contains("simago/compass")) {
			// System.out.println(topic + " WSEndpoint onMessageMQTT " +
			// message);
			// Regler einsprung
		}
	}

	@OnMessage
	public String receiveMessage(String message, Session session) throws InterruptedException {
		// Received : {"cmd":"save","ziel":"3","spiegel":"2"}
		log.info("Received : " + message);// + ", session:" + session.getId());
		// System.out.println(positionController.getPositions());
		this.session = session;
		JSONObject o = new JSONObject(message);
		String s = o.getString("cmd");

		if (s.equals("positionen")) {
			if (firsttime) {
				Thread.sleep(1000);// wegen load googlecharts
				firsttime = false;
			}
			Date von = HoraTime.strToDate(o.getString("von"));
			Date bis = HoraTime.strToDate(o.getString("bis"));
			return bean.getPositionsDiagramm(o.getInt("zielid"), von, bis).toString();
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
		if (s.equals("table")) {
			return bean.getPositionsTableData(o.getInt("zielid")).toString();
		}
		if (s.equals("edit")) {
			int id = o.getInt("id");
			boolean loesch = o.getBoolean("loesch");
			System.out.println("edit:");
			PositionEdit pe = new PositionEdit(positionController, id, loesch);
			JSONObject j = new JSONObject();
			j.put("cmd", "edit");
			j.put("id", id);
			j.put("loesch", pe.getLoesch());
			j.put("row", o.getInt("row"));
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
		mq.unregisterMqttListener(this);
		this.session = null;
	}

	public void sendMessage(String message) {
		this.session.getAsyncRemote().sendText(message);
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
