package io.quarkiverse.jetcd.runtime;

import java.util.function.Supplier;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;

@Recorder
public class JetcdRecorder {

    public Supplier<Client> clientSupplier() {
        JetcdClients producer = Arc.container().instance(JetcdClients.class).get();
        return producer::getClient;
    }

    public Supplier<KV> kvSupplier() {
        JetcdClients producer = Arc.container().instance(JetcdClients.class).get();
        return producer::getKV;
    }

    public Supplier<Watch> watchSupplier() {
        JetcdClients producer = Arc.container().instance(JetcdClients.class).get();
        return producer::getWatch;
    }

}
