package com.hazebyte.crate.cratereloaded.crate.generator.rules;

import com.hazebyte.crate.api.crate.reward.Reward;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

public class PermissionRule implements Predicate<Reward> {

    private Player player;

    public PermissionRule(Player player) {
        this.player = player;
    }

    @Override
    public boolean test(Reward reward) {
        return reward.hasPermission(player);
    }
}
