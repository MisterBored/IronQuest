package com.darrenswhite.rs.ironquest.player;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest;
import com.darrenswhite.rs.ironquest.quest.RuneMetricsQuest.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RuneMetricsServiceTest {

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  static final String RUNEMETRICS_FILE = "runemetrics.json";

  static RuneMetricsService runeMetricsService;

  @BeforeAll
  static void beforeAll() {
    String url = Objects
        .requireNonNull(RuneMetricsServiceTest.class.getClassLoader().getResource(RUNEMETRICS_FILE))
        .toString();

    runeMetricsService = new RuneMetricsService(url, OBJECT_MAPPER);
  }

  @Nested
  class Load {

    @Test
    void shouldParseAllQuests() {
      String name = "user";

      RuneMetricsQuest questA = new RuneMetricsQuest.Builder().withTitle("a")
          .withStatus(Status.NOT_STARTED).withDifficulty(1).withMembers(false).withQuestPoints(10)
          .withUserEligible(false).build();
      RuneMetricsQuest questB = new RuneMetricsQuest.Builder().withTitle("b")
          .withStatus(Status.STARTED).withDifficulty(2).withMembers(true).withQuestPoints(20)
          .withUserEligible(false).build();
      RuneMetricsQuest questC = new RuneMetricsQuest.Builder().withTitle("c")
          .withStatus(Status.COMPLETED).withDifficulty(3).withMembers(false).withQuestPoints(30)
          .withUserEligible(true).build();

      Set<RuneMetricsQuest> loadedQuests = runeMetricsService.load(name);

      assertThat(loadedQuests, notNullValue());
      assertThat(loadedQuests, hasSize(3));
      assertThat(loadedQuests, containsInAnyOrder(questA, questB, questC));
    }
  }
}
