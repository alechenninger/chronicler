package com.github.alechenninger.chronicler;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ServiceLoader;

public interface TimeSheetFactory {
  TimeSheet getTimeSheet(String[] additionalArgs);
}
