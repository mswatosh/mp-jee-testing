/**
 *
 */
package org.aguibert.testcontainers.framework.liberty;

import java.util.Map;

import org.aguibert.testcontainers.framework.spi.ServerAdapter;

/**
 * @author aguibert
 */
public class LibertyAdapter implements ServerAdapter {

    @Override
    public boolean acceptsImage(Map<String, String> dockerLayerLabels) {
        return dockerLayerLabels.getOrDefault("vendor", "unknown").toLowerCase().contains("liberty");
    }

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

}
