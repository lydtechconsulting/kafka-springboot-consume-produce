package demo.kafka.service;

import java.util.UUID;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.event.DemoOutboundEvent;
import demo.kafka.lib.KafkaClient;
import demo.kafka.mapper.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemoService {

    @Autowired
    private final KafkaClient kafkaClient;

    public void process(String key, DemoInboundEvent event) {
        DemoOutboundEvent outboundEvent = DemoOutboundEvent.builder()
                .id(UUID.randomUUID().toString())
                .data(event.getData())
                .build();
        kafkaClient.sendMessage(key, JsonMapper.writeToJson(outboundEvent));
    }
}
