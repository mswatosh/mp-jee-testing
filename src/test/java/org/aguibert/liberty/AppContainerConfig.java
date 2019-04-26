/**
 *
 */
package org.aguibert.liberty;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.aguibert.testcontainers.framework.jupiter.SharedContainerConfiguration;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.Network;

/**
 * @author aguibert
 */
public class AppContainerConfig implements SharedContainerConfiguration {

    @Override
    @SuppressWarnings("resource")
    public MicroProfileApplication<?> buildContainer() {
        // This returned instance will be started once for the entire test build
        // Any test that uses `@SharedContainerConfig(AppContainerConfig.class) will use this instance
        return new MicroProfileApplication<>()
                        .withNetwork(Network.SHARED)
                        .withAppContextRoot("/myservice")
                        .withEnv("MONGO_HOSTNAME", "testmongo")
                        .withEnv("MONGO_PORT", "27017")
                        .withEnv("org_aguibert_liberty_ExternalRestServiceClient_mp_rest_url", "http://mockserver:" + MockServerContainer.PORT);
    }

}
