# chronicler

Chronicler is a command line java application which takes some time sheet reporting source, parses it, and uploads it to Rally time sheets.

I suppose it could also be flexible about where the destination is, and perhaps could be more than Rally one day.

## Requirements

- Java 8 jre
- Rally api key setup here: https://rally1.rallydev.com/login

## Using

Download binary from release tab. Put in PATH if you like.

Run with -h to see command line flags.

You'll want to create a json config file for ease of use. By default chronicler will look for this in ~/.chronicler/config.json

Sample config.json:
```json
{
  "apiKey": "your-api-key-here",
  "server": "https://rally1.rallydev.com",
  "user": "your-email@your-company.com",
  "workspace": "The workspace you have timesheets enabled",
  "sourcePlugin": {
    "path": "/path/to/a/source/plugin.jar"
  }
}

```

When you run chronicler, you'll generally still need to supply some arguments on the command line for source plugins. For instance, if they read your timesheet data from a file or files, you'll need to pass paths to those files.

Here is an example command using the hamster plugin, assuming it is configured in the config.json:

```bash
chronicler -hc categories.json -hr report.xml
```

## Source plugins

- [chronicler-hamster][1] is a plugin for [Hamster](https://github.com/projecthamster/hamster) activity reports.
- [chronicler-toggl][2] is a plugin for [toggl.com](https://toggl.com)

[1]: https://github.com/alechenninger/chronicler-hamster
[2]: https://github.com/alechenninger/chronicler-toggl
