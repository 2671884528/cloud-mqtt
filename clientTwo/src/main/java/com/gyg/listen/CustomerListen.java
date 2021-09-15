package com.gyg.listen;

import com.gyg.service.MqttConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomerListen {

  @Autowired
  RabbitTemplate rabbitTemplate;

  @RabbitListener(
      bindings = @QueueBinding(
          value = @Queue(value = "GYG_FANOUT_QUEUE"),
          exchange = @Exchange(value = "GYG_FANOUT",type = ExchangeTypes.FANOUT)
      )
  )
  public void send(String json){
    log.info("接收到rabbitmq数据上报：{}", json);
    MqttConsumer.publish("GYG_FANOUT", json);

  }
}
