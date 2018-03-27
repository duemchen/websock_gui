package websockets;

import java.io.IOException;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;
import org.json.JSONObject;

@Stateful
@ServerEndpoint("/ws")
public class WSEndpoint {
	Logger log = Logger.getLogger(this.getClass());

	@Inject
	private Bean bean;

	// @Inject MqttConnector mq;

	private Session session = null;

	@OnMessage
	public String receiveMessage(String message, Session session) {
		log.info("Received : " + message);// + ", session:" + session.getId());
		this.session = session;
		JSONObject o = new JSONObject(message);
		String s = o.getString("cmd");
		if (s.equals("positionen")) {
			return bean.getPositions(o.getInt("zielid")).toString();
		}

		return "unbekannte Client-Meldung am Websocket empfangen. #####################################################";
	}

	@OnOpen
	public void open(Session session) throws IOException, EncodeException {
		// log.info("Open Websession:" + session.getId());
		JSONObject data = bean.getAllData();
		session.getBasicRemote().sendText(data.toString());
	}

	@OnClose
	public void close(Session session, CloseReason c) {
		// log.info("Closing WebSession:" + session.getId());
		this.session = null;
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
