package com.github.alechenninger.chronicler;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

public class ServiceLoaderTimeSheetFactory implements TimeSheetFactory {
  private final TimeSheetFactory externalFactory;

  /**
   * @param sourcePlugin Path to a .jar with a {@link ServiceLoader} provider configuration file
   * which lists an implementation of {@link TimeSheetFactory}.
   */
  public ServiceLoaderTimeSheetFactory(URL sourcePlugin) throws MalformedURLException {
    URLClassLoader classLoader = new URLClassLoader(new URL[] {sourcePlugin});

    ServiceLoader<TimeSheetFactory> serviceLoader;
    serviceLoader = ServiceLoader.load(TimeSheetFactory.class, classLoader);

    if (!serviceLoader.iterator().hasNext()) {
      throw new ChroniclerException("No TimeSheetFactory implementation found in " + sourcePlugin);
    }

    externalFactory = serviceLoader.iterator().next();
  }

  @Override
  public TimeSheet getTimeSheet(String[] additionalArgs) {
    return externalFactory.getTimeSheet(additionalArgs);
  }
}
