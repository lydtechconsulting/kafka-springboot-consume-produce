package demo.kafka.service;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.lib.KafkaClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemoService {;

    @Autowired
    private KafkaClient kafkaClient;

    public void process(String key, DemoInboundEvent event) {
        kafkaClient.sendMessage(key, event.getData());
    }
}
