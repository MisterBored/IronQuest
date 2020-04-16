package com.darrenswhite.rs.ironquest.quest.reward;

import com.darrenswhite.rs.ironquest.player.Skill;
import com.darrenswhite.rs.ironquest.quest.Quest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A class encapsulating all rewards for a {@link Quest}.
 *
 * @author Darren S. White
 */
@JsonDeserialize(builder = QuestRewards.Builder.class)
@JsonInclude(Include.NON_EMPTY)
public class QuestRewards {

  public static final QuestRewards NONE = new QuestRewards.Builder().build();

  private final Map<Skill, Double> xp;
  private final Set<LampReward> lamps;
  private final int questPoints;

  QuestRewards(Builder builder) {
    this.xp = builder.xp;
    this.lamps = builder.lamps;
    this.questPoints = builder.questPoints;
  }

  public Map<Skill, Double> getXp() {
    return xp;
  }

  public Set<LampReward> getLamps() {
    return lamps;
  }

  public int getQuestPoints() {
    return questPoints;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof QuestRewards)) {
      return false;
    }
    QuestRewards that = (QuestRewards) o;
    return questPoints == that.questPoints && Objects.equals(xp, that.xp) && Objects
        .equals(lamps, that.lamps);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int hashCode() {
    return Objects.hash(xp, lamps, questPoints);
  }

  public static class Builder {

    private Map<Skill, Double> xp = Collections.emptyMap();
    private Set<LampReward> lamps = Collections.emptySet();
    private int questPoints = 0;

    @JsonDeserialize(as = LinkedHashMap.class)
    public Builder withXp(Map<Skill, Double> xp) {
      this.xp = xp;
      return this;
    }

    @JsonDeserialize(as = LinkedHashSet.class)
    public Builder withLamps(Set<LampReward> lamps) {
      this.lamps = lamps;
      return this;
    }

    public Builder withQuestPoints(int questPoints) {
      this.questPoints = questPoints;
      return this;
    }

    public QuestRewards build() {
      return new QuestRewards(this);
    }
  }
}
