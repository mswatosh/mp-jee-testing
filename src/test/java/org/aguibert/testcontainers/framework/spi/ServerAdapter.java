/**
 *
 */
package org.aguibert.testcontainers.framework.spi;

import java.util.Map;

/**
 * @author aguibert
 *
 */
public interface ServerAdapter {

    public boolean acceptsImage(Map<String, String> dockerLayerLabels);

    /**
     * @return 1 or more ports exposed by the Docker image.
     *         NOTE: All ports returned by this method will be waited on by default,
     *         so it may not be desireable to expose all the same ports as the Docker
     *         image.
     */
    public int[] getDefaultExposedPorts();

}
