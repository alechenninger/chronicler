package com.github.alechenninger.chronicler.config;

import static org.hamcrest.CoreMatchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@RunWith(JUnit4.class)
public class DefaultChroniclerConfigTest {
  @Rule
  public ErrorCollector errorCollector = new ErrorCollector();

  @Test
  public void shouldDeserializeUsingJackson() throws IOException, URISyntaxException {
    String json = "{\n"
        + "  \"apiKey\": \"the_api_key\",\n"
        + "  \"server\": \"http://foo.foo\",\n"
        + "  \"user\": \"my_user\",\n"
        + "  \"workspace\": \"my_workspace\",\n"
        + "  \"sourcePlugin\": {\n"
        + "    \"path\": \"/path/to/plugin\",\n"
        + "    \"args\": [\"arg1\", \"arg2\"]\n"
        + "  }\n"
        + "}";

    ObjectMapper mapper = new ObjectMapper();
    DefaultChroniclerConfig config = mapper.readValue(json.getBytes(), DefaultChroniclerConfig.class);

    errorCollector.checkThat(config.apiKey(), is("the_api_key"));
    errorCollector.checkThat(config.server(), is(new URI("http://foo.foo")));
    errorCollector.checkThat(config.user(), is("my_user"));
    errorCollector.checkThat(config.workspace(), is("my_workspace"));
    errorCollector.checkThat(config.sourcePlugin(), is(Paths.get("/path/to/plugin")));
    errorCollector.checkThat(config.pluginArgs(), is(new String[] {"arg1", "arg2"}));
  }
}
