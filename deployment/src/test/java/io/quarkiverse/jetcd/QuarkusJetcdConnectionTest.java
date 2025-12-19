package io.quarkiverse.jetcd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jakarta.inject.Inject;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(EtcdTestResource.class)
public class QuarkusJetcdConnectionTest {

    @RegisterExtension
    static final QuarkusUnitTest unitTest = new QuarkusUnitTest() // Start unit test with your extension loaded
            .setFlatClassPath(true)
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("application.properties"));

    @Inject
    Client client;

    @Inject
    KV kv;

    @Inject
    Watch watch;

    @BeforeEach
    void setup() throws ExecutionException, InterruptedException {
        // clear all keys before each test
        kv.delete(
                ByteSequence.from("/", StandardCharsets.UTF_8),
                DeleteOption
                        .builder()
                        .isPrefix(true)
                        .build())
                .get();
    }

    @Test
    void shouldSupplyClientBean() {
        assertNotNull(client);
    }

    @Test
    void shouldSupplyKVClient() throws ExecutionException, InterruptedException {
        assertNotNull(kv);
        kv.put(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8), ByteSequence.from("123", StandardCharsets.UTF_8))
                .get();
        String actual = kv.get(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8)).get().getKvs().get(0).getValue()
                .toString(StandardCharsets.UTF_8);
        assertEquals("123", actual);
    }

    @Test
    void shouldSupplyWatchClient() {
        assertNotNull(watch);

        List<WatchEvent.EventType> actual = new ArrayList<>();
        watch.watch(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8), response -> {
            List<WatchEvent> events = response.getEvents();
            for (WatchEvent event : events) {
                actual.add(event.getEventType());
            }
        });

        try {
            kv.put(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8),
                    ByteSequence.from("123", StandardCharsets.UTF_8)).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        RetryPolicy<Object> retry = RetryPolicy
                .builder()
                .withDelay(Duration.ofMillis(10))
                .withMaxRetries(100)
                .build();

        Failsafe.with(retry).run(() -> assertEquals(List.of(WatchEvent.EventType.PUT), actual));
    }

}
