package com.github.alechenninger.chronicler;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ServiceLoader;

public interface TimeSheetFactory {
  /**
   * @see java.util.ServiceLoader
   */
  public static TimeSheetFactory fromServiceLoaderInJar(Path pathToJar) throws MalformedURLException {
    URLClassLoader classLoader = new URLClassLoader(new URL[] { pathToJar.toUri().toURL() });

    ServiceLoader<TimeSheetFactory> serviceLoader;
    serviceLoader = ServiceLoader.load(TimeSheetFactory.class, classLoader);

    if (!serviceLoader.iterator().hasNext()) {
      throw new ChroniclerException("No TimeSheetFactory implementation found in " + pathToJar);
    }

    return serviceLoader.iterator().next();
  }

  TimeSheet getTimeSheet(String[] additionalArgs);
}
