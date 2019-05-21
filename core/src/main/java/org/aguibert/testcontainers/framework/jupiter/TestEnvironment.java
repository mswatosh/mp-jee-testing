package org.aguibert.testcontainers.framework.jupiter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets environment variables on the Micoprofile Application's container.
 * 
 * @author mswatosh
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestEnvironment {
	//TODO: Add file handling e.g. @TestEnvironment("path/to/environment.file")
	//public String value();
	public String[] environment();
}