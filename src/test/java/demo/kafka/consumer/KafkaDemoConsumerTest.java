package demo.kafka.consumer;

import demo.kafka.event.DemoInboundEvent;
import demo.kafka.mapper.JsonMapper;
import demo.kafka.service.DemoService;
import demo.kafka.util.TestEventData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class KafkaDemoConsumerTest {

    private DemoService serviceMock;
    private KafkaDemoConsumer consumer;

    @BeforeEach
    public void setUp() {
        serviceMock = mock(DemoService.class);
        consumer = new KafkaDemoConsumer(serviceMock);
    }

    /**
     * Ensure that the JSON message is successfully passed on to the service, having been correctly unmarshalled into its PoJO form.
     */
    @Test
    public void testListen_Success() {
        String key = "test-key";
        DemoInboundEvent testEvent = TestEventData.buildDemoInboundEvent(randomUUID().toString());
        String payload = JsonMapper.writeToJson(testEvent);

        consumer.listen(key, payload);

        verify(serviceMock, times(1)).process(key, testEvent);
    }

    /**
     * If an exception is thrown, an error is logged but the processing completes successfully.
     *
     * This ensures the consumer offsets are updated so that the message is not redelivered.
     */
    @Test
    public void testListen_ServiceThrowsException() {
        String key = "test-key";
        DemoInboundEvent testEvent = TestEventData.buildDemoInboundEvent(randomUUID().toString());
        String payload = JsonMapper.writeToJson(testEvent);

        doThrow(new RuntimeException("Service failure")).when(serviceMock).process(key, testEvent);

        consumer.listen(key, payload);

        verify(serviceMock, times(1)).process(key, testEvent);
    }
}
