package io.quarkiverse.jetcd.runtime;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.ClientBuilder;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.quarkiverse.jetcd.JetcdConfig;

@Singleton
public class JetcdClients {

    @Inject
    JetcdConfig cfg;

    private volatile Client client;

    public Client getClient() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    createClient();
                }
            }
        }
        return client;
    }

    public KV getKV() {
        return getClient().getKVClient();
    }

    public Watch getWatch() {
        return getClient().getWatchClient();
    }

    private void createClient() {
        ClientBuilder builder = Client.builder().endpoints(
                cfg.endpoints().toArray(new String[0]));
        if (cfg.authenticate() && cfg.username().isPresent() && cfg.password().isPresent()) {
            String username = cfg.username().get();
            String password = cfg.password().get();
            builder
                    .user(ByteSequence.from(username, StandardCharsets.UTF_8))
                    .password(ByteSequence.from(password, StandardCharsets.UTF_8));
        }

        int connectionTimeout = cfg.connectionTimeout().orElse(5000);

        client = builder
                .connectTimeout(Duration.ofMillis(connectionTimeout))
                .build();
    }

}
