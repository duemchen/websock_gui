package service;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.json.JSONObject;

@Startup
@Singleton

public class Subcriber {

	@Inject
	private Test test;

	@Inject
	private MqttConnector mq;

	@PostConstruct
	private void init() {
		System.out.println(
				"deploy app Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		mq.subscribe("simago/system");
		mq.subscribe("simago/command");
		// mq.subscibe("simago/compass/#");
		mq.sendMqtt("simago/system", "Hallo Welt " + new Date());

		JSONObject jo = new JSONObject();
		jo.put("name", "Duemchen");
		System.out.println(jo);
		test.doit();

		// db.setMqtt(mq);
		// mq.registerMqttListener(db);

	}

	@PreDestroy
	private void cleanUp() {
		mq.unSubscribe("simago/system");
		mq.unSubscribe("simago/command");
		System.out.println(
				"undeploy app Subcriber +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
	}

	public Subcriber() {
		System.out.println("+++++unnötig+++++++++");
	}

}
