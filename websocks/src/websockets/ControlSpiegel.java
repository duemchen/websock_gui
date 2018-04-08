package websockets;

import org.json.JSONObject;

import service.MqttConnector;

/**
 * @author duemchen
 * 
 *         simago/compass/74-DA-38-3E-E8-3C' an simago/joy/74-DA-38-3E-E8-3C'
 *         das cmd=1
 *
 */
public class ControlSpiegel {

	public ControlSpiegel(MqttConnector mq, String mac, int dir) {
		String path = "simago/joy/" + mac;
		JSONObject j = new JSONObject();
		j.put("cmd", dir);
		mq.sendMqtt(path, j.toString());

	}

}
