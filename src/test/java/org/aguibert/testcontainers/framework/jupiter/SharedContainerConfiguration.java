/**
 *
 */
package org.aguibert.testcontainers.framework.jupiter;

import org.aguibert.testcontainers.framework.MicroProfileApplication;

/**
 * To be used in conjunction with {@link SharedContainerConfig}
 *
 * @author aguibert
 */
public interface SharedContainerConfiguration {

    /**
     * @return An instance of {@link MicroProfileApplication} which will be
     *         started once for the entire lifetime of the test build, and the started
     *         instance will be shared amongst any instances that refer to this class
     *         with {@link SharedContainerConfig}
     */
    public MicroProfileApplication<?> buildContainer();

}
