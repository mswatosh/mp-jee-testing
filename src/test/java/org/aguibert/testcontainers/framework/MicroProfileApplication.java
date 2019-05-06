/**
 *
 */
package org.aguibert.testcontainers.framework;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.Future;

import org.aguibert.testcontainers.framework.spi.ServerAdapter;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.ExposedPort;

/**
 * @author aguibert
 *
 */
public class MicroProfileApplication<SELF extends MicroProfileApplication<SELF>> extends GenericContainer<SELF> {

    static final Logger LOGGER = LoggerFactory.getLogger(MicroProfileApplication.class);

    private String appContextRoot;
    private ServerAdapter serverAdapter;

    public MicroProfileApplication() {
        super(new ImageFromDockerfile().withFileFromPath(".", Paths.get(".")));
        commonInit();
    }

    public MicroProfileApplication(final String dockerImageName) {
        super(dockerImageName);
        commonInit();
    }

    public MicroProfileApplication(Future<String> dockerImageName) {
        super(dockerImageName);
        commonInit();
    }

    private void commonInit() {
        // Look for a ServerAdapter implementation (optional)
        List<ServerAdapter> adapters = new ArrayList<>(1);
        for (ServerAdapter adapter : ServiceLoader.load(ServerAdapter.class)) {
            adapters.add(adapter); // TODO: temporarily disable ServerAdapter so we can exercise the default path
            LOGGER.info("Found ServerAdapter: " + adapter.getClass());
        }
        if (adapters.size() == 0) {
            LOGGER.info("No ServerAdapter found. Using default settings.");
            serverAdapter = new DefaultServerAdapter();
        } else if (adapters.size() == 1) {
            serverAdapter = adapters.get(0);
            LOGGER.info("Only 1 ServerAdapter found. Will use: " + serverAdapter);
        } else {
            throw new IllegalStateException("Expected 0 or 1 ServerAdapters, but found: " + adapters);
        }
        addExposedPorts(serverAdapter.getDefaultHttpPort());
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withAppContextRoot("/");
    }

    public SELF withAppContextRoot(String appContextRoot) {
        Objects.requireNonNull(appContextRoot);
        if (!appContextRoot.startsWith("/"))
            appContextRoot = "/" + appContextRoot;
        if (!appContextRoot.endsWith("/"))
            appContextRoot += "/";
        this.appContextRoot = appContextRoot;
        waitingFor(Wait.forHttp(this.appContextRoot)
                        .withStartupTimeout(Duration.ofSeconds(serverAdapter.getDefaultAppStartTimeout())));
        return self();
    }

    public <T> T createRestClient(Class<T> clazz, String applicationPath) {
        List<Class<?>> providers = new ArrayList<>();
        providers.add(JsonBProvider.class);
        String urlPath = getBaseURL();
        if (applicationPath != null)
            urlPath += applicationPath;
        return JAXRSClientFactory.create(urlPath, clazz, providers);
    }

    public <T> T createRestClient(Class<T> clazz) {
        return createRestClient(clazz, appContextRoot);
    }

    public String getBaseURL() throws IllegalStateException {
        if (!this.isRunning())
            throw new IllegalStateException("Container must be running to determine hostname and port");
        return "http://" + this.getContainerIpAddress() + ':' + this.getFirstMappedPort();
    }

    private class DefaultServerAdapter implements ServerAdapter {

        private final InspectImageResponse imageData;
        private final int defaultHttpPort;

        public DefaultServerAdapter() {
            imageData = DockerClientFactory.instance().client().inspectImageCmd(getDockerImageName()).exec();
            LOGGER.info("Found exposed ports: " + Arrays.toString(imageData.getContainerConfig().getExposedPorts()));
            int bestChoice = -1;
            for (ExposedPort exposedPort : imageData.getContainerConfig().getExposedPorts()) {
                int port = exposedPort.getPort();
                // If any ports end with 80, assume they are HTTP ports
                if (Integer.toString(port).endsWith("80")) {
                    bestChoice = port;
                    break;
                } else if (bestChoice == -1) {
                    // if no ports match *80, then pick the first port
                    bestChoice = port;
                }
            }
            LOGGER.info("Automatically selecting default HTTP port: " + getDefaultHttpPort());
            defaultHttpPort = bestChoice;
        }

        @Override
        public boolean acceptsImage(Map<String, String> dockerLayerLabels) {
            return true;
        }

        @Override
        public int getDefaultHttpPort() {
            return defaultHttpPort;
        }

        @Override
        public int getDefaultHttpsPort() {
            return -1;
        }

        @Override
        public int getDefaultAppStartTimeout() {
            return 30;
        }
    }

}