/**
 *
 */
package org.aguibert.testcontainers.framework.jupiter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aguibert.testcontainers.framework.MicroProfileApplication;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.junit.jupiter.Container;

/**
 * @author aguibert
 */
public class MicroProfileTestExtension implements BeforeAllCallback, TestInstancePostProcessor {
    private static final Map<Class<? extends SharedContainerConfiguration>, MicroProfileApplication<?>> sharedContainers = new HashMap<>();
    private static final Namespace NAMESPACE = Namespace.create(MicroProfileTestExtension.class);
    private static final String NAMESPACE_KEY = "mpExtensionKey";

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        ExtensionContext.Store store = context.getStore(NAMESPACE);
        store.put(NAMESPACE_KEY, testInstance);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        processSharedContainerConfig(context);
        injectRestClients(context);
    }

    private static void processSharedContainerConfig(ExtensionContext context) {
        Class<?> clazz = context.getRequiredTestClass();
        if (!clazz.isAnnotationPresent(SharedContainerConfig.class))
            return;

        Class<? extends SharedContainerConfiguration> configClass = clazz.getAnnotation(SharedContainerConfig.class).value();
        MicroProfileApplication<?> mpApp = getAppFromConfig(configClass);
        if (!mpApp.isRunning()) {
            mpApp.start();
        } else {
            System.out.println("Found already running contianer instance: " + mpApp);
        }
    }

    @SuppressWarnings("resource")
    private static void injectRestClients(ExtensionContext context) throws Exception {
        Class<?> clazz = context.getRequiredTestClass();
        List<Field> restClientFields = AnnotationSupport.findAnnotatedFields(clazz, RestClient.class);
        if (restClientFields.size() == 0)
            return;

        // There are 1 or more @RestClient fields. Match them with a server container
        MicroProfileApplication<?> mpApp = null;
        boolean usesSharedConfig = clazz.isAnnotationPresent(SharedContainerConfig.class);
        if (usesSharedConfig) {
            Class<? extends SharedContainerConfiguration> configClass = clazz.getAnnotation(SharedContainerConfig.class).value();
            mpApp = getAppFromConfig(configClass);
        } else {
            List<Field> containerFields = AnnotationSupport.findAnnotatedFields(clazz, Container.class);
            for (Field f : containerFields) {
                if (!MicroProfileApplication.class.isAssignableFrom(f.getType()))
                    continue;
                // TODO: Handle non-static @Container fields
                if (!Modifier.isStatic(f.getModifiers()))
                    continue;
                if (mpApp != null)
                    throw new ExtensionConfigurationException("Multiple MicroProfileApplication instances found." +
                                                              " Cannot auto-configure @RestClient");
                mpApp = (MicroProfileApplication<?>) f.get(null);
            }
        }

        if (mpApp == null)
            throw new ExtensionConfigurationException("No MicroProfileApplication instances found to connect @RestClient with");

        // At this point we have found exactly one MicroProfileApplication -- proceed with auto-configure
        if (!mpApp.isCreated() || !mpApp.isRunning())
            throw new ExtensionConfigurationException("Container " + mpApp.getDockerImageName() + " is not running yet. "
                                                      + "It should have been started by the @Testcontainers extension. "
                                                      + "TIP: Make sure that you list @TestContainers before @MicroProfileTest!");

        for (

        Field restClientField : restClientFields) {
            checkPublicStaticNonFinal(restClientField);
            Object restClient = mpApp.createRestClient(restClientField.getType());
            restClientField.set(null, restClient);
            System.out.println("Injecting rest client for " + restClientField);
        }
    }

    private static MicroProfileApplication<?> getAppFromConfig(Class<? extends SharedContainerConfiguration> clazz) {
        return sharedContainers.computeIfAbsent(clazz, c -> {
            SharedContainerConfiguration config;
            try {
                config = clazz.newInstance();
            } catch (Exception e) {
                throw new ExtensionConfigurationException("Unable to create instance of " + clazz, e);
            }
            return config.buildContainer();
        });
    }

    private static void checkPublicStaticNonFinal(Field f) {
        if (!Modifier.isPublic(f.getModifiers()) ||
            !Modifier.isStatic(f.getModifiers()) ||
            Modifier.isFinal(f.getModifiers())) {
            throw new ExtensionConfigurationException("@RestClient annotated field must be public, static, and non-final: " + f.getName());
        }
    }

}
