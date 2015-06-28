package com.github.alechenninger.chronicler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ServiceLoader;

public class ServiceLoaderExternalServiceFactory implements ExternalServiceFactory {
  private final ClassLoader classLoader;

  public ServiceLoaderExternalServiceFactory(Path jarPath) throws MalformedURLException {
    this(jarPath.toUri());
  }

  public ServiceLoaderExternalServiceFactory(URI jarUri) throws MalformedURLException {
    this(jarUri.toURL());
  }

  public ServiceLoaderExternalServiceFactory(URL jarUrl) {
    this(new URLClassLoader(new URL[] {jarUrl}));
  }

  public ServiceLoaderExternalServiceFactory(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  /**
   * @throws java.util.ServiceConfigurationError
   */
  @Override
  public <T> Optional<T> getService(Class<T> service) {
    ServiceLoader<T> serviceLoader = ServiceLoader.load(service, classLoader);

    if (!serviceLoader.iterator().hasNext()) {
      return Optional.empty();
    }

    return Optional.of(serviceLoader.iterator().next());
  }
}
