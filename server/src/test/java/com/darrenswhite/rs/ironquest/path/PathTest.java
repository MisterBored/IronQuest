package com.darrenswhite.rs.ironquest.path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.darrenswhite.rs.ironquest.action.Action;
import com.darrenswhite.rs.ironquest.action.LampAction;
import com.darrenswhite.rs.ironquest.action.QuestAction;
import com.darrenswhite.rs.ironquest.action.TrainAction;
import com.darrenswhite.rs.ironquest.dto.PathDTO;
import com.darrenswhite.rs.ironquest.player.Player;
import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.darrenswhite.rs.ironquest.quest.reward.LampReward;
import com.darrenswhite.rs.ironquest.quest.reward.LampType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PathTest {

  @Nested
  class CreateDTO {

    @Test
    void shouldCreateWithCorrectFields() {
      Quest quest = new Quest.Builder().build();
      Player player = new Player.Builder().build();
      LampReward lampReward = new LampReward.Builder(0).withType(LampType.XP).withXp(500).build();
      Set<Skill> skills = Set.of(Skill.HERBLORE);
      LampAction lampAction = new LampAction(player, false, quest, lampReward, skills);
      QuestAction questAction = new QuestAction(player, quest);
      TrainAction trainAction = new TrainAction(player, Skill.ATTACK, 0, 100);
      List<Action> actions = new LinkedList<>(List.of(lampAction, questAction, trainAction));
      PathStats stats = new PathStats(55);
      Path path = new Path(actions, stats);

      PathDTO dto = path.createDTO();

      assertThat(dto.getActions().size(), is(actions.size()));
      assertThat(dto.getStats().getPercentComplete(), is(stats.getPercentComplete()));
    }
  }

  @Nested
  class Equals {

    @Test
    void shouldVerifyEqualsAndHashCode() {
      EqualsVerifier.forClass(Path.class)
          .withPrefabValues(Quest.class, new Quest.Builder(0).build(), new Quest.Builder(1).build())
          .verify();
    }
  }
}
