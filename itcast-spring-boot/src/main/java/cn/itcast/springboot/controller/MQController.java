package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/mq")
@RestController
public class MQController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    /**
     * 发送一个map结构消息到activeMQ
     * @return 操作结果
     */
    @GetMapping("/send")
    public String sendMsg(){

        Map<String, Object> map = new HashMap<>();
        map.put("id", 123L);
        map.put("name", "传智播客");

        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue", map);

        return "发送spring.boot.map.queue队列消息成功.";
    }

    /**
     * 发送一个map结构消息到activeMQ并发送短信
     * @return 操作结果
     */
    @GetMapping("/sendSms")
    public String sendSmsMsg(){

        Map<String, Object> map = new HashMap<>();
        map.put("mobile", "13650250953");
        map.put("signName", "黑马");
        map.put("templateCode", "SMS_125018593");
        map.put("templateParam", "{ \"code\":\"654321\"}");

        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);

        return "发送itcast_sms_queue队列消息成功.";
    }
}
