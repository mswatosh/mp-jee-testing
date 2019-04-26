/**
 *
 */
package org.aguibert.testcontainers.framework;

import java.io.File;
import java.util.concurrent.Future;

import org.testcontainers.images.builder.ImageFromDockerfile;

/**
 * @author aguibert
 *
 */
public class ComposedMicroProfileApplication<SELF extends ComposedMicroProfileApplication<SELF>> extends MicroProfileApplication<SELF> {

    public ComposedMicroProfileApplication() {
        // TODO get base docker image from a strategy or system prop so this can work for non-Liberty runtimes

        // Compose a docker image equivalent to doing:
        // FROM open-liberty:microProfile2
        // ADD build/libs/myservice.war /config/dropins
        // COPY src/main/liberty/config /config/
        this(new ImageFromDockerfile()
                        .withDockerfileFromBuilder(builder -> builder.from("open-liberty:microProfile2")
                                        .add("/config/dropins/myservice.war", "/config/dropins/myservice.war")
                                        .copy("/config", "/config")
                                        .build())
                        .withFileFromFile("/config/dropins/myservice.war", new File("build/libs/myservice.war"))
                        .withFileFromFile("/config", new File("src/main/liberty/config")));
    }

    public ComposedMicroProfileApplication(Future<String> image) {
        super(image);
    }
}
