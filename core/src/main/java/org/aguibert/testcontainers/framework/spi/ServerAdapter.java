/**
 *
 */
package org.aguibert.testcontainers.framework.spi;

import java.io.File;

import org.testcontainers.images.builder.ImageFromDockerfile;

/**
 * @author aguibert
 */
public interface ServerAdapter {

    public int getDefaultHttpPort();

    public int getDefaultHttpsPort();

    public int getDefaultAppStartTimeout();

    public ImageFromDockerfile getDefaultImage(File appFile);

}
