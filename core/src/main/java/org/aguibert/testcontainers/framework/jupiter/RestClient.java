/**
 *
 */
package org.aguibert.testcontainers.framework.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aguibert
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestClient {

    // TODO: Implement this as a context root
    public String value() default "<auto-configure>";

}
