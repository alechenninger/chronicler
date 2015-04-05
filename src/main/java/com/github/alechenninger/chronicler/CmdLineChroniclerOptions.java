package com.github.alechenninger.chronicler;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class CmdLineChroniclerOptions implements ChroniclerOptions {
  private static Option API_KEY = new Option("k", "apiKey", true,
      "Rally API key. To generate or access API keys for your account, go to " +
      "'https://rally1.rallydev.com/login' and see 'API KEYS' in the top menu.");

  private static Option SERVER = new Option("h", "host", true,
      "Rally server URL. Defaults to 'https://rally1.rallydev.com'.");

  private static Option SOURCE = new Option("s", "source", true,
      "Source plugin: a jar that contains an implementation of TimeSheetFactory. This source "
          + "plugin will generally require additional arguments of its own.");

  private static Option USER = new Option("u", "user", true, "Rally username to add time sheet "
      + "entries to.");

  private static Option WORKSPACE = new Option("w", "workspace", true, "Rally workspace name under "
      + "your subscription (assumes 1 subscription for now) to use for timesheets.");

  private static Option HELP = new Option("h", "help", false, "Show this menu.");

  private static Option CONFIG = new Option("c", "config", true, "Path to json config file. Defaults"
      + " to ~/.chronicler/config.json");

  private static Options OPTIONS = new Options()
      .addOption(API_KEY)
      .addOption(SERVER)
      .addOption(SOURCE)
      .addOption(USER)
      .addOption(WORKSPACE)
      .addOption(HELP);

  private final CommandLine cli;

  public CmdLineChroniclerOptions(String[] args) throws ParseException {
    this(args, new BasicParser());
  }

  public CmdLineChroniclerOptions(String[] args, CommandLineParser parser) throws ParseException {
    cli = parser.parse(OPTIONS, args, true);
  }

  @Override
  public String apiKey() {
    if (!cli.hasOption(API_KEY.getOpt())) {
      throw new ChroniclerException("No api key specified: " + API_KEY);
    }

    return cli.getOptionValue(API_KEY.getOpt());
  }

  @Override
  public URI server() throws URISyntaxException {
    if (cli.hasOption(SERVER.getOpt())) {
      return new URI(cli.getOptionValue(SERVER.getOpt()));
    }

    return new URI("https://rally1.rallydev.com");
  }

  @Override
  public Path sourcePlugin() {
    if (!cli.hasOption(SOURCE.getOpt())) {
      throw new ChroniclerException("No report type specified: " + SOURCE);
    }

    return Paths.get(cli.getOptionValue(SOURCE.getOpt()));
  }

  @Override
  public String user() {
    if (!cli.hasOption(USER.getOpt())) {
      throw new ChroniclerException("No user specified: " + USER);
    }

    return cli.getOptionValue(USER.getOpt());
  }

  @Override
  public String workspace() {
    if (!cli.hasOption(WORKSPACE.getOpt())) {
      throw new ChroniclerException("No workspace specified: " + WORKSPACE);
    }

    return cli.getOptionValue(WORKSPACE.getOpt());
  }

  public Optional<Path> config() {
    return Optional.ofNullable(cli.getOptionValue(CONFIG.getOpt()))
        .map(Paths::get);
  }

  public boolean helpRequested() {
    return cli.hasOption(HELP.getOpt());
  }

  /**
   * Additional arguments passed that were not parsed among base set of options.
   */
  public String[] additionalArgs() {
    return cli.getArgs();
  }

  public static void printHelpMessage() {
    HelpFormatter help = new HelpFormatter();
    help.printHelp("java -jar path-to-jar.jar",
        "Uploads a timesheet report to Rally timesheets.",
        OPTIONS,
        "https://github.com/alechenninger/hamster-rally.git",
        true);
  }
}
