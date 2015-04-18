package com.github.alechenninger.chronicler.config;

import static org.junit.Assert.assertEquals;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class CmdLineChroniclerConfigTest {
  @Test
  public void shouldUseUserHomeDotChroniclerSlashConfigDotJsonForDefaultConfigLocation() throws ParseException {
    CmdLineChroniclerConfig config = new CmdLineChroniclerConfig(new String[0]);

    assertEquals(Paths.get(System.getProperty("user.home"), ".chronicler", "config.json"),
        config.config());
  }
}
