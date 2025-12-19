package io.quarkiverse.jetcd.deployment;

import jakarta.enterprise.context.ApplicationScoped;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.quarkiverse.jetcd.runtime.JetcdClients;
import io.quarkiverse.jetcd.runtime.JetcdRecorder;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.arc.processor.DotNames;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;

class EtcdProcessor {

    private static final String FEATURE = "jetcd";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public void registerAdditionalBeans(BuildProducer<AdditionalBeanBuildItem> additionalBeanBuildItemProducer) {
        additionalBeanBuildItemProducer.produce(
                AdditionalBeanBuildItem
                        .builder()
                        .addBeanClasses(JetcdClients.class)
                        .setUnremovable()
                        .setDefaultScope(DotNames.SINGLETON)
                        .build());
    }

    @Record(ExecutionTime.RUNTIME_INIT)
    @BuildStep
    void build(JetcdRecorder recorder,
            BuildProducer<SyntheticBeanBuildItem> syntheticBeanBuildItemBuildProducer) {
        SyntheticBeanBuildItem client = SyntheticBeanBuildItem
                .configure(Client.class)
                .scope(ApplicationScoped.class)
                .supplier(recorder.clientSupplier())
                .startup()
                .setRuntimeInit()
                .done();
        syntheticBeanBuildItemBuildProducer.produce(client);

        SyntheticBeanBuildItem kv = SyntheticBeanBuildItem
                .configure(KV.class)
                .scope(ApplicationScoped.class)
                .supplier(recorder.kvSupplier())
                .startup()
                .setRuntimeInit()
                .done();
        syntheticBeanBuildItemBuildProducer.produce(kv);

        SyntheticBeanBuildItem watch = SyntheticBeanBuildItem
                .configure(Watch.class)
                .scope(ApplicationScoped.class)
                .supplier(recorder.watchSupplier())
                .startup()
                .setRuntimeInit()
                .done();
        syntheticBeanBuildItemBuildProducer.produce(watch);
    }

}
