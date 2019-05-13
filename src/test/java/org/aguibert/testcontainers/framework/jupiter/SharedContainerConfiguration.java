/**
 *
 */
package org.aguibert.testcontainers.framework.jupiter;

/**
 * To be used in conjunction with {@link SharedContainerConfig}
 *
 * @author aguibert
 */
public interface SharedContainerConfiguration {

    /**
     * A method that may optionally be implemented to impose a specific
     * container start ordering.
     * Any containers that do not depend on other containers should make use
     * of Java 8 parallel streams:<br>
     * <code>containersToStart.parallelStream().forEach(GenericContainer::start);</code>
     */
    public default void startContainers() {
        throw new UnsupportedOperationException("Not implemented");
    }

}
