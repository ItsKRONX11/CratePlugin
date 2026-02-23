package com.hazebyte.crate.cratereloaded.util;

import com.hazebyte.crate.api.crate.Crate;
import com.hazebyte.crate.api.crate.reward.Reward;
import com.hazebyte.crate.cratereloaded.model.RewardImpl;
import java.util.Arrays;
import java.util.Objects;
import org.bukkit.OfflinePlayer;

public class RewardFactory {
    public static Reward createReward(Crate crate, OfflinePlayer player, int amount) {
        Objects.requireNonNull(crate);
        Objects.requireNonNull(player);
        if (amount < 0) {
            throw new IllegalArgumentException("RewardFactory cannot generate a reward with amount less than 0.");
        }

        Reward reward = new RewardImpl();
        reward.setParent(crate);

        String command = String.format("/crate give to %s %s %d", player.getName(), crate.getCrateName(), amount);

        reward.setCommands(Arrays.asList(command));
        reward.setDisplayItem(RewardDisplayItemFactory.createDisplayItem(crate.getDisplayItem(), amount));
        return reward;
    }
}
