package com.darrenswhite.rs.ironquest.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.darrenswhite.rs.ironquest.dto.QuestActionDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.QuestRewards;
import com.darrenswhite.rs.ironquest.util.MapBuilder;
import java.util.Map;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class QuestActionTest {

  @Nested
  class GetType {

    @Test
    void shouldReturnCorrectType() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().build();

      QuestAction questAction = new QuestAction(player, quest);

      assertThat(questAction.getType(), equalTo(ActionType.QUEST));
    }
  }

  @Nested
  class GetMessage {

    @Test
    void shouldFormatMessage() {
      String displayName = "displayName";
      Quest quest = new Quest.Builder().withDisplayName(displayName).build();
      Player player = new Player.Builder().build();

      QuestAction questAction = new QuestAction(player, quest);

      assertThat(questAction.getMessage(), equalTo(displayName));
      assertThat(questAction.toString(), equalTo(questAction.getMessage()));
    }
  }

  @Nested
  class MeetsRequirements {

    @Test
    void shouldCallMeetsAllRequirementsOnQuest() {
      Quest quest = mock(Quest.class);
      Player player = new Player.Builder().build();

      QuestAction questAction = new QuestAction(player, quest);

      questAction.meetsRequirements(player);

      verify(quest).meetsAllRequirements(player);
    }
  }

  @Nested
  class Process {

    @Test
    void shouldSetStatusToCompletedAndAddSkillXPRewardsToPlayer() {
      Map<Skill, Double> xp = new MapBuilder<Skill, Double>().put(Skill.ATTACK, 5000d)
          .put(Skill.AGILITY, 2500d).put(Skill.COOKING, 100d).build();
      QuestRewards rewards = new QuestRewards.Builder().withXp(xp).withQuestPoints(5).build();
      Quest quest = new Quest.Builder().withRewards(rewards).build();
      Player player = new Player.Builder().withQuests(Set.of(quest)).build();

      QuestAction questAction = new QuestAction(player, quest);

      questAction.process(player);

      assertThat(player.isQuestCompleted(quest), equalTo(true));
      assertThat(player.getXp(Skill.ATTACK), equalTo(5000D));
      assertThat(player.getXp(Skill.AGILITY), equalTo(2500D));
      assertThat(player.getXp(Skill.COOKING), equalTo(100D));
    }
  }

  @Nested
  class CreateDTO {

    @Test
    void shouldCreateWithCorrectFields() {
      String displayName = "displayName";
      Quest quest = new Quest.Builder().withDisplayName(displayName).build();
      Player player = new Player.Builder().build();

      QuestAction questAction = new QuestAction(player, quest);

      QuestActionDTO dto = questAction.createDTO();

      assertThat(dto.getMessage(), equalTo(questAction.getMessage()));
      assertThat(dto.getQuest(), equalTo(quest.createDTO()));
      assertThat(dto.getPlayer(), equalTo(player.createDTO()));
      assertThat(dto.getType(), equalTo(ActionType.QUEST));
      assertThat(dto.isFuture(), equalTo(false));
    }
  }

  @Nested
  class CopyForPlayer {

    @Test
    void shouldCopyAllValues() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().withName("original").build();
      Player playerToCopy = new Player.Builder().withName("copy").build();

      QuestAction questAction = new QuestAction(player, quest);

      QuestAction copied = questAction.copyForPlayer(playerToCopy);

      assertThat(copied.getQuest(), equalTo(quest));
      assertThat(copied.getPlayer(), equalTo(playerToCopy));
      assertThat(copied, not(sameInstance(questAction)));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(QuestAction.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }
}
