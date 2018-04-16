/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;

/**
 *
 * @author duemchen existiert nur einmal
 */
@ApplicationScoped
public class MqttConnector implements MqttCallback {

	private String MQTTLINK = "192.168.10.51:1883";
	// REASON_CODE_INVALID_MESSAGE 32108=Paket nicht erkannt
	// private String MQTTLINK = "duemchen.feste-ip.net:56686";
	// private String MQTTLINK = "duemchen.feste-ip.net:49893";
	private MqttClient client = null;

	private List<MqttListener> listeners = new ArrayList<MqttListener>();

	public MqttConnector() {
		System.out.println("create MqttConnector");

	}

	public synchronized void registerMqttListener(MqttListener listener) {

		// Add the listener to the list of registered listeners
		this.listeners.add(listener);

	}

	public synchronized void unregisterMqttListener(MqttListener listener) {

		// Remove the listener from the list of the registered listeners
		this.listeners.remove(listener);

	}

	protected synchronized void notifyMqttListeners(String topic, MqttMessage message) {

		// Notify each of the listeners in the list of registered listeners
		this.listeners.forEach(listener -> {
			try {
				listener.onMessage(topic, message);
			} catch (Exception e) {
				System.out.println("notifyMqttListeners: " + e + "  " + listener + " " + topic + "  " + message);
				e.printStackTrace();

			}
		});

	}

	// ****************************
	public void sendMqttPersist(String path, String data) {
		sendMqtt(path, data, true);
	}

	public void sendMqtt(String path, String data) {
		sendMqtt(path, data, false);
	}

	private void sendMqtt(String path, String data, boolean persist) {
		try {

			try {
				if (client == null) {
					MemoryPersistence persistence = new MemoryPersistence();
					SecureRandom random = new SecureRandom();
					String id = new BigInteger(60, random).toString(32);
					String link = "tcp://" + MQTTLINK;
					client = new MqttClient(link, "MQTTCONN-" + id, persistence);
					System.out.println(" totwait" + client.getTimeToWait());

				}
				if (!client.isConnected()) {
					client.connect();
					client.setCallback(this);

				}
				MqttMessage message = new MqttMessage();
				// message.setPayload(jo.toString().getBytes());
				message.setPayload(data.getBytes());
				message.setRetained(persist);
				message.setQos(0);
				// client.publish("simago/joy", message);
				client.publish(path, message);
				// log.info("sendCommand " + path + ":" + cmd);
				// System.out.println("sendCommand:: " + path + ":" + data +
				// "\n");

			} catch (MqttException ex) {
				System.out.println(ex);
				Logger.getLogger(MqttConnector.class.getName()).log(Level.SEVERE, null, ex);
			}

		} catch (JSONException ex) {
			Logger.getLogger(MqttConnector.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public boolean subscribe(String s) {
		try {
			if (client == null) {
				MemoryPersistence persistence = new MemoryPersistence();
				SecureRandom random = new SecureRandom();
				String id = new BigInteger(60, random).toString(32);
				String link = "tcp://" + MQTTLINK;
				client = new MqttClient(link, "MQTTCONN-" + id, persistence);
			}
			if (!client.isConnected()) {
				client.connect();
				client.setCallback(this);
			}
			client.subscribe(s, 0);
			return true;

		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}

	public void unSubscribe(String s) {
		try {
			if (client == null) {
				return;
			}
			if (!client.isConnected()) {
				return;
			}
			System.out.println("unsucribe: " + s);
			client.unsubscribe(s);
			return;

		} catch (Exception e) {
			System.out.println("unsucribe Error: " + e);
		}
		return;
	}

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println(cause);
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// System.out.println("messageArrived from: " + topic + ", message: " +
		// message);
		notifyMqttListeners(topic, message);
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// System.out.println("deliveryComplete: " + token);
	}

}
