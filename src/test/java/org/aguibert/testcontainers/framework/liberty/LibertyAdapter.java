/**
 *
 */
package org.aguibert.testcontainers.framework.liberty;

import java.io.File;

import org.aguibert.testcontainers.framework.spi.ServerAdapter;
import org.testcontainers.images.builder.ImageFromDockerfile;

/**
 * @author aguibert
 */
public class LibertyAdapter implements ServerAdapter {

    @Override
    public int getDefaultHttpPort() {
        return 9080;
    }

    @Override
    public int getDefaultHttpsPort() {
        return 9443;
    }

    @Override
    public int getDefaultAppStartTimeout() {
        return 15;
    }

    @Override
    public ImageFromDockerfile getDefaultImage(File appFile) {
        String appName = appFile.getName();
        // Compose a docker image equivalent to doing:
        // FROM open-liberty:microProfile2
        // ADD build/libs/myservice.war /config/dropins
        // COPY src/main/liberty/config /config/
        ImageFromDockerfile image = new ImageFromDockerfile()
                        .withDockerfileFromBuilder(builder -> builder.from("open-liberty:microProfile2")
                                        .add("/config/dropins/" + appName, "/config/dropins/" + appName)
                                        .copy("/config", "/config")
                                        .build())
                        .withFileFromFile("/config/dropins/" + appName, appFile)
                        .withFileFromFile("/config", new File("src/main/liberty/config"));
        return image;
    }
}
