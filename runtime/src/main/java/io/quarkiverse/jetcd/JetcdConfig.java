package io.quarkiverse.jetcd;

import java.util.List;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.jetcd")
@ConfigRoot(phase = ConfigPhase.RUN_TIME)
public interface JetcdConfig {

    /**
     * URI for connecting, formatted as a list of tcp://host:port
     */
    List<String> endpoints();

    /**
     * Whether to authenticate with username and password
     */
    @WithDefault("false")
    Boolean authenticate();

    /**
     * Username for authentication
     */
    Optional<String> username();

    /**
     * Password for authentication
     */
    Optional<String> password();

    /**
     * Connection timeout in milliseconds
     */
    Optional<Integer> connectionTimeout();

}
