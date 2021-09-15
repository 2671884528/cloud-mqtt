package com.gyg.service;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@Slf4j
public class MqttCustomerCallBack implements MqttCallbackExtended {

  private MqttClient client;
  private MqttConnectOptions options;
  private String[] topic;
  private int[] qos;

  public MqttCustomerCallBack(MqttClient client, MqttConnectOptions options, String[] topic,
      int[] qos) {
    this.client = client;
    this.options = options;
    this.topic = topic;
    this.qos = qos;
  }

  /**
   * <p>mqtt连接后，订阅主题</p>
   * @param reconnect
   * @param serverURI
   */
  @Override
  public void connectComplete(boolean reconnect, String serverURI) {

    if (topic!=null&&qos!=null){
      if (client.isConnected()){
        try {
          client.subscribe(topic, qos);
          log.info("mqtt连接成功");
          log.info("订阅主题"+ Arrays.toString(topic));
        } catch (MqttException e) {
          e.printStackTrace();
        }
      }
    }
  }


  /**
   * <p>断开重连</p>
   * @param cause
   */
  @Override
  public void connectionLost(Throwable cause) {
    log.info("mqtt断开重连");
    if (null!=client && !client.isConnected()){
      try {
        client.reconnect();
        log.info("mqtt尝试重连中");
      } catch (MqttException e) {
        log.info("mqtt重连失败");
        e.printStackTrace();
      }
    }else {
      try {
        log.info("mqtt重连,client不存在"+client);
        client.connect();
      } catch (MqttException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * <p>处理消息</p>
   * @param topic
   * @param message
   * @throws Exception
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    String s = new String(message.getPayload());
    log.info("收到消息：{}",s);
  }


  /**
   * <p>收到消息调用令牌</p>
   * @param token
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    log.info("deliveryComplete,{}",Arrays.toString(topic));
  }
}
