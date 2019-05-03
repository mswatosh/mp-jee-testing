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

    public int getDefaultHttpPort();

    public int getDefaultHttpsPort();

    public int getDefaultAppStartTimeout();

}
