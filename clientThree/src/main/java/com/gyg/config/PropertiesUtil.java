package com.gyg.config;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

  public static String MQTT_HOST;
  public static String MQTT_CLIENT_ID;
  public static String MQTT_USER_NAME;
  public static String MQTT_PASSWORD;
  public static String MQTT_TOPIC;
  public static Integer MQTT_TIMEOUT;
  public static Integer MQTT_KEEP_ALIVE;

  static {
    try {
      Properties properties = loadMqttProperties();
      MQTT_HOST = properties.getProperty("host");
      MQTT_CLIENT_ID = properties.getProperty("clientId");
      MQTT_USER_NAME = properties.getProperty("username");
      MQTT_PASSWORD = properties.getProperty("password");
      MQTT_TOPIC = properties.getProperty("topic");
      MQTT_TIMEOUT = Integer.valueOf(properties.getProperty("timeout"));
      MQTT_KEEP_ALIVE = Integer.valueOf(properties.getProperty("keepalive"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static Properties loadMqttProperties() throws IOException {
    InputStream inputStream = PropertiesUtil.class.getResourceAsStream("/application.yml");
    Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }

}
