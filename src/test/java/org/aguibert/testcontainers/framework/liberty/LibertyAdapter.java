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

    // Liberty exposes 9080 (http) and 9443 (https) by default. Only expose the http
    // endpoint by default, because not all servers enable https
    public static final int[] DEFAULT_EXPOSED_PORTS = { 9080 };

    @Override
    public boolean acceptsImage(Map<String, String> dockerLayerLabels) {
        return dockerLayerLabels.getOrDefault("vendor", "unknown").toLowerCase().contains("liberty");
    }

    @Override
    public int[] getDefaultExposedPorts() {
        return DEFAULT_EXPOSED_PORTS;
    }

}
