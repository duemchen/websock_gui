package websockets;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import database.Position;
import database.PositionController;
import database.Ziel;
import service.MqttConnector;
import service.MqttListener;

/**
 * @author duemchen
 * 
 *         beim Speichern wird ein Thread erzeugt, der auf das nächste
 *         PositionsEreignis wartet, Dann speichert, rückinformiert und beendet.
 * 
 *
 */
public class PositionSpeichern implements Runnable, MqttListener {

	private PositionController positionController;

	MqttConnector mq;

	private JSONObject jo;
	private String mac;
	private Ziel ziel;

	private SpeicherCallback cbs;
	private boolean stop;

	public PositionSpeichern(PositionController positionController, MqttConnector mq, String mac, WSEndpoint cbs) {
		this.mac = mac;
		this.mq = mq;
		this.positionController = positionController;
		this.cbs = cbs;
		// TODO topic berechnen
		mq.registerMqttListener(this);
	}

	@Override
	public void run() {
		// die mac des Spiegels und den topic für die richtige sendung
		// Wenn die kommt, speichern und ende.
		stop = false;
		long lEnd = System.currentTimeMillis() + 5000;
		while (!stop) {
			if (System.currentTimeMillis() > lEnd) {
				cbs.callbackSpeichern(false);
				break;
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mq.unregisterMqttListener(this);

	}

	@Override
	public void onMessage(String mtopic, MqttMessage message) throws IOException, JSONException, ParseException {
		if (stop)
			return;
		System.out.println(mac + "  <>?  " + mtopic);
		if (!mtopic.contains(mac))
			return;
		stop = true;
		// speichern und beenden
		// {"topic":"simago/compass/74-DA-38-3E-E8-3C","time":"26.04.2017
		// 09:31:17","cmd":"save","roll":-16,"dir":13,"mirrorid":"2","pitch":-20}
		String s = new String(message.getPayload());
		System.out.println("korrekter topic. payload: " + s);
		JSONObject jo = new JSONObject(s);
		jo.put("cmd", "save");
		jo.put("topic", mtopic);
		jo.put("time", Position.simpleDatetimeFormat.format(new Date()));
		System.out.println("PositionSpeichern " + jo);
		Position p = new Position(jo);
		p.setZiel(ziel);
		positionController.add(p);

		cbs.callbackSpeichern(true);

	}

	public void setZiel(Ziel ziel) {
		this.ziel = ziel;

	}

}
