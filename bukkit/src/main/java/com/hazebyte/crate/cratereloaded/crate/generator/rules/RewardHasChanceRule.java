package com.hazebyte.crate.cratereloaded.crate.generator.rules;

import com.hazebyte.crate.api.crate.reward.Reward;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

@AllArgsConstructor
public class RewardHasChanceRule implements Predicate<Reward> {

    private final Player player;

    @Override
    public boolean test(Reward reward) {
        return reward.getChance(player) > 0;
    }
}
