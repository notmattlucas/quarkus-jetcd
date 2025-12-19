package io.quarkiverse.jetcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.test.common.QuarkusTestResource;
import jakarta.inject.Inject;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void shouldSupplyClientBean() {
        assertNotNull(client);
    }

    @Test
    void shouldSupplyKVClient() throws ExecutionException, InterruptedException {
        assertNotNull(kv);
        kv.put(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8), ByteSequence.from("123", StandardCharsets.UTF_8)).get();
        String actual = kv.get(ByteSequence.from("/testing/testing", StandardCharsets.UTF_8)).get().getKvs().get(0).getValue().toString(StandardCharsets.UTF_8);
        assertEquals("123", actual);
    }

    @Test
    void shouldSupplyWatchClient() {
        assertNotNull(watch);
    }

}
