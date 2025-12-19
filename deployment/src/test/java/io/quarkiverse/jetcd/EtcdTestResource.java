package io.quarkiverse.jetcd;

import io.etcd.jetcd.launcher.Etcd;
import io.etcd.jetcd.launcher.EtcdCluster;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

public class EtcdTestResource implements QuarkusTestResourceLifecycleManager {

    private EtcdCluster etcd;

    @Override
    public Map<String, String> start() {
        etcd = Etcd.builder().withMountedDataDirectory(false).build();
        etcd.start();
        String endpoints = etcd.clientEndpoints()
                .stream()
                .map(URI::toString)
                .collect(Collectors.joining(","));
        return Map.of(
            "quarkus.jetcd.endpoints", endpoints
        );
    }

    @Override
    public void stop() {
        etcd.stop();
    }

}
