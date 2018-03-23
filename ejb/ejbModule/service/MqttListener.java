/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author duemchen
 */
interface MqttListener {

    public void onMessage(String topic, MqttMessage message);
}
