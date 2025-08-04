package com.darrenswhite.rs.ironquest.player;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * {@link Service} for retrieving skill data from the Hiscores.
 *
 * @author Darren S. White
 */
@Service
public class HiscoreService {

  private static final Logger LOG = LogManager.getLogger(HiscoreService.class);
  private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.withDelimiter(',');

  /**
   * First row is total level, so skip it.
   */
  private static final int ROW_OFFSET = 1;

  private final String url;

  public HiscoreService(@Value("${hiscores.url}") String url) {
    this.url = url;
  }

  /**
   * Retrieve skill xp data for the given username.
   *
   * @param name the username
   * @return map of xp for each skill
   */
  public Map<Skill, Double> load(String name) {
    Map<Skill, Double> skillXps = new EnumMap<>(Skill.class);

    LOG.debug("Loading hiscores for player: {}...", name);

    try {
      String hiscoresUrl = String
          .format(url, URLEncoder.encode(name, StandardCharsets.UTF_8.toString()));

      URI uri = URI.create(hiscoresUrl);
      Reader reader;

      if ("http".equalsIgnoreCase(uri.getScheme())
          || "https".equalsIgnoreCase(uri.getScheme())) {
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .timeout(Duration.ofSeconds(10))
            .GET()
            .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        reader = new InputStreamReader(response.body());
      } else {
        Path path = Path.of(uri);
        reader = Files.newBufferedReader(path);
      }

      try (Reader in = reader) {
        CSVParser parser = CSV_FORMAT.parse(in);
        List<CSVRecord> records = parser.getRecords();

        for (int i = ROW_OFFSET; i < Skill.values().length + 1; i++) {
          Skill skill = Skill.getById(i);

          if (skill == null) {
            LOG.warn("Unknown skill with id: {}", i);
            continue;
          }

          if (records.size() <= i) {
            LOG.warn("Missing hiscore data for skill: {}", skill);
            continue;
          }

          try {
            CSVRecord r = records.get(i);
            double xp = Math.max(Skill.INITIAL_XPS.get(skill), Double.parseDouble(r.get(2)));
            skillXps.put(skill, xp);
          } catch (IndexOutOfBoundsException | NumberFormatException e) {
            LOG.warn("Malformed hiscore data for skill: {}", skill, e);
          }
        }
      }
    } catch (IOException | InterruptedException e) {
      if (e instanceof InterruptedException) {
        Thread.currentThread().interrupt();
      }
      LOG.warn("Failed to load hiscores for player: {}", name, e);
    }

    return skillXps;
  }
}
