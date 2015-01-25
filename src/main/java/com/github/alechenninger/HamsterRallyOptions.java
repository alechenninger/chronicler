package com.github.alechenninger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.URI;
import java.net.URISyntaxException;

public class HamsterRallyOptions {
  private static Option API_KEY = new Option("k", "apiKey", true,
      "Rally API key. To generate or access API keys for your account, go to " +
      "'https://rally1.rallydev.com/login' and see 'API KEYS' in the top menu.");

  private static Option SERVER = new Option("h", "host", true,
      "Rally server URL. Defaults to 'https://rally1.rallydev.com'.");

  private static Option TIMESHEET_TYPE = new Option("t", "type", true,
      "Type of timesheet. Available options are 'hamsterxml'. Additional options may be required "
          + "depending on the timesheet type.");

  private static Option HELP = new Option("h", "help", false, "Show this menu.");

  private static Options OPTIONS = new Options()
      .addOption(API_KEY)
      .addOption(SERVER)
      .addOption(TIMESHEET_TYPE)
      .addOption(HELP);

  private final CommandLine cli;

  public HamsterRallyOptions(String[] args) throws ParseException {
    this(args, new BasicParser());
  }

  public HamsterRallyOptions(String[] args, CommandLineParser parser) throws ParseException {
    cli = parser.parse(OPTIONS, args);
  }

  public String apiKey() {
    if (!cli.hasOption(API_KEY.getOpt())) {
      throw new HamsterRallyException("No api key specified.");
    }

    return cli.getOptionValue(API_KEY.getOpt());
  }

  public URI server() throws URISyntaxException {
    if (cli.hasOption(SERVER.getOpt())) {
      return new URI(cli.getOptionValue(SERVER.getOpt()));
    }

    return new URI("https://rally1.rallydev.com");
  }

  public String timeSheetType() {
    if (!cli.hasOption(TIMESHEET_TYPE.getOpt())) {
      throw new HamsterRallyException("No report type specified.");
    }

    return cli.getOptionValue(TIMESHEET_TYPE.getOpt());
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
