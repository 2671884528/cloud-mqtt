package com.gyg.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SendService {

  @Autowired
  RabbitTemplate rabbitTemplate;

  /**
   * <p>10s,推送消息到MQ</p>
   */
  @Scheduled(fixedRate = 10000)
  public void send() {

    for (int i = 0; i < 10000; i++) {
      String s = "message"+i;
      rabbitTemplate.convertAndSend("GYG_FANOUT",null, s);
    }
  }
}
