package demo.kafka.component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import dev.lydtech.component.framework.client.kafka.KafkaClient;
import dev.lydtech.component.framework.extension.TestContainersSetupExtension;
import dev.lydtech.component.framework.mapper.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static demo.kafka.util.TestEventData.INBOUND_DATA;
import static demo.kafka.util.TestEventData.buildDemoInboundEvent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@Slf4j
@ExtendWith(TestContainersSetupExtension.class)
public class EndToEndCT {

    private static final String GROUP_ID = "EndToEndCT";

    private Consumer consumer;

    /**
     * Configure the wiremock to return a 503 twice times before success.
     */
    @BeforeEach
    public void setup() {
        consumer = KafkaClient.getInstance().createConsumer(GROUP_ID, "demo-outbound-topic");

        // Clear the topic.
        consumer.poll(Duration.ofSeconds(1));
    }

    @AfterEach
    public void tearDown() {
        consumer.close();
    }


    /**
     * Send in multiple events and ensure an outbound event is emitted for each.
     */
    @Test
    public void testFlow() throws Exception {
        int totalMessages = 100;
        for (int i=0; i<totalMessages; i++) {
            String key = UUID.randomUUID().toString();
            String payload = UUID.randomUUID().toString();
            KafkaClient.getInstance().sendMessage("demo-inbound-topic", key, JsonMapper.writeToJson(buildDemoInboundEvent(payload)));
        }
        List<ConsumerRecord<String, String>> outboundEvents = KafkaClient.getInstance().consumeAndAssert("testFlow", consumer, totalMessages, 3);
        outboundEvents.stream().forEach(outboundEvent -> assertThat(outboundEvent.value(), containsString(INBOUND_DATA)));
    }
}
