package org.aguibert.liberty;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.MicroProfileTest;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

@ApplicationScoped
@Testcontainers
@MicroProfileTest
public class KafkaAndLibertyTest {

	private static CountDownLatch lock = new CountDownLatch(1);

	@ClassRule
	public static Network network = Network.newNetwork();

	@Container
	public static KafkaContainer kafka = new KafkaContainer("5.2.0")
					.withNetwork(network).withExposedPorts(9092);

	@Container
	public static MicroProfileApplication<?> myService = new MicroProfileApplication<>()
					.withAppContextRoot("/myservice")
					.withNetwork(network);

	@Inject @Stream("my-stream")
	Emitter<String> emitter;

	@Test
	public void testIncomingMessage() throws InterruptedException, ExecutionException {	
		emitter.send("testMessage");
		assertTrue(lock.await(10, TimeUnit.SECONDS));
	}

	@Incoming("kafka")
	public CompletionStage<Void> consume(KafkaMessage<String, String> message) {
		assertEquals("testMessage", message.getPayload());
		lock.countDown();
		return message.ack();
	}
	
}