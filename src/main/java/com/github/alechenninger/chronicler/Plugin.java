package com.github.alechenninger.chronicler;

public interface Plugin {
  TimeSheetFactory timeSheetFactory();
  PluginInfo info();
}
