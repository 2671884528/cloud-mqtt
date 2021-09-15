package com.gyg.service;

import com.gyg.config.PropertiesUtil;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttConsumer implements ApplicationRunner {

  private static MqttClient client;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    log.info("初始化mqtt");
    this.connect();
  }

  /**
   * <p>初始化连接</p>
   */
  private void connect() {
    // 创建客户端
    getClient();
    // 设置配置
    MqttConnectOptions options = getOptions();
    String[] topic = PropertiesUtil.MQTT_TOPIC.split(",");
    // 3 消息发布质量
    int[] qos = getQos(topic.length);
    create(options,topic,qos);
  }

  public void getClient() {
    if (null == client) {
      try {
        client = new MqttClient(PropertiesUtil.MQTT_HOST, PropertiesUtil.MQTT_CLIENT_ID,
            new MemoryPersistence());
        log.info("创建客户端！");
      } catch (MqttException e) {
        e.printStackTrace();
        log.info("客户端创建失败！");
      }
    }
  }


  /**
   * <p>生成配置对象</p>
   *
   * @return MqttConnectOptions
   */
  public MqttConnectOptions getOptions() {
    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    mqttConnectOptions.setUserName(PropertiesUtil.MQTT_USER_NAME);
    mqttConnectOptions.setPassword(PropertiesUtil.MQTT_PASSWORD.toCharArray());
    mqttConnectOptions.setConnectionTimeout(PropertiesUtil.MQTT_TIMEOUT);
    mqttConnectOptions.setKeepAliveInterval(PropertiesUtil.MQTT_KEEP_ALIVE);
    mqttConnectOptions.setCleanSession(false);
    log.info("生成mqtt配置对象");
    return mqttConnectOptions;
  }

  /**
   *  MQTT协议中有三种消息发布服务质量:
   * QOS0： “至多一次”，消息发布完全依赖底层 TCP/IP 网络。会发生消息丢失或重复。这一级别可用于如下情况，环境传感器数据，丢失一次读记录无所谓，因为不久后还会有第二次发送。
   * QOS1： “至少一次”，确保消息到达，但消息重复可能会发生。
   * QOS2： “只有一次”，确保消息到达一次。这一级别可用于如下情况，在计费系统中，消息重复或丢失会导致不正确的结果，资源开销大
   */
  public int[] getQos(int length) {
    int[] qos = new int[length];
    for (int i = 0; i < length; i++) {
      qos[i] = 1;
    }
    log.info("消息发布QOS：{}",qos);
    return qos;
  }

  /**
   * <p>装载实例，订阅主题</p>
   *
   * @param options
   * @param topic
   * @param qos
   */
  public void create(MqttConnectOptions options, String[] topic, int[] qos) {
    client.setCallback(new MqttCustomerCallBack(client, options, topic, qos));
    log.info("mqtt回调处理");
    try {
      client.connect(options);
    } catch (MqttException e) {
      log.error("mqtt回调异常");
    }
  }

  /**
   * <p>订阅某个主题</p>
   * @param topic
   * @param qos
   */
  public static void subscribe(String topic, int qos) {
    log.info("订阅主题:{}", topic);
    try {
      client.subscribe(topic);
    } catch (MqttException e) {
      log.error("订阅主题失败:{}",topic);
    }
  }

  public static void publish(String topic, String msg){
    publish(1,false,topic,msg);
  }

  /**
   * <p>发布</p>
   * @param qos
   * @param retained
   * @param topic
   * @param pushMessage
   */
  public static void publish(int qos, boolean retained, String topic, String pushMessage) {
    MqttMessage mqttMessage = new MqttMessage();
    mqttMessage.setQos(1);
    mqttMessage.setRetained(retained);
    mqttMessage.setPayload(pushMessage.getBytes(StandardCharsets.UTF_8));
    MqttTopic mqttTopic = client.getTopic(topic);
    if (mqttTopic==null){
      log.error("topic不存在");
      return;
    }
    MqttDeliveryToken token;
    try {
      token = mqttTopic.publish(mqttMessage);
      token.waitForCompletion();
      if(!token.isComplete()){
        log.info("消息发送成功:{}",mqttMessage);
      }

    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
  }
