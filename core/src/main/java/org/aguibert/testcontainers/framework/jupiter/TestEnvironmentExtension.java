package org.aguibert.testcontainers.framework.jupiter;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.CheckForNull;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.testcontainers.junit.jupiter.Container;


/**
 * @author mswatosh
 */
public class TestEnvironmentExtension implements TestInstancePostProcessor, BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AfterEachCallback {
	private static final Namespace NAMESPACE = Namespace.create(TestEnvironmentExtension.class);

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
		Class<?> clazz = context.getRequiredTestClass();

		Class<? extends SharedContainerConfiguration> configClass = null;
		if (clazz.isAnnotationPresent(SharedContainerConfig.class))
			configClass = clazz.getAnnotation(SharedContainerConfig.class).value();

		List<Field> fields = Arrays.asList(clazz.getFields());
		if (configClass != null) fields.addAll(Arrays.asList(configClass.getFields()));
		
		//TODO Error if a MPApp isn't found
		MicroProfileApplication<?> container = null;
		for (Field field : fields) {
			if (field.isAnnotationPresent(Container.class)) {
				if(field.getClass().equals(MicroProfileApplication.class)) {
					container = (MicroProfileApplication<?>) field.get(testInstance);
				}
			}
		}

		ExtensionContext.Store store = context.getStore(NAMESPACE);
		store.put(MicroProfileApplication.class, container);
	}

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		Class<?> clazz = context.getRequiredTestClass();
		if (!clazz.isAnnotationPresent(TestEnvironment.class))
			return;

		List<String> testEnv = Arrays.asList(clazz.getAnnotation(TestEnvironment.class).environment());

		ExtensionContext.Store store = context.getStore(NAMESPACE);
		MicroProfileApplication<?> container = 	(MicroProfileApplication<?>) store.get(MicroProfileApplication.class);
		List<String> env = container.getEnv();
		
		if (!env.containsAll(testEnv)) {
			Properties testProps = new Properties();
			Properties envProps = new Properties();
			for (String s : testEnv) {
				testProps.load(new StringReader(s));
			}
			for (String s : env) {
				envProps.load(new StringReader(s));
			}


		}
		
		
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		//TODO if there's any change in Environment variables, the server will need to be restarted to take effect
	}

	@Override
	public void afterEach(ExtensionContext context) throws Exception {

	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {

	}

	private static Map<String,String> processTestEnvironmentAnnos(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(TestEnvironment.class))
			return Collections.emptyMap();
			
		TestEnvironment anno = clazz.getAnnotation(TestEnvironment.class);
		anno.environment();
	}

	private static Map<String,String> processTestEnvironmentAnnos(Method method) {
		if (!method.isAnnotationPresent(TestEnvironment.class))
			return Collections.emptyMap();

		
	}

	private static void setEnvironment() {

	}

	private static void resetEnvironment() {

	}

}