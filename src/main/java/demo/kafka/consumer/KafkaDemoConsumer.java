package demo.kafka.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.mapper.JsonMapper;
import demo.kafka.service.DemoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaDemoConsumer {

    final AtomicInteger counter = new AtomicInteger();
    final DemoService demoService;

    @KafkaListener(topics = "demo-inbound-topic", groupId = "demo-consumer-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key, @Payload final String payload) {
        counter.getAndIncrement();
        log.debug("Received message [" +counter.get()+ "] - key: " + key + " - payload: " + payload);
        try {
            DemoInboundEvent event = JsonMapper.readFromJson(payload, DemoInboundEvent.class);
            demoService.process(key, event);
        } catch (Exception e) {
            log.error("Error processing message: " + e.getMessage());
        }
    }
}
