package org.particleframework.context.env;

import org.particleframework.core.value.PropertyResolver;
import org.particleframework.context.LifeCycle;
import org.particleframework.context.ServiceLocator;
import org.particleframework.core.convert.ConversionService;
import org.particleframework.core.io.ResourceLoader;
import org.particleframework.core.io.scan.ClassPathAnnotationScanner;
import org.particleframework.core.reflect.ClassUtils;
import org.particleframework.inject.BeanConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The current application environment
 *
 * @author Graeme Rocher
 * @since 1.0
 */
public interface Environment extends PropertyResolver, LifeCycle<Environment>, ConversionService<Environment>, ResourceLoader, ServiceLocator {

    /**
     * The test environment
     */
    String TEST = "test";

    /**
     * The development environment
     */
    String DEVELOPMENT = "dev";

    /**
     * @return The name of the environment
     */
    String getName();

    /**
     * Adds a property source to this environment
     *
     * @param propertySource The property source
     * @return This environment
     */
    Environment addPropertySource(PropertySource propertySource);

    /**
     * Add an application package. Application packages are candidates for scanning for tools that need it (such as JPA or GORM)
     *
     * @param pkg The package to add
     * @return This environment
     */
    default Environment addPackage(Package pkg) {
        addPackage(pkg.getName());
        return this;
    }

    /**
     * Add an application package. Application packages are candidates for scanning for tools that need it (such as JPA or GORM)
     *
     * @param pkg The package to add
     * @return This environment
     */
    Environment addPackage(String pkg);

    /**
     * Exclude configurations by name
     *
     * @param names The names of the configuration
     * @return This environment
     */
    Environment addConfigurationExcludes(String...names);

    /**
     * Exclude configurations by name
     *
     * @param names The names of the configuration
     * @return This environment
     */
    Environment addConfigurationIncludes(String...names);

    /**
     * @return The application packages
     */
    Collection<String> getPackages();

    /**
     * Scan the current environment for classes annotated with the given annotation. Use with care, repeated
     * invocations should be avoided for performance reasons.
     *
     * @param annotation The annotation to scan
     * @return The classes
     */
    default Stream<Class> scan(Class<? extends Annotation> annotation) {
        ClassPathAnnotationScanner scanner = new ClassPathAnnotationScanner(getClassLoader());
        return scanner.scan(annotation, getPackages());
    }

    /**
     * @return The class loader for the environment
     */
    default ClassLoader getClassLoader() {
        return Environment.class.getClassLoader();
    }

    /**
     * Check whether the given class is present within this environment
     *
     * @param className The class name
     * @return True if it is
     */
    default boolean isPresent(String className) {
        return ClassUtils.isPresent(className, getClassLoader());
    }

    @Override
    default Optional<InputStream> getResourceAsStream(String path) {
InputStream inputStream = getClassLoader().getResourceAsStream(path);
        if(inputStream != null) {
            return Optional.of(inputStream);
        }
        return Optional.empty();
    }

    /**
     * Whether the current environment includes the given configuration
     *
     * @param configuration The configuration
     * @return True if it does
     */
    boolean isActive(BeanConfiguration configuration);

    default Optional<URL> getResource(String path) {
        URL resource = getClassLoader().getResource(path);
        if(resource != null) {
            return Optional.of(resource);
        }
        return Optional.empty();
    }

    default Stream<URL> getResources(String fileName) {
        Enumeration<URL> all;
        try {
            all = getClassLoader().getResources(fileName);
        } catch (IOException e) {
            return Stream.empty();
        }
        Stream.Builder<URL> builder = Stream.<URL>builder();
        while (all.hasMoreElements()) {
            URL url = all.nextElement();
            builder.accept(url);
        }
        return builder.build();
    };
}