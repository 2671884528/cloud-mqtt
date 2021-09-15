package com.gyg.controller;

import com.gyg.service.MqttConsumer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendController {

  @GetMapping("/push")
  public Object push(@RequestParam("topic") String topic,@RequestParam("msg") String msg){
    MqttConsumer.publish(topic, msg);
    return "发布成功";
  }
}
