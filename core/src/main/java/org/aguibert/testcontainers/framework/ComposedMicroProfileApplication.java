/**
 *
 */
package org.aguibert.testcontainers.framework;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.aguibert.testcontainers.framework.spi.ServerAdapter;

/**
 * @author aguibert
 *
 */
public class ComposedMicroProfileApplication<SELF extends ComposedMicroProfileApplication<SELF>> extends MicroProfileApplication<SELF> {

    public ComposedMicroProfileApplication() {
        this(resolveAdatper().getDefaultImage(findAppFile()));
    }

    public ComposedMicroProfileApplication(Future<String> image) {
        super(image);
    }

    private static ServerAdapter resolveAdatper() {
        List<ServerAdapter> adapters = new ArrayList<>(1);
        for (ServerAdapter adapter : ServiceLoader.load(ServerAdapter.class)) {
            adapters.add(adapter);
            LOGGER.info("Found ServerAdapter: " + adapter.getClass());
        }
        if (adapters.size() != 1)
            throw new IllegalStateException("Exepcted to find exactly 1 ServerAdapter but found: " + adapters.size());
        return adapters.get(0);
    }

    private static File findAppFile() {
        // Find a .war or .ear file in the build/ or target/ directories
        Set<File> matches = new HashSet<>();
        matches.addAll(findAppFiles("build"));
        matches.addAll(findAppFiles("target"));
        if (matches.size() == 0)
            throw new IllegalStateException("No .war or .ear files found in build/ or target/ output folders.");
        if (matches.size() > 1)
            throw new IllegalStateException("Found multiple application files in build/ or target output folders: " + matches +
                                            " Expecting exactly 1 application file to be found.");
        File appFile = matches.iterator().next();
        LOGGER.info("Found application file at: " + appFile.getAbsolutePath());
        return appFile;
    }

    private static Set<File> findAppFiles(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            try {
                return Files.walk(dir.toPath())
                                .filter(Files::isRegularFile)
                                .filter(p -> p.toString().toLowerCase().endsWith(".war"))
                                .map(p -> p.toFile())
                                .collect(Collectors.toSet());
            } catch (IOException ignore) {
            }
        }
        return Collections.emptySet();
    }
}
