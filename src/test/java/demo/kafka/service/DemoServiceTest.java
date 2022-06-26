package demo.kafka.service;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.lib.KafkaClient;
import demo.kafka.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DemoServiceTest {

    private KafkaClient mockKafkaClient;
    private DemoService service;

    @BeforeEach
    public void setUp() {
        mockKafkaClient = mock(KafkaClient.class);
        service = new DemoService(mockKafkaClient);
    }

    /**
     * Ensure the Kafka client is called to emit a message.
     */
    @Test
    public void testProcess() {
        String key = "test-key";
        DemoInboundEvent testEvent = TestEventData.buildDemoInboundEvent(randomUUID().toString());

        service.process(key, testEvent);

        verify(mockKafkaClient, times(1)).sendMessage(key, testEvent.getData());
    }
}
