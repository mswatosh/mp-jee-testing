/**
 *
 */
package org.aguibert.testcontainers.framework.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

/**
 * References a SharedContainerConfiguration to be used by a test class
 *
 * @author aguibert
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MicroProfileTestExtension.class)
public @interface SharedContainerConfig {

    public Class<? extends SharedContainerConfiguration> value();

}
