/**
 *
 */
package org.aguibert.testcontainers.framework;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Future;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

/**
 * @author aguibert
 *
 */
public class MicroProfileApplication<SELF extends MicroProfileApplication<SELF>> extends GenericContainer<SELF> {

    static final Logger LOGGER = LoggerFactory.getLogger(MicroProfileApplication.class);

    private String appContextRoot;

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
        withExposedPorts(9080);
        withLogConsumer(new Slf4jLogConsumer(LOGGER));
        withAppContextRoot("/");
        waitingFor(Wait.forHttp(this.appContextRoot) // TODO: can eventually default this to MP Health 2.0 readiness check
                        .withStartupTimeout(Duration.ofSeconds(15))); // lower default from 60s to 15s so we fail faster when things go wrong
    }

    public SELF withAppContextRoot(String appContextRoot) {
        Objects.requireNonNull(appContextRoot);
        if (!appContextRoot.startsWith("/"))
            appContextRoot = "/" + appContextRoot;
        this.appContextRoot = appContextRoot;
        waitingFor(Wait.forHttp(this.appContextRoot));
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

}