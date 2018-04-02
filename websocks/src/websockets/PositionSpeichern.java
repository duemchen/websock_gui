package websockets;

import java.io.IOException;

import javax.enterprise.event.Event;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import service.MqttConnector;
import service.MqttListener;

/**
 * @author duemchen
 * 
 *         beim Speichern wird ein Tread erzeugt, der auf das nächste
 *         PositionsEreignis wartet, Dann speichert, rückinformiert und beendet.
 * 
 *
 */
public class PositionSpeichern implements Runnable, MqttListener {

	private Event<UserEvent> event;

	private PositionController positionController;

	MqttConnector mq;

	private JSONObject jo;

	private String topic;

	public PositionSpeichern(PositionController positionController, MqttConnector mq, String topic,
			Event<UserEvent> event) {
		this.topic = topic;
		this.mq = mq;
		this.positionController = positionController;
		this.event = event;
		// TODO topic berechnen
		mq.registerMqttListener(this);
	}

	@Override
	public void run() {
		// die mac des Spiegels und den topic für die richtige sendung
		// Wenn die kommt, speichern und ende.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(positionController.getPositions());
		System.out.println("PositionSpeichern " + jo);
		mq.unregisterMqttListener(this);
		event.fire(new UserEvent());
	}

	@Override
	public void onMessage(String topic, MqttMessage message) throws IOException {
		System.out.println("ps " + topic);
		if (!topic.equals(this.topic))
			return;
		// speichern

	}

}
